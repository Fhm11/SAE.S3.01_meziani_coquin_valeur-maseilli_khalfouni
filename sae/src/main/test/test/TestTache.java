package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import source.Tache;
import source.TacheFactory;
import source.TacheManager;

class TestTache {

    private TacheManager manager;

    @BeforeEach
    void setUp() {
        manager = TacheManager.getInstance();
        manager.getTaches().clear();
    }

    @Test
    void testCreationTacheSimpleValide() {
        Tache t = TacheFactory.creerTacheSimple("Titre1", "Description1", "Lundi", "Lundi");

        assertNotNull(t);
        assertEquals("Titre1", t.getTitre());
        assertFalse(t.estComposite());

        manager.getTaches().add(t);
        assertTrue(manager.getTaches().contains(t));
    }

    @Test
    void testCreationTacheComposite() {
        Tache compo = TacheFactory.creerTacheComposite("Projet", "Gros projet", "Lundi", "Lundi");
        assertTrue(compo.estComposite());

        Tache sousTache = TacheFactory.creerTacheSimple("Sous-tâche", "Détail", "Lundi", "Lundi");
        compo.ajouterSousTache(sousTache);

        assertEquals(1, compo.getSousTaches().size());
        assertEquals("Sous-tâche", compo.getSousTaches().get(0).getTitre());
    }

    @Test
    void testTitreNull() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            TacheFactory.creerTacheSimple(null, "idk", "Lundi", "Lundi");
        });
        assertEquals("titre obligatoire", exception.getMessage());
    }

    @Test
    void testTitreVide() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            TacheFactory.creerTacheSimple("", "idk", "Lundi", "Lundi");
        });
        assertEquals("titre obligatoire", exception.getMessage());
    }

    @Test
    void testAjoutViaManager() {
        manager.creerTacheSimple("T1", "D1", "Lundi", "Lundi");
        manager.creerTacheComposite("C1", "D2", "Lundi", "Lundi");

        assertEquals(2, manager.getTaches().size());
        assertTrue(manager.getTaches().get(1).estComposite());
    }

    @Test
    void testSuppressionTache() {
        manager.creerTacheSimple("A supprimer", "desc", "Lundi", "Lundi");
        Tache t = manager.getTaches().get(0);

        manager.supprimerTache(t);

        assertTrue(manager.getTaches().isEmpty());
    }

    @Test
    void testModifierTache() {
        manager.creerTacheSimple("Ancien Titre", "Ancienne Description", "Lundi", "Lundi");
        Tache t = manager.getTaches().get(0);

        manager.modifierTache(t, "Nouveau Titre", "Nouvelle Description");

        assertEquals("Nouveau Titre", t.getTitre());
        assertEquals("Nouvelle Description", t.getDescription());
    }
}