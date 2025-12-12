package source;

/**
 * Classe représentant le patron Fabrique
 * il n'est pas utile pour le moment mais servira lors
 * de la création de sous tâche
 */
public class TacheFactory {

    /**
     * Méthode qui permet de créer une tâche
     * @param titre le titre de la tâche
     * @param description sa description
     * @return la tâche créé
     */
    public static Tache creerTache(String titre, String description) {
        if (titre == null || titre.trim().isEmpty()) {
            throw new IllegalArgumentException("titre obliger");
        }

        return new Tache(titre, description);
    }
}
