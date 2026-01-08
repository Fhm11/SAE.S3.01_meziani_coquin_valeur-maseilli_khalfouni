package source;

import javafx.scene.Node;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import java.util.*;

/**
 * Gestionnaire réutilisable pour le drag & drop
 */
public class GestionnaireDragDrop {

    private Controller controleur;
    private List<Node> elementsConfigures;

    private static final String STYLE_TACHE_SURVOL = "-fx-border-color: #FF9800; -fx-border-width: 2;";

    public GestionnaireDragDrop(Controller controleur) {
        this.controleur = controleur;
        this.elementsConfigures = new ArrayList<>();
    }

    /**
     * Nettoie tous les handlers configurés
     */
    public void nettoyer() {
        elementsConfigures.clear();
    }

    /**
     * Enregistre une colonne pour le drag & drop
     */
    public void enregistrerColonne(VBox colonne) {
        elementsConfigures.add(colonne);
    }

    /**
     * Enregistre une carte de tâche
     */
    public void enregistrerCarte(VBox carte, Tache tache) {
        elementsConfigures.add(carte);

        if (tache.estComposite()) {
            configurerDropPourTacheComposite(carte, tache);
        }
    }

    /**
     * Enregistre une sous-tâche
     */
    public void enregistrerSousTache(Node boiteSousTache, Tache sousTache) {
        elementsConfigures.add(boiteSousTache);

        if (sousTache.estComposite()) {
            configurerDropPourTacheComposite(boiteSousTache, sousTache);
        }
    }

    /**
     * Configure le drop pour une tâche composite
     */
    private void configurerDropPourTacheComposite(Node noeud, Tache tacheComposite) {
        noeud.setOnDragOver(evenement -> {
            if (evenement.getGestureSource() != noeud &&
                    evenement.getDragboard().hasString()) {
                Tache source = controleur.getTacheEnDeplacement();
                if (source != null && source != tacheComposite) {
                    evenement.acceptTransferModes(TransferMode.MOVE);
                }
            }
            evenement.consume();
        });

        noeud.setOnDragEntered(evenement -> {
            if (evenement.getGestureSource() != noeud &&
                    evenement.getDragboard().hasString()) {
                Tache source = controleur.getTacheEnDeplacement();
                if (source != null && source != tacheComposite) {
                    noeud.setStyle(noeud.getStyle() + STYLE_TACHE_SURVOL);
                }
            }
            evenement.consume();
        });

        noeud.setOnDragExited(evenement -> {
            Object styleOrigine = noeud.getProperties().get("style_origine");
            if (styleOrigine != null) {
                noeud.setStyle((String) styleOrigine);
            }
            evenement.consume();
        });

        noeud.setOnDragDropped(evenement -> {
            Dragboard dragboard = evenement.getDragboard();
            boolean succes = false;

            if (dragboard.hasString()) {
                Tache source = controleur.getTacheEnDeplacement();
                if (source != null && source != tacheComposite) {
                    if (!source.contientTache(tacheComposite)) {
                        controleur.devenirSousTacheDe(source, tacheComposite);
                        succes = true;
                    }
                }
            }

            evenement.setDropCompleted(succes);
            evenement.consume();
        });
    }
}