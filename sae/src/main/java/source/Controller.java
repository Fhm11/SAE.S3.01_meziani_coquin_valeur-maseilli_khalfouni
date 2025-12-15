package source;

public class Controller {

    private TacheManager manager;

    public Controller() {
        manager = TacheManager.getInstance();
    }

    public void creerTache(String titre, String description) {
        Tache t = TacheFactory.creerTache(titre, description);
        manager.ajouterTache(t);
    }

    public void modifierTache(Tache t, String titre, String description) {
        t.setTitre(titre);
        t.setDescription(description);
        manager.notifierObservateur();
    }
}
