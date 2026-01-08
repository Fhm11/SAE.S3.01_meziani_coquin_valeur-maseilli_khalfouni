package source;

import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.Insets;
import java.util.ArrayList;
import java.util.List;

/**
 * Vue Bureau (vue Kanban/Trello)
 * Affiche les tâches organisées en colonnes
 */
public class VueBureau implements Observateur {

    // Composants graphiques principaux
    private VBox racine;
    private TacheManager modele;
    private HBox conteneurColonnes;
    private ScrollPane panneauDefilement;

    // Listes pour stocker les éléments graphiques
    private List<VBox> colonnesGraphiques = new ArrayList<>();
    private List<Button> boutonsInteractifs = new ArrayList<>();
    private List<VBox> cartesTaches = new ArrayList<>();
    private List<HBox> sousTachesBoxes = new ArrayList<>();

    public VueBureau() {}

    /**
     * Constructeur de la vue Bureau
     * @param modele le gestionnaire de tâches
     */
    public VueBureau(TacheManager modele) {
        this.modele = modele;
        initialiserInterface();
    }

    /**
     * Initialise l'interface graphique
     */
    private void initialiserInterface() {
        racine = new VBox(10);
        racine.setPadding(new Insets(10));

        panneauDefilement = new ScrollPane();
        panneauDefilement.setFitToHeight(true);
        panneauDefilement.setFitToWidth(true);

        // Crée le conteneur pour les colonnes
        conteneurColonnes = new HBox(15);
        conteneurColonnes.setPadding(new Insets(10));

        panneauDefilement.setContent(conteneurColonnes);

        VBox.setVgrow(panneauDefilement, Priority.ALWAYS);

        racine.getChildren().addAll(panneauDefilement);
    }

    /**
     * Récupère la racine de la vue (pour l'ajouter à la scène)
     * @return le conteneur racine
     */
    public VBox getRacine() {
        return racine;
    }

    /**
     * Récupère la liste des boutons interactifs
     * @return la liste des boutons
     */
    public List<Button> getBoutonsInteractifs() {
        return boutonsInteractifs;
    }

    /**
     * Récupère la liste des cartes de tâches
     * @return la liste des cartes
     */
    public List<VBox> getCartesTaches() {
        return cartesTaches;
    }

    /**
     * Récupère la liste des boîtes de sous-tâches
     * @return la liste des boîtes
     */
    public List<HBox> getSousTachesBoxes() {
        return sousTachesBoxes;
    }

    /**
     * Récupère la liste des colonnes graphiques
     * @return la liste des colonnes
     */
    public List<VBox> getColonnesGraphiques() {
        return colonnesGraphiques;
    }

    /**
     * Récupère le conteneur des colonnes
     * @return le conteneur HBox
     */
    public HBox getConteneurColonnes() {
        return conteneurColonnes;
    }

    /**
     * Actualise l'affichage de la vue (patron Observateur)
     */
    @Override
    public void actualiser() {
        conteneurColonnes.getChildren().clear();
        colonnesGraphiques.clear();
        boutonsInteractifs.clear();
        cartesTaches.clear();
        sousTachesBoxes.clear();

        for (String nomColonne : modele.getColonnes()) {
            VBox colonneBox = creerColonne(nomColonne);
            colonnesGraphiques.add(colonneBox);

            for (Tache tache : modele.getTaches()) {
                if (nomColonne.equals(tache.getEtat())) {
                    VBox carte = creerAffichageTache(tache);
                    cartesTaches.add(carte);
                    colonneBox.getChildren().add(carte);
                }
            }

            conteneurColonnes.getChildren().add(colonneBox);
        }
    }

