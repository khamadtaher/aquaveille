package fr.aquaveille.nappe;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/** Entité hydrogéologique du référentiel BDLISA (BRGM) à laquelle un point d'eau est rattaché. */
@Embeddable
public class EntiteHydrogeologique {

    @Column(nullable = false, length = 20)
    private String code;

    @Column(length = 1000)
    private String nom;

    protected EntiteHydrogeologique() {
        // requis par JPA
    }

    public EntiteHydrogeologique(String code, String nom) {
        this.code = code;
        this.nom = nom;
    }

    public String getCode() { return code; }
    public String getNom() { return nom; }
}
