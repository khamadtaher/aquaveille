package fr.aquaveille.web;

import fr.aquaveille.mesure.MesureRepository;
import fr.aquaveille.web.Dtos.AlerteDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/alertes")
public class AlerteController {

    private final MesureRepository mesures;
    private final Clock clock;

    public AlerteController(MesureRepository mesures, Clock clock) {
        this.mesures = mesures;
        this.clock = clock;
    }

    /** Dépassements de seuil des N derniers jours (365 par défaut), les plus récents d'abord. */
    @GetMapping
    public List<AlerteDto> lister(@RequestParam(defaultValue = "365") int jours) {
        LocalDate depuis = LocalDate.now(clock).minusDays(Math.max(1, jours));
        return mesures.findDepassementsDepuis(depuis).stream().map(AlerteDto::of).toList();
    }
}
