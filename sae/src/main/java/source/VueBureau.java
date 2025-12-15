package source;

import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.Insets;

public class VueBureau implements Observateur {
    private VBox root;

    public VueBureau() {
        root = new VBox(10);
        root.setPadding(new Insets(10));
    }

    public VBox getRoot() {
        return root;
    }

    @Override
    public void actualiser() {
        root.getChildren().clear();

        // bouton pour créer une tâche
        Button btnCreer = new Button("Créer une tâche");
        btnCreer.setOnAction(e -> TacheManager.getInstance().afficherFormulaireCreation());
        root.getChildren().add(btnCreer);

        // afficher les tâches
        for (Tache t : TacheManager.getInstance().getTaches()) {
            root.getChildren().add(creerAffichageTache(t));
        }
    }

    private VBox creerAffichageTache(Tache t) {
        VBox conteneur = new VBox(5);
        conteneur.setPadding(new Insets(10));
        conteneur.setStyle("-fx-background-color: lightblue; -fx-background-radius: 5;");

        Label label = new Label(t.getTitre() + "\n" + t.getDescription());

        // double-clic pour modifier
        conteneur.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
                TacheManager.getInstance().afficherFormulaireModification(t);
            }
        });

        HBox boutons = new HBox(5);

        if (t.estComposite()) {
            Button btnAjouterSous = new Button("+ Sous-tâche");
            btnAjouterSous.setOnAction(e ->
                    TacheManager.getInstance().afficherFormulaireSousTache(t));
            boutons.getChildren().add(btnAjouterSous);
        }

        Button btnSupprimer = new Button("Supprimer");
        btnSupprimer.setOnAction(e -> Controller.supprimerTache(t));
        boutons.getChildren().add(btnSupprimer);

        conteneur.getChildren().addAll(label, boutons);

        // afficher les sous-tâches si composite
        if (t.estComposite()) {
            afficherSousTaches(conteneur, (TacheComposite) t);
        }

        return conteneur;
    }

    private void afficherSousTaches(VBox parentContainer, TacheComposite composite) {
        VBox sousContainer = new VBox(5);
        sousContainer.setPadding(new Insets(5, 0, 0, 20));

        for (Tache sousTache : composite.getSousTaches()) {
            HBox sousBox = new HBox(5);
            sousBox.setPadding(new Insets(5));
            sousBox.setStyle("-fx-background-color: #e6f3ff; -fx-background-radius: 3;");

            Label sousLabel = new Label("  | " + sousTache.getTitre() +
                    "\n      " + sousTache.getDescription());

            sousBox.setOnMouseClicked(e -> {
                if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
                    TacheManager.getInstance().afficherFormulaireModification(sousTache);
                }
            });

            sousBox.getChildren().add(sousLabel);
            sousContainer.getChildren().add(sousBox);
        }

        parentContainer.getChildren().add(sousContainer);
    }
}