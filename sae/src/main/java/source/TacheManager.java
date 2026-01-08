package source;

import java.util.ArrayList;
import java.io.*;
import java.util.*;

/**
 * Gestionnaire principal des tâches (Modèle dans MVC, Singleton)
 * Gère la liste des tâches, les colonnes, la sauvegarde et les observateurs
 */
public class TacheManager implements Sujet {

    // Instance unique (Singleton)
    private static TacheManager instance;

    // Listes de données
    private ArrayList<Observateur> observateurs;
    private ArrayList<Tache> listeTaches;
    private ArrayList<String> colonnes;

    // Fichiers de sauvegarde
    private static final String FICHIER_SAUVEGARDE = "taches.sauvegarde";
    private static final String FICHIER_COLONNES = "colonnes.sauvegarde";

    // Liste des jours de la semaine pour les calculs
    private static final List<String> JOURS_REFERENCE = Arrays.asList(
            "Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche"
    );

    /**
     * Constructeur privé (Singleton)
     */
    private TacheManager() {
        observateurs = new ArrayList<>();
        chargerTaches();
        chargerColonnes();
    }

    /**
     * Récupère l'instance unique du gestionnaire (Singleton)
     * @return l'instance de TacheManager
     */
    public static synchronized TacheManager getInstance() {
        if (instance == null) {
            instance = new TacheManager();
        }
        return instance;
    }

    /**
     * Récupère la liste des colonnes disponibles
     * @return la liste des noms de colonnes
     */
    public ArrayList<String> getColonnes() {
        return colonnes;
    }


