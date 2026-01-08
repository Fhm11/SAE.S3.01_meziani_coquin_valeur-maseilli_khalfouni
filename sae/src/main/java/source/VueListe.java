package source;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.*;

/**
 * Vue Liste par Jour
 * Affiche les tâches organisées par jour de la semaine
 */
public class VueListe implements Observateur {

    // Référence au modèle
    private TacheManager modele;

    // Composants graphiques
    private ScrollPane panneauDefilement;
    private VBox contenuPrincipal;

    // Listes pour stocker les éléments interactifs
    private List<Button> boutonsInteractifs = new ArrayList<>();
    private List<VBox> cartesTaches = new ArrayList<>();

    // Ordre des jours de la semaine
    private static final String[] JOURS_SEMAINE = {
            "Lundi", "Mardi", "Mercredi", "Jeudi",
            "Vendredi", "Samedi", "Dimanche"
    };

    public VueListe() {}

    /**
     * Constructeur de la vue Liste
     * @param modele le gestionnaire de tâches
     */
    public VueListe(TacheManager modele) {
        this.modele = modele;
        initialiserInterface();
    }

    /**
     * Initialise l'interface graphique
     */
    private void initialiserInterface() {
        // Crée le contenu principal
        contenuPrincipal = new VBox(15);
        contenuPrincipal.setPadding(new Insets(10));

        // Crée le panneau de défilement
        panneauDefilement = new ScrollPane(contenuPrincipal);
        panneauDefilement.setFitToWidth(true);
        panneauDefilement.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        actualiser(); // Initialise l'affichage
    }

    /**
     * Récupère la racine de la vue
     * @return le panneau de défilement
     */
    public ScrollPane getRacine() {
        return panneauDefilement;
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
     * Récupère le contenu principal
     * @return le conteneur VBox principal
     */
    public VBox getContenuPrincipal() {
        return contenuPrincipal;
    }

    /**
     * Actualise l'affichage de la vue
     */
    @Override
    public void actualiser() {
        // Nettoie les anciens éléments
        contenuPrincipal.getChildren().clear();
        boutonsInteractifs.clear();
        cartesTaches.clear();

        // Pour chaque jour de la semaine
        for (String jour : JOURS_SEMAINE) {
            // Crée une section pour ce jour
            VBox sectionJour = creerSectionJour(jour);

            // Ajoute les tâches pour ce jour
            boolean aDesTaches = false;
            for (Tache tache : modele.getTaches()) {
                // Filtre les tâches archivées
                if (jour.equals(tache.getJDebut()) && !"archive".equals(tache.getEtat())) {
                    aDesTaches = true;
                    VBox carte = creerCarteTache(tache);
                    cartesTaches.add(carte);
                    sectionJour.getChildren().add(carte);

                    // Ajoute les sous-tâches si la tâche est composite
                    if (tache.estComposite()) {
                        afficherSousTachesRecursif(tache, sectionJour, 1);
                    }
                }
            }

            // Si la section n'a pas de tâches, ajoute un message
            if (!aDesTaches) {
                Label labelVide = new Label("Aucune tâche pour ce jour");
                labelVide.setStyle("-fx-text-fill: #999999; -fx-font-size: 11px; -fx-font-style: italic;");
                labelVide.setPadding(new Insets(5, 0, 5, 10));
                sectionJour.getChildren().add(labelVide);
            }

            // Ajoute la section du jour au contenu principal
            contenuPrincipal.getChildren().add(sectionJour);

            // Ajoute un séparateur entre les jours (sauf après le dernier)
            if (!jour.equals(JOURS_SEMAINE[JOURS_SEMAINE.length - 1])) {
                Separator separateur = new Separator();
                separateur.setPadding(new Insets(10, 0, 10, 0));
                contenuPrincipal.getChildren().add(separateur);
            }
        }
    }

    /**
     * Crée une section visuelle pour un jour donné
     * @param jour le nom du jour
     * @return le conteneur VBox de la section
     */
    private VBox creerSectionJour(String jour) {
        VBox section = new VBox(8);
        section.setPadding(new Insets(5));
        section.setUserData(jour); // Stocke le jour dans la section

        // Titre du jour
        Label titreJour = new Label(jour);
        titreJour.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; " +
                "-fx-text-fill: #2c3e50; -fx-padding: 0 0 8 0;");

        section.getChildren().add(titreJour);
        return section;
    }

