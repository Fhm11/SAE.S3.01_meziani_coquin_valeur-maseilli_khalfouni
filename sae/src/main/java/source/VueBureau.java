package source;

import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.control.Label;
import javafx.geometry.Insets;

/**
 * Classe qui permet d'afficher la vue Bureau
 */
public class VueBureau implements Observateur {

    private VBox root;

    /**
     * Constructeur pour créer la vue
     */
    public VueBureau() {
        root = new VBox(10);
        root.setPadding(new Insets(10));
    }

    /**
     * Getter pour l'attribut root
     * @return le root
     */
    public VBox getRoot() {
        return root;
    }

    /**
     * Méthode actualiser qui permet de mettre à jour la vue
     */
    @Override
    public void actualiser() {
        root.getChildren().clear();
        for (Tache t : TacheManager.getInstance().getTaches()) {
            HBox rectangle = new HBox();
            rectangle.setPadding(new Insets(10));
            rectangle.setSpacing(10);
            rectangle.setStyle(
                    "-fx-background-color: lightblue; " +
                            "-fx-background-radius: 5;"
            );
            Label label = new Label(t.getTitre() + "\n" + t.getDescription());
            rectangle.setOnMouseClicked(e -> Formulaire.modifierTache(t));

            rectangle.getChildren().add(label);
            root.getChildren().add(rectangle);
            System.out.println(t);
        }
    }
}