    /**
     * Crée l'affichage graphique d'une tâche
     * @param tache la tâche à afficher
     * @return le conteneur VBox de la carte
     */
    private VBox creerAffichageTache(Tache tache) {
        VBox conteneur = new VBox(5);
        conteneur.setPadding(new Insets(10));

        String styleOrigine = "-fx-background-color: white; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 1); " +
                "-fx-background-radius: 5;";

        conteneur.getProperties().put("style_origine", styleOrigine);
        conteneur.setStyle(styleOrigine);

        conteneur.setUserData(tache);

        Label labelTitre = new Label(tache.getTitre());
        labelTitre.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");

        Label labelPriorite = new Label(tache.getPriorite().toUpperCase());
        String stylePriorite = "-fx-font-size: 9px; -fx-text-fill: white; " +
                "-fx-padding: 2 5; -fx-background-radius: 3; " +
                "-fx-font-weight: bold;";

        if ("Importante".equals(tache.getPriorite())) {
            labelPriorite.setStyle(stylePriorite + "-fx-background-color: #e74c3c;");
        } else if ("Moyenne".equals(tache.getPriorite())) {
            labelPriorite.setStyle(stylePriorite + "-fx-background-color: #f1c40f;");
        } else {
            labelPriorite.setStyle(stylePriorite + "-fx-background-color: #008000;");
        }

        Label labelDesc = new Label(tache.getDescription());
        labelDesc.setStyle("-fx-text-fill: #555555; -fx-font-size: 11px;");
        labelDesc.setWrapText(true); // Retour à la ligne automatique

        Label labelDate = new Label("Date début : " + tache.getJDebut());
        labelDate.setStyle("-fx-text-fill: #888888; -fx-font-size: 10px; -fx-font-style: italic;");

        HBox boutons = new HBox(5);
        boutons.setAlignment(Pos.CENTER_RIGHT);

        if (tache.estComposite()) {
            Button btnAjouterSous = new Button("+");
            btnAjouterSous.setUserData(tache);
            boutonsInteractifs.add(btnAjouterSous);
            boutons.getChildren().add(btnAjouterSous);
        }

        Button btnArchiver = new Button("Archiver");
        btnArchiver.setStyle("-fx-text-fill: white; -fx-background-color: #e67e22; -fx-font-weight: bold;");
        btnArchiver.setUserData(tache);
        boutonsInteractifs.add(btnArchiver);
        boutons.getChildren().add(btnArchiver);

        conteneur.getChildren().addAll(
                labelTitre,
                labelPriorite,
                labelDesc,
                boutons,
                labelDate
        );

        if (tache.estComposite()) {
            afficherSousTachesRecursif(tache, conteneur, 1);
        }

        return conteneur;
    }

    /**
     * Crée une colonne pour afficher les tâches d'un état particulier
     * @param titre le nom de la colonne
     * @return le conteneur VBox de la colonne
     */
    private VBox creerColonne(String titre) {
        VBox colonne = new VBox(10);
        colonne.setPadding(new Insets(10));
        colonne.setMinWidth(250); // Largeur minimale

        // Style de base de la colonne
        String styleOrigine = STYLE_COLONNE;
        colonne.setStyle(styleOrigine);
        colonne.getProperties().put("style_origine", styleOrigine);

        // Stocke le nom de la colonne
        colonne.setUserData(titre);

        HBox enTete = new HBox(10);
        enTete.setAlignment(Pos.CENTER_LEFT);

        Label labelTitre = new Label(titre);
        labelTitre.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        labelTitre.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(labelTitre, Priority.ALWAYS);

        Button btnSupprimerColonne = new Button("X");
        btnSupprimerColonne.setStyle("-fx-text-fill: white; -fx-background-color: #ff4444; " +
                "-fx-font-size: 10px; -fx-font-weight: bold;");
        btnSupprimerColonne.setUserData(titre);
        boutonsInteractifs.add(btnSupprimerColonne);

        enTete.getChildren().addAll(labelTitre, btnSupprimerColonne);
        colonne.getChildren().add(enTete);

        return colonne;
    }