    /**
     * Crée une carte visuelle pour une tâche principale
     * @param tache la tâche à afficher
     * @return le conteneur VBox de la carte
     */
    private VBox creerCarteTache(Tache tache) {
        VBox carte = new VBox(5);
        carte.setPadding(new Insets(8));

        // Style de base de la carte
        String styleOrigine = "-fx-background-color: white; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.08), 3, 0, 0, 1); " +
                "-fx-border-radius: 4; -fx-background-radius: 4; " +
                "-fx-border-color: #e0e0e0; -fx-border-width: 1;";

        carte.setStyle(styleOrigine);
        carte.setUserData(tache); // Stocke la tâche dans la carte
        carte.getProperties().put("style_origine", styleOrigine);

        // ===== TITRE AVEC INDICATEUR D'ÉTAT =====
        Label titre = new Label(tache.getTitre());
        titre.setStyle(getStyleTitreParEtat(tache.getEtat()));
        titre.setWrapText(true);

        // ===== DESCRIPTION =====
        Label description = new Label(tache.getDescription());
        description.setStyle("-fx-text-fill: #666666; -fx-font-size: 11px;");
        description.setWrapText(true);

        // ===== INFORMATIONS SUPPLÉMENTAIRES =====
        HBox boiteInfo = new HBox(10);

        // Jour de fin
        Label infoJour = new Label("Fin: " + tache.getJFin());
        infoJour.setStyle("-fx-text-fill: #888888; -fx-font-size: 10px;");

        // État
        Label infoEtat = new Label("État: " + getEtatTexte(tache.getEtat()));
        infoEtat.setStyle(getStyleEtat(tache.getEtat()));

        // Priorité
        Label infoPriorite = new Label(" | Prio: " + tache.getPriorite());

        // Couleur selon la priorité
        if ("Importante".equals(tache.getPriorite())) {
            infoPriorite.setStyle("-fx-font-size: 10px; -fx-text-fill: #e74c3c; -fx-font-weight: bold;");
        } else if ("Moyenne".equals(tache.getPriorite())) {
            infoPriorite.setStyle("-fx-font-size: 10px; -fx-text-fill: #f1c40f; -fx-font-weight: bold;");
        } else {
            infoPriorite.setStyle("-fx-font-size: 10px; -fx-text-fill: #008000;");
        }

        boiteInfo.getChildren().addAll(infoJour, infoEtat, infoPriorite);

        // ===== BOUTONS D'ACTION =====
        HBox boutons = new HBox(5);
        boutons.setPadding(new Insets(5, 0, 0, 0));

        // Bouton "+" pour les tâches composites
        if (tache.estComposite()) {
            Button btnSousTache = new Button("+");
            btnSousTache.setUserData(tache);
            btnSousTache.setStyle("-fx-font-size: 11px; -fx-padding: 3 8;");
            boutonsInteractifs.add(btnSousTache);
            boutons.getChildren().add(btnSousTache);
        }

        // Bouton "Archiver"
        Button btnArchiver = new Button("Archiver");
        btnArchiver.setUserData(tache);
        btnArchiver.setStyle("-fx-font-size: 11px; -fx-padding: 3 8; -fx-text-fill: #e67e22; -fx-font-weight: bold;");
        boutonsInteractifs.add(btnArchiver);
        boutons.getChildren().add(btnArchiver);

        // Ajoute tous les éléments à la carte
        carte.getChildren().addAll(titre, description, boiteInfo, boutons);

        return carte;
    }

