package fr.aquaveille.nappe;

import fr.aquaveille.hubeau.HubEauAnalyseNappe;
import fr.aquaveille.hubeau.HubEauClient;
import fr.aquaveille.hubeau.HubEauPointEau;
import fr.aquaveille.importation.RapportImport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Test d'intégration : import simulé des eaux souterraines puis lecture via l'API REST. */
@SpringBootTest
@AutoConfigureMockMvc
class ImportNappesServiceTest {

    @MockitoBean
    HubEauClient hubEau;

    @Autowired
    ImportNappesService importService;

    @Autowired
    MesureNappeRepository mesures;

    @Autowired
    MockMvc mvc;

    @Test
    void importeLesPointsEtLesNappesCaptees() throws Exception {
        OffsetDateTime recente = LocalDate.now().minusDays(30).atTime(9, 0).atOffset(ZoneOffset.UTC);
        when(hubEau.pointsEauDuDepartement("77")).thenReturn(List.of(
                // Forage multicouche : deux entités BDLISA
                new HubEauPointEau("BSSTEST001", "02574X0103/F", "Forage test", "Melun", "77",
                        48.54, 2.66, "Forage", 16.5, "Inconnu", "Captif",
                        List.of("113AA01", "926AA01"), List.of("Calcaires de Champigny", "Sables de Fontainebleau"),
                        List.of("Tertiaire - Champigny - en Brie et Soissonnais"), "http://example.org/BSSTEST001"),
                // Puits sur une seule nappe
                new HubEauPointEau("BSSTEST002", "02198X0186/PZA", "Puits test", "Meaux", "77",
                        48.96, 2.88, "Puits", 9.0, null, "Libre",
                        List.of("107AK01"), List.of("Alluvions de la Marne"), null, null),
                // Point sans analyse : ignoré
                new HubEauPointEau("BSSTEST003", null, null, "Provins", "77",
                        48.56, 3.29, "Source", null, null, null, null, null, null, null)));
        when(hubEau.analysesNappesDuDepartement(eq("77"), any(), any())).thenReturn(List.of(
                new HubEauAnalyseNappe("BSSTEST001", 1340, recente, 62.0),          // nitrates > 50 : alerte
                new HubEauAnalyseNappe("BSSTEST001", 1340, recente.plusHours(2), 61.0), // même jour : ignoré
                new HubEauAnalyseNappe("BSSTEST002", 1311, recente, 2.0),           // O2 bas : pas d'alerte en nappe
                new HubEauAnalyseNappe("BSSTEST002", 9999, recente, 1.0),           // paramètre non suivi
                new HubEauAnalyseNappe("INCONNU", 1340, recente, 80.0)));           // point inconnu

        RapportImport rapport = importService.importerDepartement("77");

        assertThat(rapport.stations()).isEqualTo(2);
        assertThat(rapport.nouvellesMesures()).isEqualTo(2);
        assertThat(rapport.depassements()).isEqualTo(1);

        // Un second import ne crée pas de doublons
        importService.importerDepartement("77");
        assertThat(mesures.count()).isEqualTo(2);

        mvc.perform(get("/api/nappes/points").param("departement", "77"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        mvc.perform(get("/api/nappes/points/BSSTEST001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nappeUnique").value(false))
                .andExpect(jsonPath("$.entites.length()").value(2))
                .andExpect(jsonPath("$.entites[0].code").value("113AA01"))
                .andExpect(jsonPath("$.enAlerte").value(true));

        mvc.perform(get("/api/nappes/points/BSSTEST002"))
                .andExpect(jsonPath("$.nappeUnique").value(true))
                .andExpect(jsonPath("$.enAlerte").value(false));

        mvc.perform(get("/api/nappes/points/BSSTEST001/mesures"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
}
