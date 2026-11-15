package fr.aquaveille.importation;

import fr.aquaveille.config.AquaVeilleProperties;
import fr.aquaveille.hubeau.HubEauAnalyse;
import fr.aquaveille.hubeau.HubEauClient;
import fr.aquaveille.hubeau.HubEauStation;
import fr.aquaveille.mesure.Mesure;
import fr.aquaveille.mesure.MesureRepository;
import fr.aquaveille.parametre.Parametre;
import fr.aquaveille.parametre.ParametreRepository;
import fr.aquaveille.station.Station;
import fr.aquaveille.station.StationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Importe les stations et les analyses d'un département depuis Hub'Eau,
 * puis marque les mesures qui dépassent les seuils des paramètres suivis.
 */
@Service
public class ImportService {

    private static final Logger log = LoggerFactory.getLogger(ImportService.class);

    private final HubEauClient hubEau;
    private final StationRepository stations;
    private final ParametreRepository parametres;
    private final MesureRepository mesures;
    private final AquaVeilleProperties properties;
    private final Clock clock;

    public ImportService(HubEauClient hubEau, StationRepository stations, ParametreRepository parametres,
                         MesureRepository mesures, AquaVeilleProperties properties, Clock clock) {
        this.hubEau = hubEau;
        this.stations = stations;
        this.parametres = parametres;
        this.mesures = mesures;
        this.properties = properties;
        this.clock = clock;
    }

    /** Import automatique chaque nuit à 3 h des départements configurés. */
    @Scheduled(cron = "${aquaveille.import-cron:0 0 3 * * *}")
    public void importPlanifie() {
        properties.departements().forEach(dep -> {
            try {
                importerDepartement(dep);
            } catch (RuntimeException e) {
                log.error("Échec de l'import du département {}", dep, e);
            }
        });
    }

    @Transactional
    public RapportImport importerDepartement(String codeDepartement) {
        Map<String, Station> stationsParCode = importerStations(codeDepartement);

        Map<String, Parametre> parametresSuivis = parametres.findAll().stream()
                .collect(Collectors.toMap(Parametre::getCode, Function.identity()));
        LocalDate depuis = LocalDate.now(clock).minusDays(properties.historiqueJours());

        int nouvelles = 0;
        int depassements = 0;
        for (HubEauAnalyse analyse : hubEau.analysesDuDepartement(
                codeDepartement, parametresSuivis.keySet(), depuis)) {
            Station station = stationsParCode.get(analyse.codeStation());
            Parametre parametre = parametresSuivis.get(analyse.codeParametre());
            if (!estExploitable(analyse, station, parametre)
                    || mesures.existsByCodeAnalyse(analyse.codeAnalyse())) {
                continue;
            }
            Mesure mesure = mesures.save(new Mesure(analyse.codeAnalyse(), station, parametre,
                    analyse.datePrelevement(), analyse.resultat()));
            nouvelles++;
            if (mesure.isHorsSeuil()) {
                depassements++;
            }
        }

        RapportImport rapport = new RapportImport(codeDepartement, stationsParCode.size(), nouvelles, depassements);
        log.info("Import terminé : {}", rapport);
        return rapport;
    }

    private Map<String, Station> importerStations(String codeDepartement) {
        return hubEau.stationsDuDepartement(codeDepartement).stream()
                .filter(s -> s.latitude() != null && s.longitude() != null)
                .map(this::enregistrerStation)
                .collect(Collectors.toMap(Station::getCode, Function.identity(), (a, b) -> a));
    }

    private Station enregistrerStation(HubEauStation source) {
        Station station = stations.findById(source.codeStation())
                .orElseGet(() -> new Station(source.codeStation(), source.libelleStation(),
                        source.libelleCommune(), source.codeDepartement(), source.nomCoursEau(),
                        source.latitude(), source.longitude()));
        station.mettreAJour(source.libelleStation(), source.libelleCommune(), source.codeDepartement(),
                source.nomCoursEau(), source.latitude(), source.longitude());
        return stations.save(station);
    }

    private static boolean estExploitable(HubEauAnalyse a, Station station, Parametre parametre) {
        return station != null && parametre != null
                && a.codeAnalyse() != null && a.datePrelevement() != null && a.resultat() != null;
    }
}
