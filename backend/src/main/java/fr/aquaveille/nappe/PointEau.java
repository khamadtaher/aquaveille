package fr.aquaveille.nappe;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * Point d'eau souterraine (forage, puits, source) identifié dans la Banque du sous-sol (BSS).
 * Un point peut être rattaché à plusieurs entités hydrogéologiques s'il traverse plusieurs nappes.
 */
@Entity
@Table(name = "point_eau")
public class PointEau {

    @Id
    @Column(name = "bss_id", length = 20)
    private String bssId;

    @Column(name = "code_bss", length = 60)
    private String codeBss;

    @Column(length = 1000)
    private String libelle;

    private String commune;

    @Column(name = "code_departement", length = 3)
    private String codeDepartement;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    /** Nature du point : forage, puits, source… */
    private String nature;

    /** Profondeur d'investigation du point, en mètres. */
    private Double profondeur;

    /** Mode de gisement de la nappe : libre, captive… */
    @Column(name = "mode_gisement")
    private String modeGisement;

    @Column(name = "caracteristique_aquifere")
    private String caracteristiqueAquifere;

    /** Noms des masses d'eau DCE, séparés par " ; ". */
    @Column(name = "masses_eau", length = 2000)
    private String massesEau;

    @Column(name = "urn_bss")
    private String urnBss;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "point_eau_entite", joinColumns = @JoinColumn(name = "point_bss_id"))
    @OrderColumn(name = "ordre")
    private List<EntiteHydrogeologique> entites = new ArrayList<>();

    protected PointEau() {
        // requis par JPA
    }

    public PointEau(String bssId) {
        this.bssId = bssId;
    }

    /** Recopie les informations descriptives reçues de Hub'Eau. */
    public void mettreAJour(String codeBss, String libelle, String commune, String codeDepartement,
                            double latitude, double longitude, String nature, Double profondeur,
                            String modeGisement, String caracteristiqueAquifere, String massesEau,
                            String urnBss, List<EntiteHydrogeologique> entites) {
        this.codeBss = codeBss;
        this.libelle = libelle;
        this.commune = commune;
        this.codeDepartement = codeDepartement;
        this.latitude = latitude;
        this.longitude = longitude;
        this.nature = nature;
        this.profondeur = profondeur;
        this.modeGisement = modeGisement;
        this.caracteristiqueAquifere = caracteristiqueAquifere;
        this.massesEau = massesEau;
        this.urnBss = urnBss;
        this.entites.clear();
        this.entites.addAll(entites);
    }

    /** Vrai si le point n'est rattaché qu'à une seule entité hydrogéologique. */
    public boolean isNappeUnique() {
        return entites.size() == 1;
    }

    public String getBssId() { return bssId; }
    public String getCodeBss() { return codeBss; }
    public String getLibelle() { return libelle; }
    public String getCommune() { return commune; }
    public String getCodeDepartement() { return codeDepartement; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public String getNature() { return nature; }
    public Double getProfondeur() { return profondeur; }
    public String getModeGisement() { return modeGisement; }
    public String getCaracteristiqueAquifere() { return caracteristiqueAquifere; }
    public String getMassesEau() { return massesEau; }
    public String getUrnBss() { return urnBss; }
    public List<EntiteHydrogeologique> getEntites() { return entites; }
}
