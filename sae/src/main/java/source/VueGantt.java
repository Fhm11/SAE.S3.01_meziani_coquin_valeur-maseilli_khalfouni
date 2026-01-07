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
    private List<String> JOURS_REF = Arrays.asList("Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche");

    public VueGantt(TacheManager modele) {
        this.modele = modele;
        this.gp = new GridPane();
        this.gp.setHgap(0);
        this.gp.setVgap(15);
        this.gp.setPadding(new Insets(20));
        this.sp = new ScrollPane();
        this.sp.setContent(gp);
        this.sp.setFitToWidth(false);
    }

    public ScrollPane getRoot() {
        return sp;
    }

    @Override
    public void actualiser() {
        gp.getChildren().clear();
        gp.getColumnConstraints().clear();

        int maxOffset = 6;
        for (Tache t : modele.getTaches()) {
            maxOffset = Math.max(maxOffset, calculerOffsetFin(t));
            if (t.estComposite()) {
                for (Tache st : t.getSousTaches()) {
                    maxOffset = Math.max(maxOffset, calculerOffsetFin(st));
                }
            }
        }

        gp.getColumnConstraints().add(new ColumnConstraints(150));
        for (int i = 0; i <= maxOffset; i++) {
            gp.getColumnConstraints().add(new ColumnConstraints(120));
            String nomJour = JOURS_REF.get(i % 7);
            if (i >= 7) nomJour += " (semaine suivante)";

            Label lbl = new Label(nomJour);
            lbl.setMaxWidth(Double.MAX_VALUE);
            lbl.setAlignment(Pos.CENTER);
            lbl.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 10; -fx-background-color: #f8f9fa; -fx-border-color: #999; -fx-border-width: 0 0 2 2;");
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

    private int calculerOffsetFin(Tache t) {
        int idxDep = JOURS_REF.indexOf(t.getJDebut());
        int idxFin = JOURS_REF.indexOf(t.getJFin());
        if (idxFin < idxDep) {
            return idxFin + 7;
        }
        return idxFin;
    }

    private int dessinerLigneTache(Tache t, int numLigne, int niveau) {
        int idxDep = JOURS_REF.indexOf(t.getJDebut());
        int offsetFin = calculerOffsetFin(t);
        int duree = offsetFin - idxDep + 1;

        Label barre = new Label(t.getTitre());
        String baseStyle = "-fx-text-fill: white; -fx-padding: 8; -fx-background-radius: 5; -fx-font-size: 13px; -fx-font-weight: bold; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 3, 0, 0, 1);";

        String color = (niveau > 0) ? "#5dade2" : "#3498db";
        barre.setStyle("-fx-background-color: " + color + "; " + baseStyle);
        barre.setMaxWidth(Double.MAX_VALUE);

        StackPane conteneurBarre = new StackPane(barre);
        conteneurBarre.setPadding(new Insets(0, 5, 0, 5));

        gp.add(conteneurBarre, idxDep + 1, numLigne, duree, 1);

        return numLigne + 1;
    }
}