package source;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        // 1. Modèle
        TacheManager modele = TacheManager.getInstance();

        // 2. Vue (avec modèle)
        VueBureau vue = new VueBureau(modele);

        // 3. Contrôleur (avec modèle)
        Controller controleur = new Controller(modele);

        // 4. Enregistrer la vue comme observateur
        modele.ajouterObservateur(vue);

        // 5. Créer le bouton "Nouvelle Tâche" DANS MAIN (comme dans le cours)
        Button btnCreer = new Button("Nouvelle Tâche");
        btnCreer.setStyle("-fx-font-size: 14px; -fx-base: #4CAF50;");

        // Configurer le handler DANS MAIN (patron Stratégie)
        btnCreer.setOnAction(e -> {
            VueFormulaire.afficherFormulaireCreation(controleur);
        });

        // Ajouter le bouton à la vue
        vue.getRoot().getChildren().add(0, btnCreer);

        // 6. Observateur pour reconfigurer les handlers après chaque changement
        modele.ajouterObservateur(new Observateur() {
            @Override
            public void actualiser() {
                // Reconfigurer les handlers quand la vue est actualisée
                configurerHandlers(vue, controleur);
            }
        });

        // 7. Première initialisation
        vue.actualiser();
        configurerHandlers(vue, controleur);

        // 8. Afficher
        Scene scene = new Scene(vue.getRoot(), 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Trello");
        primaryStage.show();
    }

    // Méthode privée dans Main pour configurer les handlers (comme dans l'exemple du cours)
    private void configurerHandlers(VueBureau vue, Controller controleur) {
        // 1. Configurer les boutons "Supprimer" et "+ Sous-tâche"
        for (Button btn : vue.getBoutonsInteractifs()) {
            btn.setOnAction(controleur);
        }

        // 2. Configurer le double-clic sur les cartes
        for (VBox carte : vue.getCartesTaches()) {
            carte.setOnMouseClicked(e -> {
                if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
                    Tache t = (Tache) carte.getUserData();
                    VueFormulaire.afficherFormulaireModification(t, controleur);
                }
            });
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}