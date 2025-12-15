package source;


import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.CheckBox;
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

        CheckBox chkComposite = new CheckBox("Peut avoir des sous-tâches");

        Button btnSave = new Button("Sauvegarder");

        btnSave.setOnAction(e -> {
            try {
                String titre = txtTitre.getText();
                String description = txtDesc.getText();
                boolean estComposite = chkComposite.isSelected();

                if (estComposite) {
                    controller.creerTacheComposite(titre, description);
                } else {
                    controller.creerTacheSimple(titre, description);
                }
                fenetre.close();
            } catch (IllegalArgumentException ex) {
                System.err.println(ex.getMessage());
            }
        });

        root.getChildren().addAll(lblTitre, txtTitre, lblDesc, txtDesc, chkComposite, btnSave);
        fenetre.setScene(new Scene(root, 300, 200));
        fenetre.setTitle("Créer une tâche");
        fenetre.show();
    }

    public static void modifierTache(Tache t, Controller controller) {
        Stage fenetre = new Stage();
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        Label lblTitre = new Label("Titre :");
        TextField txtTitre = new TextField(t.getTitre());

        Label lblDesc = new Label("Description :");
        TextField txtDesc = new TextField(t.getDescription());

        Button btnSave = new Button("Sauvegarder");

        btnSave.setOnAction(e -> {
            try {
                controller.modifierTache(t, txtTitre.getText(), txtDesc.getText());
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

    public static void afficherPopupSousTache(Controller controller, Tache parent) {
        Stage fenetre = new Stage();
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        Label lblTitre = new Label("Ajouter une sous-tâche à : " + parent.getTitre());
        Label lblSousTitre = new Label("Titre de la sous-tâche :");
        TextField txtSousTitre = new TextField();

        Label lblSousDesc = new Label("Description :");
        TextField txtSousDesc = new TextField();

        Button btnSave = new Button("Ajouter");

        btnSave.setOnAction(e -> {
            try {
                // Utiliser une méthode spécifique du Controller
                controller.ajouterSousTache(parent, txtSousTitre.getText(), txtSousDesc.getText());
                fenetre.close();
            } catch (IllegalArgumentException ex) {
                System.err.println(ex.getMessage());
            }
        });

        root.getChildren().addAll(lblTitre, lblSousTitre, txtSousTitre,
                lblSousDesc, txtSousDesc, btnSave);

        fenetre.setScene(new Scene(root, 300, 250));
        fenetre.setTitle("Ajouter une sous-tâche");
        fenetre.show();
    }
}
