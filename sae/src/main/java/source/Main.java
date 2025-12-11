package main.java.source;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        VueTexte vue = new VueTexte();
        TacheManager.getInstance().ajouterObservateur(vue);
        Button btnCreer = new Button("Créer une tâche");
        btnCreer.setOnAction(e -> PopupCreationTache.afficherPopup());
        VBox root = new VBox(20);
        root.getChildren().add(btnCreer);
        Scene scene = new Scene(root, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.setTitle("test");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
