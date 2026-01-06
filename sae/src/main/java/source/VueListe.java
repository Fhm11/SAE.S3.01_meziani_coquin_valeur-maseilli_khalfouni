package source;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.*;

public class VueListe implements Observateur {
    private TacheManager modele;
    private ScrollPane scrollPane;
    private VBox contenuPrincipal;
    private List<Button> boutonsInteractifs = new ArrayList<>();
    private List<VBox> cartesTaches = new ArrayList<>();

    // ordre des jours de la semaine
    private final String[] JOURS_SEMAINE = {
            "Lundi", "Mardi", "Mercredi", "Jeudi",
            "Vendredi", "Samedi", "Dimanche"
    };

    public VueListe(TacheManager modele) {
        this.modele = modele;

        // crée le contenu principal
        contenuPrincipal = new VBox(15);
        contenuPrincipal.setPadding(new Insets(10));

        // crée le ScrollPane
        scrollPane = new ScrollPane(contenuPrincipal);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        actualiser(); // initialiser l'affichage
    }

    public ScrollPane getRoot() {
        return scrollPane;
    }

    public List<Button> getBoutonsInteractifs() {
        return boutonsInteractifs;
    }

    public List<VBox> getCartesTaches() {
        return cartesTaches;
    }

    @Override
    public void actualiser() {
        // réinitialiser
        contenuPrincipal.getChildren().clear();
        boutonsInteractifs.clear();
        cartesTaches.clear();

        // pour chaque jour de la semaine
        for (String jour : JOURS_SEMAINE) {
            // crée une section pour ce jour
            VBox sectionJour = creerSectionJour(jour);

            // ajouter les tâches pour ce jour
            boolean hasTaches = false;
            for (Tache tache : modele.getTaches()) {
                if (jour.equals(tache.getJDebut())) {
                    hasTaches = true;
                    VBox carte = creerCarteTache(tache);
                    cartesTaches.add(carte);
                    sectionJour.getChildren().add(carte);

                    // ajouter les sous-tâches si composite
                    if (tache.estComposite()) {
                        for (Tache sousTache : tache.getSousTaches()) {
                            afficherSousTachesRecursif(tache, sectionJour, 1);
                        }
                    }
                }
            }

            // si la section n'a pas de tâches, ajouter un message
            if (!hasTaches) {
                Label labelVide = new Label("Aucune tâche pour ce jour");
                labelVide.setStyle("-fx-text-fill: #999999; -fx-font-size: 11px; -fx-font-style: italic;");
                labelVide.setPadding(new Insets(5, 0, 5, 10));
                sectionJour.getChildren().add(labelVide);
            }

            contenuPrincipal.getChildren().add(sectionJour);

            // ajouter un séparateur entre les jours (sauf après le dernier)
            if (!jour.equals(JOURS_SEMAINE[JOURS_SEMAINE.length - 1])) {
                Separator separator = new Separator();
                separator.setPadding(new Insets(10, 0, 10, 0));
                contenuPrincipal.getChildren().add(separator);
            }
        }
    }

    private VBox creerSectionJour(String jour) {
        VBox section = new VBox(8);
        section.setPadding(new Insets(5));

        // Titre du jour
        Label titreJour = new Label(jour);
        titreJour.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; " +
                "-fx-text-fill: #2c3e50; -fx-padding: 0 0 8 0;");

