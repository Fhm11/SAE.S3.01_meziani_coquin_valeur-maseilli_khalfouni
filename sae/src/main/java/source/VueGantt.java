package source;

import java.util.Arrays;
import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

/**
 * Vue Diagramme de Gantt
 * Affiche les tâches sur une timeline avec leur durée
 */
public class VueGantt implements Observateur {

    // Composants graphiques
    private ScrollPane panneauDefilement;
    private GridPane grille;
    private TacheManager modele;

    // Référence des jours de la semaine
    private static final List<String> JOURS_REFERENCE = Arrays.asList(
            "Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche"
    );

    public VueGantt() {
    }

    /**
     * Constructeur de la vue Gantt
     * @param modele le gestionnaire de tâches
     */
    public VueGantt(TacheManager modele) {
        this.modele = modele;
        this.grille = new GridPane();
        this.panneauDefilement = new ScrollPane();

        initialiserInterface();
    }

    /**
     * Initialise l'interface graphique
     */
    private void initialiserInterface() {
        // Configure la grille
        this.grille.setHgap(0);
        this.grille.setVgap(15);
        this.grille.setPadding(new Insets(20));

        // Configure le panneau de défilement
        this.panneauDefilement.setContent(grille);
        this.panneauDefilement.setFitToWidth(false); // Permet le défilement horizontal
    }

    /**
     * Récupère la racine de la vue
     * @return le panneau de défilement
     */
    public ScrollPane getRacine() {
        return panneauDefilement;
    }

    /**
     * Actualise l'affichage du diagramme de Gantt
     */
    @Override
    public void actualiser() {
        // Nettoie l'affichage précédent
        grille.getChildren().clear();
        grille.getColumnConstraints().clear();

        // Trouve le décalage maximum nécessaire
        int decalageMax = 6; // Une semaine de base
        for (Tache tache : modele.getTaches()) {
            decalageMax = Math.max(decalageMax, trouverDecalageMaxRecursif(tache));
        }

        // Crée la colonne pour les noms des tâches
        grille.getColumnConstraints().add(new ColumnConstraints(150));

        // Crée les colonnes pour les jours
        for (int i = 0; i <= decalageMax; i++) {
            grille.getColumnConstraints().add(new ColumnConstraints(120));

            // Détermine le nom du jour
            String nomJour = JOURS_REFERENCE.get(i % 7);
            if (i >= 7) {
                nomJour += " (semaine suivante)";
            }

            // Crée le label pour l'en-tête du jour
            Label labelJour = new Label(nomJour);
            labelJour.setMaxWidth(Double.MAX_VALUE);
            labelJour.setAlignment(Pos.CENTER);
            labelJour.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; " +
                    "-fx-padding: 10; -fx-background-color: #f8f9fa; " +
                    "-fx-border-color: #999; -fx-border-width: 0 0 2 2;");

            // Ajoute le label à la grille (ligne 0)
            grille.add(labelJour, i + 1, 0);
        }

        // Affiche toutes les tâches
        int ligneCourante = 1;
        for (Tache tache : modele.getTaches()) {
            ligneCourante = afficherTacheRecursif(tache, ligneCourante, 0);
        }
    }

    /**
     * Trouve le décalage maximum récursivement dans une hiérarchie de tâches
     * @param tache la tâche à analyser
     * @return le décalage maximum
     */
    private int trouverDecalageMaxRecursif(Tache tache) {
        int max = calculerDecalageFin(tache);

        // Si composite, vérifie aussi les sous-tâches
        if (tache.estComposite()) {
            for (Tache sousTache : tache.getSousTaches()) {
                max = Math.max(max, trouverDecalageMaxRecursif(sousTache));
            }
        }

        return max;
    }

    /**
     * Affiche récursivement une tâche et ses sous-tâches
     * @param tache la tâche à afficher
     * @param numeroLigne le numéro de ligne actuel
     * @param niveau le niveau de profondeur
     * @return le prochain numéro de ligne disponible
     */
    private int afficherTacheRecursif(Tache tache, int numeroLigne, int niveau) {
        int prochaineLigne = dessinerLigneTache(tache, numeroLigne, niveau);

        // Affiche récursivement les sous-tâches
        if (tache.estComposite()) {
            for (Tache sousTache : tache.getSousTaches()) {
                prochaineLigne = afficherTacheRecursif(sousTache, prochaineLigne, niveau + 1);
            }
        }

        return prochaineLigne;
    }

    /**
     * Calcule le décalage (index) du jour de fin
     * @param tache la tâche à analyser
     * @return l'index du jour de fin
     */
    private int calculerDecalageFin(Tache tache) {
        int indexDebut = JOURS_REFERENCE.indexOf(tache.getJDebut());
        int indexFin = JOURS_REFERENCE.indexOf(tache.getJFin());

        if (indexFin < indexDebut) {
            // Cas où la tâche se termine la semaine suivante
            return indexFin + 7;
        }
        return indexFin;
    }

    /**
     * Dessine une ligne représentant une tâche dans le diagramme
     * @param tache la tâche à dessiner
     * @param numeroLigne le numéro de ligne
     * @param niveau le niveau de profondeur
     * @return le prochain numéro de ligne disponible
     */
    private int dessinerLigneTache(Tache tache, int numeroLigne, int niveau) {
        // Calcule la position et la durée
        int indexDebut = JOURS_REFERENCE.indexOf(tache.getJDebut());
        int decalageFin = calculerDecalageFin(tache);
        int duree = decalageFin - indexDebut + 1; // +1 pour inclure le jour de début

        // Crée la barre de la tâche
        Label barre = new Label(tache.getTitre());

        // Style de base pour toutes les barres
        String styleBase = "-fx-text-fill: white; -fx-padding: 8; " +
                "-fx-background-radius: 5; -fx-font-size: 13px; " +
                "-fx-font-weight: bold; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 3, 0, 0, 1);";

        // Couleur différente selon le niveau
        String couleur;
        if (niveau == 0) {
            couleur = "#3498db"; // Bleu pour les tâches principales
        } else if (niveau == 1) {
            couleur = "#5dade2"; // Bleu clair pour les sous-tâches de niveau 1
        } else {
            couleur = "#aed6f1"; // Bleu très clair pour les niveaux supérieurs
        }

        barre.setStyle("-fx-background-color: " + couleur + "; " + styleBase);
        barre.setMaxWidth(Double.MAX_VALUE);

        // Conteneur pour la barre (pour le padding)
        StackPane conteneurBarre = new StackPane(barre);
        conteneurBarre.setPadding(new Insets(0, 5, 0, 5));

        grille.add(conteneurBarre, indexDebut + 1, numeroLigne, duree, 1);

        // Retourne la ligne suivante
        return numeroLigne + 1;
    }
}