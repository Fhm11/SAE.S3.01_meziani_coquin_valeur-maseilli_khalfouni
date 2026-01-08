package source;

import javafx.scene.control.Button;

/**
 * Contrôleur spécialisé pour la vue Archive
 */
public class ControllerArchive {

    private Controller controleur;

    public ControllerArchive(Controller controleur) {
        this.controleur = controleur;
    }

    /**
     * Configure la vue Archive
     */
    public void configurerVue(VueArchive vue) {
        configurerBoutons(vue);
    }

    /**
     * Configure les boutons de la vue Archive
     */
    private void configurerBoutons(VueArchive vue) {
        for (Button bouton : vue.getBoutonsInteractifs()) {
            bouton.setOnAction(e -> controleur.handle(e));
        }
    }
}