package source;

import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import java.util.*;

/**
 * Contrôleur spécialisé pour la vue Bureau
 */
public class ControllerBureau {

    private Controller controleur;
    private GestionnaireDragDrop gestionnaireDragDrop;

    // Constantes de style
    private static final String STYLE_COLONNE = "-fx-border-color: lightgray; -fx-border-width: 1; -fx-background-color: #f4f4f4;";
    private static final String STYLE_COLONNE_SURVOL = "-fx-border-color: #4CAF50; -fx-border-width: 2; -fx-background-color: #e8f5e9;";
    private static final String STYLE_ZONE_EXTRACTION_SURVOL = "-fx-background-color: rgba(255, 193, 7, 0.3);";

    public ControllerBureau(Controller controleur) {
        this.controleur = controleur;
        this.gestionnaireDragDrop = new GestionnaireDragDrop(controleur);
    }

    /**
     * Configure la vue Bureau
     */
    public void configurerVue(VueBureau vue) {
        // Nettoie les anciens handlers
        gestionnaireDragDrop.nettoyer();

        // Configure les boutons
        configurerBoutons(vue);

        // Configure le drag & drop pour les colonnes
        configurerColonnes(vue);

        // Configure les cartes de tâches
        configurerCartes(vue);

        // Configure les sous-tâches
        configurerSousTaches(vue);

        // Configure les zones d'extraction
        configurerZonesExtraction(vue);
    }

    /**
     * Configure les boutons de la vue Bureau
     */
    private void configurerBoutons(VueBureau vue) {
        for (Button bouton : vue.getBoutonsInteractifs()) {
            Object donnees = bouton.getUserData();

            // Bouton de suppression de colonne
            if (donnees instanceof String && bouton.getText().equals("X")) {
                String nomColonne = (String) donnees;
                bouton.setOnAction(e -> afficherConfirmationSuppressionColonne(nomColonne));
            }

            // Boutons normaux (Archiver, +)
            else if (donnees instanceof Tache) {
                Tache tache = (Tache) donnees;
                String texteBouton = bouton.getText();

                if ("Archiver".equals(texteBouton)) {
                    // CORRECTION : Utiliser handle() au lieu de finaliserDeplacement
                    bouton.setOnAction(e -> controleur.handle(e));
                } else if ("+".equals(texteBouton)) {
                    bouton.setOnAction(e -> VueFormulaire.afficherFormulaireSousTache(tache, controleur));
                }
            }
        }
    }

    /**
     * Configure les colonnes pour le drag & drop
     */
    private void configurerColonnes(VueBureau vue) {
        for (VBox colonne : vue.getColonnesGraphiques()) {
            String nomColonne = (String) colonne.getUserData();

            colonne.setOnDragOver(evenement -> {
                if (evenement.getGestureSource() != colonne &&
                        evenement.getDragboard().hasString()) {
                    evenement.acceptTransferModes(TransferMode.MOVE);
                }
                evenement.consume();
            });

            colonne.setOnDragEntered(evenement -> {
                if (evenement.getGestureSource() != colonne &&
                        evenement.getDragboard().hasString()) {
                    colonne.setStyle(STYLE_COLONNE_SURVOL);
                }
                evenement.consume();
            });

            colonne.setOnDragExited(evenement -> {
                colonne.setStyle(STYLE_COLONNE);
                evenement.consume();
            });

            final String nomColonneFinal = nomColonne;
            colonne.setOnDragDropped(evenement -> {
                Dragboard dragboard = evenement.getDragboard();
                boolean succes = false;

                if (dragboard.hasString()) {
                    Tache tache = controleur.getTacheEnDeplacement();
                    if (tache != null) {
                        // Vérifie si c'est une sous-tâche
                        if (estSousTache(tache)) {
                            // Pour les sous-tâches, on utilise extraireVersColonne
                            controleur.extraireVersColonne(tache, nomColonneFinal);
                        } else {
                            // Pour les tâches principales, on utilise finaliserDeplacement
                            controleur.finaliserDeplacement(nomColonneFinal);
                        }
                        succes = true;
                    }
                }
                evenement.setDropCompleted(succes);
                evenement.consume();
            });

            // Enregistre la colonne dans le gestionnaire drag drop
            gestionnaireDragDrop.enregistrerColonne(colonne);
        }
    }

