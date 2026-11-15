package fr.aquaveille.web;

import fr.aquaveille.mesure.Mesure;
import fr.aquaveille.parametre.Parametre;
import fr.aquaveille.station.Station;

import java.time.LocalDate;

/** Objets exposés par l'API REST (séparés des entités JPA). */
public final class Dtos {

    private Dtos() {
    }

    public record StationDto(String code, String libelle, String commune, String codeDepartement,
                             String coursEau, double latitude, double longitude, boolean enAlerte) {
        public static StationDto of(Station s, boolean enAlerte) {
            return new StationDto(s.getCode(), s.getLibelle(), s.getCommune(), s.getCodeDepartement(),
                    s.getCoursEau(), s.getLatitude(), s.getLongitude(), enAlerte);
        }
    }

    public record ParametreDto(String code, String libelle, String unite, Double seuilMin, Double seuilMax) {
        public static ParametreDto of(Parametre p) {
            return new ParametreDto(p.getCode(), p.getLibelle(), p.getUnite(), p.getSeuilMin(), p.getSeuilMax());
        }
    }

    public record MesureDto(LocalDate date, String codeParametre, String parametre, String unite,
                            double valeur, boolean horsSeuil) {
        public static MesureDto of(Mesure m) {
            Parametre p = m.getParametre();
            return new MesureDto(m.getDatePrelevement(), p.getCode(), p.getLibelle(), p.getUnite(),
                    m.getValeur(), m.isHorsSeuil());
        }
    }

    public record AlerteDto(String codeStation, String station, String commune, LocalDate date,
                            String parametre, double valeur, String unite, Double seuilMin, Double seuilMax) {
        public static AlerteDto of(Mesure m) {
            Station s = m.getStation();
            Parametre p = m.getParametre();
            return new AlerteDto(s.getCode(), s.getLibelle(), s.getCommune(), m.getDatePrelevement(),
                    p.getLibelle(), m.getValeur(), p.getUnite(), p.getSeuilMin(), p.getSeuilMax());
        }
    }
}
