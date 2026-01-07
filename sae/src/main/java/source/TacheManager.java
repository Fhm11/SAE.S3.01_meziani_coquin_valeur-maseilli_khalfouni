package source;

import java.util.ArrayList;
import java.io.*;
import java.io.File;
import java.util.*;

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
            VueFormulaire.afficherAlerteErreur("Erreur lors de la sauvegarde : " + e.getMessage(), "Erreur de sauvegarde");
        }
    }

    /**
     * Méthode pour créer une tâche simple
     * @param titre le titre
     * @param description la description
     */
    public void creerTacheSimple(String titre, String description, String debut, String fin, String prio) {
        if (titre == null || titre.trim().isEmpty()) {
            VueFormulaire.afficherAlerteErreur("Le titre est obligatoire", "Erreur de création");
            throw new IllegalArgumentException("titre obligatoire");
        }
        Tache t = TacheFactory.creerTacheSimple(titre, description, debut, fin, prio);
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
    public void creerTacheComposite(String titre, String description, String debut, String fin, String prio) {
        if (titre == null || titre.trim().isEmpty()) {
            VueFormulaire.afficherAlerteErreur("Le titre est obligatoire", "Erreur de création");
            throw new IllegalArgumentException("titre obligatoire");
        }
        Tache t = TacheFactory.creerTacheComposite(titre, description, debut, fin, prio);
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
    public void modifierTache(Tache t, String titre, String description, String prio) {
        if (t == null || titre == null || titre.trim().isEmpty()) {
            VueFormulaire.afficherAlerteErreur("Paramètres invalides", "Erreur de modification");
            throw new IllegalArgumentException("Paramètres invalides");
        }
        t.setTitre(titre);
        t.setDescription(description);
        t.setPriorite(prio); // AJOUT: Modifier la priorité
        notifierObservateur();
        sauvegarder();
    }

    /**
     * Méthode pour ajouter une sous-tâche
     * @param parent la tâche parente
     * @param titre son titre
     * @param description sa description
     */
    public void ajouterSousTache(Tache parent, String titre, String description, boolean estComposite, String debut, String fin, String prio) {
        if (parent == null || !parent.estComposite() ||
                titre == null || titre.trim().isEmpty()) {
            VueFormulaire.afficherAlerteErreur("Impossible d'ajouter une sous-tâche", "Erreur d'ajout");
            throw new IllegalArgumentException("Impossible d'ajouter une sous-tâche");
        }

        if (!estIntervalleValide(parent, debut, fin)) {
            String message = "La sous-tâche doit être comprise entre " + parent.getJDebut() + " et " + parent.getJFin();
            VueFormulaire.afficherAlerteErreur(message, "Erreur d'intervalle");
            throw new IllegalArgumentException(message);
        }

        Tache sousTache;
        if (estComposite) {
            sousTache = TacheFactory.creerTacheComposite(titre, description, debut, fin, prio);
        } else {
            sousTache = TacheFactory.creerTacheSimple(titre, description, debut, fin, prio);
        }
        TacheComposite composite = (TacheComposite) parent;
        composite.ajouterSousTache(sousTache);
        notifierObservateur();
        sauvegarder();
    }

    /**
     * Méthode pour supprimer une tâche
     * @param t la tâche à supprimer
     */
    public void supprimerTache(Tache t) {
        if (t == null) return;

        // essayer de supprimer des tâches principales
        if (listeTaches.remove(t)) {
            notifierObservateur();
            sauvegarder();
            return;
        }

        // chercher récursivement dans toutes les tâches composites
        for (Tache tache : listeTaches) {
            if (supprimerSousTacheRecursif(tache, t)) {
                notifierObservateur();
                sauvegarder();
                return;
            }
        }
        // si on arrive ici, la tâche n'a pas été trouvée
        VueFormulaire.afficherAlerteErreur("Tâche non trouvée", "Erreur de suppression");
    }

    private boolean supprimerSousTacheRecursif(Tache parent, Tache aSupprimer) {
        if (parent.estComposite()) {
            TacheComposite composite = (TacheComposite) parent;

            // hercher directement dans les sous-tâches
            if (composite.retirerSousTache(aSupprimer)) {
                return true;
            }

            // chercher récursivement dans les sous-sous-tâches
            for (Tache sousTache : composite.getSousTaches()) {
                if (supprimerSousTacheRecursif(sousTache, aSupprimer)) {
                    return true;
                }
            }
        }
        return false;
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
        // vérifier si la colonne existe déjà
        if (colonnes.contains(titre)) {
            VueFormulaire.afficherAlerteErreur("Une colonne avec ce nom existe déjà", "Erreur de création");
            return;
        }

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
            System.err.println("Erreur lors de la sauvegarde des colonnes : " + e.getMessage());
            VueFormulaire.afficherAlerteErreur("Erreur lors de la sauvegarde des colonnes : " + e.getMessage(), "Erreur de sauvegarde");
        }
    }

    private boolean estIntervalleValide(Tache parent, String debutEnfant, String finEnfant) {
        int dureeParent = calculerDistance(parent.getJFin(), parent.getJDebut());

        int posDebutEnfant = calculerDistance(debutEnfant, parent.getJDebut());

        int posFinEnfant = calculerDistance(finEnfant, parent.getJDebut());

        return posDebutEnfant <= dureeParent && posFinEnfant <= dureeParent && posDebutEnfant <= posFinEnfant;
    }

    private int calculerDistance(String jourCible, String jourDepart) {
        List<String> joursRef = Arrays.asList("Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche");
        int idxDep = joursRef.indexOf(jourDepart);
        int idxCible = joursRef.indexOf(jourCible);

        if (idxCible < idxDep) {
            return idxCible + 7 - idxDep;
        }
        return idxCible - idxDep;
    }

    /**
     * AJOUT: Archive une tâche (ne la supprime pas, change juste son état)
     * @param t la tâche à archiver
     */
    public void archiverTache(Tache t) {
        if (t != null) {
            t.setAncienEtat(t.getEtat());
            t.setEtat("archive");
            notifierObservateur();
            sauvegarder();
        }
    }

    /**
     * AJOUT: Restaure une tâche archivée vers son état d'origine
     * @param t la tâche à restaurer
     */
    public void restaurerTache(Tache t) {
        if (t != null) {
            if (t.getAncienEtat() != null) {
                t.setEtat(t.getAncienEtat());
            } else {
                t.setEtat("À faire");
            }
            notifierObservateur();
            sauvegarder();
        }
    }

    public void devenirSousTacheDe(Tache enfant, Tache nouveauParent) {
        // vérifications de base
        if (enfant == null || nouveauParent == null) {
            VueFormulaire.afficherAlerteErreur("Tâche invalide", "Erreur de parentage");
            return;
        }

        if (enfant == nouveauParent) {
            VueFormulaire.afficherAlerteErreur("Une tâche ne peut pas être sous-tâche d'elle-même", "Erreur de parentage");
            return;
        }

        if (!nouveauParent.estComposite()) {
            VueFormulaire.afficherAlerteErreur("La tâche parente ne peut pas recevoir de sous-tâches", "Erreur de parentage");
            return;
        }

        // vérifier les cycles (l'enfant ne doit pas contenir le parent)
        if (enfant.contientTache(nouveauParent)) {
            VueFormulaire.afficherAlerteErreur("Erreur : Cycle détecté - l'enfant contient déjà le parent", "Erreur de parentage");
            return;
        }

        // retirer l'enfant de son ancienne position
        boolean retire = false;

        // chercher dans les tâches principales
        if (listeTaches.remove(enfant)) {
            retire = true;
        } else {
            // chercher récursivement dans les sous-tâches
            for (Tache tache : listeTaches) {
                if (retirerSousTacheRecursif(tache, enfant)) {
                    retire = true;
                    break;
                }
            }
        }

        // ajouter au nouveau parent
        if (retire) {
            TacheComposite parentComposite = (TacheComposite) nouveauParent;
            parentComposite.ajouterSousTache(enfant);

            // ajuster les jours si nécessaire (la sous-tâche doit être dans l'intervalle du parent)
            ajusterJoursPourSousTache(enfant, nouveauParent);

            notifierObservateur();
            sauvegarder();
        }
    }

    /**
     * Ajuste les jours d'une sous-tâche pour qu'elle soit dans l'intervalle du parent
     */
    private void ajusterJoursPourSousTache(Tache enfant, Tache parent) {
        // si les jours de l'enfant sont en dehors de l'intervalle du parent, les ajuster
        if (!estIntervalleValide(parent, enfant.getJDebut(), enfant.getJFin())) {
            // par défaut, mettre les mêmes jours que le parent
            enfant.setJDebut(parent.getJDebut());
            enfant.setJFin(parent.getJFin());
        }
    }

    /**
     * Cherche et retire une sous-tâche récursivement
     */
    private boolean retirerSousTacheRecursif(Tache parent, Tache aRetirer) {
        if (parent.estComposite()) {
            TacheComposite composite = (TacheComposite) parent;

            // chercher directement
            if (composite.retirerSousTache(aRetirer)) {
                return true;
            }

            // chercher récursivement
            for (Tache sousTache : composite.getSousTaches()) {
                if (retirerSousTacheRecursif(sousTache, aRetirer)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Extraire une sous-tâche pour en faire une tâche principale
     */
    public void extraireSousTache(Tache sousTache) {
        // trouver et retirer de son parent
        Tache parent = trouverParent(sousTache);
        if (parent != null && parent.estComposite()) {
            TacheComposite parentComposite = (TacheComposite) parent;
            parentComposite.retirerSousTache(sousTache);

            // ajouter aux tâches principales
            listeTaches.add(sousTache);
            notifierObservateur();
            sauvegarder();
        } else {
            VueFormulaire.afficherAlerteErreur("Impossible d'extraire la sous-tâche", "Erreur d'extraction");
        }
    }

    /**
     * Extraire une sous-tâche vers une colonne spécifique
     * CORRECTION : Vérifier et retirer correctement de l'ancien parent
     */
    public void extraireVersColonne(Tache sousTache, String etat) {
        if (sousTache == null || etat == null) return;

        // retirer de n'importe où dans la hiérarchie
        retirerDeTouteHierarchie(sousTache);

        // ajouter aux principales si nécessaire
        if (!listeTaches.contains(sousTache)) {
            listeTaches.add(sousTache);
        }

        // changer état
        sousTache.setEtat(etat);

        // sauvegarder et notifier
        notifierObservateur();
        sauvegarder();
    }

    private void retirerDeTouteHierarchie(Tache aRetirer) {
        // d'abord essayer dans les tâches principales
        if (listeTaches.remove(aRetirer)) {
            return;
        }

        // sinon chercher récursivement
        for (Tache tache : listeTaches) {
            if (retirerRecursifDeHierarchie(tache, aRetirer)) {
                return;
            }
        }
    }

    private boolean retirerRecursifDeHierarchie(Tache parent, Tache aRetirer) {
        if (parent.estComposite()) {
            TacheComposite composite = (TacheComposite) parent;

            // essayer de retirer directement
            if (composite.retirerSousTache(aRetirer)) {
                return true;
            }

            // chercher récursivement dans les enfants
            for (Tache sousTache : composite.getSousTaches()) {
                if (retirerRecursifDeHierarchie(sousTache, aRetirer)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Extraire une sous-tâche vers un jour spécifique
     * CORRECTION : Vérifier et retirer correctement de l'ancien parent
     */
    public void extraireVersJour(Tache sousTache, String jour) {
        if (sousTache == null || jour == null) {
            VueFormulaire.afficherAlerteErreur("Paramètres invalides", "Erreur d'extraction");
            return;
        }

        // retirer de n'importe où dans la hiérarchie
        retirerDeTouteHierarchie(sousTache);

        // si la sous-tâche n'est pas déjà dans listeTaches, l'ajouter
        if (!listeTaches.contains(sousTache)) {
            listeTaches.add(sousTache);
        }

        // changer le jour
        sousTache.setJDebut(jour);

        // ajuster également la date de fin pour conserver la même durée
        int duree = calculerDistance(sousTache.getJFin(), sousTache.getJDebut());
        String nouvelleFin = calculerJourSuivant(jour, duree);
        sousTache.setJFin(nouvelleFin);

        // notifier et sauvegarder
        notifierObservateur();
        sauvegarder();
    }

    /**
     * Déplace une tâche vers un nouveau jour en ajustant automatiquement la date de fin
     */
    public void deplacerTacheVersJour(Tache t, String nouveauJour) {
        if (t == null || nouveauJour == null) {
            VueFormulaire.afficherAlerteErreur("Paramètres invalides", "Erreur de déplacement");
            return;
        }

        // calculer la durée actuelle
        int duree = calculerDistance(t.getJFin(), t.getJDebut());

        // changer le jour de début
        t.setJDebut(nouveauJour);

        // calculer le nouveau jour de fin en conservant la même durée
        String nouveauJFin = calculerJourSuivant(nouveauJour, duree);
        t.setJFin(nouveauJFin);

        notifierObservateur();
        sauvegarder();
    }

    /**
     * Calcule le jour suivant après un certain nombre de jours
     */
    private String calculerJourSuivant(String jourDepart, int nbJours) {
        List<String> joursRef = Arrays.asList("Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche");
        int idx = joursRef.indexOf(jourDepart);
        int nouvelIdx = (idx + nbJours) % 7;
        return joursRef.get(nouvelIdx);
    }

    /**
     * Trouve le parent d'une sous-tâche
     */
    private Tache trouverParent(Tache enfant) {
        // chercher dans les tâches principales
        for (Tache tache : listeTaches) {
            if (trouverParentRecursif(tache, enfant)) {
                return tache;
            }
        }
        return null;
    }

    private boolean trouverParentRecursif(Tache parent, Tache enfant) {
        if (parent.estComposite()) {
            TacheComposite composite = (TacheComposite) parent;
            // vérifier directement
            if (composite.getSousTaches().contains(enfant)) {
                return true;
            }
            // chercher récursivement
            for (Tache sousTache : composite.getSousTaches()) {
                if (trouverParentRecursif(sousTache, enfant)) {
                    return true;
                }
            }
        }
        return false;
    }
}