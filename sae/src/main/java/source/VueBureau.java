package source;

import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
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
            VBox conteneurTache = new VBox(5);
            conteneurTache.setPadding(new Insets(10));
            conteneurTache.setStyle(
                    "-fx-background-color: lightblue; " +
                            "-fx-background-radius: 5;"
            );

            HBox header = new HBox(10);
            Label label = new Label(t.getTitre() + "\n" + t.getDescription());

            conteneurTache.setOnMouseClicked(e -> {
                if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
                    // Double-clic pour modifier
                    Formulaire.modifierTache(t, new Controller());
                }
            });

            header.getChildren().add(label);

            if (t.estComposite()) {
                Button btnAjouterSousTache = new Button("+");
                btnAjouterSousTache.setOnAction(e -> {
                    // Ouvrir formulaire pour sous-tâche
                    Formulaire.afficherPopupSousTache(new Controller(), t);
                });
                header.getChildren().add(btnAjouterSousTache);
            }

            conteneurTache.getChildren().add(header);

            if (t.estComposite()) {
                VBox sousTachesContainer = new VBox(5);
                sousTachesContainer.setPadding(new Insets(5, 0, 0, 20)); // Indentation

                TacheComposite tComposite = (TacheComposite) t;
                for (Tache sousTache : tComposite.getSousTaches()) {
                    HBox sousTaskBox = new HBox();
                    sousTaskBox.setPadding(new Insets(5));
                    sousTaskBox.setStyle(
                            "-fx-background-color: #e6f3ff; " +
                                    "-fx-background-radius: 3;"
                    );

                    Label sousLabel = new Label("  | " + sousTache.getTitre() +
                            "\n      " + sousTache.getDescription());

                    // Clic pour modifier la sous-tâche
                    sousTaskBox.setOnMouseClicked(e -> {
                        if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
                            Formulaire.modifierTache(sousTache, new Controller());
                        }
                    });

                    sousTaskBox.getChildren().add(sousLabel);
                    sousTachesContainer.getChildren().add(sousTaskBox);
                }

                conteneurTache.getChildren().add(sousTachesContainer);
            }

            root.getChildren().add(conteneurTache);
            System.out.println(t);
        }
    }
}
