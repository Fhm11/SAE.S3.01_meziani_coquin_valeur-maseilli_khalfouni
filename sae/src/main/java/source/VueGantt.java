package source;

import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import java.util.*;

public class VueGantt implements Observateur {

    private ScrollPane sp;
    private GridPane gp;
    private TacheManager modele;
    private List<String> JOURS;

    public VueGantt(TacheManager modele) {
        this.modele = modele;

        this.JOURS = new ArrayList<>();
        this.JOURS.add("Lundi");
        this.JOURS.add("Mardi");
        this.JOURS.add("Mercredi");
        this.JOURS.add("Jeudi");
        this.JOURS.add("Vendredi");
        this.JOURS.add("Samedi");
        this.JOURS.add("Dimanche");

        this.gp = new GridPane();
        this.gp.setHgap(0);
        this.gp.setVgap(15);
        this.gp.setPadding(new Insets(20));

        this.sp = new ScrollPane();
        this.sp.setContent(gp);
        this.sp.setFitToWidth(true);
    }

    public ScrollPane getRoot() {
        return sp;
    }

    @Override
    public void actualiser() {
        gp.getChildren().clear();
        gp.getColumnConstraints().clear();

        ColumnConstraints colVideTitre = new ColumnConstraints(20);
        gp.getColumnConstraints().add(colVideTitre);

        for (int i = 0; i < JOURS.size(); i++) {
            ColumnConstraints colJour = new ColumnConstraints(120);
            gp.getColumnConstraints().add(colJour);

            Label lbl = new Label(JOURS.get(i));
            lbl.setMaxWidth(Double.MAX_VALUE);
            lbl.setAlignment(Pos.CENTER);
            lbl.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-padding: 10; -fx-background-color: #f8f9fa; -fx-border-color: #999; -fx-border-width: 0 0 2 2;");

            gp.add(lbl, i + 1, 0);
        }

        int ligne = 1;
        for (Tache t : modele.getTaches()) {
            ligne = dessinerLigneTache(t, ligne, 0);

            if (t.estComposite()) {
                for (Tache st : t.getSousTaches()) {
                    ligne = dessinerLigneTache(st, ligne, 1);
                }
            }
        }
    }

    private int dessinerLigneTache(Tache t, int numLigne, int niveau) {
        int colDep = JOURS.indexOf(t.getJDebut()) + 1;
        int colFin = JOURS.indexOf(t.getJFin()) + 1;
        int duree = Math.max(1, colFin - colDep + 1);

        Label barre = new Label(t.getTitre());
        String baseStyle = "-fx-text-fill: white; -fx-padding: 8; -fx-background-radius: 5; -fx-font-size: 13px; -fx-font-weight: bold; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 3, 0, 0, 1);";

        StackPane conteneurBarre = new StackPane(barre);
        conteneurBarre.setPadding(new Insets(0, 5, 0, 5));

        if (niveau > 0) {
            barre.setStyle("-fx-background-color: #5dade2; " + baseStyle);
        } else {
            barre.setStyle("-fx-background-color: #3498db; " + baseStyle);
        }

        barre.setMaxWidth(Double.MAX_VALUE);
        gp.add(conteneurBarre, colDep, numLigne, duree, 1);

        return numLigne + 1;
    }
}