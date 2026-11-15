package fr.aquaveille.mesure;

import fr.aquaveille.parametre.Parametre;
import fr.aquaveille.station.Station;
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

/** Résultat d'une analyse : une valeur d'un paramètre, à une station, à une date. */
@Entity
@Table(name = "mesure")
public class Mesure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Identifiant de l'analyse chez Hub'Eau, pour éviter les doublons à l'import. */
    @Column(name = "code_analyse", nullable = false, unique = true, length = 40)
    private String codeAnalyse;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "station_code")
    private Station station;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "parametre_code")
    private Parametre parametre;

    @Column(name = "date_prelevement", nullable = false)
    private LocalDate datePrelevement;

    @Column(nullable = false)
    private double valeur;

    @Column(name = "hors_seuil", nullable = false)
    private boolean horsSeuil;

    protected Mesure() {
    }

    public Mesure(String codeAnalyse, Station station, Parametre parametre,
                  LocalDate datePrelevement, double valeur) {
        this.codeAnalyse = codeAnalyse;
        this.station = station;
        this.parametre = parametre;
        this.datePrelevement = datePrelevement;
        this.valeur = valeur;
        this.horsSeuil = parametre.estHorsSeuil(valeur);
    }

    public Long getId() { return id; }
    public String getCodeAnalyse() { return codeAnalyse; }
    public Station getStation() { return station; }
    public Parametre getParametre() { return parametre; }
    public LocalDate getDatePrelevement() { return datePrelevement; }
    public double getValeur() { return valeur; }
    public boolean isHorsSeuil() { return horsSeuil; }
}
