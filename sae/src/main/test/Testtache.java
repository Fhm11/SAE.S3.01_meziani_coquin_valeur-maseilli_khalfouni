import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import source.Tache;
import source.TacheFactory;
import source.TacheManager;

import static org.junit.jupiter.api.Assertions.*;

class Testtache {

    private TacheManager manager;

    @BeforeEach
    void setUp() {
        manager = TacheManager.getInstance();
        manager.getTaches().clear();
    }

    @Test
    void testCreationTacheValide() {
        Tache t = TacheFactory.creerTache("Titre1", "Description1");
        assertNotNull(t, "tache doit pas etre null ou vide");
        assertEquals("Titre1", t.getTitre());
        assertEquals("Description1", t.getDescription());

        manager.ajouterTache(t);
        assertTrue(manager.getTaches().contains(t), "il doit y avaoit la tache ajouter");
    }

    @Test
    void testTitreNull() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            TacheFactory.creerTache(null, "idk");
        });
        assertEquals("titre obliger", exception.getMessage());
    }

    @Test
    void testTitreVide() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            TacheFactory.creerTache("", "idk");
        });
        assertEquals("titre obliger", exception.getMessage());
    }

    @Test
    void testAjoutMultipleTaches() {
        Tache t1 = TacheFactory.creerTache("t1", "d1");
        Tache t2 = TacheFactory.creerTache("t2", "d2");
        manager.ajouterTache(t1);
        manager.ajouterTache(t2);

        assertEquals(2, manager.getTaches().size(), "doit avoir 2 taches snon faux");
        assertTrue(manager.getTaches().contains(t1));
        assertTrue(manager.getTaches().contains(t2));
    }
}
