package source;

import java.util.ArrayList;
import java.io.*;
import java.io.File;

public class TacheManager implements Sujet {
    private static TacheManager instance;
    private ArrayList<Observateur> observateurs;
    private ArrayList<Tache> listeTaches;
    private static final String FICHIER_SAUVEGARDE = "taches.sauvegarde";
    private static final String FICHIER_COLONNES = "colonnes.sauvegarde";
    private ArrayList<String> colonnes;

    /**
     * Le constructeur pour créer le modele
     */
    private TacheManager() {
        observateurs = new ArrayList<>();
        charger();
        chargerColonnes();
    }

    /**
     * Méthode pour retourner l'instance de Singleton
     * @return l'instance
     */
    public static synchronized TacheManager getInstance() {
        if (instance == null) {
            instance = new TacheManager();
        }
        return instance;
    }

    public ArrayList<String> getColonnes() {
        return colonnes;
    }

    /**
     * Méthode pour charger les données au démarrage
     * de l'application
     */
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

    /**
     * Méthode pour sauvegarder les données à chaque modification
     * de l'application
     */
    private void sauvegarder() {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(FICHIER_SAUVEGARDE))) {
            oos.writeObject(listeTaches);
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde : " + e.getMessage());
        }
    }

    /**
     * Méthode pour créer une tâche simple
     * @param titre le titre
     * @param description la description
     */
    public void creerTacheSimple(String titre, String description, String debut, String fin) {
        if (titre == null || titre.trim().isEmpty()) {
            throw new IllegalArgumentException("titre obligatoire");
        }
        Tache t = TacheFactory.creerTacheSimple(titre, description, debut, fin);
        if (!colonnes.isEmpty()) {
            t.setEtat(colonnes.get(0));
        }
        listeTaches.add(t);
        notifierObservateur();
        sauvegarder();
    }

    /**
     * Méthode pour créer une tâche composite
     * @param titre son titre
     * @param description sa description
     */
    public void creerTacheComposite(String titre, String description, String debut, String fin) {
        if (titre == null || titre.trim().isEmpty()) {
            throw new IllegalArgumentException("titre obligatoire");
        }
        Tache t = TacheFactory.creerTacheComposite(titre, description, debut, fin);
        if (!colonnes.isEmpty()) {
            t.setEtat(colonnes.get(0));
        }
        listeTaches.add(t);
        notifierObservateur();
        sauvegarder();
    }

    /**
     * Méthode pour modifier une tâche
     * @param t la tâche à modifier
     * @param titre son nouveau titre
     * @param description sa nouvelle description
     */
    public void modifierTache(Tache t, String titre, String description) {
        if (t == null || titre == null || titre.trim().isEmpty()) {
            throw new IllegalArgumentException("Paramètres invalides");
        }
        t.setTitre(titre);
        t.setDescription(description);
        notifierObservateur();
        sauvegarder();
    }

    /**
     * Méthode pour ajouter une sous-tâche
     * @param parent la tâche parente
     * @param titre son titre
     * @param description sa description
     */
    public void ajouterSousTache(Tache parent, String titre, String description, boolean estComposite, String debut, String fin) {
        if (parent == null || !parent.estComposite() ||
                titre == null || titre.trim().isEmpty()) {
            throw new IllegalArgumentException("Impossible d'ajouter une sous-tâche");
        }
        Tache sousTache;
        if (estComposite) {
            sousTache = TacheFactory.creerTacheComposite(titre, description, debut, fin);
        } else {
            sousTache = TacheFactory.creerTacheSimple(titre, description, debut, fin);
        }
        TacheComposite composite = (TacheComposite) parent;
        composite.ajouterSousTache(sousTache);
        notifierObservateur();
        sauvegarder();
    }

    /**
     * Méthode poursupprimer une tâche
     * @param t la tâche à supprimer
     */
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


    /**
     * Méthode pour ajouter les observateurs
     * @param o l'observateur à ajouter
     */
    @Override
    public void ajouterObservateur(Observateur o) {
        if (o != null && !observateurs.contains(o)) {
            observateurs.add(o);
        }
    }

    /**
     * Méthodes pour supprimer les observateurs
     * @param o l'observateur à supp
     */
    @Override
    public void supprimerObservateur(Observateur o) {
        observateurs.remove(o);
    }

    /**
     * Méthodes pour notifier les observateurs
     */
    @Override
    public void notifierObservateur() {
        for (Observateur o : observateurs) {
            o.actualiser();
        }
    }

    /**
     * Getter pour voir les tâches
     * @return les tâches
     */
    public ArrayList<Tache> getTaches() {
        return listeTaches;
    }

    /**
     * Méthode pour le drag and drop
     * @param t la tâche à déplacer
     * @param nouvelEtat la colonne ou la tâche est placée
     */
    public void deplacerTache(Tache t, String nouvelEtat) {
        if (t != null && nouvelEtat != null) {
            t.setEtat(nouvelEtat);
            notifierObservateur();
            sauvegarder(); 
        }
    }

    public void ajouterColonne(String titre) {
            colonnes.add(titre);
            notifierObservateur();
            sauvegarderColonnes();
    }

    public void supprimerColonne(String titre) {
        if (colonnes.remove(titre)) {
            ArrayList<Tache> aSupprimer = new ArrayList<>();
            for (Tache t : listeTaches) {
                if (t.getEtat().equals(titre)) {
                    aSupprimer.add(t);
                }
            }
            listeTaches.removeAll(aSupprimer);

            notifierObservateur();
            sauvegarderColonnes();
            sauvegarder();
        }
    }

    private void chargerColonnes() {
        File fichier = new File(FICHIER_COLONNES);
        if (fichier.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fichier))) {
                colonnes = (ArrayList<String>) ois.readObject();
            } catch (Exception e) {
                initColonnesDefaut();
            }
        } else {
            initColonnesDefaut();
        }
    }

    private void initColonnesDefaut() {
        colonnes = new ArrayList<>();
        colonnes.add("À faire");
        colonnes.add("En cours");
        colonnes.add("Terminée");
    }

    private void sauvegarderColonnes() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FICHIER_COLONNES))) {
            oos.writeObject(colonnes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}