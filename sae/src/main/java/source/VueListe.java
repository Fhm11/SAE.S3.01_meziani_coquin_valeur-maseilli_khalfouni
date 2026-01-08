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

    private TacheManager modele;

    private ScrollPane panneauDefilement;
    private VBox contenuPrincipal;

    private List<Button> boutonsInteractifs = new ArrayList<>();
    private List<VBox> cartesTaches = new ArrayList<>();

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
        contenuPrincipal = new VBox(15);
        contenuPrincipal.setPadding(new Insets(10));

        panneauDefilement = new ScrollPane(contenuPrincipal);
        panneauDefilement.setFitToWidth(true);
        panneauDefilement.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        actualiser();
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
        contenuPrincipal.getChildren().clear();
        boutonsInteractifs.clear();
        cartesTaches.clear();

        for (String jour : JOURS_SEMAINE) {
            VBox sectionJour = creerSectionJour(jour);

            boolean aDesTaches = false;
            for (Tache tache : modele.getTaches()) {
                if (jour.equals(tache.getJDebut()) && !"archive".equals(tache.getEtat())) {
                    aDesTaches = true;
                    VBox carte = creerCarteTache(tache);
                    cartesTaches.add(carte);
                    sectionJour.getChildren().add(carte);

                    if (tache.estComposite()) {
                        afficherSousTachesRecursif(tache, sectionJour, 1);
                    }
                }
            }

            if (!aDesTaches) {
                Label labelVide = new Label("Aucune tâche pour ce jour");
                labelVide.setStyle("-fx-text-fill: #999999; -fx-font-size: 11px; -fx-font-style: italic;");
                labelVide.setPadding(new Insets(5, 0, 5, 10));
                sectionJour.getChildren().add(labelVide);
            }

            contenuPrincipal.getChildren().add(sectionJour);

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

        String styleOrigine = "-fx-background-color: white; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.08), 3, 0, 0, 1); " +
                "-fx-border-radius: 4; -fx-background-radius: 4; " +
                "-fx-border-color: #e0e0e0; -fx-border-width: 1;";

        carte.setStyle(styleOrigine);
        carte.setUserData(tache); // Stocke la tâche dans la carte
        carte.getProperties().put("style_origine", styleOrigine);

        Label titre = new Label(tache.getTitre());
        titre.setStyle(getStyleTitreParEtat(tache.getEtat()));
        titre.setWrapText(true);

        Label description = new Label(tache.getDescription());
        description.setStyle("-fx-text-fill: #666666; -fx-font-size: 11px;");
        description.setWrapText(true);

        HBox boiteInfo = new HBox(10);

        Label infoJour = new Label("Fin: " + tache.getJFin());
        infoJour.setStyle("-fx-text-fill: #888888; -fx-font-size: 10px;");

        Label infoEtat = new Label("État: " + getEtatTexte(tache.getEtat()));
        infoEtat.setStyle(getStyleEtat(tache.getEtat()));

        Label infoPriorite = new Label(" | Prio: " + tache.getPriorite());

        if ("Importante".equals(tache.getPriorite())) {
            infoPriorite.setStyle("-fx-font-size: 10px; -fx-text-fill: #e74c3c; -fx-font-weight: bold;");
        } else if ("Moyenne".equals(tache.getPriorite())) {
            infoPriorite.setStyle("-fx-font-size: 10px; -fx-text-fill: #f1c40f; -fx-font-weight: bold;");
        } else {
            infoPriorite.setStyle("-fx-font-size: 10px; -fx-text-fill: #008000;");
        }

        boiteInfo.getChildren().addAll(infoJour, infoEtat, infoPriorite);

        HBox boutons = new HBox(5);
        boutons.setPadding(new Insets(5, 0, 0, 0));

        if (tache.estComposite()) {
            Button btnSousTache = new Button("+");
            btnSousTache.setUserData(tache);
            btnSousTache.setStyle("-fx-font-size: 11px; -fx-padding: 3 8;");
            boutonsInteractifs.add(btnSousTache);
            boutons.getChildren().add(btnSousTache);
        }

        Button btnArchiver = new Button("Archiver");
        btnArchiver.setUserData(tache);
        btnArchiver.setStyle("-fx-font-size: 11px; -fx-padding: 3 8; -fx-text-fill: #e67e22; -fx-font-weight: bold;");
        boutonsInteractifs.add(btnArchiver);
        boutons.getChildren().add(btnArchiver);

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

        carte.setPadding(new Insets(5, 5, 5, 25 * niveau));

        String styleOrigine = "-fx-background-color: #f8f8f8; " +
                "-fx-border-radius: 3; -fx-background-radius: 3;";

        carte.setStyle(styleOrigine);
        carte.getProperties().put("style_origine", styleOrigine);

        HBox ligneTitre = new HBox(5);

        Label point = new Label("•");
        point.setStyle("-fx-text-fill: #777777; -fx-font-size: 12px;");

        Label titre = new Label(sousTache.getTitre());

        if ("Importante".equals(sousTache.getPriorite())) {
            titre.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 11px; -fx-font-weight: bold;");
        } else if ("Moyenne".equals(sousTache.getPriorite())) {
            titre.setStyle("-fx-text-fill: #f1c40f; -fx-font-size: 11px; -fx-font-weight: bold;");
        } else {
            titre.setStyle("-fx-text-fill: #008000; -fx-font-size: 11px; -fx-font-weight: bold;");
        }

        Button btnArchiver = new Button("Archiver");
        btnArchiver.setStyle("-fx-font-size: 9px; -fx-text-fill: white; " +
                "-fx-background-color: #e67e22; -fx-padding: 2 6; " +
                "-fx-background-radius: 4;");
        btnArchiver.setUserData(sousTache);
        boutonsInteractifs.add(btnArchiver);

        ligneTitre.getChildren().addAll(point, titre, btnArchiver);

        if (sousTache.estComposite()) {
            Button btnAjouter = new Button("+");
            btnAjouter.setStyle("-fx-font-size: 9px; -fx-text-fill: blue; -fx-padding: 2 5;");
            btnAjouter.setUserData(sousTache);
            boutonsInteractifs.add(btnAjouter);
            ligneTitre.getChildren().add(btnAjouter);
        }

        Label description = new Label(sousTache.getDescription());
        description.setStyle("-fx-text-fill: #777777; -fx-font-size: 10px;");
        description.setWrapText(true);
        description.setPadding(new Insets(0, 0, 0, 15));

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
            if (!"archive".equals(sousTache.getEtat())) {
                VBox carteSousTache = creerCarteSousTache(sousTache, niveau);
                cartesTaches.add(carteSousTache);
                conteneur.getChildren().add(carteSousTache);

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
            default:
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
            default:
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
                return etat;
        }
    }
}