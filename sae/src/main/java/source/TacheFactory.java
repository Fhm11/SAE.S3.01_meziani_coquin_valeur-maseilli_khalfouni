package source;

/**
 * Classe représentant le patron Fabrique
 * il n'est pas utile pour le moment mais servira lors
 * de la création de sous tâche
 */
public class TacheFactory {

    public static Tache creerTacheSimple(String titre, String description) {
        if (titre == null || titre.trim().isEmpty()) {
            throw new IllegalArgumentException("titre obligatoire");
        }
        return new TacheSimple(titre, description);
    }

    public static Tache creerTacheComposite(String titre, String description) {
        if (titre == null || titre.trim().isEmpty()) {
            throw new IllegalArgumentException("titre obligatoire");
        }
        return new TacheComposite(titre, description);
    }
}
