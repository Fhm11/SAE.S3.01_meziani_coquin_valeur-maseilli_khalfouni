package source;

import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.Insets;
import java.util.ArrayList;
import java.util.List;

public class VueBureau implements Observateur {
    private VBox root;
    private TacheManager modele;
    private HBox conteneurColonnes;
    private List<VBox> colonnesGraphiques = new ArrayList<>();
    private List<Button> boutonsInteractifs = new ArrayList<>();
    private List<VBox> cartesTaches = new ArrayList<>();
    private List<HBox> sousTachesBoxes = new ArrayList<>();

    public VueBureau(TacheManager modele) {
        this.modele = modele;
        root = new VBox(10);
        root.setPadding(new Insets(10));

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);

        conteneurColonnes = new HBox(15);
        conteneurColonnes.setPadding(new Insets(10));

        scrollPane.setContent(conteneurColonnes);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        root.getChildren().addAll(scrollPane);
    }

    public VBox getRoot() {
        return root;
    }

    public List<Button> getBoutonsInteractifs() {
        return boutonsInteractifs;
    }

    public List<VBox> getCartesTaches() {
        return cartesTaches;
    }

    public List<HBox> getSousTachesBoxes() {
        return sousTachesBoxes;
    }

    public List<VBox> getColonnesGraphiques() {
        return colonnesGraphiques;
    }

    public HBox getConteneurColonnes() {
        return conteneurColonnes;
    }

    @Override
    public void actualiser() {
        conteneurColonnes.getChildren().clear();
        colonnesGraphiques.clear();
        boutonsInteractifs.clear();
        cartesTaches.clear();
        sousTachesBoxes.clear();

        for (String nomColonne : modele.getColonnes()) {
            VBox colBox = creerColonne(nomColonne);
            colonnesGraphiques.add(colBox);
            for (Tache t : modele.getTaches()) {
                if (nomColonne.equals(t.getEtat())) {
                    VBox carte = creerAffichageTache(t);
                    cartesTaches.add(carte);
                    colBox.getChildren().add(carte);
                }
            }
            conteneurColonnes.getChildren().add(colBox);
        }
    }

    private VBox creerAffichageTache(Tache t) {
        VBox conteneur = new VBox(5);
        conteneur.setPadding(new Insets(10));
        String styleOrigine = "-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 1); -fx-background-radius: 5;";
        conteneur.getProperties().put("style_origine", styleOrigine);
        conteneur.setStyle(styleOrigine);
        conteneur.setUserData(t);

        Label labelTitre = new Label(t.getTitre());
        labelTitre.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");

        Label labelPriorite = new Label(t.getPriorite().toUpperCase());
        String stylePriorite = "-fx-font-size: 9px; -fx-text-fill: white; -fx-padding: 2 5; -fx-background-radius: 3; -fx-font-weight: bold;";

        if ("Importante".equals(t.getPriorite())) {
            labelPriorite.setStyle(stylePriorite + "-fx-background-color: #e74c3c;");
        } else if ("Moyenne".equals(t.getPriorite())) {
            labelPriorite.setStyle(stylePriorite + "-fx-background-color: #f1c40f;");
        } else {
            labelPriorite.setStyle(stylePriorite + "-fx-background-color: #008000;");
        }

        Label labelDesc = new Label(t.getDescription());
        labelDesc.setStyle("-fx-text-fill: #555555; -fx-font-size: 11px;");
        labelDesc.setWrapText(true);

        Label labelDate = new Label("dta deb : " + t.getJDebut());
        labelDate.setStyle("-fx-text-fill: #888888; -fx-font-size: 10px; -fx-font-style: italic;");

        conteneur.setUserData(t);

        HBox boutons = new HBox(5);
        boutons.setAlignment(Pos.CENTER_RIGHT);

        if (t.estComposite()) {
            Button btnAjouterSous = new Button("+");
            btnAjouterSous.setUserData(t);
            boutonsInteractifs.add(btnAjouterSous);
            boutons.getChildren().add(btnAjouterSous);
        }

        Button btnSupprimer = new Button("Archiver");
        btnSupprimer.setStyle("-fx-text-fill: white; -fx-background-color: #e67e22; -fx-font-weight: bold;");
        btnSupprimer.setUserData(t);
        boutonsInteractifs.add(btnSupprimer);
        boutons.getChildren().add(btnSupprimer);

        conteneur.getChildren().addAll(labelTitre, labelPriorite, labelDesc, boutons, labelDate);

        if (t.estComposite()) {
            afficherSousTachesRecursif(t, conteneur, 1);
        }
        return conteneur;
    }

    private VBox creerColonne(String titre) {
        VBox col = new VBox(10);
        col.setPadding(new Insets(10));
        col.setMinWidth(250);
        String styleOrigine = "-fx-border-color: lightgray; -fx-border-width: 1; -fx-background-color: #f4f4f4;";
        col.setStyle(styleOrigine);
        col.getProperties().put("style_origine", styleOrigine);
        col.setUserData(titre);

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        Label lblTitre = new Label(titre);
        lblTitre.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        lblTitre.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(lblTitre, Priority.ALWAYS);
        Button btnSupCol = new Button("X");
        btnSupCol.setStyle("-fx-text-fill: white; -fx-background-color: #ff4444; -fx-font-size: 10px; -fx-font-weight: bold;");
        btnSupCol.setUserData(titre);
        boutonsInteractifs.add(btnSupCol);
        header.getChildren().addAll(lblTitre, btnSupCol);
        col.getChildren().add(header);
        return col;
    }

    private void afficherSousTachesRecursif(Tache parent, VBox conteneurParent, int niveau) {
        if (!parent.estComposite())
            return;
        for (Tache sub : parent.getSousTaches()) {
            if (!"archive".equals(sub.getEtat())) {
                HBox boxSousTache = new HBox(5);

                int decalage = 20 + (niveau * 15);
                boxSousTache.setPadding(new Insets(2, 0, 2, decalage));

                // style avec bordure gauche pour montrer la hiérarchie
                boxSousTache.setStyle("-fx-border-color: #eeeeee; -fx-border-width: 0 0 0 2;");
                boxSousTache.setUserData(sub);

                // ajouter aux listes pour pouvoir les manipuler
                sousTachesBoxes.add(boxSousTache); // Pour le drag & drop

                // contenu de la sous-tâche
                Label lTitre = new Label("• " + sub.getTitre());
                lTitre.setStyle("-fx-text-fill: #333333; -fx-font-size: 11px; -fx-font-weight: bold;");
                lTitre.setUserData(sub);

                Label labelPriorite = new Label(sub.getPriorite().toUpperCase());
                String stylePriorite = "-fx-font-size: 9px; -fx-text-fill: white; -fx-padding: 2 5; -fx-background-radius: 3; -fx-font-weight: bold;";

                if ("Importante".equals(sub.getPriorite())) {
                    labelPriorite.setStyle(stylePriorite + "-fx-background-color: #e74c3c;");
                } else if ("Moyenne".equals(sub.getPriorite())) {
                    labelPriorite.setStyle(stylePriorite + "-fx-background-color: #f1c40f;");
                } else {
                    labelPriorite.setStyle(stylePriorite + "-fx-background-color: #008000;");
                }

                Label lDate = new Label("dta deb" + sub.getJDebut());
                lDate.setStyle("-fx-text-fill: #999999; -fx-font-size: 10px;");
                lDate.setUserData(sub);

                Button btnSup = new Button("Archiver");
                btnSup.setStyle(
                        "-fx-font-size: 9px; -fx-text-fill: white; -fx-background-color: #e67e22; -fx-padding: 2 6; -fx-background-radius: 4;");
                btnSup.setUserData(sub);
                boutonsInteractifs.add(btnSup);

                // ligne avec tous les éléments
                HBox ligne = new HBox(5);
                ligne.setAlignment(Pos.CENTER_LEFT);
                ligne.getChildren().addAll(lTitre, labelPriorite, btnSup, lDate);

                // bouton "+" si composite
                if (sub.estComposite()) {
                    Button btnAdd = new Button("+");
                    btnAdd.setText("+");
                    btnAdd.setStyle("-fx-font-size: 9px; -fx-text-fill: blue;");
                    btnAdd.setUserData(sub);
                    boutonsInteractifs.add(btnAdd);
                    ligne.getChildren().add(btnAdd);
                }

                boxSousTache.getChildren().add(ligne);

                // ajouter la sous-tâche au conteneur parent
                conteneurParent.getChildren().add(boxSousTache);

                // appel récursif pour les sous-sous-tâches (avec niveau+1)
                afficherSousTachesRecursif(sub, conteneurParent, niveau + 1);
            }
        }
    }
}