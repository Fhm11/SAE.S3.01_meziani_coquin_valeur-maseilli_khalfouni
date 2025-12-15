package source;

public class Controller {
    // Juste 4 méthodes qui appellent directement TacheManager
    public static void creerTache(String titre, String description, boolean estComposite) {
        if (estComposite) {
            TacheManager.getInstance().creerTacheComposite(titre, description);
        } else {
            TacheManager.getInstance().creerTacheSimple(titre, description);
        }
    }

    public static void modifierTache(Tache t, String titre, String description) {
        TacheManager.getInstance().modifierTache(t, titre, description);
    }

    public static void ajouterSousTache(Tache parent, String titre, String description) {
        TacheManager.getInstance().ajouterSousTache(parent, titre, description);
    }

    public static void supprimerTache(Tache t) {
        TacheManager.getInstance().supprimerTache(t);
    }
}