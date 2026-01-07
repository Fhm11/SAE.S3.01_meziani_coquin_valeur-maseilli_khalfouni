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
            maxOffset = Math.max(maxOffset, trouverMaxOffsetRecursif(t));
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

        int ligneCourante = 1;
        for (Tache t : modele.getTaches()) {
            ligneCourante = afficherTacheRecursif(t, ligneCourante, 0);
        }
    }

    private int trouverMaxOffsetRecursif(Tache t) {
        int max = calculerOffsetFin(t);
        if (t.estComposite()) {
            for (Tache st : t.getSousTaches()) {
                max = Math.max(max, trouverMaxOffsetRecursif(st));
            }
        }
        return max;
    }

    private int afficherTacheRecursif(Tache t, int numLigne, int niveau) {
        int prochaineLigne = dessinerLigneTache(t, numLigne, niveau);

        if (t.estComposite()) {
            for (Tache st : t.getSousTaches()) {
                prochaineLigne = afficherTacheRecursif(st, prochaineLigne, niveau + 1);
            }
        }
        return prochaineLigne;
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

        String color;
        if (niveau == 0) color = "#3498db";
        else if (niveau == 1) color = "#5dade2";
        else color = "#aed6f1";

        barre.setStyle("-fx-background-color: " + color + "; " + baseStyle);
        barre.setMaxWidth(Double.MAX_VALUE);

        StackPane conteneurBarre = new StackPane(barre);
        conteneurBarre.setPadding(new Insets(0, 5, 0, 5));

        gp.add(conteneurBarre, idxDep + 1, numLigne, duree, 1);

        return numLigne + 1;
    }
}