package source;

import java.util.ArrayList;

public class TacheManager implements Sujet {
    private static TacheManager instance;
    private ArrayList<Observateur> observateurs;
    private ArrayList<Tache> listeTaches;

    private TacheManager() {
        observateurs = new ArrayList<>();
        listeTaches = new ArrayList<>();
    }

    public static synchronized TacheManager getInstance() {
        if (instance == null) {
            instance = new TacheManager();
        }
        return instance;
    }

    public void creerTacheSimple(String titre, String description) {
        if (titre == null || titre.trim().isEmpty()) {
            throw new IllegalArgumentException("titre obligatoire");
        }
        Tache t = TacheFactory.creerTacheSimple(titre, description);
        listeTaches.add(t);
        notifierObservateur();
    }

    public void creerTacheComposite(String titre, String description) {
        if (titre == null || titre.trim().isEmpty()) {
            throw new IllegalArgumentException("titre obligatoire");
        }
        Tache t = TacheFactory.creerTacheComposite(titre, description);
        listeTaches.add(t);
        notifierObservateur();
    }

    public void modifierTache(Tache t, String titre, String description) {
        if (t == null || titre == null || titre.trim().isEmpty()) {
            throw new IllegalArgumentException("Paramètres invalides");
        }
        t.setTitre(titre);
        t.setDescription(description);
        notifierObservateur();
    }

    public void ajouterSousTache(Tache parent, String titre, String description) {
        if (parent == null || !parent.estComposite() ||
                titre == null || titre.trim().isEmpty()) {
            throw new IllegalArgumentException("Impossible d'ajouter une sous-tâche");
        }

        Tache sousTache = TacheFactory.creerTacheSimple(titre, description);
        TacheComposite composite = (TacheComposite) parent;
        composite.ajouterSousTache(sousTache);
        notifierObservateur();
    }

    public void supprimerTache(Tache t) {
        if (t == null) return;

        if (listeTaches.remove(t)) {
            notifierObservateur();
            return;
        }

        for (Tache tache : listeTaches) {
            if (tache.estComposite()) {
                TacheComposite composite = (TacheComposite) tache;
                if (composite.retirerSousTache(t)) {
                    notifierObservateur();
                    return;
                }
            }
        }
    }


    @Override
    public void ajouterObservateur(Observateur o) {
        if (o != null && !observateurs.contains(o)) {
            observateurs.add(o);
        }
    }

    @Override
    public void supprimerObservateur(Observateur o) {
        observateurs.remove(o);
    }

    @Override
    public void notifierObservateur() {
        for (Observateur o : observateurs) {
            o.actualiser();
        }
    }

    public ArrayList<Tache> getTaches() {
        return listeTaches;
    }

}