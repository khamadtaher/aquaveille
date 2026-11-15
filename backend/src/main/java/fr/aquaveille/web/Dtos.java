package fr.aquaveille.web;

import fr.aquaveille.mesure.Mesure;
import fr.aquaveille.nappe.EntiteHydrogeologique;
import fr.aquaveille.nappe.MesureNappe;
import fr.aquaveille.nappe.PointEau;
import fr.aquaveille.parametre.Parametre;
import fr.aquaveille.station.Station;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

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

    public record ParametreDto(String code, String libelle, String unite, Double seuilMin, Double seuilMax,
                               Double seuilMinNappe, Double seuilMaxNappe) {
        public static ParametreDto of(Parametre p) {
            return new ParametreDto(p.getCode(), p.getLibelle(), p.getUnite(), p.getSeuilMin(), p.getSeuilMax(),
                    p.getSeuilMinNappe(), p.getSeuilMaxNappe());
        }
    }

    public record MesureDto(LocalDate date, String codeParametre, String parametre, String unite,
                            double valeur, boolean horsSeuil) {
        public static MesureDto of(Mesure m) {
            Parametre p = m.getParametre();
            return new MesureDto(m.getDatePrelevement(), p.getCode(), p.getLibelle(), p.getUnite(),
                    m.getValeur(), m.isHorsSeuil());
        }

        public static MesureDto ofNappe(MesureNappe m) {
            Parametre p = m.getParametre();
            return new MesureDto(m.getDatePrelevement(), p.getCode(), p.getLibelle(), p.getUnite(),
                    m.getValeur(), m.isHorsSeuil());
        }
    }

    public record EntiteDto(String code, String nom) {
        public static EntiteDto of(EntiteHydrogeologique e) {
            return new EntiteDto(e.getCode(), e.getNom());
        }
    }

    public record PointEauDto(String bssId, String codeBss, String libelle, String commune, String codeDepartement,
                              double latitude, double longitude, String nature, Double profondeur,
                              String modeGisement, String caracteristiqueAquifere, List<String> massesEau,
                              List<EntiteDto> entites, boolean nappeUnique, String urnBss, boolean enAlerte) {
        public static PointEauDto of(PointEau p, boolean enAlerte) {
            List<String> masses = p.getMassesEau() == null ? List.of()
                    : Arrays.stream(p.getMassesEau().split(" ; ")).toList();
            return new PointEauDto(p.getBssId(), p.getCodeBss(), p.getLibelle(), p.getCommune(),
                    p.getCodeDepartement(), p.getLatitude(), p.getLongitude(), p.getNature(), p.getProfondeur(),
                    p.getModeGisement(), p.getCaracteristiqueAquifere(), masses,
                    p.getEntites().stream().map(EntiteDto::of).toList(), p.isNappeUnique(), p.getUrnBss(),
                    enAlerte);
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
