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

/**
 * Vue Archive
 * Affiche les tâches archivées avec possibilité de les restaurer
 */
public class VueArchive implements Observateur {

    private TacheManager modele;

    private ScrollPane panneauDefilement;
    private VBox contenuPrincipal;

    private List<Button> boutonsInteractifs = new ArrayList<>();

    public VueArchive() {
    }

    /**
     * Constructeur de la vue Archive
     * 
     * @param modele le gestionnaire de tâches
     */
    public VueArchive(TacheManager modele) {
        this.modele = modele;

        contenuPrincipal = new VBox(10);
        contenuPrincipal.setPadding(new Insets(10));

        panneauDefilement = new ScrollPane(contenuPrincipal);
        panneauDefilement.setFitToWidth(true);
        panneauDefilement.setStyle("-fx-background: #f0f0f0;");
    }

    /**
     * Récupère la racine de la vue
     * 
     * @return le panneau de défilement
     */
    public ScrollPane getRacine() {
        return panneauDefilement;
    }

    /**
     * Récupère la liste des boutons interactifs
     * 
     * @return la liste des boutons
     */
    public List<Button> getBoutonsInteractifs() {
        return boutonsInteractifs;
    }

    /**
     * Actualise l'affichage de l'archive
     */
    @Override
    public void actualiser() {
        contenuPrincipal.getChildren().clear();
        boutonsInteractifs.clear();

        boolean archiveVide = true;

        for (Tache tache : modele.getTaches()) {
            if (chercherArchivesRecursif(tache)) {
                archiveVide = false;
            }
        }

        if (archiveVide) {
            Label labelVide = new Label("0");
            labelVide.setStyle("-fx-text-fill: gray; -fx-font-style: italic; " +
                    "-fx-font-weight: bold; -fx-font-size: 60px;");
            labelVide.setAlignment(Pos.CENTER);
            contenuPrincipal.getChildren().add(labelVide);
        }
    }

    /**
     * Crée une carte pour une tâche archivée
     * 
     * @param tache la tâche archivée
     * @return le conteneur VBox de la carte
     */
    private VBox creerCarteArchive(Tache tache) {
        VBox carte = new VBox(5);
        carte.setStyle("-fx-background-color: white; " +
                "-fx-border-color: #cccccc; -fx-padding: 10; " +
                "-fx-background-radius: 5;");

        carte.setUserData(tache);

        String provenance;
        if (tache.getAncienEtat() != null) {
            provenance = tache.getAncienEtat();
        } else {
            provenance = "inconnue";
        }

        Label titre = new Label(tache.getTitre() + " (provenance : " + provenance + ")");
        titre.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label description = new Label(tache.getDescription());
        description.setStyle("-fx-text-fill: #666;");

        HBox boutons = new HBox(10);
        boutons.setAlignment(Pos.CENTER_RIGHT);

        Button btnRestaurer = new Button("Restaurer");
        btnRestaurer.setStyle("-fx-text-fill: white; -fx-background-color: #27ae60; -fx-font-weight: bold;");
        btnRestaurer.setUserData(tache);
        boutonsInteractifs.add(btnRestaurer);

        Button btnSupprimer = new Button("supprimer");
        btnSupprimer.setStyle("-fx-text-fill: white; -fx-background-color: #c0392b; -fx-font-weight: bold;");
        btnSupprimer.setUserData(tache);
        boutonsInteractifs.add(btnSupprimer);

        boutons.getChildren().addAll(btnRestaurer, btnSupprimer);

        carte.getChildren().addAll(titre, description, boutons);

        return carte;
    }

    /**
     * Cherche récursivement les tâches archivées
     * 
     * @param tache la tâche à examiner
     * @return true si une tâche archivée a été trouvée
     */
    private boolean chercherArchivesRecursif(Tache tache) {
        boolean trouve = false;

        if ("archive".equals(tache.getEtat())) {
            contenuPrincipal.getChildren().add(creerCarteArchive(tache));
            return true;
        }

        if (tache.estComposite()) {
            for (Tache sousTache : tache.getSousTaches()) {
                if (chercherArchivesRecursif(sousTache)) {
                    trouve = true;
                }
            }
        }

        return trouve;
    }
}