package fr.aquaveille.nappe;

import fr.aquaveille.parametre.Parametre;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;

/** Résultat d'une analyse d'eau souterraine : un paramètre, un point d'eau, une date. */
@Entity
@Table(name = "mesure_nappe")
public class MesureNappe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "point_bss_id")
    private PointEau point;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "parametre_code")
    private Parametre parametre;

    @Column(name = "date_prelevement", nullable = false)
    private LocalDate datePrelevement;

    @Column(nullable = false)
    private double valeur;

    @Column(name = "hors_seuil", nullable = false)
    private boolean horsSeuil;

    protected MesureNappe() {
    }

    public MesureNappe(PointEau point, Parametre parametre, LocalDate datePrelevement, double valeur) {
        this.point = point;
        this.parametre = parametre;
        this.datePrelevement = datePrelevement;
        this.valeur = valeur;
        this.horsSeuil = parametre.estHorsSeuilNappe(valeur);
    }

    public Long getId() { return id; }
    public PointEau getPoint() { return point; }
    public Parametre getParametre() { return parametre; }
    public LocalDate getDatePrelevement() { return datePrelevement; }
    public double getValeur() { return valeur; }
    public boolean isHorsSeuil() { return horsSeuil; }
}
