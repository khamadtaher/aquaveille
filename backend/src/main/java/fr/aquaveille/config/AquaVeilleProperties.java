package fr.aquaveille.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Paramètres de l'application, définis dans application.yml (préfixe "aquaveille").
 *
 * @param hubeauBaseUrl     URL de base de l'API Hub'Eau
 * @param departements      départements importés automatiquement (ex. 91, 75)
 * @param historiqueJours   profondeur de l'historique des analyses à importer
 * @param corsOrigins       origines autorisées à appeler l'API (front Angular)
 */
@ConfigurationProperties(prefix = "aquaveille")
public record AquaVeilleProperties(
        String hubeauBaseUrl,
        List<String> departements,
        int historiqueJours,
        List<String> corsOrigins
) {
}