    /**
     * Charge les tâches depuis le fichier de sauvegarde
     */
    private void chargerTaches() {
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
     * Sauvegarde les tâches dans le fichier
     */
    private void sauvegarderTaches() {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(FICHIER_SAUVEGARDE))) {
            oos.writeObject(listeTaches);
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde : " + e.getMessage());
            VueFormulaire.afficherAlerteErreur("Erreur lors de la sauvegarde : " + e.getMessage(),
                    "Erreur de sauvegarde");
        }
    }

    /**
     * Initialise les colonnes par défaut
     */
    private void initialiserColonnesDefaut() {
        colonnes = new ArrayList<>();
        colonnes.add("afaire");
        colonnes.add("encours");
        colonnes.add("terminer");
    }

    /**
     * Charge les colonnes depuis le fichier
     */
    private void chargerColonnes() {
        File fichier = new File(FICHIER_COLONNES);
        if (fichier.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fichier))) {
                colonnes = (ArrayList<String>) ois.readObject();
            } catch (Exception e) {
                initialiserColonnesDefaut();
            }
        } else {
            initialiserColonnesDefaut();
        }
    }

    /**
     * Sauvegarde les colonnes dans le fichier
     */
    private void sauvegarderColonnes() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FICHIER_COLONNES))) {
            oos.writeObject(colonnes);
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde des colonnes : " + e.getMessage());
            VueFormulaire.afficherAlerteErreur("Erreur lors de la sauvegarde des colonnes : " + e.getMessage(),
                    "Erreur de sauvegarde");
        }
    }


    /**
     * Crée une tâche simple
     * @param titre le titre de la tâche
     * @param description sa description
     * @param debut le jour de début
     * @param fin le jour de fin
     * @param priorite la priorité
     */
    public void creerTacheSimple(String titre, String description,
                                 String debut, String fin, String priorite) {
        if (titre == null || titre.trim().isEmpty()) {
            VueFormulaire.afficherAlerteErreur("Le titre est obligatoire", "Erreur de création");
            throw new IllegalArgumentException("Le titre est obligatoire");
        }
        Tache tache = TacheFactory.creerTacheSimple(titre, description, debut, fin, priorite);
        // Place la tâche dans la première colonne par défaut
        if (!colonnes.isEmpty()) {
            tache.setEtat(colonnes.get(0));
        }
        listeTaches.add(tache);
        notifierObservateur();
        sauvegarderTaches();
    }

    /**
     * Crée une tâche composite
     * @param titre le titre de la tâche
     * @param description sa description
     * @param debut le jour de début
     * @param fin le jour de fin
     * @param priorite la priorité
     */
    public void creerTacheComposite(String titre, String description,
                                    String debut, String fin, String priorite) {
        if (titre == null || titre.trim().isEmpty()) {
            VueFormulaire.afficherAlerteErreur("Le titre est obligatoire", "Erreur de création");
            throw new IllegalArgumentException("Le titre est obligatoire");
        }
        Tache tache = TacheFactory.creerTacheComposite(titre, description, debut, fin, priorite);
        if (!colonnes.isEmpty()) {
            tache.setEtat(colonnes.get(0));
        }
        listeTaches.add(tache);
        notifierObservateur();
        sauvegarderTaches();
    }

    /**
     * Modifie une tâche existante
     * @param t la tâche à modifier
     * @param titre le nouveau titre
     * @param description la nouvelle description
     * @param priorite la nouvelle priorité
     */
    public void modifierTache(Tache t, String titre, String description, String priorite) {
        if (t == null || titre == null || titre.trim().isEmpty()) {
            VueFormulaire.afficherAlerteErreur("Paramètres invalides", "Erreur de modification");
            throw new IllegalArgumentException("Paramètres invalides");
        }
        t.setTitre(titre);
        t.setDescription(description);
        t.setPriorite(priorite);
        notifierObservateur();
        sauvegarderTaches();
    }

    /**
     * Ajoute une sous-tâche à une tâche parente
     * @param parent la tâche parente (doit être composite)
     * @param titre le titre de la sous-tâche
     * @param description sa description
     * @param estComposite true si la sous-tâche est composite
     * @param debut le jour de début
     * @param fin le jour de fin
     * @param priorite la priorité
     */
    public void ajouterSousTache(Tache parent, String titre, String description,
                                 boolean estComposite, String debut, String fin, String priorite) {
        if (parent == null || !parent.estComposite() ||
                titre == null || titre.trim().isEmpty()) {
            VueFormulaire.afficherAlerteErreur("Impossible d'ajouter une sous-tâche", "Erreur d'ajout");
            throw new IllegalArgumentException("Impossible d'ajouter une sous-tâche");
        }

        if (!estIntervalleValide(parent, debut, fin)) {
            String message = "La sous-tâche doit être comprise entre " +
                    parent.getJDebut() + " et " + parent.getJFin();
            VueFormulaire.afficherAlerteErreur(message, "Erreur d'intervalle");
            throw new IllegalArgumentException(message);
        }

        Tache sousTache;
        if (estComposite) {
            sousTache = TacheFactory.creerTacheComposite(titre, description, debut, fin, priorite);
        } else {
            sousTache = TacheFactory.creerTacheSimple(titre, description, debut, fin, priorite);
        }

        sousTache.setEtat(parent.getEtat());

        TacheComposite composite = (TacheComposite) parent;
        composite.ajouterSousTache(sousTache);

        notifierObservateur();
        sauvegarderTaches();
    }

    /**
     * Supprime une tâche (principale ou sous-tâche)
     * @param t la tâche à supprimer
     */
    public void supprimerTache(Tache t) {
        if (t == null) return;

        if (listeTaches.remove(t)) {
            notifierObservateur();
            sauvegarderTaches();
            return;
        }

        for (Tache tache : listeTaches) {
            if (supprimerSousTacheRecursif(tache, t)) {
                notifierObservateur();
                sauvegarderTaches();
                return;
            }
        }

        VueFormulaire.afficherAlerteErreur("Tâche non trouvée", "Erreur de suppression");
    }

    /**
     * Supprime récursivement une sous-tâche
     * @param parent la tâche parente où chercher
     * @param aSupprimer la tâche à supprimer
     * @return true si la tâche a été supprimée
     */
    private boolean supprimerSousTacheRecursif(Tache parent, Tache aSupprimer) {
        if (parent.estComposite()) {
            TacheComposite composite = (TacheComposite) parent;

            if (composite.retirerSousTache(aSupprimer)) {
                return true;
            }

            for (Tache sousTache : composite.getSousTaches()) {
                if (supprimerSousTacheRecursif(sousTache, aSupprimer)) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * Ajoute une nouvelle colonne
     * @param titre le nom de la colonne
     */
    public void ajouterColonne(String titre) {
        if (colonnes.contains(titre)) {
            VueFormulaire.afficherAlerteErreur("Une colonne avec ce nom existe déjà", "Erreur de création");
            return;
        }

        colonnes.add(titre);
        notifierObservateur();
        sauvegarderColonnes();
    }

    /**
     * Supprime une colonne et toutes ses tâches
     * @param titre le nom de la colonne à supprimer
     */
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
            sauvegarderTaches();
        }
    }


    /**
     * Déplace une tâche vers une nouvelle colonne (changement d'état)
     * @param t la tâche à déplacer
     * @param nouvelEtat le nouvel état/colonne
     */
    public void deplacerTache(Tache t, String nouvelEtat) {
        if (t != null && nouvelEtat != null) {
            t.setEtat(nouvelEtat);
            notifierObservateur();
            sauvegarderTaches();
        }
    }

    /**
     * Archive une tâche (la met dans l'état "archive")
     * @param t la tâche à archiver
     */
    public void archiverTache(Tache t) {
        if (t != null) {
            t.setAncienEtat(t.getEtat());
            t.setEtat("archive");
            notifierObservateur();
            sauvegarderTaches();
        }
    }

    /**
     * Restaure une tâche archivée vers son état d'origine
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
            sauvegarderTaches();
        }
    }


    /**
     * Fait d'une tâche une sous-tâche d'une autre
     * @param enfant la tâche à devenir sous-tâche
     * @param nouveauParent la nouvelle tâche parente
     */
    public void devenirSousTacheDe(Tache enfant, Tache nouveauParent) {
        if (enfant == null || nouveauParent == null) {
            VueFormulaire.afficherAlerteErreur("Tâche invalide", "Erreur de parentage");
            return;
        }

        if (enfant == nouveauParent) {
            VueFormulaire.afficherAlerteErreur("Une tâche ne peut pas être sous-tâche d'elle-même",
                    "Erreur de parentage");
            return;
        }

        if (!nouveauParent.estComposite()) {
            VueFormulaire.afficherAlerteErreur("La tâche parente ne peut pas recevoir de sous-tâches",
                    "Erreur de parentage");
            return;
        }

        if (enfant.contientTache(nouveauParent)) {
            VueFormulaire.afficherAlerteErreur("Erreur : Cycle détecté - l'enfant contient déjà le parent",
                    "Erreur de parentage");
            return;
        }

        boolean retire = false;

        if (listeTaches.remove(enfant)) {
            retire = true;
        } else {
            for (Tache tache : listeTaches) {
                if (retirerSousTacheRecursif(tache, enfant)) {
                    retire = true;
                    break;
                }
            }
        }

        if (retire) {
            TacheComposite parentComposite = (TacheComposite) nouveauParent;
            parentComposite.ajouterSousTache(enfant);

            ajusterJoursPourSousTache(enfant, nouveauParent);

            notifierObservateur();
            sauvegarderTaches();
        }
    }



    /**
     * Ajuste les jours d'une sous-tâche pour qu'elle soit dans l'intervalle du parent
     */
    private void ajusterJoursPourSousTache(Tache enfant, Tache parent) {
        if (!estIntervalleValide(parent, enfant.getJDebut(), enfant.getJFin())) {
            // Par défaut, mettre les mêmes jours que le parent
            enfant.setJDebut(parent.getJDebut());
            enfant.setJFin(parent.getJFin());
        }
    }

    /**
     * Retire une sous-tâche récursivement
     */
    private boolean retirerSousTacheRecursif(Tache parent, Tache aRetirer) {
        if (parent.estComposite()) {
            TacheComposite composite = (TacheComposite) parent;

            if (composite.retirerSousTache(aRetirer)) {
                return true;
            }

            for (Tache sousTache : composite.getSousTaches()) {
                if (retirerSousTacheRecursif(sousTache, aRetirer)) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * Extrait une sous-tâche pour en faire une tâche principale
     * @param sousTache la sous-tâche à extraire
     */
    public void extraireSousTache(Tache sousTache) {
        Tache parent = trouverParent(sousTache);
        if (parent != null && parent.estComposite()) {
            TacheComposite parentComposite = (TacheComposite) parent;
            parentComposite.retirerSousTache(sousTache);

            listeTaches.add(sousTache);
            notifierObservateur();
            sauvegarderTaches();
        } else {
            VueFormulaire.afficherAlerteErreur("Impossible d'extraire la sous-tâche",
                    "Erreur d'extraction");
        }
    }

    /**
     * Extrait une sous-tâche vers une colonne spécifique
     * @param sousTache la sous-tâche à extraire
     * @param etat la colonne de destination
     */
    public void extraireVersColonne(Tache sousTache, String etat) {
        if (sousTache == null || etat == null) return;

        retirerDeTouteHierarchie(sousTache);

        if (!listeTaches.contains(sousTache)) {
            listeTaches.add(sousTache);
        }

        sousTache.setEtat(etat);

        notifierObservateur();
        sauvegarderTaches();
    }

    /**
     * Extrait une sous-tâche vers un jour spécifique
     * @param sousTache la sous-tâche à extraire
     * @param jour le jour de destination
     */
    public void extraireVersJour(Tache sousTache, String jour) {
        if (sousTache == null || jour == null) {
            VueFormulaire.afficherAlerteErreur("Paramètres invalides", "Erreur d'extraction");
            return;
        }

        retirerDeTouteHierarchie(sousTache);

        if (!listeTaches.contains(sousTache)) {
            listeTaches.add(sousTache);
        }

        sousTache.setJDebut(jour);

        int duree = calculerDistance(sousTache.getJFin(), sousTache.getJDebut());
        String nouvelleFin = calculerJourSuivant(jour, duree);
        sousTache.setJFin(nouvelleFin);

        notifierObservateur();
        sauvegarderTaches();
    }


    /**
     * Retire une tâche de toute la hiérarchie
     */
    private void retirerDeTouteHierarchie(Tache aRetirer) {
        if (listeTaches.remove(aRetirer)) {
            return;
        }

        for (Tache tache : listeTaches) {
            if (retirerRecursifDeHierarchie(tache, aRetirer)) {
                return;
            }
        }
    }

    private boolean retirerRecursifDeHierarchie(Tache parent, Tache aRetirer) {
        if (parent.estComposite()) {
            TacheComposite composite = (TacheComposite) parent;

            if (composite.retirerSousTache(aRetirer)) {
                return true;
            }

            for (Tache sousTache : composite.getSousTaches()) {
                if (retirerRecursifDeHierarchie(sousTache, aRetirer)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Trouve le parent d'une sous-tâche
     */
    private Tache trouverParent(Tache enfant) {
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
            if (composite.getSousTaches().contains(enfant)) {
                return true;
            }
            for (Tache sousTache : composite.getSousTaches()) {
                if (trouverParentRecursif(sousTache, enfant)) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * Vérifie si un intervalle de dates est valide par rapport au parent
     */
    private boolean estIntervalleValide(Tache parent, String debutEnfant, String finEnfant) {
        int dureeParent = calculerDistance(parent.getJFin(), parent.getJDebut());
        int posDebutEnfant = calculerDistance(debutEnfant, parent.getJDebut());
        int posFinEnfant = calculerDistance(finEnfant, parent.getJDebut());

        return posDebutEnfant <= dureeParent &&
                posFinEnfant <= dureeParent &&
                posDebutEnfant <= posFinEnfant;
    }

    /**
     * Calcule la distance entre deux jours
     */
    private int calculerDistance(String jourCible, String jourDepart) {
        int idxDep = JOURS_REFERENCE.indexOf(jourDepart);
        int idxCible = JOURS_REFERENCE.indexOf(jourCible);

        if (idxCible < idxDep) {
            return idxCible + 7 - idxDep;
        }
        return idxCible - idxDep;
    }

    /**
     * Calcule le jour suivant après un certain nombre de jours
     */
    private String calculerJourSuivant(String jourDepart, int nbJours) {
        int idx = JOURS_REFERENCE.indexOf(jourDepart);
        int nouvelIdx = (idx + nbJours) % 7;
        return JOURS_REFERENCE.get(nouvelIdx);
    }


    /**
     * Ajoute un observateur
     * @param o l'observateur à ajouter
     */
    @Override
    public void ajouterObservateur(Observateur o) {
        if (o != null && !observateurs.contains(o)) {
            observateurs.add(o);
        }
    }

    /**
     * Supprime un observateur
     * @param o l'observateur à supprimer
     */
    @Override
    public void supprimerObservateur(Observateur o) {
        observateurs.remove(o);
    }

    /**
     * Notifie tous les observateurs d'un changement
     */
    @Override
    public void notifierObservateur() {
        for (Observateur o : observateurs) {
            o.actualiser();
        }
    }

    /**
     * Récupère la liste des tâches principales
     * @return la liste des tâches
     */
    public ArrayList<Tache> getTaches() {
        return listeTaches;
    }
}