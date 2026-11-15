package fr.aquaveille.mesure;

import fr.aquaveille.parametre.Parametre;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ParametreTest {

    private final Parametre nitrates = new Parametre("1340", "Nitrates", "mg(NO3)/L", null, 50.0);
    private final Parametre oxygene = new Parametre("1311", "Oxygène dissous", "mg(O2)/L", 6.0, null);
    private final Parametre ph = new Parametre("1302", "pH", "unité pH", 6.0, 9.0);

    @Test
    void depasseLeSeuilMaximum() {
        assertThat(nitrates.estHorsSeuil(50.0)).isFalse();
        assertThat(nitrates.estHorsSeuil(52.3)).isTrue();
    }

    @Test
    void passeSousLeSeuilMinimum() {
        assertThat(oxygene.estHorsSeuil(7.1)).isFalse();
        assertThat(oxygene.estHorsSeuil(4.2)).isTrue();
    }

    @Test
    void verifieUnePlageAvecDeuxBornes() {
        assertThat(ph.estHorsSeuil(7.4)).isFalse();
        assertThat(ph.estHorsSeuil(5.5)).isTrue();
        assertThat(ph.estHorsSeuil(9.6)).isTrue();
    }
}
