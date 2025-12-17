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

    private VBox colonneAFaire;
    private VBox colonneEnCours;
    private VBox colonneTermine;

    // Stocker les composants pour configuration par Main
    private List<Button> boutonsInteractifs = new ArrayList<>();
    private List<VBox> cartesTaches = new ArrayList<>();

    public VueBureau(TacheManager modele) {
        this.modele = modele;
        root = new VBox(10);
        root.setPadding(new Insets(10));

        colonneAFaire = creerColonne("À faire");
        colonneEnCours = creerColonne("En cours");
        colonneTermine = creerColonne("Terminée");

        HBox conteneurColonnes = new HBox(15);
        conteneurColonnes.getChildren().addAll(colonneAFaire, colonneEnCours, colonneTermine);

        HBox.setHgrow(colonneAFaire, Priority.ALWAYS);
        HBox.setHgrow(colonneEnCours, Priority.ALWAYS);
        HBox.setHgrow(colonneTermine, Priority.ALWAYS);
        VBox.setVgrow(conteneurColonnes, Priority.ALWAYS);

        root.getChildren().addAll(conteneurColonnes);
    }

    public VBox getRoot() {
        return root;
    }

    // Getters pour que Main configure les handlers
    public List<Button> getBoutonsInteractifs() {
        return boutonsInteractifs;
    }

    public List<VBox> getCartesTaches() {
        return cartesTaches;
    }

    @Override
    public void actualiser() {
        nettoyerColonne(colonneAFaire);
        nettoyerColonne(colonneEnCours);
        nettoyerColonne(colonneTermine);

        boutonsInteractifs.clear();
        cartesTaches.clear();

        for (Tache t : modele.getTaches()) {
            VBox carte = creerAffichageTache(t);
            cartesTaches.add(carte);

            String etat = t.getEtat();
            if ("afaire".equals(etat)) {
                colonneAFaire.getChildren().add(carte);
            } else if ("encours".equals(etat)) {
                colonneEnCours.getChildren().add(carte);
            } else if ("terminer".equals(etat)) {
                colonneTermine.getChildren().add(carte);
            }
        }
    }

    private void nettoyerColonne(VBox col) {
        if (col.getChildren().size() > 1) {
            col.getChildren().remove(1, col.getChildren().size());
        }
    }

    private VBox creerAffichageTache(Tache t) {
        VBox conteneur = new VBox(5);
        conteneur.setPadding(new Insets(10));
        conteneur.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 1); -fx-background-radius: 5;");

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
        col.setStyle("-fx-border-color: lightgray; -fx-border-width: 1; -fx-background-color: #f4f4f4;");
        Label lblTitre = new Label(titre);
        lblTitre.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        col.getChildren().add(lblTitre);
        return col;
    }
}