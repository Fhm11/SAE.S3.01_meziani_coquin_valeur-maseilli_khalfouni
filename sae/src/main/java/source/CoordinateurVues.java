package source;

import javafx.scene.layout.BorderPane;

/**
 * Coordinateur des vues
 * Gère le changement de vue et les références
 */
public class CoordinateurVues {

    private BorderPane racinePrincipale;
    private Controller controleur;

    private VueBureau vueBureau;
    private VueListe vueListe;
    private VueGantt vueGantt;
    private VueArchive vueArchive;

    private ControllerBureau controleurBureau;
    private ControllerListe controleurListe;
    private ControllerArchive controleurArchive;

    public CoordinateurVues(BorderPane racinePrincipale, Controller controleur) {
        this.racinePrincipale = racinePrincipale;
        this.controleur = controleur;
    }

    /**
     * Initialise toutes les vues de l'application
     */
    public void initialiserVues(TacheManager modele) {
        vueBureau = new VueBureau(modele);
        vueListe = new VueListe(modele);
        vueGantt = new VueGantt(modele);
        vueArchive = new VueArchive(modele);

        controleurBureau = new ControllerBureau(controleur);
        controleurListe = new ControllerListe(controleur);
        controleurArchive = new ControllerArchive(controleur);

        vueBureau.actualiser();
        vueListe.actualiser();
        vueGantt.actualiser();
        vueArchive.actualiser();
    }

    /**
     * Change la vue affichée
     */
    public void changerVue(String nomVue) {
        switch(nomVue) {
            case "Vue Liste par Jour":
                racinePrincipale.setCenter(vueListe.getRacine());
                controleurListe.configurerVue(vueListe);
                break;

            case "Vue Gantt":
                racinePrincipale.setCenter(vueGantt.getRacine());
                vueGantt.actualiser();
                break;

            case "Vue Archive":
                racinePrincipale.setCenter(vueArchive.getRacine());
                vueArchive.actualiser();
                controleurArchive.configurerVue(vueArchive);
                break;

            case "Vue Bureau":
            default:
                racinePrincipale.setCenter(vueBureau.getRacine());
                controleurBureau.configurerVue(vueBureau);
                break;
        }
    }

    public VueBureau getVueBureau() {
        return vueBureau;
    }

    public VueListe getVueListe() {
        return vueListe;
    }

    public VueGantt getVueGantt() {
        return vueGantt;
    }

    public VueArchive getVueArchive() {
        return vueArchive;
    }
}