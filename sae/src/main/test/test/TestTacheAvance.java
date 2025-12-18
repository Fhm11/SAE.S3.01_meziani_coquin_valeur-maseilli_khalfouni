package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import source.Tache;
import source.TacheManager;
import source.Controller;

import java.io.File;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class TestTacheAvance {

    private TacheManager manager;
    private Controller controller;

    @BeforeEach
    void setUp() {
        manager = TacheManager.getInstance();
        manager.getTaches().clear();
        controller = new Controller(manager);
    }

    @Test
    void testSerializationSauvegarde() {
        manager.creerTacheSimple("Tache Sauvegarde", "Description");

        File fichier = new File("taches.sauvegarde");
        assertTrue(fichier.exists());
    }

    @Test
    void testDeplacementTacheViaController() {
        manager.creerTacheSimple("Tache Drag", "Desc");
        Tache t = manager.getTaches().get(0);
        assertEquals("afaire", t.getEtat());

        controller.debuterDeplacement(t);
        controller.finaliserDeplacement("encours");

        assertEquals("encours", t.getEtat());
    }

    @Test
    void testDeplacementTacheDirectManager() {
        manager.creerTacheSimple("Tache Manager", "Desc");
        Tache t = manager.getTaches().get(0);

        manager.deplacerTache(t, "terminer");

        assertEquals("terminer", t.getEtat());
    }

    @Test
    void testDeplacementNull() {
        manager.creerTacheSimple("Tache", "Desc");
        Tache t = manager.getTaches().get(0);

        controller.debuterDeplacement(t);
        controller.finaliserDeplacement(null);

        assertEquals("afaire", t.getEtat());
    }

    @Test
    void testChargementDonnees() {
        manager.getTaches().clear();
        manager.creerTacheSimple("Persistance", "Verif");

        ArrayList<Tache> listeAvant = new ArrayList<>(manager.getTaches());

        try {
            java.lang.reflect.Method method = TacheManager.class.getDeclaredMethod("charger");
            method.setAccessible(true);
            method.invoke(manager);
        } catch (Exception e) {
            fail("Erreur lors de l'appel manuel de charger()");
        }

        assertFalse(manager.getTaches().isEmpty());
        assertEquals("Persistance", manager.getTaches().get(0).getTitre());
    }

    @Test
    void testEtatInitialNouvelleTache() {
        controller.creerTache("Test Etat", "Desc", false);
        Tache t = manager.getTaches().get(0);
        assertEquals("afaire", t.getEtat());
    }
}