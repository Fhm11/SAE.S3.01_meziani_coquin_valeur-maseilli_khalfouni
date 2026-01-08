package source;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

/**
 * Contrôleur principal de l'application (patron MVC)
 * Gère les événements des boutons et coordonne les actions entre la vue et le modèle
 */
public class Controller implements EventHandler<ActionEvent> {

    private TacheManager modele;

    private Tache tacheEnDeplacement;

    /**
     * Constructeur du contrôleur
     * @param modele le gestionnaire de tâches (modèle)
     */
    public Controller(TacheManager modele) {
        this.modele = modele;
    }

    /**
     * Méthode principale pour gérer les événements des boutons
     * @param evenement l'événement déclenché
     */
    @Override
    public void handle(ActionEvent evenement) {
        Object source = evenement.getSource();

        // Vérifie si l'événement vient d'un bouton
        if (source instanceof Button) {
            Button bouton = (Button) source;
            Object donnees = bouton.getUserData();

            // Vérifie si le bouton est associé à une tâche
            if (donnees instanceof Tache) {
                Tache tache = (Tache) donnees;
                String texteBouton = bouton.getText();

                // Détermine l'action en fonction du texte du bouton
                if ("Archiver".equals(texteBouton)) {
                    modele.archiverTache(tache);
                }
                else if ("Restaurer".equals(texteBouton)) {
                    modele.restaurerTache(tache);
                }
                else if ("supprimer".equals(texteBouton)) {
                    modele.supprimerTache(tache);
                }
                else if ("Supprimer".equals(texteBouton)) {
                    modele.supprimerTache(tache);
                }
                else if ("+".equals(texteBouton)) {
                    // Affiche le formulaire pour ajouter une sous-tâche
                    VueFormulaire.afficherFormulaireSousTache(tache, this);
                }
            }
        }
    }

    /**
     * Crée une nouvelle tâche (simple ou composite)
     * @param titre le titre de la tâche
     * @param description sa description
     * @param estComposite true pour une tâche composite, false pour une tâche simple
     * @param debut le jour de début (Lundi, Mardi, etc.)
     * @param fin le jour de fin
     * @param priorite la priorité (Basse, Moyenne, Importante)
     */
    public void creerTache(String titre, String description, boolean estComposite,
                           String debut, String fin, String priorite) {
        if (estComposite) {
            modele.creerTacheComposite(titre, description, debut, fin, priorite);
        } else {
            modele.creerTacheSimple(titre, description, debut, fin, priorite);
        }
    }



    /**
     * Modifie une tâche existante
     * @param tache la tâche à modifier
     * @param titre le nouveau titre
     * @param description la nouvelle description
     * @param priorite la nouvelle priorité
     */
    public void modifierTache(Tache tache, String titre, String description, String priorite) {
        modele.modifierTache(tache, titre, description, priorite);
    }

    /**
     * Ajoute une sous-tâche à une tâche existante
     * @param parent la tâche parente (doit être composite)
     * @param titre le titre de la sous-tâche
     * @param description sa description
     * @param estComposite true si la sous-tâche est elle-même composite
     * @param debut le jour de début
     * @param fin le jour de fin
     * @param priorite la priorité
     */
    public void ajouterSousTache(Tache parent, String titre, String description,
                                 boolean estComposite, String debut, String fin, String priorite) {
        modele.ajouterSousTache(parent, titre, description, estComposite, debut, fin, priorite);
    }


    /**
     * Débute le déplacement d'une tâche (appelé au début du drag)
     * @param tache la tâche à déplacer
     */
    public void debuterDeplacement(Tache tache) {
        this.tacheEnDeplacement = tache;
    }

    /**
     * Finalise le déplacement d'une tâche (appelé à la fin du drop)
     * @param nouvelEtat le nouvel état/colonne de la tâche
     */
    public void finaliserDeplacement(String nouvelEtat) {
        if (tacheEnDeplacement != null) {
            modele.deplacerTache(tacheEnDeplacement, nouvelEtat);
            tacheEnDeplacement = null; // Réinitialise pour le prochain déplacement
        }
    }

    /**
     * Déplace une tâche vers un nouveau jour en conservant sa durée
     * @param tache la tâche à déplacer
     * @param nouveauJour le nouveau jour de début
     */
    public void deplacerTacheVersJour(Tache tache, String nouveauJour) {
        if (tache != null && nouveauJour != null) {
            // Calcule la durée actuelle de la tâche
            int duree = UtilitairesDates.calculerDuree(tache.getJDebut(), tache.getJFin());

            // Change le jour de début
            tache.setJDebut(nouveauJour);

            // Calcule le nouveau jour de fin en conservant la durée
            String nouveauJFin = UtilitairesDates.calculerJourSuivant(nouveauJour, duree);
            tache.setJFin(nouveauJFin);

            // Notifie les observateurs
            modele.notifierObservateur();
        }
    }

    /**
     * Version simplifiée du déplacement de jour (change seulement le début)
     * @param tache la tâche à déplacer
     * @param nouveauJour le nouveau jour de début
     */
    public void changerJourTache(Tache tache, String nouveauJour) {
        if (tache != null && nouveauJour != null) {
            tache.setJDebut(nouveauJour);
            modele.notifierObservateur();
        }
    }

    /**
     * Ajoute une nouvelle colonne au bureau
     * @param titre le nom de la colonne
     */
    public void ajouterColonne(String titre) {
        modele.ajouterColonne(titre);
    }

    /**
     * Supprime une colonne du bureau
     * @param titre le nom de la colonne à supprimer
     */
    public void supprimerColonne(String titre) {
        modele.supprimerColonne(titre);
    }


    /**
     * Fait d'une tâche une sous-tâche d'une autre tâche
     * @param enfant la tâche à devenir sous-tâche
     * @param parentCible la nouvelle tâche parente
     */
    public void devenirSousTacheDe(Tache enfant, Tache parentCible) {
        modele.devenirSousTacheDe(enfant, parentCible);
    }


    /**
     * Extrait une sous-tâche vers une colonne spécifique
     * @param sousTache la sous-tâche à extraire
     * @param etat la colonne de destination
     */
    public void extraireVersColonne(Tache sousTache, String etat) {
        modele.extraireVersColonne(sousTache, etat);
    }

    /**
     * Extrait une sous-tâche vers un jour spécifique
     * @param sousTache la sous-tâche à extraire
     * @param jour le jour de destination
     */
    public void extraireVersJour(Tache sousTache, String jour) {
        modele.extraireVersJour(sousTache, jour);
    }


    /**
     * Récupère la tâche en cours de déplacement
     * @return la tâche en déplacement, ou null si aucune
     */
    public Tache getTacheEnDeplacement() {
        return tacheEnDeplacement;
    }
}