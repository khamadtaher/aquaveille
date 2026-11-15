package fr.aquaveille.web;

import fr.aquaveille.importation.RapportImport;
import fr.aquaveille.nappe.ImportNappesService;
import fr.aquaveille.nappe.MesureNappeRepository;
import fr.aquaveille.nappe.PointEau;
import fr.aquaveille.nappe.PointEauRepository;
import fr.aquaveille.web.Dtos.MesureDto;
import fr.aquaveille.web.Dtos.PointEauDto;
import jakarta.validation.constraints.Pattern;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Clock;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** API des eaux souterraines : points d'eau, mesures et import depuis Hub'Eau. */
@RestController
@RequestMapping("/api/nappes")
public class NappeController {

    private final PointEauRepository points;
    private final MesureNappeRepository mesures;
    private final ImportNappesService importService;
    private final Clock clock;

    public NappeController(PointEauRepository points, MesureNappeRepository mesures,
                           ImportNappesService importService, Clock clock) {
        this.points = points;
        this.mesures = mesures;
        this.importService = importService;
        this.clock = clock;
    }

    @GetMapping("/points")
    @Transactional(readOnly = true)
    public List<PointEauDto> lister(@RequestParam(required = false) String departement) {
        List<PointEau> liste = departement == null
                ? points.findAllAvecEntites()
                : points.findByDepartementAvecEntites(departement);
        Set<String> enAlerte = new HashSet<>(mesures.findPointsEnAlerte(debutFenetre()));
        return liste.stream().map(p -> PointEauDto.of(p, enAlerte.contains(p.getBssId()))).toList();
    }

    @GetMapping("/points/{bssId}")
    @Transactional(readOnly = true)
    public PointEauDto detail(@PathVariable String bssId) {
        PointEau point = points.findById(bssId)
                .orElseThrow(() -> new RessourceIntrouvableException("Point d'eau inconnu : " + bssId));
        return PointEauDto.of(point, mesures.findPointsEnAlerte(debutFenetre()).contains(bssId));
    }

    @GetMapping("/points/{bssId}/mesures")
    @Transactional(readOnly = true)
    public List<MesureDto> mesures(@PathVariable String bssId,
                                   @RequestParam(required = false) String parametre) {
        if (!points.existsById(bssId)) {
            throw new RessourceIntrouvableException("Point d'eau inconnu : " + bssId);
        }
        return mesures.findHistorique(bssId, parametre).stream().map(MesureDto::ofNappe).toList();
    }

    @PostMapping("/import/{departement}")
    public RapportImport importer(
            @PathVariable @Pattern(regexp = "\\d{2}|2[AB]|97\\d", message = "Code département invalide")
            String departement) {
        return importService.importerDepartement(departement);
    }

    private LocalDate debutFenetre() {
        return LocalDate.now(clock).minusDays(StationController.FENETRE_ALERTE_JOURS);
    }
}
