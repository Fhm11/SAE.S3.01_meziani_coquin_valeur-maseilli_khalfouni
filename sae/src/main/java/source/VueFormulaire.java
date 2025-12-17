package source;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class VueFormulaire {

    public static void afficherFormulaireCreation(Controller controleur) {
        Stage fenetre = new Stage();
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        TextField txtTitre = new TextField();
        TextField txtDesc = new TextField();
        CheckBox chkComposite = new CheckBox("Peut avoir des sous-tâches");
        Button btnSave = new Button("Sauvegarder");

        btnSave.setOnAction(e -> {
            try {
                String titre = txtTitre.getText();
                String description = txtDesc.getText();
                controleur.creerTache(titre, description, chkComposite.isSelected());
                fenetre.close();
            } catch (IllegalArgumentException ex) {
                System.err.println(ex.getMessage());
            }
        });

        root.getChildren().addAll(
                new Label("Titre :"), txtTitre,
                new Label("Description :"), txtDesc,
                chkComposite, btnSave
        );
        fenetre.setScene(new Scene(root, 300, 200));
        fenetre.setTitle("Créer une tâche");
        fenetre.show();
    }

    public static void afficherFormulaireModification(Tache t, Controller controleur) {
        Stage fenetre = new Stage();
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        TextField txtTitre = new TextField(t.getTitre());
        TextField txtDesc = new TextField(t.getDescription());
        Button btnSave = new Button("Sauvegarder");

        btnSave.setOnAction(e -> {
            try {
                controleur.modifierTache(t, txtTitre.getText(), txtDesc.getText());
                fenetre.close();
            } catch (IllegalArgumentException ex) {
                System.err.println(ex.getMessage());
            }
        });

        root.getChildren().addAll(
                new Label("Titre :"), txtTitre,
                new Label("Description :"), txtDesc,
                btnSave
        );
        fenetre.setScene(new Scene(root, 300, 200));
        fenetre.setTitle("Modifier la tâche");
        fenetre.show();
    }

    public static void afficherFormulaireSousTache(Tache parent, Controller controleur) {
        Stage fenetre = new Stage();
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        TextField txtTitre = new TextField();
        TextField txtDesc = new TextField();
        Button btnSave = new Button("Ajouter");

        btnSave.setOnAction(e -> {
            try {
                controleur.ajouterSousTache(parent, txtTitre.getText(), txtDesc.getText());
                fenetre.close();
            } catch (IllegalArgumentException ex) {
                System.err.println(ex.getMessage());
            }
        });

        root.getChildren().addAll(
                new Label("Ajouter sous-tâche à : " + parent.getTitre()),
                new Label("Titre :"), txtTitre,
                new Label("Description :"), txtDesc,
                btnSave
        );
        fenetre.setScene(new Scene(root, 300, 250));
        fenetre.setTitle("Ajouter une sous-tâche");
        fenetre.show();
    }
}