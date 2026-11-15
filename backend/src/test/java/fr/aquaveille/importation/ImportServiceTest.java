package fr.aquaveille.importation;

import fr.aquaveille.hubeau.HubEauAnalyse;
import fr.aquaveille.hubeau.HubEauClient;
import fr.aquaveille.hubeau.HubEauStation;
import fr.aquaveille.mesure.MesureRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Test d'intégration : import simulé (Hub'Eau mocké) puis lecture via l'API REST. */
@SpringBootTest
@AutoConfigureMockMvc
class ImportServiceTest {

    @MockitoBean
    HubEauClient hubEau;

    @Autowired
    ImportService importService;

    @Autowired
    MesureRepository mesures;

    @Autowired
    MockMvc mvc;

    @Test
    void importeLesMesuresEtDetecteLesDepassements() throws Exception {
        LocalDate recente = LocalDate.now().minusDays(30);
        when(hubEau.stationsDuDepartement("91")).thenReturn(List.of(
                new HubEauStation("03084000", "L'Essonne à Ballancourt", "Ballancourt-sur-Essonne",
                        "91", "L'Essonne", 48.525, 2.385)));
        when(hubEau.analysesDuDepartement(eq("91"), any(), any())).thenReturn(List.of(
                new HubEauAnalyse("A1", "03084000", "1340", recente, 31.0),   // nitrates conformes
                new HubEauAnalyse("A2", "03084000", "1311", recente, 4.5),    // oxygène trop bas
                new HubEauAnalyse("A3", "99999999", "1340", recente, 80.0),   // station inconnue : ignorée
                new HubEauAnalyse("A4", "03084000", "9999", recente, 1.0)));  // paramètre non suivi : ignoré

        RapportImport rapport = importService.importerDepartement("91");

        assertThat(rapport.stations()).isEqualTo(1);
        assertThat(rapport.nouvellesMesures()).isEqualTo(2);
        assertThat(rapport.depassements()).isEqualTo(1);

        // Un second import ne crée pas de doublons
        assertThat(importService.importerDepartement("91").nouvellesMesures()).isZero();
        assertThat(mesures.count()).isEqualTo(2);

        mvc.perform(get("/api/stations").param("departement", "91"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("03084000"))
                .andExpect(jsonPath("$[0].enAlerte").value(true));

        mvc.perform(get("/api/alertes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].parametre").value("Oxygène dissous"));

        mvc.perform(get("/api/stations/inconnue"))
                .andExpect(status().isNotFound());
    }
}
