package source;

/**
 * Classe représentant le patron Fabrique
 * il n'est pas utile pour le moment mais servira lors
 * de la création de sous tâche
 */
public class TacheFactory {

    /**
     * Méthode pour créer une tâche sans sous-tâche
     * @param titre son titre
     * @param description sa description
     * @return la tâche créée
     */
    public static Tache creerTacheSimple(String titre, String description, String debut, String fin, String prio) {
        if (titre == null || titre.trim().isEmpty()) {
            throw new IllegalArgumentException("titre obligatoire");
        }
        Tache t = new TacheSimple(titre, description);
        t.setJDebut(debut);
        t.setJFin(fin);
        t.setPriorite(prio);
        return t;
    }

    /**
     * Méthode pour créer une tâche avec des sous-tâches
     * @param titre son titre
     * @param description sa description
     * @return la tâche créée
     */
    public static Tache creerTacheComposite(String titre, String description, String debut, String fin, String prio) {
        if (titre == null || titre.trim().isEmpty()) {
            throw new IllegalArgumentException("titre obligatoire");
        }
        Tache t = new TacheComposite(titre, description);
        t.setJDebut(debut);
        t.setJFin(fin);
        t.setPriorite(prio);
        return t;
    }
}
