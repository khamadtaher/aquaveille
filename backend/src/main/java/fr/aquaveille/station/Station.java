package fr.aquaveille.station;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Station de mesure de la qualité d'un cours d'eau (référentiel SANDRE). */
@Entity
@Table(name = "station")
public class Station {

    @Id
    @Column(length = 20)
    private String code;

    @Column(nullable = false)
    private String libelle;

    private String commune;

    @Column(name = "code_departement", length = 3)
    private String codeDepartement;

    @Column(name = "cours_eau")
    private String coursEau;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    protected Station() {
        // requis par JPA
    }

    public Station(String code, String libelle, String commune, String codeDepartement,
                   String coursEau, double latitude, double longitude) {
        this.code = code;
        mettreAJour(libelle, commune, codeDepartement, coursEau, latitude, longitude);
    }

    public void mettreAJour(String libelle, String commune, String codeDepartement,
                            String coursEau, double latitude, double longitude) {
        this.libelle = libelle;
        this.commune = commune;
        this.codeDepartement = codeDepartement;
        this.coursEau = coursEau;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getCode() { return code; }
    public String getLibelle() { return libelle; }
    public String getCommune() { return commune; }
    public String getCodeDepartement() { return codeDepartement; }
    public String getCoursEau() { return coursEau; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
}
