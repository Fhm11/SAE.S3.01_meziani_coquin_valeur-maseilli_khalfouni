package source;

import javafx.geometry.Pos;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class  VueBureau implements Observateur {
    private VBox root;

    private VBox colonneAFaire;
    private VBox colonneEnCours;
    private VBox colonneTermine;


    public VueBureau() {
        root = new VBox(10);
        root.setPadding(new Insets(10));

        colonneAFaire = creerColonne("afaire");
        colonneEnCours = creerColonne("encours");
        colonneTermine = creerColonne("terminée");

        HBox conteneurColonnes = new HBox(15);
        conteneurColonnes.getChildren().addAll(colonneAFaire, colonneEnCours, colonneTermine);

        Button btnCreer = new Button("Nouvelle Tâche");
        btnCreer.setStyle("-fx-font-size: 14px; -fx-base: #4CAF50;");
        btnCreer.setOnAction(e -> VueFormulaire.afficherFormulaireCreation());

        HBox.setHgrow(colonneAFaire, Priority.ALWAYS);
        HBox.setHgrow(colonneEnCours, Priority.ALWAYS);
        HBox.setHgrow(colonneTermine, Priority.ALWAYS);
        VBox.setVgrow(conteneurColonnes, Priority.ALWAYS);

        root.getChildren().addAll(btnCreer, conteneurColonnes);
    }

    public VBox getRoot() {
        return root;
    }

    @Override
    public void actualiser() {
        nettoyerColonne(colonneAFaire);
        nettoyerColonne(colonneEnCours);
        nettoyerColonne(colonneTermine);

        for (Tache t : TacheManager.getInstance().getTaches()) {
            VBox carte = creerAffichageTache(t);
            String etat = t.getEtat();

            if ("afaire".equals(etat)) {
                colonneAFaire.getChildren().add(carte);
            } else if ("encours".equals(etat)) {
                colonneEnCours.getChildren().add(carte);
            } else if ("terminer".equals(etat)) {
                colonneTermine.getChildren().add(carte);
            }
        }
    }

    private void nettoyerColonne(VBox col) {
        if (col.getChildren().size() > 1) {
            col.getChildren().remove(1, col.getChildren().size());
        }
    }


    private VBox creerAffichageTache(Tache t) {
        VBox conteneur = new VBox(5);
        conteneur.setPadding(new Insets(10));
        conteneur.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 1); -fx-background-radius: 5;");

        Label labelTitre = new Label(t.getTitre());
        labelTitre.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");

        Label labelDesc = new Label(t.getDescription());
        labelDesc.setStyle("-fx-text-fill: #555555; -fx-font-size: 11px;");
        labelDesc.setWrapText(true);

        conteneur.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
                VueFormulaire.afficherFormulaireModification(t);
            }
        });

        HBox boutons = new HBox(5);
        boutons.setAlignment(Pos.CENTER_RIGHT);

        if (t.estComposite()) {
            Button btnAjouterSous = new Button("+ Sous-tâche");
            btnAjouterSous.setOnAction(e ->
                    VueFormulaire.afficherFormulaireSousTache(t));
            boutons.getChildren().add(btnAjouterSous);
        }

        Button btnSupprimer = new Button("Supprimer");
        btnSupprimer.setOnAction(e -> Controller.supprimerTache(t));
        boutons.getChildren().add(btnSupprimer);

        conteneur.getChildren().addAll(labelTitre, labelDesc, boutons);

        if (t.estComposite()) {
            for(Tache sub : t.getSousTaches()) {
                VBox boxSousTache = new VBox(2);
                boxSousTache.setPadding(new Insets(0, 0, 0, 10));

                Label lTitre = new Label(" > " + sub.getTitre());
                lTitre.setStyle("-fx-text-fill: #333333; -fx-font-size: 10px; -fx-font-weight: bold;");

                Label lDesc = new Label("   " + sub.getDescription());
                lDesc.setStyle("-fx-text-fill: gray; -fx-font-size: 9px;");
                lDesc.setWrapText(true);

                boxSousTache.getChildren().addAll(lTitre, lDesc);
                conteneur.getChildren().add(boxSousTache);
            }
        }
        return conteneur;
    }

    private VBox creerColonne(String titre) {
        VBox col = new VBox(10);
        col.setPadding(new Insets(10));
        col.setStyle("-fx-border-color: lightgray; -fx-border-width: 1; -fx-background-color: #f4f4f4;");
        Label lblTitre = new Label(titre);
        lblTitre.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        col.getChildren().add(lblTitre);
        return col;
    }
}