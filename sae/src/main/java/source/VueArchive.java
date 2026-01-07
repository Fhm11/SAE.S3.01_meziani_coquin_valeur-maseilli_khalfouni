package source;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class VueArchive implements Observateur {
    private TacheManager modele;
    private ScrollPane scrollPane;
    private VBox contenuPrincipal;
    private List<Button> boutonsInteractifs = new ArrayList<>();

    public VueArchive(TacheManager modele) {
        this.modele = modele;
        contenuPrincipal = new VBox(10);
        contenuPrincipal.setPadding(new Insets(10));
        scrollPane = new ScrollPane(contenuPrincipal);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #f0f0f0;");
    }

    public ScrollPane getRoot() {
        return scrollPane;
    }

    public List<Button> getBoutonsInteractifs() {
        return boutonsInteractifs;
    }

    @Override
    public void actualiser() {
        contenuPrincipal.getChildren().clear();
        boutonsInteractifs.clear();
        boolean vide = true;
        for (Tache t : modele.getTaches()) {
            if (chercherArchivesRecursif(t)) {
                vide = false;
            }
        }
        if (vide) {
            Label lblVide = new Label("0");
            lblVide.setStyle("-fx-text-fill: gray; -fx-font-style: italic;");
            contenuPrincipal.getChildren().add(lblVide);
        }
    }

    private VBox creerCarteArchive(Tache t) {
        VBox carte = new VBox(5);
        carte.setStyle(
                "-fx-background-color: white; -fx-border-color: #cccccc; -fx-padding: 10; -fx-background-radius: 5;");
        carte.setUserData(t);
        String provenance;
        if (t.getAncienEtat() != null) {
            provenance = t.getAncienEtat();
        } else {
            provenance = "jsp";
        }
        Label titre = new Label(t.getTitre() + " (vient de : " + provenance + ")");
        titre.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        Label desc = new Label(t.getDescription());
        desc.setStyle("-fx-text-fill: #666;");
        HBox boutons = new HBox(10);
        boutons.setAlignment(Pos.CENTER_RIGHT);
        Button btnRestaurer = new Button("Restaurer");
        btnRestaurer.setStyle("-fx-text-fill: white; -fx-background-color: #27ae60; -fx-font-weight: bold;");
        btnRestaurer.setUserData(t);
        boutonsInteractifs.add(btnRestaurer);
        Button btnSupp = new Button("supprimer");
        btnSupp.setStyle("-fx-text-fill: white; -fx-background-color: #c0392b; -fx-font-weight: bold;");
        btnSupp.setUserData(t);
        boutonsInteractifs.add(btnSupp);
        boutons.getChildren().addAll(btnRestaurer, btnSupp);
        carte.getChildren().addAll(titre, desc, boutons);
        return carte;
    }

    private boolean chercherArchivesRecursif(Tache t) {
        boolean trouve = false;
        if ("archive".equals(t.getEtat())) {
            contenuPrincipal.getChildren().add(creerCarteArchive(t));
            return true;
        }
        if (t.estComposite()) {
            for (Tache sub : t.getSousTaches()) {
                if (chercherArchivesRecursif(sub)) {
                    trouve = true;
                }
            }
        }
        return trouve;
    }
}