package source;

import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import java.util.*;

/**
 * Contrôleur spécialisé pour la vue Liste
 */
public class ControllerListe {

    private Controller controleur;
    private GestionnaireDragDrop gestionnaireDragDrop;

    public ControllerListe(Controller controleur) {
        this.controleur = controleur;
        this.gestionnaireDragDrop = new GestionnaireDragDrop(controleur);
    }

    /**
     * Configure la vue Liste
     */
    public void configurerVue(VueListe vue) {
        // Nettoie les anciens handlers
        gestionnaireDragDrop.nettoyer();

        // Configure les boutons
        configurerBoutons(vue);

        // Configure les cartes
        configurerCartes(vue);

        // Configure les zones d'extraction
        configurerZonesExtraction(vue);
    }

    /**
     * Configure les boutons de la vue Liste
     */
    private void configurerBoutons(VueListe vue) {
        for (Button bouton : vue.getBoutonsInteractifs()) {
            Object donnees = bouton.getUserData();

            if (donnees instanceof Tache) {
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
     * Configure les cartes de tâches
     */
    private void configurerCartes(VueListe vue) {
        for (VBox carte : vue.getCartesTaches()) {
            Tache tache = (Tache) carte.getUserData();

            // Double-clic pour modification
            carte.setOnMouseClicked(evenement -> {
                if (evenement.getButton() == MouseButton.PRIMARY &&
                        evenement.getClickCount() == 2) {
                    VueFormulaire.afficherFormulaireModification(tache, controleur);
                }
            });

            // Drag détecté
            carte.setOnDragDetected(evenement -> {
                controleur.debuterDeplacement(tache);
                Dragboard dragboard = carte.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent contenu = new ClipboardContent();
                contenu.putString(tache.getTitre());
                dragboard.setContent(contenu);
                evenement.consume();
            });

            // Enregistre la carte dans le gestionnaire drag drop
            gestionnaireDragDrop.enregistrerCarte(carte, tache);
        }
    }

    /**
     * Configure les zones d'extraction entre les jours
     */
    private void configurerZonesExtraction(VueListe vue) {
        VBox contenuPrincipal = vue.getContenuPrincipal();

        for (javafx.scene.Node noeud : contenuPrincipal.getChildren()) {
            if (noeud instanceof VBox) {
                VBox section = (VBox) noeud;

                // Trouve le label du jour
                String jour = trouverJourDansSection(section);
                if (jour == null) continue;

                final String jourFinal = jour;

                // Crée une zone de drop transparente
                Pane zoneDrop = new Pane();
                zoneDrop.setPrefHeight(20);
                zoneDrop.setStyle("-fx-background-color: transparent;");

                // Configure la zone d'extraction
                zoneDrop.setOnDragOver(evenement -> {
                    if (evenement.getDragboard().hasString()) {
                        evenement.acceptTransferModes(TransferMode.MOVE);
                    }
                    evenement.consume();
                });

                zoneDrop.setOnDragEntered(evenement -> {
                    zoneDrop.setStyle("-fx-background-color: rgba(76, 175, 80, 0.3);");
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
                            // Vérifie si c'est une sous-tâche
                            if (estSousTache(source)) {
                                // Pour les sous-tâches, on utilise extraireVersJour
                                controleur.extraireVersJour(source, jourFinal);
                            } else {
                                // Pour les tâches principales, on utilise deplacerTacheVersJour
                                controleur.deplacerTacheVersJour(source, jourFinal);
                            }
                            succes = true;
                        }
                    }

                    evenement.setDropCompleted(succes);
                    evenement.consume();
                });

                // Insère la zone après le titre du jour
                if (section.getChildren().size() >= 1) {
                    section.getChildren().add(1, zoneDrop);
                }
            }
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
     * Trouve le jour dans une section
     */
    private String trouverJourDansSection(VBox section) {
        for (javafx.scene.Node enfant : section.getChildren()) {
            if (enfant instanceof Label) {
                Label label = (Label) enfant;
                String texte = label.getText();
                if (texte.matches("Lundi|Mardi|Mercredi|Jeudi|Vendredi|Samedi|Dimanche")) {
                    return texte;
                }
            }
        }
        return null;
    }
}