package fr.aquaveille.hubeau;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.LocalDate;

/** Résultat d'analyse renvoyé par /qualite_rivieres/analyse_pc (champs utiles uniquement). */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record HubEauAnalyse(
        String codeAnalyse,
        String codeStation,
        String codeParametre,
        LocalDate datePrelevement,
        Double resultat
) {
}
