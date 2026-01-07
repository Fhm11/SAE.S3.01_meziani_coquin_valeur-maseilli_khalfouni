package source;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Modality;

public class VueFormulaire {

    /**
     * Affiche une alerte d'erreur
     * @param message le message d'erreur
     * @param titre le titre de la fenêtre
     */
    public static void afficherAlerteErreur(String message, String titre) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.showAndWait();
    }

    /**
     * Affiche une alerte d'information
     * @param message le message
     * @param titre le titre de la fenêtre
     */
    public static void afficherAlerteInformation(String message, String titre) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.showAndWait();
    }

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

        javafx.collections.ObservableList<String> jours = javafx.collections.FXCollections.observableArrayList(
                "Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche");
        ComboBox<String> comboDebut = new ComboBox<>(jours);
        comboDebut.setValue("Lundi");
        ComboBox<String> comboFin = new ComboBox<>(jours);
        comboFin.setValue("Lundi");

        javafx.collections.ObservableList<String> priorites = javafx.collections.FXCollections
                .observableArrayList("Basse", "Moyenne", "Importante");
        ComboBox<String> comboPriorite = new ComboBox<>(priorites);
        comboPriorite.setValue("Moyenne");

        CheckBox chkComposite = new CheckBox("Peut avoir des sous-tâches");
        Button btnSave = new Button("Sauvegarder");

        btnSave.setOnAction(e -> {
            try {
                controleur.creerTache(
                        txtTitre.getText(),
                        txtDesc.getText(),
                        chkComposite.isSelected(),
                        comboDebut.getValue(),
                        comboFin.getValue(),
                        comboPriorite.getValue());
                fenetre.close();
            } catch (IllegalArgumentException ex) {
                VueFormulaire.afficherAlerteErreur(ex.getMessage(), "Erreur de création");
                System.err.println(ex.getMessage());
            }
        });

        root.getChildren().addAll(
                new Label("Titre :"), txtTitre,
                new Label("Description :"), txtDesc,
                new Label("Jour Début :"), comboDebut,
                new Label("Jour Fin :"), comboFin,
                new Label("Priorite :"), comboPriorite,
                chkComposite, btnSave);
        fenetre.setScene(new Scene(root, 450, 500));
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

        javafx.collections.ObservableList<String> optionsPriorite = javafx.collections.FXCollections
                .observableArrayList("Basse", "Moyenne", "Importante");
        ComboBox<String> comboPriorite = new ComboBox<>(optionsPriorite);
        comboPriorite.setValue(t.getPriorite());

        Button btnSave = new Button("Sauvegarder");

        btnSave.setOnAction(e -> {
            try {
                controleur.modifierTache(t, txtTitre.getText(), txtDesc.getText(), comboPriorite.getValue());
                fenetre.close();
            } catch (IllegalArgumentException ex) {
                VueFormulaire.afficherAlerteErreur(ex.getMessage(), "Erreur de modification");
                System.err.println(ex.getMessage());
            }
        });

        root.getChildren().addAll(
                new Label("Titre :"), txtTitre,
                new Label("Description :"), txtDesc,
                new Label("Priorite :"), comboPriorite,
                btnSave);
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

        javafx.collections.ObservableList<String> jours = javafx.collections.FXCollections.observableArrayList(
                "Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche");
        ComboBox<String> comboDebut = new ComboBox<>(jours);
        comboDebut.setValue("Lundi");
        ComboBox<String> comboFin = new ComboBox<>(jours);
        comboFin.setValue("Lundi");
        CheckBox chkComposite = new CheckBox("Peut avoir une sous-tâche");
        Button btnSave = new Button("Ajouter");

        ComboBox<String> comboPriorite = new ComboBox<>(
                FXCollections.observableArrayList("Basse", "Moyenne", "Importante"));
        comboPriorite.setValue("Moyenne");

        btnSave.setOnAction(e -> {
            try {
                controleur.ajouterSousTache(
                        parent,
                        txtTitre.getText(),
                        txtDesc.getText(),
                        chkComposite.isSelected(),
                        comboDebut.getValue(),
                        comboFin.getValue(),
                        comboPriorite.getValue());
                fenetre.close();
            } catch (IllegalArgumentException ex) {
                VueFormulaire.afficherAlerteErreur(ex.getMessage(), "Erreur d'ajout de sous-tâche");
                System.err.println(ex.getMessage());
            }
        });

        root.getChildren().addAll(
                new Label("Ajouter sous-tâche à : " + parent.getTitre()),
                new Label("Titre :"), txtTitre,
                new Label("Description :"), txtDesc,
                new Label("Jour Début :"), comboDebut,
                new Label("Jour Fin :"), comboFin,
                new Label("Priorite :"), comboPriorite,
                chkComposite,
                btnSave);
        fenetre.setScene(new Scene(root, 500, 500));
        fenetre.setTitle("Ajouter une sous-tâche");
        fenetre.show();
    }
}