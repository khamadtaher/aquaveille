package fr.aquaveille.web;

import fr.aquaveille.mesure.MesureRepository;
import fr.aquaveille.station.Station;
import fr.aquaveille.station.StationRepository;
import fr.aquaveille.web.Dtos.MesureDto;
import fr.aquaveille.web.Dtos.StationDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Clock;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/stations")
public class StationController {

    /** Une station est "en alerte" si elle a eu un dépassement dans cette fenêtre. */
    static final int FENETRE_ALERTE_JOURS = 365;

    private final StationRepository stations;
    private final MesureRepository mesures;
    private final Clock clock;

    public StationController(StationRepository stations, MesureRepository mesures, Clock clock) {
        this.stations = stations;
        this.mesures = mesures;
        this.clock = clock;
    }

    @GetMapping
    public List<StationDto> lister(@RequestParam(required = false) String departement) {
        List<Station> liste = departement == null
                ? stations.findAll()
                : stations.findByCodeDepartementOrderByLibelle(departement);
        Set<String> enAlerte = new HashSet<>(mesures.findCodesStationsEnAlerte(debutFenetre()));
        return liste.stream()
                .map(s -> StationDto.of(s, enAlerte.contains(s.getCode())))
                .toList();
    }

    @GetMapping("/{code}")
    public StationDto detail(@PathVariable String code) {
        Station station = stations.findById(code)
                .orElseThrow(() -> new RessourceIntrouvableException("Station inconnue : " + code));
        boolean enAlerte = mesures.findCodesStationsEnAlerte(debutFenetre()).contains(code);
        return StationDto.of(station, enAlerte);
    }

    @GetMapping("/{code}/mesures")
    public List<MesureDto> mesures(@PathVariable String code,
                                   @RequestParam(required = false) String parametre) {
        if (!stations.existsById(code)) {
            throw new RessourceIntrouvableException("Station inconnue : " + code);
        }
        return mesures.findHistorique(code, parametre).stream().map(MesureDto::of).toList();
    }

    private LocalDate debutFenetre() {
        return LocalDate.now(clock).minusDays(FENETRE_ALERTE_JOURS);
    }
}
