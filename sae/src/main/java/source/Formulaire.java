package source;


import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Formulaire {

    public static void afficherPopup(Controller controller) {
        Stage fenetre = new Stage();
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        Label lblTitre = new Label("Titre :");
        TextField txtTitre = new TextField();

        Label lblDesc = new Label("Description :");
        TextField txtDesc = new TextField();

        Button btnSave = new Button("Sauvegarder");

        btnSave.setOnAction(e -> {
            try {
                controller.creerTache(
                        txtTitre.getText(),
                        txtDesc.getText()
                );
                fenetre.close();
            } catch (IllegalArgumentException ex) {
                System.err.println(ex.getMessage());
            }
        });

        root.getChildren().addAll(lblTitre, txtTitre, lblDesc, txtDesc, btnSave);
        fenetre.setScene(new Scene(root, 300, 200));
        fenetre.setTitle("Créer une tâche");
        fenetre.show();
    }

    public static void modifierTache(Tache t) {
        Stage fenetre = new  Stage();
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        Label lblTitre = new Label("Titre :");
        TextField txtTitre = new TextField(t.getTitre());

        Label lblDesc = new Label("Description :");
        TextField txtDesc = new TextField(t.getDescription());

        Button btnSave = new Button("Sauvegarder");

        btnSave.setOnAction(e -> {
            try {
                c.modifierTache(t,txtTitre.getText(),txtDesc.getText());
                fenetre.close();
            } catch (IllegalArgumentException ex) {
                System.err.println(ex.getMessage());
            }
        });

        root.getChildren().addAll(lblTitre, txtTitre, lblDesc, txtDesc, btnSave);
        fenetre.setScene(new Scene(root, 300, 200));
        fenetre.setTitle("Modifier la tâche");
        fenetre.show();
    }
}
