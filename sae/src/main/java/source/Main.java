package source;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * Classe principale de l'application
 * Point d'entrée de l'application JavaFX
 * Fait le lien entre le modèle, les vues et le contrôleur
 */
public class Main extends Application {

    private BorderPane racinePrincipale;
    private CoordinateurVues coordinateurVues;
    private Controller controleur;
    private TacheManager modele;
    private ComboBox<String> selecteurVue;

    @Override
    public void start(Stage fenetrePrincipale) {
        initialiserModele();
        initialiserInterface(fenetrePrincipale);
        configurerObservateurs();
    }

    /**
     * Initialise le modèle et le contrôleur
     */
    private void initialiserModele() {
        modele = TacheManager.getInstance();
        controleur = new Controller(modele);
    }

    /**
     * Initialise l'interface graphique
     * @param fenetre la fenêtre principale
     */
    private void initialiserInterface(Stage fenetre) {
        // Crée le conteneur principal
        racinePrincipale = new BorderPane();

        // Crée le coordinateur de vues
        coordinateurVues = new CoordinateurVues(racinePrincipale, controleur);

        // Initialise toutes les vues avec le modèle
        coordinateurVues.initialiserVues(modele);

        // Crée et place la barre d'outils
        HBox barreOutils = creerBarreOutils();
        racinePrincipale.setTop(barreOutils);

        // Affiche la vue Bureau par défaut
        coordinateurVues.changerVue("Vue Bureau");

        // Configure la scène et la fenêtre
        Scene scene = new Scene(racinePrincipale, 1200, 800);
        fenetre.setScene(scene);
        fenetre.setTitle("Trello - Gestion de Tâches");
        fenetre.show();
    }

    /**
     * Crée la barre d'outils en haut de la fenêtre
     * @return la barre d'outils configurée
     */
    private HBox creerBarreOutils() {
        HBox barreOutils = new HBox(10);
        barreOutils.setPadding(new Insets(10));
        barreOutils.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #ddd; -fx-border-width: 0 0 1 0;");

        // ===== BOUTON NOUVELLE TÂCHE =====
        Button btnNouvelleTache = new Button("Nouvelle Tâche");
        btnNouvelleTache.setStyle("-fx-font-size: 14px; -fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 8 15;");
        btnNouvelleTache.setOnAction(evenement -> {
            VueFormulaire.afficherFormulaireCreation(controleur);
        });

        // ===== BOUTON CRÉER COLONNE =====
        Button btnNouvelleColonne = new Button("Créer Colonne");
        btnNouvelleColonne.setStyle("-fx-font-size: 14px; -fx-padding: 8 15;");
        btnNouvelleColonne.setOnAction(evenement -> {
            afficherDialogueCreationColonne();
        });

        // ===== SÉPARATEUR =====
        Separator separateur = new Separator();
        separateur.setOrientation(javafx.geometry.Orientation.VERTICAL);

        // ===== SÉLECTEUR DE VUE =====
        Label labelVue = new Label("Vue :");
        labelVue.setStyle("-fx-font-weight: bold; -fx-padding: 0 5 0 0;");

        selecteurVue = new ComboBox<>();
        selecteurVue.getItems().addAll("Vue Bureau", "Vue Liste par Jour", "Vue Gantt", "Vue Archive");
        selecteurVue.setValue("Vue Bureau");
        selecteurVue.setStyle("-fx-font-size: 14px;");
        selecteurVue.setOnAction(evenement -> {
            changerVue();
        });

        // ===== ESPACEUR POUR ALIGNER À GAUCHE =====
        Pane espaceur = new Pane();
        HBox.setHgrow(espaceur, Priority.ALWAYS);

        // Ajoute tous les composants à la barre d'outils
        barreOutils.getChildren().addAll(
                btnNouvelleTache,
                btnNouvelleColonne,
                separateur,
                labelVue,
                selecteurVue,
                espaceur
        );

        return barreOutils;
    }

    /**
     * Affiche un dialogue pour créer une nouvelle colonne
     */
    private void afficherDialogueCreationColonne() {
        TextInputDialog dialogue = new TextInputDialog();
        dialogue.setTitle("Créer Colonne");
        dialogue.setHeaderText(null);
        dialogue.setContentText("Entrez le nom :");

        // Attend la saisie de l'utilisateur
        String nom = dialogue.showAndWait().orElse(null);

        // Crée la colonne si le nom n'est pas vide
        if (nom != null && !nom.trim().isEmpty()) {
            controleur.ajouterColonne(nom);
        }
    }

    /**
     * Change la vue affichée en fonction du sélecteur
     */
    private void changerVue() {
        String vueSelectionnee = selecteurVue.getValue();
        coordinateurVues.changerVue(vueSelectionnee);
    }

    /**
     * Configure les observateurs pour les mises à jour automatiques
     */
    private void configurerObservateurs() {
        // Enregistre toutes les vues comme observatrices
        modele.ajouterObservateur(coordinateurVues.getVueBureau());
        modele.ajouterObservateur(coordinateurVues.getVueListe());
        modele.ajouterObservateur(coordinateurVues.getVueGantt());
        modele.ajouterObservateur(coordinateurVues.getVueArchive());

        // Observateur pour reconfigurer après mise à jour
        modele.ajouterObservateur(new Observateur() {
            @Override
            public void actualiser() {
                javafx.application.Platform.runLater(() -> {
                    String vueCourante = selecteurVue.getValue();
                    coordinateurVues.changerVue(vueCourante);
                });
            }
        });
    }

    /**
     * Point d'entrée principal de l'application
     * @param args arguments de la ligne de commande
     */
    public static void main(String[] args) {
        launch(args);
    }
}