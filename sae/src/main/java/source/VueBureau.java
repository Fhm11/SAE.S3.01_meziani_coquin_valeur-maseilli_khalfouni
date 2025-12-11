package source;

import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.control.Label;
import javafx.geometry.Insets;

public class VueBureau implements Observateur {

    private VBox root;

    public VueBureau() {
        root = new VBox(10);
        root.setPadding(new Insets(10));
    }

    public VBox getRoot() {
        return root;
    }

    @Override
    public void actualiser() {
        root.getChildren().clear();
        for (Tache t : TacheManager.getInstance().getTaches()) {
            HBox rectangle = new HBox();
            rectangle.setPadding(new Insets(10));
            rectangle.setSpacing(10);
            rectangle.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, new CornerRadii(5), Insets.EMPTY)));
            Label label = new Label(t.getTitre() + "\n" + t.getDescription());
            rectangle.getChildren().add(label);
            root.getChildren().add(rectangle);
            System.out.println(t);
        }
    }
}
