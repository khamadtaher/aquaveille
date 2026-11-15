package fr.aquaveille.nappe;

import fr.aquaveille.config.AquaVeilleProperties;
import fr.aquaveille.hubeau.HubEauAnalyseNappe;
import fr.aquaveille.hubeau.HubEauClient;
import fr.aquaveille.hubeau.HubEauPointEau;
import fr.aquaveille.importation.RapportImport;
import fr.aquaveille.parametre.Parametre;
import fr.aquaveille.parametre.ParametreRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Importe les points d'eau souterraine et leurs analyses depuis Hub'Eau (données ADES).
 * Seuls les points ayant au moins une analyse sur la période sont conservés.
 */
@Service
public class ImportNappesService {

    private static final Logger log = LoggerFactory.getLogger(ImportNappesService.class);

    private final HubEauClient hubEau;
    private final PointEauRepository points;
    private final MesureNappeRepository mesures;
    private final ParametreRepository parametres;
    private final AquaVeilleProperties properties;
    private final Clock clock;

    public ImportNappesService(HubEauClient hubEau, PointEauRepository points, MesureNappeRepository mesures,
                               ParametreRepository parametres, AquaVeilleProperties properties, Clock clock) {
        this.hubEau = hubEau;
        this.points = points;
        this.mesures = mesures;
        this.parametres = parametres;
        this.properties = properties;
        this.clock = clock;
    }

    /** Import automatique chaque nuit des départements configurés. */
    @Scheduled(cron = "${aquaveille.import-cron:0 0 3 * * *}")
    public void importPlanifie() {
        properties.departements().forEach(dep -> {
            try {
                importerDepartement(dep);
            } catch (RuntimeException e) {
                log.error("Échec de l'import des eaux souterraines du département {}", dep, e);
            }
        });
    }

    @Transactional
    public RapportImport importerDepartement(String codeDepartement) {
        Map<String, Parametre> parametresSuivis = parametres.findAll().stream()
                .collect(Collectors.toMap(Parametre::getCode, Function.identity()));
        LocalDate depuis = LocalDate.now(clock).minusDays(properties.historiqueJours());

        List<HubEauAnalyseNappe> analyses = hubEau.analysesNappesDuDepartement(
                codeDepartement, parametresSuivis.keySet(), depuis);
        Set<String> pointsAvecAnalyses = analyses.stream()
                .map(HubEauAnalyseNappe::bssId)
                .collect(Collectors.toSet());

        Map<String, PointEau> pointsParId = new HashMap<>();
        for (HubEauPointEau source : hubEau.pointsEauDuDepartement(codeDepartement)) {
            if (source.bssId() != null && pointsAvecAnalyses.contains(source.bssId())
                    && source.latitude() != null && source.longitude() != null) {
                pointsParId.put(source.bssId(), enregistrerPoint(source));
            }
        }

        Set<String> dejaEnregistrees = new HashSet<>();
        for (Object[] cle : mesures.findClesExistantes(codeDepartement)) {
            dejaEnregistrees.add(cle(String.valueOf(cle[0]), String.valueOf(cle[1]), (LocalDate) cle[2]));
        }

        int nouvelles = 0;
        int depassements = 0;
        for (HubEauAnalyseNappe analyse : analyses) {
            PointEau point = pointsParId.get(analyse.bssId());
            Parametre parametre = analyse.codeParam() == null
                    ? null : parametresSuivis.get(String.valueOf(analyse.codeParam()));
            if (point == null || parametre == null
                    || analyse.dateDebutPrelevement() == null || analyse.resultat() == null) {
                continue;
            }
            LocalDate date = analyse.dateDebutPrelevement().toLocalDate();
            // Une seule valeur par point, paramètre et jour (contrainte uk_mesure_nappe).
            if (!dejaEnregistrees.add(cle(point.getBssId(), parametre.getCode(), date))) {
                continue;
            }
            MesureNappe mesure = mesures.save(new MesureNappe(point, parametre, date, analyse.resultat()));
            nouvelles++;
            if (mesure.isHorsSeuil()) {
                depassements++;
            }
        }

        RapportImport rapport = new RapportImport(codeDepartement, pointsParId.size(), nouvelles, depassements);
        log.info("Import eaux souterraines terminé : {}", rapport);
        return rapport;
    }

    private PointEau enregistrerPoint(HubEauPointEau source) {
        PointEau point = points.findById(source.bssId()).orElseGet(() -> new PointEau(source.bssId()));
        point.mettreAJour(source.codeBss(), source.libellePe(), source.nomCommune(), source.numDepartement(),
                source.latitude(), source.longitude(), source.nomNaturePe(), source.profondeurInvestigation(),
                source.nomModeGisement(), source.nomCaracteristiqueAquifere(),
                joindre(source.nomsMasseEauEdl()), source.urnBss(), entites(source));
        return points.save(point);
    }

    /** Associe chaque code BDLISA à son nom (les deux listes Hub'Eau sont dans le même ordre). */
    private static List<EntiteHydrogeologique> entites(HubEauPointEau source) {
        List<String> codes = source.codesEntiteHgBdlisa();
        List<String> noms = source.nomsEntiteHgBdlisa();
        List<EntiteHydrogeologique> resultat = new ArrayList<>();
        if (codes == null) {
            return resultat;
        }
        for (int i = 0; i < codes.size(); i++) {
            String nom = noms != null && i < noms.size() ? noms.get(i) : null;
            if (codes.get(i) != null) {
                resultat.add(new EntiteHydrogeologique(codes.get(i), nom));
            }
        }
        return resultat;
    }

    private static String joindre(List<String> valeurs) {
        return valeurs == null || valeurs.isEmpty() ? null : String.join(" ; ", valeurs);
    }

    private static String cle(String bssId, String codeParametre, LocalDate date) {
        return bssId + '|' + codeParametre + '|' + date;
    }
}
