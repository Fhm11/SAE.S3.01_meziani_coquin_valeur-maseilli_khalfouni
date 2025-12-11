package source;

public class Controller {

    private TacheManager manager;
    private TacheFactory factory;

    public Controller() {
        manager = TacheManager.getInstance();
        factory = new TacheFactory();
    }

    public void ajouterTacheSimple(String nom) {
        ITache t = factory.creerTacheSimple(nom);
        manager.ajouterTache(t);
    }

    public void ajouterTacheComposite(String nom) {
        ITache t = factory.creerTacheComposite(nom);
        manager.ajouterTache(t);
    }

    public void supprimerTache(ITache t) {
        manager.supprimerTache(t);
    }

    public void ajouterSousTache(TacheComposite parent, ITache enfant) {
        parent.ajouter(enfant);
        manager.notifierObservateur();
    }
}
    