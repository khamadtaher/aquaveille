package fr.aquaveille.hubeau;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/** Station telle que renvoyée par /qualite_rivieres/station_pc (champs utiles uniquement). */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record HubEauStation(
        String codeStation,
        String libelleStation,
        String libelleCommune,
        String codeDepartement,
        String nomCoursEau,
        Double latitude,
        Double longitude
) {
}
