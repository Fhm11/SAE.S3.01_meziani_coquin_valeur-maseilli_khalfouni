package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import source.Tache;
import source.TacheManager;
import source.Controller;

import java.io.File;
import java.util.*;

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
        manager.creerTacheSimple("Tache Sauvegarde", "Description", "Lundi", "Lundi");

        File fichier = new File("taches.sauvegarde");
        assertTrue(fichier.exists());
    }

    @Test
    void testDeplacementTacheViaController() {
        manager.creerTacheSimple("Tache Drag", "Desc", "Lundi", "Lundi");
        Tache t = manager.getTaches().get(0);
        assertEquals("afaire", t.getEtat());

        controller.debuterDeplacement(t);
        controller.finaliserDeplacement("encours");

        assertEquals("encours", t.getEtat());
    }

    @Test
    void testDeplacementTacheDirectManager() {
        manager.creerTacheSimple("Tache Manager", "Desc", "Lundi", "Lundi");
        Tache t = manager.getTaches().get(0);

        manager.deplacerTache(t, "terminer");

        assertEquals("terminer", t.getEtat());
    }

    @Test
    void testDeplacementNull() {
        manager.creerTacheSimple("Tache", "Desc", "Lundi", "Lundi");
        Tache t = manager.getTaches().get(0);

        controller.debuterDeplacement(t);
        controller.finaliserDeplacement(null);

        assertEquals("afaire", t.getEtat());
    }

    @Test
    void testChargementDonnees() {
        manager.getTaches().clear();
        manager.creerTacheSimple("Persistance", "Verif", "Lundi", "Lundi");

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
        controller.creerTache("Test Etat", "Desc", false, "Lundi", "Lundi");
        Tache t = manager.getTaches().get(0);
        assertEquals("afaire", t.getEtat());
    }

    @Test
    void testCreerTacheAvecJours() {
        controller.creerTache("Tache Gantt", "Description", false, "Mardi", "Vendredi");
        Tache t = manager.getTaches().get(0);

        assertEquals("Mardi", t.getJDebut());
        assertEquals("Vendredi", t.getJFin());
    }

    @Test
    void testAjouterSousTacheAvecJours() {
        manager.creerTacheComposite("Parent", "Desc", "Lundi", "Dimanche");
        Tache parent = manager.getTaches().get(0);

        controller.ajouterSousTache(parent, "Sous-tache", "Desc", false, "Mercredi", "Jeudi");

        Tache enfant = parent.getSousTaches().get(0);
        assertEquals("Mercredi", enfant.getJDebut());
        assertEquals("Jeudi", enfant.getJFin());
    }

    @Test
    void testGestionColonnesDynamiques() {
        int nbInitial = manager.getColonnes().size();

        controller.ajouterColonne("En attente");
        assertTrue(manager.getColonnes().contains("En attente"));
        assertEquals(nbInitial + 1, manager.getColonnes().size());

        controller.supprimerColonne("En attente");
        assertFalse(manager.getColonnes().contains("En attente"));
        assertEquals(nbInitial, manager.getColonnes().size());
    }

    @Test
    void testChangerJourTache() {
        manager.creerTacheSimple("Tache Temp", "Desc", "Lundi", "Lundi");
        Tache t = manager.getTaches().get(0);

        controller.changerJourTache(t, "Samedi");
        assertEquals("Samedi", t.getJDebut());
    }

    @Test
    void testCalculDureeGantt() {
        List<String> joursRef = Arrays.asList("Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche");

        String debut = "Lundi";
        String fin = "Mercredi";

        int colDep = joursRef.indexOf(debut) + 1;
        int colFin = joursRef.indexOf(fin) + 1;
        int duree = Math.max(1, colFin - colDep + 1);

        assertEquals(1, colDep);
        assertEquals(3, duree);
    }

    @Test
    void testSuppressionColonneNettoieTaches() {
        controller.ajouterColonne("jsp");
        manager.creerTacheSimple("titre", "Desc", "Lundi", "Mardi");
        Tache t = manager.getTaches().get(0);
        t.setEtat("jsp");

        controller.supprimerColonne("jsp");

        assertFalse(manager.getTaches().contains(t));
    }

    @Test
    void testValidationIntervalleSousTache() {
        manager.creerTacheComposite("Parent", "Desc", "Lundi", "Mardi");
        Tache parent = manager.getTaches().get(0);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            controller.ajouterSousTache(parent, "Sous-tache invalide", "Desc", false, "Mardi", "Mercredi");
        });

        assertTrue(exception.getMessage().contains("La sous-tâche doit être comprise entre Lundi et Mardi"));
    }

    @Test
    void testIntervalleValideSousTache() {
        manager.creerTacheComposite("Parent", "Desc", "Lundi", "Vendredi");
        Tache parent = manager.getTaches().get(0);

        assertDoesNotThrow(() -> {
            controller.ajouterSousTache(parent, "Sous-tache valide", "Desc", false, "Mardi", "Jeudi");
        });

        assertEquals(1, parent.getSousTaches().size());
    }
}