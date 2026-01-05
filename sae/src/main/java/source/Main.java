package source;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.Optional;

public class Main extends Application {

    // style par défaut des colonnes
    private final String STYLE_COLONNE = "-fx-border-color: lightgray; -fx-border-width: 1; -fx-background-color: #f4f4f4;";
    // style quand on survole une colonne avec une tâche
    private final String STYLE_COLONNE_SURVOL = "-fx-border-color: #4CAF50; -fx-border-width: 2; -fx-background-color: #e8f5e9;";

    private BorderPane rootPrincipal;
    private VueBureau vueBureau;
    private VueListe vueListe;
    private Controller controleur;
    private TacheManager modele;
    private ComboBox<String> comboVue;

    @Override
    public void start(Stage primaryStage) {
        modele = TacheManager.getInstance();

        // crée les vues
        vueBureau = new VueBureau(modele);
        vueListe = new VueListe(modele);

        controleur = new Controller(modele);

        // enregistrer les observateurs
        modele.ajouterObservateur(vueBureau);
        modele.ajouterObservateur(vueListe);

        // crée le conteneur principal avec BorderPane
        rootPrincipal = new BorderPane();

        // crée la barre d'outils en haut
        HBox toolbar = creerToolbar();
        rootPrincipal.setTop(toolbar);

        // afficher la vue Bureau par défaut
        rootPrincipal.setCenter(vueBureau.getRoot());

        // configurer les handlers pour la vue initiale (Bureau)
        configurerHandlersColonnes(vueBureau, controleur);
        vueBureau.actualiser();
        vueListe.actualiser();
        configurerHandlersPourVueActive();

        // observer pour reconfigurer les handlers quand la vue est actualisée
        modele.ajouterObservateur(new Observateur() {
            @Override
            public void actualiser() {
                reconfigurerHandlersPourVueActive();
            }
        });

        Scene scene = new Scene(rootPrincipal, 1000, 700);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Trello - Gestion de Tâches");
        primaryStage.show();
    }

    private HBox creerToolbar() {
        HBox toolbar = new HBox(10);
        toolbar.setPadding(new Insets(10));
        toolbar.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #ddd; -fx-border-width: 0 0 1 0;");

        // btn Nouvelle Tâche
        Button btnCreer = new Button("Nouvelle Tâche");
        btnCreer.setStyle("-fx-font-size: 14px; -fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 8 15;");
        btnCreer.setOnAction(e -> {
            VueFormulaire.afficherFormulaireCreation(controleur);
        });

        // btn Créer Colonne
        Button btnAjoutCol = new Button("Créer Colonne");
        btnAjoutCol.setStyle("-fx-font-size: 14px; -fx-padding: 8 15;");
        btnAjoutCol.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Créer Colonne");
            dialog.setHeaderText(null);
            dialog.setGraphic(null);
            dialog.setContentText("Entrez le nom :");
            String nom = dialog.showAndWait().orElse(null);
            if (nom != null && !nom.trim().isEmpty()) {
                controleur.ajouterColonne(nom);
            }
        });

        // séparateur
        Separator separator1 = new Separator();
        separator1.setOrientation(javafx.geometry.Orientation.VERTICAL);

        // sélecteur de vue
        Label labelVue = new Label("Vue :");
        labelVue.setStyle("-fx-font-weight: bold; -fx-padding: 0 5 0 0;");

        comboVue = new ComboBox<>();
        comboVue.getItems().addAll("Vue Bureau", "Vue Liste par Jour");
        comboVue.setValue("Vue Bureau");
        comboVue.setStyle("-fx-font-size: 14px;");

        comboVue.setOnAction(e -> {
            basculerVue();
        });

        // espaceur pour pousser les éléments à gauche
        Pane espaceur = new Pane();
        HBox.setHgrow(espaceur, Priority.ALWAYS);

        toolbar.getChildren().addAll(btnCreer, btnAjoutCol, separator1, labelVue, comboVue, espaceur);
        return toolbar;
    }

    private void basculerVue() {
        String vueSelectionnee = comboVue.getValue();
        switch (vueSelectionnee) {
            case "Vue Liste par Jour":
                rootPrincipal.setCenter(vueListe.getRoot());
                configurerHandlersCartesListe(vueListe, controleur);
                break;
            case "Vue Bureau":
            default:
                rootPrincipal.setCenter(vueBureau.getRoot());
                configurerHandlersColonnes(vueBureau, controleur);
                configurerHandlersCartes(vueBureau, controleur);
                configurerBoutonsSuppressionColonne(vueBureau, controleur);
                break;
        }
    }

    private void configurerHandlersPourVueActive() {
        if (rootPrincipal.getCenter() == vueBureau.getRoot()) {
            configurerHandlersColonnes(vueBureau, controleur);
            configurerHandlersCartes(vueBureau, controleur);
            configurerBoutonsSuppressionColonne(vueBureau, controleur);
        } else if (rootPrincipal.getCenter() == vueListe.getRoot()) {
            configurerHandlersCartesListe(vueListe, controleur);
        }
    }

    private void reconfigurerHandlersPourVueActive() {
        configurerHandlersPourVueActive();
    }

    private void configurerHandlersCartesListe(VueListe vue, Controller controleur) {
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
        }
    }

    private void setupColonneDrop(VBox colonne, String etatCible, Controller controleur) {
        colonne.setOnDragOver(event -> {
            if (event.getGestureSource() != colonne && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });
        colonne.setOnDragEntered(event -> {
            if (event.getGestureSource() != colonne && event.getDragboard().hasString()) {
                colonne.setStyle(STYLE_COLONNE_SURVOL);
            }
            event.consume();
        });

        colonne.setOnDragExited(event -> {
            colonne.setStyle(STYLE_COLONNE);
            event.consume();
        });

        colonne.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasString()) {
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

                controleur.debuterDeplacement(t);
                Dragboard db = carte.startDragAndDrop(TransferMode.MOVE);

                ClipboardContent content = new ClipboardContent();
                content.putString(t.getTitre());
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