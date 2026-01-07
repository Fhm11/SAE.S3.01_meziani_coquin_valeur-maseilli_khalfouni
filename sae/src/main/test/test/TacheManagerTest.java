package source;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;

public class TacheManagerTest {

    private TacheManager manager;

    @BeforeEach
    void setUp() {
        new File("taches.sauvegarde").delete();
        new File("colonnes.sauvegarde").delete();
        manager = TacheManager.getInstance();
        manager.getTaches().clear();
    }

    @Test
    void testPrioriteInitialisation() {
        manager.creerTacheSimple("T1", "D1", "Lundi", "Mardi", "Importante");
        Tache t = manager.getTaches().get(0);
        assertEquals("Importante", t.getPriorite());
    }

    @Test
    void testChangementPriorite() {
        manager.creerTacheSimple("T1", "D1", "Lundi", "Lundi", "Basse");
        Tache t = manager.getTaches().get(0);
        manager.modifierTache(t, "T1", "D1", "Moyenne");
        assertEquals("Moyenne", t.getPriorite());
    }

    @Test
    void testProcessusArchivage() {
        manager.creerTacheSimple("T1", "D1", "Lundi", "Lundi", "Moyenne");
        Tache t = manager.getTaches().get(0);
        String etatOrigine = t.getEtat();

        manager.archiverTache(t);

        assertEquals("archive", t.getEtat());
        assertEquals(etatOrigine, t.getAncienEtat());
    }

    @Test
    void testProcessusRestauration() {
        manager.creerTacheSimple("T1", "D1", "Lundi", "Lundi", "Moyenne");
        Tache t = manager.getTaches().get(0);
        String etatInitial = t.getEtat();

        manager.archiverTache(t);
        manager.restaurerTache(t);

        assertEquals(etatInitial, t.getEtat());
        assertNotEquals("archive", t.getEtat());
    }

    @Test
    void testSuppressionDefinitive() {
        manager.creerTacheSimple("T1", "D1", "Lundi", "Lundi", "Moyenne");
        Tache t = manager.getTaches().get(0);

        manager.supprimerTache(t);

        assertEquals(0, manager.getTaches().size());
    }

    @Test
    void testHierarchieArchivage() {
        manager.creerTacheComposite("P1", "D1", "Lundi", "Dimanche", "Moyenne");
        Tache p = manager.getTaches().get(0);
        manager.ajouterSousTache(p, "S1", "D2", false, "Mardi", "Mercredi", "Basse");
        Tache s = p.getSousTaches().get(0);

        manager.archiverTache(s);

        assertEquals("archive", s.getEtat());
        assertNotEquals("archive", p.getEtat());
    }
}