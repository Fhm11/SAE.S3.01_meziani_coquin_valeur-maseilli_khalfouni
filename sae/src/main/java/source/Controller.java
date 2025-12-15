package source;

public class Controller {

    private TacheManager manager;

    public Controller() {
        manager = TacheManager.getInstance();
    }

    public void creerTacheSimple(String titre, String description) {
        Tache t = TacheFactory.creerTacheSimple(titre, description);
        manager.ajouterTache(t);
    }

    public void creerTacheComposite(String titre, String description) {
        Tache t = TacheFactory.creerTacheComposite(titre, description);
        manager.ajouterTache(t);
    }

    public void modifierTache(Tache t, String titre, String description) {
        if (titre == null || titre.trim().isEmpty()) {
            throw new IllegalArgumentException("titre obligatoire");
        }

        t.setTitre(titre);
        t.setDescription(description);
        manager.notifierObservateur();
    }

    public void ajouterSousTache(Tache parent, String titre, String description) {
        if (parent == null || !parent.estComposite()) {
            throw new IllegalArgumentException("Impossible d'ajouter une sous-tâche");
        }

        Tache sousTache = TacheFactory.creerTacheSimple(titre, description);
        TacheManager.getInstance().ajouterSousTache(parent, sousTache);
    }

    public void supprimerTache(Tache t) {
        if (t == null) return;

        if (manager.getTaches().contains(t)) {
            manager.supprimerTache(t);
            return;
        }

        TacheComposite parent = trouverParent(t);
        if (parent != null) {
            parent.retirerSousTache(t);
            manager.notifierObservateur();
        }
    }

    private TacheComposite trouverParent(Tache enfant) {
        for (Tache tachePrincipale : manager.getTaches()) {
            if (tachePrincipale.estComposite()) {
                TacheComposite parent = (TacheComposite) tachePrincipale;
                if (parent.getSousTaches().contains(enfant)) {
                    return parent;
                }
            }
        }
        return null;
    }
}

