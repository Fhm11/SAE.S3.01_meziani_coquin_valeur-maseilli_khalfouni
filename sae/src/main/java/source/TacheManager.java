package source;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.ArrayList;

public class TacheManager implements Sujet {
    private static TacheManager instance;
    private ArrayList<Observateur> observateurs;
    private ArrayList<Tache> listeTaches;

    private TacheManager() {
        observateurs = new ArrayList<>();
        listeTaches = new ArrayList<>();
    }

    public static synchronized TacheManager getInstance() {
        if (instance == null) {
            instance = new TacheManager();
        }
        return instance;
    }

    public void creerTacheSimple(String titre, String description) {
        if (titre == null || titre.trim().isEmpty()) {
            throw new IllegalArgumentException("titre obligatoire");
        }
        Tache t = TacheFactory.creerTacheSimple(titre, description);
        listeTaches.add(t);
        notifierObservateur();
    }

    public void creerTacheComposite(String titre, String description) {
        if (titre == null || titre.trim().isEmpty()) {
            throw new IllegalArgumentException("titre obligatoire");
        }
        Tache t = TacheFactory.creerTacheComposite(titre, description);
        listeTaches.add(t);
        notifierObservateur();
    }

    public void modifierTache(Tache t, String titre, String description) {
        if (t == null || titre == null || titre.trim().isEmpty()) {
            throw new IllegalArgumentException("Paramètres invalides");
        }
        t.setTitre(titre);
        t.setDescription(description);
        notifierObservateur();
    }

    public void ajouterSousTache(Tache parent, String titre, String description) {
        if (parent == null || !parent.estComposite() ||
                titre == null || titre.trim().isEmpty()) {
            throw new IllegalArgumentException("Impossible d'ajouter une sous-tâche");
        }

        Tache sousTache = TacheFactory.creerTacheSimple(titre, description);
        TacheComposite composite = (TacheComposite) parent;
        composite.ajouterSousTache(sousTache);
        notifierObservateur();
    }

    public void supprimerTache(Tache t) {
        if (t == null) return;

        if (listeTaches.remove(t)) {
            notifierObservateur();
            return;
        }

        for (Tache tache : listeTaches) {
            if (tache.estComposite()) {
                TacheComposite composite = (TacheComposite) tache;
                if (composite.retirerSousTache(t)) {
                    notifierObservateur();
                    return;
                }
            }
        }
    }

    public void afficherFormulaireCreation() {
        Stage fenetre = new Stage();
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        TextField txtTitre = new TextField();
        TextField txtDesc = new TextField();
        CheckBox chkComposite = new CheckBox("Peut avoir des sous-tâches");
        Button btnSave = new Button("Sauvegarder");

        btnSave.setOnAction(e -> {
            try {
                String titre = txtTitre.getText();
                String description = txtDesc.getText();

                if (chkComposite.isSelected()) {
                    creerTacheComposite(titre, description);
                } else {
                    creerTacheSimple(titre, description);
                }
                fenetre.close();
            } catch (IllegalArgumentException ex) {
                System.err.println(ex.getMessage());
            }
        });

        root.getChildren().addAll(
                new Label("Titre :"), txtTitre,
                new Label("Description :"), txtDesc,
                chkComposite, btnSave
        );

        fenetre.setScene(new Scene(root, 300, 200));
        fenetre.setTitle("Créer une tâche");
        fenetre.show();
    }

    public void afficherFormulaireModification(Tache t) {
        Stage fenetre = new Stage();
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        TextField txtTitre = new TextField(t.getTitre());
        TextField txtDesc = new TextField(t.getDescription());
        Button btnSave = new Button("Sauvegarder");

        btnSave.setOnAction(e -> {
            try {
                modifierTache(t, txtTitre.getText(), txtDesc.getText());
                fenetre.close();
            } catch (IllegalArgumentException ex) {
                System.err.println(ex.getMessage());
            }
        });

        root.getChildren().addAll(
                new Label("Titre :"), txtTitre,
                new Label("Description :"), txtDesc,
                btnSave
        );

        fenetre.setScene(new Scene(root, 300, 200));
        fenetre.setTitle("Modifier la tâche");
        fenetre.show();
    }

    public void afficherFormulaireSousTache(Tache parent) {
        Stage fenetre = new Stage();
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        TextField txtTitre = new TextField();
        TextField txtDesc = new TextField();
        Button btnSave = new Button("Ajouter");

        btnSave.setOnAction(e -> {
            try {
                ajouterSousTache(parent, txtTitre.getText(), txtDesc.getText());
                fenetre.close();
            } catch (IllegalArgumentException ex) {
                System.err.println(ex.getMessage());
            }
        });

        root.getChildren().addAll(
                new Label("Ajouter sous-tâche à : " + parent.getTitre()),
                new Label("Titre :"), txtTitre,
                new Label("Description :"), txtDesc,
                btnSave
        );

        fenetre.setScene(new Scene(root, 300, 250));
        fenetre.setTitle("Ajouter une sous-tâche");
        fenetre.show();
    }

    @Override
    public void ajouterObservateur(Observateur o) {
        if (o != null && !observateurs.contains(o)) {
            observateurs.add(o);
        }
    }

    @Override
    public void supprimerObservateur(Observateur o) {
        observateurs.remove(o);
    }

    @Override
    public void notifierObservateur() {
        for (Observateur o : observateurs) {
            o.actualiser();
        }
    }

    public ArrayList<Tache> getTaches() {
        return listeTaches;
    }
}