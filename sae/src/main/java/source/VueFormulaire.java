package source;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class VueFormulaire {

    /**
     * Méthode pour afficher le formulaire de création de tâche
     * @param controleur le controller
     */
    public static void afficherFormulaireCreation(Controller controleur) {
        Stage fenetre = new Stage();
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        TextField txtTitre = new TextField();
        TextField txtDesc = new TextField();

        javafx.collections.ObservableList<String> jours =
                javafx.collections.FXCollections.observableArrayList(
                        "Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche"
                );
        ComboBox<String> comboDebut = new ComboBox<>(jours);
        comboDebut.setValue("Lundi");
        ComboBox<String> comboFin = new ComboBox<>(jours);
        comboFin.setValue("Lundi");

        CheckBox chkComposite = new CheckBox("Peut avoir des sous-tâches");
        Button btnSave = new Button("Sauvegarder");

        btnSave.setOnAction(e -> {
            try {
                controleur.creerTache(
                        txtTitre.getText(),
                        txtDesc.getText(),
                        chkComposite.isSelected(),
                        comboDebut.getValue(),
                        comboFin.getValue()
                );
                fenetre.close();
            } catch (IllegalArgumentException ex) {
                System.err.println(ex.getMessage());
            }
        });

        root.getChildren().addAll(
                new Label("Titre :"), txtTitre,
                new Label("Description :"), txtDesc,
                new Label("Jour Début :"), comboDebut,
                new Label("Jour Fin :"), comboFin,
                chkComposite, btnSave
        );
        fenetre.setScene(new Scene(root, 300, 450));
        fenetre.setTitle("Créer une tâche");
        fenetre.show();
    }

    /**
     * Méthode pour afficher le formulaire de modification de tâche
     * @param t la tâche à modifiée
     * @param controleur le controlleur
     */
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
        fenetre.setScene(new Scene(root, 300, 450));
        fenetre.setTitle("Modifier la tâche");
        fenetre.show();
    }

    /**
     * Méthode pour la création de sous-tâche
     * @param parent la tâche parente
     * @param controleur le controller
     */
    public static void afficherFormulaireSousTache(Tache parent, Controller controleur) {
        Stage fenetre = new Stage();
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        TextField txtTitre = new TextField();
        TextField txtDesc = new TextField();

        javafx.collections.ObservableList<String> jours =
                javafx.collections.FXCollections.observableArrayList(
                        "Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche"
                );
        ComboBox<String> comboDebut = new ComboBox<>(jours);
        comboDebut.setValue("Lundi");
        ComboBox<String> comboFin = new ComboBox<>(jours);
        comboFin.setValue("Lundi");
        CheckBox chkComposite = new CheckBox("Ajouter une sous-tâche");
        Button btnSave = new Button("Ajouter");

        btnSave.setOnAction(e -> {
            try {
                controleur.ajouterSousTache(
                        parent,
                        txtTitre.getText(),
                        txtDesc.getText(),
                        chkComposite.isSelected(),
                        comboDebut.getValue(),
                        comboFin.getValue()
                );
                fenetre.close();
            } catch (IllegalArgumentException ex) {
                System.err.println(ex.getMessage());
            }
        });

        root.getChildren().addAll(
                new Label("Ajouter sous-tâche à : " + parent.getTitre()),
                new Label("Titre :"), txtTitre,
                new Label("Description :"), txtDesc,
                new Label("Jour Début :"), comboDebut,
                new Label("Jour Fin :"), comboFin,
                chkComposite,
                btnSave
        );
        fenetre.setScene(new Scene(root, 300, 350));
        fenetre.setTitle("Ajouter une sous-tâche");
        fenetre.show();
    }
}