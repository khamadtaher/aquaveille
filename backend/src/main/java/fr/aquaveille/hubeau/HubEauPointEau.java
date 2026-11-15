package fr.aquaveille.hubeau;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

/** Point d'eau renvoyé par /qualite_nappes/stations (champs utiles uniquement). */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record HubEauPointEau(
        String bssId,
        String codeBss,
        String libellePe,
        String nomCommune,
        String numDepartement,
        Double latitude,
        Double longitude,
        String nomNaturePe,
        Double profondeurInvestigation,
        String nomCaracteristiqueAquifere,
        String nomModeGisement,
        List<String> codesEntiteHgBdlisa,
        List<String> nomsEntiteHgBdlisa,
        List<String> nomsMasseEauEdl,
        String urnBss
) {
}
