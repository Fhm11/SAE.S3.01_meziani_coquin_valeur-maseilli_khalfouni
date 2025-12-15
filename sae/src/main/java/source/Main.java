package source;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        Controller c = new Controller();

        VBox root = new VBox(10);
        VueBureau vue = new VueBureau();

        TacheManager.getInstance().ajouterObservateur(vue);

        Button btnCreer = new Button("Créer une tâche");
        btnCreer.setOnAction(e -> Formulaire.afficherPopup(c));
        root.getChildren().addAll(btnCreer, vue.getRoot());
        Scene scene = new Scene(root, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("vue bureau");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
