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
        t.setTitre(titre);
        t.setDescription(description);
        manager.notifierObservateur();
    }
}
