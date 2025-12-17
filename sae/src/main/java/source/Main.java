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
        TacheManager modele = TacheManager.getInstance();

        VueBureau vue = new VueBureau(modele);

        Controller controleur = new Controller(modele);

        modele.ajouterObservateur(vue);

        Button btnCreer = new Button("Nouvelle Tâche");
        btnCreer.setStyle("-fx-font-size: 14px; -fx-base: #4CAF50;");

        btnCreer.setOnAction(e -> {
            VueFormulaire.afficherFormulaireCreation(controleur);
        });

        vue.getRoot().getChildren().add(0, btnCreer);

        modele.ajouterObservateur(new Observateur() {
            @Override
            public void actualiser() {
                // reconfigurer les handlers quand la vue est actualisée
                configurerHandlers(vue, controleur);
            }
        });

        vue.actualiser();
        configurerHandlers(vue, controleur);

        Scene scene = new Scene(vue.getRoot(), 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Trello");
        primaryStage.show();
    }

    private void configurerHandlers(VueBureau vue, Controller controleur) {
        for (Button btn : vue.getBoutonsInteractifs()) {
            btn.setOnAction(controleur);
        }

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