package source;

import javafx.geometry.Pos;
import javafx.scene.input.MouseButton;
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
        conteneur.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 1); -fx-background-radius: 5;");
        conteneur.setUserData(t);
        Label labelTitre = new Label(t.getTitre());
        labelTitre.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");

        Label labelDesc = new Label(t.getDescription());
        labelDesc.setStyle("-fx-text-fill: #555555; -fx-font-size: 11px;");
        labelDesc.setWrapText(true);

        conteneur.setUserData(t);

        HBox boutons = new HBox(5);
        boutons.setAlignment(Pos.CENTER_RIGHT);

        if (t.estComposite()) {
            Button btnAjouterSous = new Button("+ Sous-tâche");
            btnAjouterSous.setUserData(t);
            boutonsInteractifs.add(btnAjouterSous);
            boutons.getChildren().add(btnAjouterSous);
        }

        Button btnSupprimer = new Button("Supprimer");
        btnSupprimer.setUserData(t);
        boutonsInteractifs.add(btnSupprimer);
        boutons.getChildren().add(btnSupprimer);

        conteneur.getChildren().addAll(labelTitre, labelDesc, boutons);

        if (t.estComposite()) {
            afficherSousTachesRecursif(t, conteneur);
        }

        if (t.estComposite()) {
            for(Tache sub : t.getSousTaches()) {
                VBox boxSousTache = new VBox(2);
                boxSousTache.setPadding(new Insets(0, 0, 0, 10));

                Label lTitre = new Label(" > " + sub.getTitre());
                lTitre.setStyle("-fx-text-fill: #333333; -fx-font-size: 10px; -fx-font-weight: bold;");

                Label lDesc = new Label("   " + sub.getDescription());
                lDesc.setStyle("-fx-text-fill: gray; -fx-font-size: 9px;");
                lDesc.setWrapText(true);

                boxSousTache.getChildren().addAll(lTitre, lDesc);
                conteneur.getChildren().add(boxSousTache);
            }
        }

        return conteneur;
    }

    private VBox creerColonne(String titre) {
        VBox col = new VBox(10);
        col.setPadding(new Insets(10));
        col.setMinWidth(250);
        col.setStyle("-fx-border-color: lightgray; -fx-border-width: 1; -fx-background-color: #f4f4f4;");
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

    private void afficherSousTachesRecursif(Tache parent, VBox conteneurParent) {
        if (!parent.estComposite()) return;
        for (Tache sub : parent.getSousTaches()) {
            VBox boxSousTache = new VBox(2);
            boxSousTache.setPadding(new Insets(2, 0, 2, 20));
            boxSousTache.setStyle("-fx-border-color: #eeeeee; -fx-border-width: 0 0 0 2;");
            HBox ligne = new HBox(5);
            ligne.setAlignment(Pos.CENTER_LEFT);
            Label lTitre = new Label("• " + sub.getTitre());
            lTitre.setStyle("-fx-text-fill: #333333; -fx-font-size: 11px; -fx-font-weight: bold;");
            Button btnSup = new Button("X");
            btnSup.setStyle("-fx-font-size: 9px; -fx-text-fill: red; -fx-font-weight: bold; -fx-background-color: transparent;");
            btnSup.setText("Supprimer");
            btnSup.setStyle("-fx-font-size: 9px; -fx-text-fill: red;");
            btnSup.setUserData(sub);
            boutonsInteractifs.add(btnSup);
            ligne.getChildren().addAll(lTitre, btnSup);
            if (sub.estComposite()) {
                Button btnAdd = new Button("+");
                btnAdd.setText("+ Sous-tâche");
                btnAdd.setStyle("-fx-font-size: 9px; -fx-text-fill: blue;");
                btnAdd.setUserData(sub);
                boutonsInteractifs.add(btnAdd);
                ligne.getChildren().add(btnAdd);
            }
            boxSousTache.getChildren().add(ligne);
            afficherSousTachesRecursif(sub, boxSousTache);
            conteneurParent.getChildren().add(boxSousTache);
        }
    }
}