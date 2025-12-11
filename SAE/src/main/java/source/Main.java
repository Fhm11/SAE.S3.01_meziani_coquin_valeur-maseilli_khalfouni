package source;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {

        // ---- ici on crée la vue console et on l'abonne ----
        VueTexte vue = new VueTexte();
        TacheManager.getInstance().ajouterObservateur(vue);

        // ---- ici le bouton pour ouvrir la popup de création ----
        Button btnCreer = new Button("Créer une tâche");
        btnCreer.setOnAction(e -> PopupCreationTache.afficherPopup());

        // ---- interface simple pour tester ----
        VBox root = new VBox(20);
        root.getChildren().add(btnCreer);

        Scene scene = new Scene(root, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Gestionnaire de tâches - Test");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