    /**
     * Vérifie si une tâche est une sous-tâche
     */
    private boolean estSousTache(Tache tache) {
        TacheManager modele = TacheManager.getInstance();

        // Vérifie si la tâche est dans la liste principale
        if (modele.getTaches().contains(tache)) {
            return false; // C'est une tâche principale
        }

        // Sinon, c'est probablement une sous-tâche
        return true;
    }

    /**
     * Configure les cartes de tâches
     */
    private void configurerCartes(VueBureau vue) {
        for (VBox carte : vue.getCartesTaches()) {
            Tache tache = (Tache) carte.getUserData();
            if (tache == null) continue;

            // Double-clic pour modification
            carte.setOnMouseClicked(evenement -> {
                if (evenement.getButton() == MouseButton.PRIMARY &&
                        evenement.getClickCount() == 2) {
                    VueFormulaire.afficherFormulaireModification(tache, controleur);
                }
            });

            carte.setOnDragDetected(evenement -> {
                controleur.debuterDeplacement(tache);
                Dragboard dragboard = carte.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent contenu = new ClipboardContent();
                contenu.putString(tache.getTitre());
                dragboard.setContent(contenu);
                evenement.consume();
            });

            gestionnaireDragDrop.enregistrerCarte(carte, tache);
        }
    }

    /**
     * Configure les sous-tâches
     */
    private void configurerSousTaches(VueBureau vue) {
        for (HBox boiteSousTache : vue.getSousTachesBoxes()) {
            Tache sousTache = (Tache) boiteSousTache.getUserData();
            if (sousTache == null) continue;

            boiteSousTache.setOnMouseClicked(evenement -> {
                if (evenement.getButton() == MouseButton.PRIMARY &&
                        evenement.getClickCount() == 2) {
                    VueFormulaire.afficherFormulaireModification(sousTache, controleur);
                }
            });

            boiteSousTache.setOnDragDetected(evenement -> {
                controleur.debuterDeplacement(sousTache);
                Dragboard dragboard = boiteSousTache.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent contenu = new ClipboardContent();
                contenu.putString("SOUS_TACHE:" + sousTache.getTitre());
                dragboard.setContent(contenu);
                evenement.consume();
            });

            gestionnaireDragDrop.enregistrerSousTache(boiteSousTache, sousTache);
        }
    }

    /**
     * Configure les zones d'extraction
     */
    private void configurerZonesExtraction(VueBureau vue) {
        for (VBox colonne : vue.getColonnesGraphiques()) {
            String etatColonne = (String) colonne.getUserData();

            // Crée une zone de drop transparente en haut
            Pane zoneDrop = new Pane();
            zoneDrop.setPrefHeight(15);
            zoneDrop.setStyle("-fx-background-color: transparent;");

            final String etatFinal = etatColonne;

            // Configure la zone d'extraction
            zoneDrop.setOnDragOver(evenement -> {
                if (evenement.getDragboard().hasString()) {
                    evenement.acceptTransferModes(TransferMode.MOVE);
                }
                evenement.consume();
            });

            zoneDrop.setOnDragEntered(evenement -> {
                zoneDrop.setStyle(STYLE_ZONE_EXTRACTION_SURVOL);
            });

            zoneDrop.setOnDragExited(evenement -> {
                zoneDrop.setStyle("-fx-background-color: transparent;");
            });

            zoneDrop.setOnDragDropped(evenement -> {
                Dragboard dragboard = evenement.getDragboard();
                boolean succes = false;

                if (dragboard.hasString()) {
                    Tache source = controleur.getTacheEnDeplacement();
                    if (source != null) {
                        controleur.extraireVersColonne(source, etatFinal);
                        succes = true;
                    }
                }

                evenement.setDropCompleted(succes);
                evenement.consume();
            });

            // Insère la zone après l'en-tête
            if (colonne.getChildren().size() > 1) {
                colonne.getChildren().add(1, zoneDrop);
            } else {
                colonne.getChildren().add(zoneDrop);
            }
        }
    }

    /**
     * Affiche une confirmation pour supprimer une colonne
     */
    private void afficherConfirmationSuppressionColonne(String nomColonne) {
        Alert alerte = new Alert(Alert.AlertType.CONFIRMATION);
        alerte.setTitle("Supprimer colonne");
        alerte.setGraphic(null);
        alerte.setHeaderText("Supprimer la colonne '" + nomColonne + "' ?");
        alerte.setContentText("Cette action supprimera aussi toutes les tâches de cette colonne.");

        alerte.showAndWait().ifPresent(type -> {
            if (type == ButtonType.OK) {
                controleur.supprimerColonne(nomColonne);
            }
        });
    }
}