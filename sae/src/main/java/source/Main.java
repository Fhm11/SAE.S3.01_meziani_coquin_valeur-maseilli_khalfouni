package source;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class Main extends Application {

    // style par défaut des colonnes
    private final String STYLE_COLONNE = "-fx-border-color: lightgray; -fx-border-width: 1; -fx-background-color: #f4f4f4;";
    // style quand on survole une colonne avec une tâche
    private final String STYLE_COLONNE_SURVOL = "-fx-border-color: #4CAF50; -fx-border-width: 2; -fx-background-color: #e8f5e9;";
    // style quand on survole une tâche pour parentage
    private final String STYLE_TACHE_SURVOL = "-fx-border-color: #FF9800; -fx-border-width: 2;";
    // style pour les zones d'extraction
    private final String STYLE_ZONE_EXTRACTION_SURVOL = "-fx-background-color: rgba(255, 193, 7, 0.3);";

    private BorderPane rootPrincipal;
    private VueBureau vueBureau;
    private VueListe vueListe;
    private VueGantt vueGantt;
    private VueArchive vueArchive; // AJOUT: Vue Archive
    private Controller controleur;
    private TacheManager modele;
    private ComboBox<String> comboVue;

    @Override
    public void start(Stage primaryStage) {
        modele = TacheManager.getInstance();

        // crée les vues
        vueBureau = new VueBureau(modele);
        vueListe = new VueListe(modele);
        vueGantt = new VueGantt(modele);
        vueArchive = new VueArchive(modele); // AJOUT: Vue Archive

        controleur = new Controller(modele);

        // enregistrer les observateurs
        modele.ajouterObservateur(vueBureau);
        modele.ajouterObservateur(vueListe);
        modele.ajouterObservateur(vueGantt);
        modele.ajouterObservateur(vueArchive); // AJOUT: Observateur Archive

        // crée le conteneur principal avec BorderPane
        rootPrincipal = new BorderPane();

        // crée la barre d'outils en haut
        HBox toolbar = creerToolbar();
        rootPrincipal.setTop(toolbar);

        // afficher la vue Bureau par défaut
        rootPrincipal.setCenter(vueBureau.getRoot());

        // Initialiser les vues
        vueBureau.actualiser();
        vueListe.actualiser();
        vueGantt.actualiser();
        vueArchive.actualiser(); // AJOUT: Initialiser Archive

        // configuration immédiate des handlers pour vueBureau
        configurerHandlersVueBureau();

        // observer pour reconfigurer les handlers quand la vue est actualisée
        modele.ajouterObservateur(new Observateur() {
            @Override
            public void actualiser() {
                // reconfigurer les handlers quand la vue est actualisée
                if (rootPrincipal.getCenter() == vueBureau.getRoot()) {
                    configurerHandlersVueBureau();
                } else if (rootPrincipal.getCenter() == vueListe.getRoot()) {
                    configurerHandlersVueListe();
                } else if (rootPrincipal.getCenter() == vueArchive.getRoot()) {
                    configurerHandlersArchive();
                }
            }
        });

        Scene scene = new Scene(rootPrincipal, 1200, 800);
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
        comboVue.getItems().addAll("Vue Bureau", "Vue Liste par Jour", "Vue Gantt", "Vue Archive"); // AJOUT: Archive
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
                configurerHandlersVueListe();
                break;

            case "Vue Gantt":
                rootPrincipal.setCenter(vueGantt.getRoot());
                vueGantt.actualiser();
                break;

            case "Vue Archive": // AJOUT: Cas Archive
                rootPrincipal.setCenter(vueArchive.getRoot());
                vueArchive.actualiser();
                configurerHandlersArchive();
                break;

            case "Vue Bureau":
            default:
                rootPrincipal.setCenter(vueBureau.getRoot());
                configurerHandlersVueBureau();
                break;
        }
    }

    private void configurerHandlersArchive() {
        for (Button btn : vueArchive.getBoutonsInteractifs()) {
            btn.setOnAction(controleur);
        }
    }

    private void configurerHandlersVueBureau() {
        // configuration des colonnes
        configurerColonnesVueBureau();

        // configuration des boutons
        configurerBoutonsVueBureau();

        // configuration des cartes principales
        configurerCartesPrincipalesVueBureau();

        // configuration des sous-tâches
        configurerSousTachesVueBureau();

        // zones d'extraction en haut des colonnes
        configurerZonesExtractionColonnes();
    }

    private void configurerColonnesVueBureau() {
        for (VBox colonneBox : vueBureau.getColonnesGraphiques()) {
            String nomColonne = (String) colonneBox.getUserData();

            // configurer le drop sur la colonne pour toutes les tâches
            colonneBox.setOnDragOver(event -> {
                if (event.getGestureSource() != colonneBox && event.getDragboard().hasString()) {
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                event.consume();
            });

            colonneBox.setOnDragEntered(event -> {
                if (event.getGestureSource() != colonneBox && event.getDragboard().hasString()) {
                    colonneBox.setStyle(STYLE_COLONNE_SURVOL);
                }
                event.consume();
            });

            colonneBox.setOnDragExited(event -> {
                colonneBox.setStyle(STYLE_COLONNE);
                event.consume();
            });

            // gérer le drop sur une colonne
            final String nomColonneFinal = nomColonne;
            colonneBox.setOnDragDropped(event -> {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasString()) {
                    Tache tache = controleur.getTacheEnDeplacement();
                    if (tache != null) {
                        // Vérifier si c'est une sous-tâche
                        boolean estSousTache = false;
                        for (Tache t : modele.getTaches()) {
                            if (estSousTacheRecursif(t, tache)) {
                                estSousTache = true;
                                break;
                            }
                        }

                        if (estSousTache) {
                            // vérifier que la sous-tâche n'est pas déjà extraite
                            if (!modele.getTaches().contains(tache)) {
                                controleur.extraireVersColonne(tache, nomColonneFinal);
                            } else {
                                // si déjà extraite, juste changer de colonne
                                controleur.finaliserDeplacement(nomColonneFinal);
                            }
                        } else {
                            // c'est une tâche principale : la déplacer normalement
                            controleur.finaliserDeplacement(nomColonneFinal);
                        }
                        success = true;
                    }
                }
                event.setDropCompleted(success);
                event.consume();
            });
        }
    }

    private boolean estSousTacheRecursif(Tache parent, Tache recherche) {
        if (parent == recherche) {
            return false; // une tâche n'est pas sa propre sous-tâche
        }

        if (parent.estComposite()) {
            for (Tache sousTache : parent.getSousTaches()) {
                if (sousTache == recherche) {
                    return true;
                }
                if (estSousTacheRecursif(sousTache, recherche)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void configurerBoutonsVueBureau() {
        for (Button btn : vueBureau.getBoutonsInteractifs()) {
            btn.setOnAction(controleur);

            // boutons de suppression de colonne
            Object data = btn.getUserData();
            if (data instanceof String && btn.getText().equals("X")) {
                String nomColonne = (String) data;
                btn.setOnAction(e -> {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Supprimer colonne");
                    alert.setHeaderText("Supprimer la colonne '" + nomColonne + "' ?");
                    alert.setContentText("Cette action supprimera aussi toutes les tâches de cette colonne.");

                    alert.showAndWait().ifPresent(type -> {
                        if (type == ButtonType.OK) {
                            controleur.supprimerColonne(nomColonne);
                        }
                    });
                });
            }
        }
    }

    private void configurerCartesPrincipalesVueBureau() {
        for (VBox carte : vueBureau.getCartesTaches()) {
            Tache tacheCarte = (Tache) carte.getUserData();

            if (tacheCarte == null) continue;

            // double-clic pour modification
            carte.setOnMouseClicked(e -> {
                if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
                    Tache t = (Tache) carte.getUserData();
                    VueFormulaire.afficherFormulaireModification(t, controleur);
                }
            });

            // drag détecté pour les tâches principales
            carte.setOnDragDetected(event -> {
                Tache t = (Tache) carte.getUserData();
                if (t != null) {
                    controleur.debuterDeplacement(t);
                    Dragboard db = carte.startDragAndDrop(TransferMode.MOVE);
                    ClipboardContent content = new ClipboardContent();
                    content.putString(t.getTitre());
                    db.setContent(content);
                    event.consume();
                }
            });

            // si la carte est composite, configurer le drop pour devenir parent
            if (tacheCarte.estComposite()) {
                carte.setOnDragOver(event -> {
                    if (event.getGestureSource() != carte && event.getDragboard().hasString()) {
                        Tache source = controleur.getTacheEnDeplacement();
                        if (source != null && source != tacheCarte) {
                            event.acceptTransferModes(TransferMode.MOVE);
                        }
                    }
                    event.consume();
                });

                carte.setOnDragEntered(event -> {
                    if (event.getGestureSource() != carte && event.getDragboard().hasString()) {
                        Tache source = controleur.getTacheEnDeplacement();
                        if (source != null && source != tacheCarte) {
                            carte.setStyle(carte.getStyle() + STYLE_TACHE_SURVOL);
                        }
                    }
                    event.consume();
                });

                carte.setOnDragExited(event -> {
                    // Restaurer le style original
                    Object styleOrigine = carte.getProperties().get("style_origine");
                    if (styleOrigine != null) {
                        carte.setStyle((String) styleOrigine);
                    }
                    event.consume();
                });

                // gérer le drop pour devenir sous-tâche
                carte.setOnDragDropped(event -> {
                    Dragboard db = event.getDragboard();
                    boolean success = false;

                    if (db.hasString()) {
                        Tache source = controleur.getTacheEnDeplacement();
                        if (source != null && source != tacheCarte) {
                            // IMPORTANT : Vérifier qu'on ne crée pas de cycle
                            if (!source.contientTache(tacheCarte)) {
                                controleur.devenirSousTacheDe(source, tacheCarte);
                                success = true;
                            }
                        }
                    }

                    event.setDropCompleted(success);
                    event.consume();
                });
            }
        }
    }

    private void configurerSousTachesVueBureau() {
        for (HBox sousTacheBox : vueBureau.getSousTachesBoxes()) {
            Tache sousTache = (Tache) sousTacheBox.getUserData();

            if (sousTache == null) continue;

            // drag pour la sous-tâche (pour la déplacer vers une colonne)
            sousTacheBox.setOnDragDetected(event -> {
                controleur.debuterDeplacement(sousTache);
                Dragboard db = sousTacheBox.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putString("SOUS_TACHE:" + sousTache.getTitre());
                db.setContent(content);
                event.consume();
            });

            // double-clic sur la sous-tâche
            sousTacheBox.setOnMouseClicked(e -> {
                if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
                    VueFormulaire.afficherFormulaireModification(sousTache, controleur);
                    e.consume();
                }
            });

            // si la sous-tâche est composite, configurer le drop pour devenir parent
            if (sousTache.estComposite()) {
                sousTacheBox.setOnDragOver(event -> {
                    if (event.getGestureSource() != sousTacheBox && event.getDragboard().hasString()) {
                        Tache source = controleur.getTacheEnDeplacement();
                        if (source != null && source != sousTache) {
                            event.acceptTransferModes(TransferMode.MOVE);
                        }
                    }
                    event.consume();
                });

                sousTacheBox.setOnDragEntered(event -> {
                    if (event.getGestureSource() != sousTacheBox && event.getDragboard().hasString()) {
                        Tache source = controleur.getTacheEnDeplacement();
                        if (source != null && source != sousTache) {
                            sousTacheBox.setStyle(sousTacheBox.getStyle() + STYLE_TACHE_SURVOL);
                        }
                    }
                    event.consume();
                });

                sousTacheBox.setOnDragExited(event -> {
                    // Restaurer le style original
                    Object styleOrigine = sousTacheBox.getProperties().get("style_origine");
                    if (styleOrigine != null) {
                        sousTacheBox.setStyle((String) styleOrigine);
                    }
                    event.consume();
                });

                // gérer le drop pour devenir sous-tâche d'une autre sous-tâche composite
                sousTacheBox.setOnDragDropped(event -> {
                    Dragboard db = event.getDragboard();
                    boolean success = false;

                    if (db.hasString()) {
                        Tache source = controleur.getTacheEnDeplacement();
                        if (source != null && source != sousTache) {
                            // IMPORTANT : Vérifier qu'on ne crée pas de cycle
                            if (!source.contientTache(sousTache)) {
                                controleur.devenirSousTacheDe(source, sousTache);
                                success = true;
                            }
                        }
                    }

                    event.setDropCompleted(success);
                    event.consume();
                });
            }
        }
    }

    private void configurerZonesExtractionColonnes() {
        for (VBox colonne : vueBureau.getColonnesGraphiques()) {
            String etatColonne = (String) colonne.getUserData();

            // Créer une zone de drop en haut de la colonne
            Pane zoneDropExtraction = new Pane();
            zoneDropExtraction.setPrefHeight(15);
            zoneDropExtraction.setStyle("-fx-background-color: transparent;");

            // Configurer le drop
            zoneDropExtraction.setOnDragOver(event -> {
                if (event.getDragboard().hasString()) {
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                event.consume();
            });

            zoneDropExtraction.setOnDragEntered(event -> {
                zoneDropExtraction.setStyle(STYLE_ZONE_EXTRACTION_SURVOL);
            });

            zoneDropExtraction.setOnDragExited(event -> {
                zoneDropExtraction.setStyle("-fx-background-color: transparent;");
            });

            final String etatFinal = etatColonne;
            zoneDropExtraction.setOnDragDropped(event -> {
                Dragboard db = event.getDragboard();
                boolean success = false;

                if (db.hasString()) {
                    Tache source = controleur.getTacheEnDeplacement();
                    if (source != null) {
                        // Extraire et mettre dans cette colonne
                        controleur.extraireVersColonne(source, etatFinal);
                        success = true;
                    }
                }

                event.setDropCompleted(success);
                event.consume();
            });

            // insérer après l'en-tête (position 1)
            if (colonne.getChildren().size() > 1) {
                colonne.getChildren().add(1, zoneDropExtraction);
            } else {
                colonne.getChildren().add(zoneDropExtraction);
            }
        }
    }

    private void configurerHandlersVueListe() {
        // configuration des boutons
        for (Button btn : vueListe.getBoutonsInteractifs()) {
            btn.setOnAction(controleur);
        }

        // configuration des cartes
        for (VBox carte : vueListe.getCartesTaches()) {
            Tache tacheCarte = (Tache) carte.getUserData();

            // double-clic pour modification
            carte.setOnMouseClicked(e -> {
                if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
                    Tache t = (Tache) carte.getUserData();
                    VueFormulaire.afficherFormulaireModification(t, controleur);
                }
            });

            // drag détecté
            carte.setOnDragDetected(event -> {
                Tache t = (Tache) carte.getUserData();
                if (t != null) {
                    controleur.debuterDeplacement(t);
                    Dragboard db = carte.startDragAndDrop(TransferMode.MOVE);
                    ClipboardContent content = new ClipboardContent();
                    content.putString(t.getTitre());
                    db.setContent(content);
                    event.consume();
                }
            });

            // si la carte est composite, configurer le drop
            if (tacheCarte.estComposite()) {
                carte.setOnDragOver(event -> {
                    if (event.getGestureSource() != carte && event.getDragboard().hasString()) {
                        Tache source = controleur.getTacheEnDeplacement();
                        if (source != null && source != tacheCarte) {
                            event.acceptTransferModes(TransferMode.MOVE);
                        }
                    }
                    event.consume();
                });

                carte.setOnDragEntered(event -> {
                    if (event.getGestureSource() != carte && event.getDragboard().hasString()) {
                        Tache source = controleur.getTacheEnDeplacement();
                        if (source != null && source != tacheCarte) {
                            carte.setStyle(carte.getStyle() + STYLE_TACHE_SURVOL);
                        }
                    }
                    event.consume();
                });

                carte.setOnDragExited(event -> {
                    // restaurer le style original
                    Object styleOrigine = carte.getProperties().get("style_origine");
                    if (styleOrigine != null) {
                        carte.setStyle((String) styleOrigine);
                    }
                    event.consume();
                });

                carte.setOnDragDropped(event -> {
                    Dragboard db = event.getDragboard();
                    boolean success = false;

                    if (db.hasString()) {
                        Tache source = controleur.getTacheEnDeplacement();
                        if (source != null && source != tacheCarte) {
                            // Vérifier qu'on ne crée pas de cycle
                            if (!source.contientTache(tacheCarte)) {
                                controleur.devenirSousTacheDe(source, tacheCarte);
                                success = true;
                            }
                        }
                    }

                    event.setDropCompleted(success);
                    event.consume();
                });
            }
        }

        // configuration des zones d'extraction entre les jours
        configurerZonesExtractionListe();
    }

    private void configurerZonesExtractionListe() {
        VBox contenuPrincipal = vueListe.getContenuPrincipal();

        // pour chaque section de jour
        for (javafx.scene.Node node : contenuPrincipal.getChildren()) {
            if (node instanceof VBox) {
                VBox section = (VBox) node;

                // trouver le label du jour
                String jour = null;
                for (javafx.scene.Node child : section.getChildren()) {
                    if (child instanceof Label) {
                        Label label = (Label) child;
                        String texte = label.getText();
                        if (texte.matches("Lundi|Mardi|Mercredi|Jeudi|Vendredi|Samedi|Dimanche")) {
                            jour = texte;
                            break;
                        }
                    }
                }

                if (jour == null) continue;

                // créer une copie finale pour la lambda
                final String jourFinal = jour;

                // créer une zone de drop
                Pane zoneDrop = new Pane();
                zoneDrop.setPrefHeight(20);
                zoneDrop.setStyle("-fx-background-color: transparent;");

                zoneDrop.setOnDragOver(event -> {
                    if (event.getDragboard().hasString()) {
                        event.acceptTransferModes(TransferMode.MOVE);
                    }
                    event.consume();
                });

                zoneDrop.setOnDragEntered(event -> {
                    zoneDrop.setStyle("-fx-background-color: rgba(76, 175, 80, 0.3);");
                });

                zoneDrop.setOnDragExited(event -> {
                    zoneDrop.setStyle("-fx-background-color: transparent;");
                });

                zoneDrop.setOnDragDropped(event -> {
                    Dragboard db = event.getDragboard();
                    boolean success = false;

                    if (db.hasString()) {
                        Tache source = controleur.getTacheEnDeplacement();
                        if (source != null) {
                            // Vérifier si c'est une sous-tâche
                            boolean estSousTache = false;
                            for (Tache t : modele.getTaches()) {
                                if (estSousTacheRecursif(t, source)) {
                                    estSousTache = true;
                                    break;
                                }
                            }

                            if (estSousTache) {
                                // Extraire la sous-tâche vers ce jour
                                controleur.extraireVersJour(source, jourFinal);
                            } else {
                                // Déplacer la tâche principale vers ce jour
                                controleur.deplacerTacheVersJour(source, jourFinal);
                            }
                            success = true;
                        }
                    }

                    event.setDropCompleted(success);
                    event.consume();
                });

                // insérer après le titre
                if (section.getChildren().size() >= 1) {
                    section.getChildren().add(1, zoneDrop);
                }
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}