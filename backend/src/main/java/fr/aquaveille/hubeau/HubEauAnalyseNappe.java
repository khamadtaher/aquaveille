package fr.aquaveille.hubeau;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.OffsetDateTime;

/** Résultat d'analyse renvoyé par /qualite_nappes/analyses (champs utiles uniquement). */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record HubEauAnalyseNappe(
        String bssId,
        Integer codeParam,
        OffsetDateTime dateDebutPrelevement,
        Double resultat
) {
}
