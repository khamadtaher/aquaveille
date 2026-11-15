package fr.aquaveille.hubeau;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Client de l'API publique Hub'Eau "Qualité des cours d'eau" (v2).
 * Documentation : https://hubeau.eaufrance.fr/page/api-qualite-cours-deau
 */
@Component
public class HubEauClient {

    private static final Logger log = LoggerFactory.getLogger(HubEauClient.class);
    private static final int TAILLE_PAGE = 1000;
    /** Hub'Eau limite la profondeur d'accès à 20 000 résultats par requête. */
    private static final int MAX_PAGES = 20;

    private static final String CHAMPS_STATION =
            "code_station,libelle_station,libelle_commune,code_departement,nom_cours_eau,latitude,longitude";
    private static final String CHAMPS_ANALYSE =
            "code_analyse,code_station,code_parametre,date_prelevement,resultat";

    private final RestClient restClient;

    public HubEauClient(RestClient hubEauRestClient) {
        this.restClient = hubEauRestClient;
    }

    public List<HubEauStation> stationsDuDepartement(String codeDepartement) {
        return toutesLesPages(
                "/api/v2/qualite_rivieres/station_pc?code_departement=" + codeDepartement
                        + "&fields=" + CHAMPS_STATION + "&size=" + TAILLE_PAGE,
                new ParameterizedTypeReference<HubEauPage<HubEauStation>>() { });
    }

    public List<HubEauAnalyse> analysesDuDepartement(String codeDepartement,
                                                     Collection<String> codesParametres,
                                                     LocalDate depuis) {
        String url = "/api/v2/qualite_rivieres/analyse_pc"
                + "?code_departement=" + codeDepartement
                + "&code_parametre=" + String.join(",", codesParametres)
                + "&date_debut_prelevement=" + depuis
                + "&fields=" + CHAMPS_ANALYSE
                + "&size=" + TAILLE_PAGE;
        return toutesLesPages(url, new ParameterizedTypeReference<HubEauPage<HubEauAnalyse>>() { });
    }

    /** Parcourt les pages en suivant le lien "next" fourni par l'API. */
    private <T> List<T> toutesLesPages(String premiereUrl,
                                       ParameterizedTypeReference<HubEauPage<T>> type) {
        List<T> resultats = new ArrayList<>();
        String url = premiereUrl;
        int page = 0;
        while (url != null && page < MAX_PAGES) {
            HubEauPage<T> reponse = url.startsWith("http")
                    ? restClient.get().uri(URI.create(url)).retrieve().body(type)
                    : restClient.get().uri(url).retrieve().body(type);
            if (reponse == null || reponse.data() == null) {
                break;
            }
            resultats.addAll(reponse.data());
            url = reponse.next();
            page++;
        }
        log.debug("Hub'Eau : {} éléments récupérés ({} pages) depuis {}", resultats.size(), page, premiereUrl);
        return resultats;
    }
}
