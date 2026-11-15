package fr.aquaveille.parametre;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Paramètre physico-chimique suivi (code SANDRE) et ses seuils d'alerte.
 * Un seuil null signifie "pas de limite dans ce sens".
 */
@Entity
@Table(name = "parametre")
public class Parametre {

    @Id
    @Column(length = 10)
    private String code;

    @Column(nullable = false)
    private String libelle;

    @Column(nullable = false)
    private String unite;

    @Column(name = "seuil_min")
    private Double seuilMin;

    @Column(name = "seuil_max")
    private Double seuilMax;

    /** Seuils appliqués aux eaux souterraines (null = pas d'alerte pour les nappes). */
    @Column(name = "seuil_min_nappe")
    private Double seuilMinNappe;

    @Column(name = "seuil_max_nappe")
    private Double seuilMaxNappe;

    protected Parametre() {
    }

    public Parametre(String code, String libelle, String unite, Double seuilMin, Double seuilMax) {
        this.code = code;
        this.libelle = libelle;
        this.unite = unite;
        this.seuilMin = seuilMin;
        this.seuilMax = seuilMax;
    }

    public Parametre(String code, String libelle, String unite, Double seuilMin, Double seuilMax,
                     Double seuilMinNappe, Double seuilMaxNappe) {
        this(code, libelle, unite, seuilMin, seuilMax);
        this.seuilMinNappe = seuilMinNappe;
        this.seuilMaxNappe = seuilMaxNappe;
    }

    /** Vrai si la valeur sort de la plage autorisée [seuilMin ; seuilMax]. */
    public boolean estHorsSeuil(double valeur) {
        return (seuilMin != null && valeur < seuilMin)
                || (seuilMax != null && valeur > seuilMax);
    }

    /** Même règle que {@link #estHorsSeuil(double)}, avec les seuils des eaux souterraines. */
    public boolean estHorsSeuilNappe(double valeur) {
        return (seuilMinNappe != null && valeur < seuilMinNappe)
                || (seuilMaxNappe != null && valeur > seuilMaxNappe);
    }

    public String getCode() { return code; }
    public String getLibelle() { return libelle; }
    public String getUnite() { return unite; }
    public Double getSeuilMin() { return seuilMin; }
    public Double getSeuilMax() { return seuilMax; }
    public Double getSeuilMinNappe() { return seuilMinNappe; }
    public Double getSeuilMaxNappe() { return seuilMaxNappe; }
}
