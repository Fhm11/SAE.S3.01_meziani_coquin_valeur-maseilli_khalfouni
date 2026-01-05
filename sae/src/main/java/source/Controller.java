package source;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

public class Controller implements EventHandler<ActionEvent> {
    private TacheManager modele;
    private Tache tacheEnDeplacement;

    public Controller(TacheManager modele) {
        this.modele = modele;
    }

    /**
     * Méthode pour gérer les évènements
     * @param event les évènements
     */
    @Override
    public void handle(ActionEvent event) {
        Object source = event.getSource();

        if (source instanceof Button) {
            Button btn = (Button) source;
            Object data = btn.getUserData();

            if (data instanceof Tache) {
                Tache t = (Tache) data;

                if ("Supprimer".equals(btn.getText())) {
                    modele.supprimerTache(t);
                } else if ("+ Sous-tâche".equals(btn.getText())) {
                    VueFormulaire.afficherFormulaireSousTache(t, this);
                }
            }
        }
    }

    /**
     * Méthodes pour vueFormulaire qui créé une tache
     * @param titre le titre
     * @param description la descrption
     * @param estComposite true si elle peut avoir une sous-tâche, false sinon
     */
    public void creerTache(String titre, String description, boolean estComposite, String debut, String fin) {
        if (estComposite) {
            modele.creerTacheComposite(titre, description, debut, fin);
        } else {
            modele.creerTacheSimple(titre, description, debut, fin);
        }
    }

    /**
     * Méthode pour modifier une tâche
     * @param t la tâche à modifier
     * @param titre le titre modifié
     * @param description la description modifié
     */
    public void modifierTache(Tache t, String titre, String description) {
        modele.modifierTache(t, titre, description);
    }

    /**
     * Méthode pour ajouter une tâche
     * à une tâche existante
     * @param parent la tâche parente
     * @param titre le titre de la sous-tâche
     * @param description la description de la sous-tâche
     */
    public void ajouterSousTache(Tache parent, String titre, String description, String debut, String fin) {
        modele.ajouterSousTache(parent, titre, description, debut, fin);
    }

    /**
     * Méthode appelée quand on clique sur une tâche
     * pour la déplacée
     * @param t la tâche
     */
    public void debuterDeplacement(Tache t) {
        this.tacheEnDeplacement = t;
    }

    /**
     * Appelé quand on lache la tâche dans une colonne
      */

    public void finaliserDeplacement(String nouvelEtat) {
        if (tacheEnDeplacement != null) {
            modele.deplacerTache(tacheEnDeplacement, nouvelEtat);
            tacheEnDeplacement = null; // Reset
        }
    }

    public void ajouterColonne(String titre) {
        modele.ajouterColonne(titre);
    }

    public void supprimerColonne(String titre) {
        modele.supprimerColonne(titre);
    }

    /**
     * Méthode pour changer le jour d'une tâche (pour la vue liste)
     * @param t la tâche à déplacer
     * @param nouveauJour le nouveau jour ("Lundi", "Mardi", etc.)
     */
    public void changerJourTache(Tache t, String nouveauJour) {
        if (t != null && nouveauJour != null) {
            t.setJDebut(nouveauJour);
            modele.notifierObservateur();
        }
    }

}