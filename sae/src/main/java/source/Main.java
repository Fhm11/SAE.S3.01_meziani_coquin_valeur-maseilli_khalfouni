package source;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

    // Style par défaut des colonnes (pour le rétablir après le survol)
    private final String STYLE_COLONNE = "-fx-border-color: lightgray; -fx-border-width: 1; -fx-background-color: #f4f4f4;";
    // Style quand on survole une colonne avec une tâche
    private final String STYLE_COLONNE_SURVOL = "-fx-border-color: #4CAF50; -fx-border-width: 2; -fx-background-color: #e8f5e9;";

    @Override
    public void start(Stage primaryStage) {
        TacheManager modele = TacheManager.getInstance();

        VueBureau vue = new VueBureau(modele);

        Controller controleur = new Controller(modele);

        modele.ajouterObservateur(vue);

        Button btnCreer = new Button("Nouvelle Tâche");
        btnCreer.setStyle("-fx-font-size: 14px; -fx-base: #4CAF50;");

        btnCreer.setOnAction(e -> {
            VueFormulaire.afficherFormulaireCreation(controleur);
        });

        vue.getRoot().getChildren().add(0, btnCreer);

        modele.ajouterObservateur(new Observateur() {
            @Override
            public void actualiser() {
                // reconfigurer les handlers quand la vue est actualisée
                configurerHandlersCartes(vue, controleur);
            }
        });
        configurerHandlersColonnes(vue, controleur);
        vue.actualiser();
        configurerHandlersCartes(vue, controleur);

        Scene scene = new Scene(vue.getRoot(), 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Trello");
        primaryStage.show();
    }

    private void setupColonneDrop(VBox colonne, String etatCible, Controller controleur) {
        // Accepter le Drag si ça vient d'ailleurs
        colonne.setOnDragOver(event -> {
            if (event.getGestureSource() != colonne && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });
        // Changement visuel quand on entre
        colonne.setOnDragEntered(event -> {
            if (event.getGestureSource() != colonne && event.getDragboard().hasString()) {
                colonne.setStyle(STYLE_COLONNE_SURVOL);
            }
            event.consume();
        });

        // Restaurer le style quand on sort
        colonne.setOnDragExited(event -> {
            colonne.setStyle(STYLE_COLONNE);
            event.consume();
        });

        // Gérer le lâcher (Drop)
        colonne.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasString()) {
                // Le contrôleur a gardé la référence de la tâche
                controleur.finaliserDeplacement(etatCible);
                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }
    private void configurerHandlersColonnes(VueBureau vue, Controller controleur) {
        setupColonneDrop(vue.getColonneAFaire(), "afaire", controleur);
        setupColonneDrop(vue.getColonneEnCours(), "encours", controleur);
        setupColonneDrop(vue.getColonneTermine(), "terminer", controleur);
    }
    private void configurerHandlersCartes(VueBureau vue, Controller controleur) {
        for (Button btn : vue.getBoutonsInteractifs()) {
            btn.setOnAction(controleur);
        }

        for (VBox carte : vue.getCartesTaches()) {
            carte.setOnMouseClicked(e -> {
                if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
                    Tache t = (Tache) carte.getUserData();
                    VueFormulaire.afficherFormulaireModification(t, controleur);
                }
            });
            carte.setOnDragDetected(event -> {
                Tache t = (Tache) carte.getUserData();

                // Informer le contrôleur de la tâche qu'on déplace
                controleur.debuterDeplacement(t);

                // Démarrer le Drag and Drop
                Dragboard db = carte.startDragAndDrop(TransferMode.MOVE);

                // Mettre un contenu (obligatoire pour que le DnD fonctionne)
                ClipboardContent content = new ClipboardContent();
                content.putString(t.getTitre()); // On met juste le titre comme info texte
                db.setContent(content);

                event.consume();
            });
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}