    /**
     * Crée une carte visuelle pour une sous-tâche
     * @param sousTache la sous-tâche à afficher
     * @param niveau le niveau de profondeur (pour l'indentation)
     * @return le conteneur VBox de la sous-tâche
     */
    private VBox creerCarteSousTache(Tache sousTache, int niveau) {
        VBox carte = new VBox(3);
        carte.setUserData(sousTache);

        // Indentation progressive selon le niveau
        carte.setPadding(new Insets(5, 5, 5, 25 * niveau));

        // Style de base
        String styleOrigine = "-fx-background-color: #f8f8f8; " +
                "-fx-border-radius: 3; -fx-background-radius: 3;";

        carte.setStyle(styleOrigine);
        carte.getProperties().put("style_origine", styleOrigine);

        // ===== LIGNE DU TITRE =====
        HBox ligneTitre = new HBox(5);

        // Puce
        Label point = new Label("•");
        point.setStyle("-fx-text-fill: #777777; -fx-font-size: 12px;");

        // Titre avec couleur selon la priorité
        Label titre = new Label(sousTache.getTitre());

        if ("Importante".equals(sousTache.getPriorite())) {
            titre.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 11px; -fx-font-weight: bold;");
        } else if ("Moyenne".equals(sousTache.getPriorite())) {
            titre.setStyle("-fx-text-fill: #f1c40f; -fx-font-size: 11px; -fx-font-weight: bold;");
        } else {
            titre.setStyle("-fx-text-fill: #008000; -fx-font-size: 11px; -fx-font-weight: bold;");
        }

        // Bouton "Archiver"
        Button btnArchiver = new Button("Archiver");
        btnArchiver.setStyle("-fx-font-size: 9px; -fx-text-fill: white; " +
                "-fx-background-color: #e67e22; -fx-padding: 2 6; " +
                "-fx-background-radius: 4;");
        btnArchiver.setUserData(sousTache);
        boutonsInteractifs.add(btnArchiver);

        ligneTitre.getChildren().addAll(point, titre, btnArchiver);

        // Bouton "+" si la sous-tâche est composite
        if (sousTache.estComposite()) {
            Button btnAjouter = new Button("+");
            btnAjouter.setStyle("-fx-font-size: 9px; -fx-text-fill: blue; -fx-padding: 2 5;");
            btnAjouter.setUserData(sousTache);
            boutonsInteractifs.add(btnAjouter);
            ligneTitre.getChildren().add(btnAjouter);
        }

        // ===== DESCRIPTION =====
        Label description = new Label(sousTache.getDescription());
        description.setStyle("-fx-text-fill: #777777; -fx-font-size: 10px;");
        description.setWrapText(true);
        description.setPadding(new Insets(0, 0, 0, 15));

        // Ajoute tous les éléments à la carte
        carte.getChildren().addAll(ligneTitre, description);

        return carte;
    }

    /**
     * Affiche récursivement les sous-tâches
     * @param parent la tâche parente
     * @param conteneur le conteneur où ajouter les sous-tâches
     * @param niveau le niveau de profondeur
     */
    private void afficherSousTachesRecursif(Tache parent, VBox conteneur, int niveau) {
        if (!parent.estComposite()) return;

        for (Tache sousTache : parent.getSousTaches()) {
            // Filtre les tâches archivées
            if (!"archive".equals(sousTache.getEtat())) {
                VBox carteSousTache = creerCarteSousTache(sousTache, niveau);
                cartesTaches.add(carteSousTache);
                conteneur.getChildren().add(carteSousTache);

                // Appel récursif pour les sous-sous-tâches
                if (sousTache.estComposite()) {
                    afficherSousTachesRecursif(sousTache, conteneur, niveau + 1);
                }
            }
        }
    }

    /**
     * Détermine le style CSS du titre selon l'état de la tâche
     * @param etat l'état de la tâche
     * @return le style CSS correspondant
     */
    private String getStyleTitreParEtat(String etat) {
        switch (etat) {
            case "En cours":
                return "-fx-font-weight: bold; -fx-text-fill: #e67e22; -fx-font-size: 13px;";
            case "Terminée":
                return "-fx-font-weight: bold; -fx-text-fill: #27ae60; -fx-font-size: 13px; " +
                        "-fx-strikethrough: true;";
            default: // "À faire" et autres
                return "-fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-font-size: 13px;";
        }
    }

    /**
     * Détermine le style CSS du label d'état
     * @param etat l'état de la tâche
     * @return le style CSS correspondant
     */
    private String getStyleEtat(String etat) {
        switch (etat) {
            case "En cours":
                return "-fx-font-size: 10px; -fx-text-fill: #e67e22; -fx-font-weight: bold;";
            case "Terminée":
                return "-fx-font-size: 10px; -fx-text-fill: #27ae60; -fx-font-weight: bold;";
            default: // "À faire" et autres
                return "-fx-font-size: 10px; -fx-text-fill: #7f8c8d;";
        }
    }

    /**
     * Convertit le code d'état en texte lisible
     * @param etat le code d'état
     * @return le texte correspondant
     */
    private String getEtatTexte(String etat) {
        switch (etat) {
            case "afaire":
                return "À faire";
            case "encours":
                return "En cours";
            case "terminer":
                return "Terminé";
            default:
                return etat; // Retourne tel quel si non reconnu
        }
    }
}