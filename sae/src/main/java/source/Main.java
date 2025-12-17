package source;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        VueBureau vue = new VueBureau();

        // enregistrer la vue comme observateur du modèle
        TacheManager.getInstance().ajouterObservateur(vue);

        Scene scene = new Scene(vue.getRoot(), 500, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Trello");
        primaryStage.show();

        // initialiser l'affichage
        vue.actualiser();
    }

    public static void main(String[] args) {
        launch(args);
    }
}