    /**
     * Affiche récursivement les sous-tâches d'une tâche composite
     * @param parent la tâche parente
     * @param conteneurParent le conteneur où ajouter les sous-tâches
     * @param niveau le niveau de profondeur (pour l'indentation)
     */
    private void afficherSousTachesRecursif(Tache parent, VBox conteneurParent, int niveau) {
        if (!parent.estComposite()) return;

        for (Tache sousTache : parent.getSousTaches()) {
            // Ne pas afficher les tâches archivées
            if (!"archive".equals(sousTache.getEtat())) {
                HBox boiteSousTache = creerBoiteSousTache(sousTache, niveau);

                // Ajoute la boîte aux listes pour manipulation
                sousTachesBoxes.add(boiteSousTache);

                // Ajoute la sous-tâche au conteneur parent
                conteneurParent.getChildren().add(boiteSousTache);

                // Appel récursif pour les sous-sous-tâches
                afficherSousTachesRecursif(sousTache, conteneurParent, niveau + 1);
            }
        }
    }

    /**
     * Crée une boîte d'affichage pour une sous-tâche
     * @param sousTache la sous-tâche à afficher
     * @param niveau le niveau de profondeur
     * @return le conteneur HBox de la sous-tâche
     */
    private HBox creerBoiteSousTache(Tache sousTache, int niveau) {
        HBox boite = new HBox(5);

        int decalage = 20 + (niveau * 15);
        boite.setPadding(new Insets(2, 0, 2, decalage));

        boite.setStyle("-fx-border-color: #eeeeee; -fx-border-width: 0 0 0 2;");

        boite.setUserData(sousTache);

        HBox ligne = new HBox(5);
        ligne.setAlignment(Pos.CENTER_LEFT);

        Label labelTitre = new Label("• " + sousTache.getTitre());
        labelTitre.setStyle("-fx-text-fill: #333333; -fx-font-size: 11px; -fx-font-weight: bold;");
        labelTitre.setUserData(sousTache);

        Label labelPriorite = new Label(sousTache.getPriorite().toUpperCase());
        String stylePriorite = "-fx-font-size: 9px; -fx-text-fill: white; " +
                "-fx-padding: 2 5; -fx-background-radius: 3; " +
                "-fx-font-weight: bold;";

        if ("Importante".equals(sousTache.getPriorite())) {
            labelPriorite.setStyle(stylePriorite + "-fx-background-color: #e74c3c;");
        } else if ("Moyenne".equals(sousTache.getPriorite())) {
            labelPriorite.setStyle(stylePriorite + "-fx-background-color: #f1c40f;");
        } else {
            labelPriorite.setStyle(stylePriorite + "-fx-background-color: #008000;");
        }

        Button btnArchiver = new Button("Archiver");
        btnArchiver.setStyle("-fx-font-size: 9px; -fx-text-fill: white; " +
                "-fx-background-color: #e67e22; -fx-padding: 2 6; " +
                "-fx-background-radius: 4;");
        btnArchiver.setUserData(sousTache);
        boutonsInteractifs.add(btnArchiver);

        Label labelDate = new Label("déb : " + sousTache.getJDebut());
        labelDate.setStyle("-fx-text-fill: #999999; -fx-font-size: 10px;");
        labelDate.setUserData(sousTache);

        if (sousTache.estComposite()) {
            Button btnAjouter = new Button("+");
            btnAjouter.setStyle("-fx-font-size: 9px; -fx-text-fill: blue;");
            btnAjouter.setUserData(sousTache);
            boutonsInteractifs.add(btnAjouter);
            ligne.getChildren().add(btnAjouter);
        }

        ligne.getChildren().addAll(labelTitre, labelPriorite, btnArchiver, labelDate);

        boite.getChildren().add(ligne);

        return boite;
    }

    private static final String STYLE_COLONNE =
            "-fx-border-color: lightgray; -fx-border-width: 1; -fx-background-color: #f4f4f4;";
}