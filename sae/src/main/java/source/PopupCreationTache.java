package source;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PopupCreationTache {

    public static void afficherPopup() {
        Stage fenetre = new Stage();
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        Label lblTitre = new Label("Titre :");
        TextField txtTitre = new TextField();

        Label lblDesc = new Label("Description :");
        TextField txtDesc = new TextField();

        Button btnSave = new Button("Sauvegarder");

        btnSave.setOnAction(e -> {
            String titre = txtTitre.getText();
            String desc = txtDesc.getText();
            try {
                Tache t = TacheFactory.creerTache(titre, desc);
                TacheManager.getInstance().ajouterTache(t);
                fenetre.close();
            } catch (IllegalArgumentException ex) {
                System.err.println("ta oubleir qqch : " + ex.getMessage());
            }
        });

        root.getChildren().addAll(lblTitre, txtTitre, lblDesc, txtDesc, btnSave);

        fenetre.setScene(new Scene(root, 300, 200));
        fenetre.setTitle("Créer une tâche");
        fenetre.show();
    }
}
