package source;

import java.util.ArrayList;
import java.io.*;
import java.io.File;

public class TacheManager implements Sujet {
    private static TacheManager instance;
    private ArrayList<Observateur> observateurs;
    private ArrayList<Tache> listeTaches;
    private static final String FICHIER_SAUVEGARDE = "taches.sauvegarde";

    private TacheManager() {
        observateurs = new ArrayList<>();
        charger();
    }

    public static synchronized TacheManager getInstance() {
        if (instance == null) {
            instance = new TacheManager();
        }
        return instance;
    }

    // chargement automatique au démarrage
    private void charger() {
        File fichier = new File(FICHIER_SAUVEGARDE);
        if (fichier.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(fichier))) {
                listeTaches = (ArrayList<Tache>) ois.readObject();
                System.out.println("Tâches chargées depuis " + FICHIER_SAUVEGARDE);
            } catch (Exception e) {
                System.out.println("Nouvelle session, pas de sauvegarde trouvée");
                listeTaches = new ArrayList<>();
            }
        } else {
            listeTaches = new ArrayList<>();
        }
    }

    // sauvegarde automatique après chaque modification
    private void sauvegarder() {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(FICHIER_SAUVEGARDE))) {
            oos.writeObject(listeTaches);
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde : " + e.getMessage());
        }
    }

    public void creerTacheSimple(String titre, String description) {
        if (titre == null || titre.trim().isEmpty()) {
            throw new IllegalArgumentException("titre obligatoire");
        }
        Tache t = TacheFactory.creerTacheSimple(titre, description);
        listeTaches.add(t);
        notifierObservateur();
        sauvegarder();
    }

    public void creerTacheComposite(String titre, String description) {
        if (titre == null || titre.trim().isEmpty()) {
            throw new IllegalArgumentException("titre obligatoire");
        }
        Tache t = TacheFactory.creerTacheComposite(titre, description);
        listeTaches.add(t);
        notifierObservateur();
        sauvegarder();
    }

    public void modifierTache(Tache t, String titre, String description) {
        if (t == null || titre == null || titre.trim().isEmpty()) {
            throw new IllegalArgumentException("Paramètres invalides");
        }
        t.setTitre(titre);
        t.setDescription(description);
        notifierObservateur();
        sauvegarder();
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
        sauvegarder();
    }

    public void supprimerTache(Tache t) {
        if (t == null) return;

        if (listeTaches.remove(t)) {
            notifierObservateur();
            sauvegarder();
            return;
        }

        for (Tache tache : listeTaches) {
            if (tache.estComposite()) {
                TacheComposite composite = (TacheComposite) tache;
                if (composite.retirerSousTache(t)) {
                    notifierObservateur();
                    sauvegarder();
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

    public void deplacerTache(Tache t, String nouvelEtat) {
        if (t != null && nouvelEtat != null) {
            t.setEtat(nouvelEtat);
            notifierObservateur();
            sauvegarder(); 
        }
    }
}