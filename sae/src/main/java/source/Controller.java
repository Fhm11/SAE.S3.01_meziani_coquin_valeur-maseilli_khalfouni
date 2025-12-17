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

    // Méthodes pour VueFormulaire
    public void creerTache(String titre, String description, boolean estComposite) {
        if (estComposite) {
            modele.creerTacheComposite(titre, description);
        } else {
            modele.creerTacheSimple(titre, description);
        }
    }

    public void modifierTache(Tache t, String titre, String description) {
        modele.modifierTache(t, titre, description);
    }

    public void ajouterSousTache(Tache parent, String titre, String description) {
        modele.ajouterSousTache(parent, titre, description);
    }

    // Appelé quand le drag commence
    public void debuterDeplacement(Tache t) {
        this.tacheEnDeplacement = t;
    }

    // Appelé quand on lache la tâche dans une colonne
    public void finaliserDeplacement(String nouvelEtat) {
        if (tacheEnDeplacement != null) {
            modele.deplacerTache(tacheEnDeplacement, nouvelEtat);
            tacheEnDeplacement = null; // Reset
        }
    }
}