        section.getChildren().add(titreJour);
        return section;
    }

    private VBox creerCarteTache(Tache tache) {
        VBox carte = new VBox(5);
        carte.setPadding(new Insets(8));
        carte.setStyle("-fx-background-color: white; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.08), 3, 0, 0, 1); " +
                "-fx-border-radius: 4; -fx-background-radius: 4; " +
                "-fx-border-color: #e0e0e0; -fx-border-width: 1;");

        carte.setUserData(tache);

        // titre avec indicateur d'état
        Label titre = new Label(tache.getTitre());
        titre.setStyle(getStyleTitreParEtat(tache.getEtat()));
        titre.setWrapText(true);

        // description
        Label description = new Label(tache.getDescription());
        description.setStyle("-fx-text-fill: #666666; -fx-font-size: 11px;");
        description.setWrapText(true);

        // info jour de fin et état
        HBox infoBox = new HBox(10);
        Label infoJour = new Label("Fin: " + tache.getJFin());
        infoJour.setStyle("-fx-text-fill: #888888; -fx-font-size: 10px;");

        Label infoEtat = new Label("État: " + getEtatTexte(tache.getEtat()));
        infoEtat.setStyle(getStyleEtat(tache.getEtat()));

        infoBox.getChildren().addAll(infoJour, infoEtat);

        // boutons d'action
        HBox boutons = new HBox(5);
        boutons.setPadding(new Insets(5, 0, 0, 0));

        // utiliser le même texte que dans VueBureau
        if (tache.estComposite()) {
            Button btnSousTache = new Button("+ Sous-tâche");
            btnSousTache.setUserData(tache);
            btnSousTache.setStyle("-fx-font-size: 11px; -fx-padding: 3 8;");
            boutonsInteractifs.add(btnSousTache);
            boutons.getChildren().add(btnSousTache);
        }

        Button btnSupprimer = new Button("Supprimer");
        btnSupprimer.setUserData(tache);
        btnSupprimer.setStyle("-fx-font-size: 11px; -fx-padding: 3 8; -fx-text-fill: #cc0000;");
        boutonsInteractifs.add(btnSupprimer);
        boutons.getChildren().add(btnSupprimer);

        carte.getChildren().addAll(titre, description, infoBox, boutons);
        return carte;
    }

    private VBox creerCarteSousTache(Tache sousTache, int niveau) {
        VBox carte = new VBox(3);
        carte.setUserData(sousTache);
        carte.setPadding(new Insets(5, 5, 5, 25*niveau)); // Indentation plus marquée
        carte.setStyle("-fx-background-color: #f8f8f8; " +
                "-fx-border-radius: 3; -fx-background-radius: 3;");

        HBox ligneTitre = new HBox(5);

        Label point = new Label("•");
        point.setStyle("-fx-text-fill: #777777; -fx-font-size: 12px;");

        Label titre = new Label(sousTache.getTitre());
        titre.setStyle("-fx-text-fill: #555555; -fx-font-size: 11px; -fx-font-weight: bold;");

        ligneTitre.getChildren().addAll(point, titre);
        if (sousTache.estComposite()) {
            Button btnAdd = new Button("rajoute");
            btnAdd.setStyle("-fx-font-size: 9px; -fx-text-fill: blue; -fx-padding: 2 5;");
            btnAdd.setUserData(sousTache);
            boutonsInteractifs.add(btnAdd);
            ligneTitre.getChildren().add(btnAdd);
        }
        Label description = new Label(sousTache.getDescription());
        description.setStyle("-fx-text-fill: #777777; -fx-font-size: 10px;");
        description.setWrapText(true);
        description.setPadding(new Insets(0, 0, 0, 15));

        carte.getChildren().addAll(ligneTitre, description);
        return carte;
    }

    private String getStyleTitreParEtat(String etat) {
        switch (etat) {
            case "encours":
                return "-fx-font-weight: bold; -fx-text-fill: #e67e22; -fx-font-size: 13px;";
            case "terminer":
                return "-fx-font-weight: bold; -fx-text-fill: #27ae60; -fx-font-size: 13px; " +
                        "-fx-strikethrough: true;";
            default: // afaire
                return "-fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-font-size: 13px;";
        }
    }

    private String getStyleEtat(String etat) {
        switch (etat) {
            case "encours":
                return "-fx-font-size: 10px; -fx-text-fill: #e67e22; -fx-font-weight: bold;";
            case "terminer":
                return "-fx-font-size: 10px; -fx-text-fill: #27ae60; -fx-font-weight: bold;";
            default: // afaire
                return "-fx-font-size: 10px; -fx-text-fill: #7f8c8d;";
        }
    }

    private String getEtatTexte(String etat) {
        switch (etat) {
            case "afaire": return "À faire";
            case "encours": return "En cours";
            case "terminer": return "Terminé";
            default: return etat;
        }
    }

    private void afficherSousTachesRecursif(Tache parent, VBox conteneur, int niveau) {
        if (!parent.estComposite()) return;

        for (Tache sousTache : parent.getSousTaches()) {
            VBox carteSousTache = creerCarteSousTache(sousTache, niveau);
            cartesTaches.add(carteSousTache);
            conteneur.getChildren().add(carteSousTache);
            if (sousTache.estComposite()) {
                afficherSousTachesRecursif(sousTache, conteneur, niveau + 1);
            }
        }
    }
}