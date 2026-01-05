package source;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Optional;

public class Main extends Application {

    // style par défaut des colonnes
    private final String STYLE_COLONNE = "-fx-border-color: lightgray; -fx-border-width: 1; -fx-background-color: #f4f4f4;";
    // style quand on survole une colonne avec une tâche
    private final String STYLE_COLONNE_SURVOL = "-fx-border-color: #4CAF50; -fx-border-width: 2; -fx-background-color: #e8f5e9;";

    @Override
    public void start(Stage primaryStage) {
        TacheManager modele = TacheManager.getInstance();

        VueBureau vue = new VueBureau(modele);

        Controller controleur = new Controller(modele);

        modele.ajouterObservateur(vue);
        HBox barreOutils = new HBox(10);

        Button btnCreer = new Button("Nouvelle Tâche");
        btnCreer.setStyle("-fx-font-size: 14px; -fx-base: #4CAF50;");

        btnCreer.setOnAction(e -> {
            VueFormulaire.afficherFormulaireCreation(controleur);
        });

        Button btnAjoutCol = new Button("cree colonne");
        btnAjoutCol.setStyle("-fx-font-size: 14px;");
        btnAjoutCol.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("cree colonne");
            dialog.setHeaderText(null);
            dialog.setGraphic(null);
            dialog.setContentText("Entrez le nom :");
            String nom = dialog.showAndWait().orElse(null);
            if (nom != null && !nom.trim().isEmpty()) {
                controleur.ajouterColonne(nom);
            }
        });

        barreOutils.getChildren().addAll(btnCreer, btnAjoutCol);

        vue.getRoot().getChildren().add(0, barreOutils);

        modele.ajouterObservateur(new Observateur() {
            @Override
            public void actualiser() {
                configurerHandlersColonnes(vue, controleur);
                // reconfigurer les handlers quand la vue est actualisée
                configurerHandlersCartes(vue, controleur);
                configurerBoutonsSuppressionColonne(vue, controleur);
            }
        });
        vue.actualiser();
        configurerHandlersColonnes(vue, controleur);
        configurerHandlersCartes(vue, controleur);
        configurerBoutonsSuppressionColonne(vue, controleur);

        Scene scene = new Scene(vue.getRoot(), 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Trello");
        primaryStage.show();
    }

    private void setupColonneDrop(VBox colonne, String etatCible, Controller controleur) {
        // accepter le drag si ça vient d'ailleurs
        colonne.setOnDragOver(event -> {
            if (event.getGestureSource() != colonne && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });
        // changement visuel quand on entre
        colonne.setOnDragEntered(event -> {
            if (event.getGestureSource() != colonne && event.getDragboard().hasString()) {
                colonne.setStyle(STYLE_COLONNE_SURVOL);
            }
            event.consume();
        });

        // restaurer le style quand on sort
        colonne.setOnDragExited(event -> {
            colonne.setStyle(STYLE_COLONNE);
            event.consume();
        });

        // gérer le lâcher (Drop)
        colonne.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasString()) {
                // le contrôleur a gardé la référence de la tâche
                controleur.finaliserDeplacement(etatCible);
                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }
    private void configurerHandlersColonnes(VueBureau vue, Controller controleur) {
        for (VBox colonneBox : vue.getColonnesGraphiques()) {
            String nomColonne = (String) colonneBox.getUserData();
            setupColonneDrop(colonneBox, nomColonne, controleur);
        }
    }
    private void configurerHandlersCartes(VueBureau vue, Controller controleur) {
        // pour chaque bouton stocké par VueBureau
        for (Button btn : vue.getBoutonsInteractifs()) {
            btn.setOnAction(controleur);
        }

        // Pour chaque  carte stockée par VueBureau
        for (VBox carte : vue.getCartesTaches()) {
            carte.setOnMouseClicked(e -> {
                if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
                    Tache t = (Tache) carte.getUserData();
                    VueFormulaire.afficherFormulaireModification(t, controleur);
                }
            });
            carte.setOnDragDetected(event -> {
                Tache t = (Tache) carte.getUserData();

                // informer le contrôleur de la tâche qu'on déplace
                controleur.debuterDeplacement(t);
                // démarrer le drag&drop
                Dragboard db = carte.startDragAndDrop(TransferMode.MOVE);

                // mettre un contenu (obligatoire pour que le d&d fonctionne)
                ClipboardContent content = new ClipboardContent();
                content.putString(t.getTitre()); // on met juste le titre comme info texte
                db.setContent(content);

                event.consume();
            });
        }
    }

    private void configurerBoutonsSuppressionColonne(VueBureau vue, Controller controleur) {
        for (Button btn : vue.getBoutonsInteractifs()) {
            Object data = btn.getUserData();
            if (data instanceof String) {
                String nomColonne = (String) data;
                btn.setOnAction(e -> controleur.supprimerColonne(nomColonne));
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}