package source;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import java.util.Arrays;
import java.util.List;

public class Controller implements EventHandler<ActionEvent> {
    private TacheManager modele;
    private Tache tacheEnDeplacement;

    public Controller(TacheManager modele) {
        this.modele = modele;
    }

    /**
     * Méthode pour gérer les évènements
     *
     * @param event les évènements
     */
    @Override
    public void handle(ActionEvent event) {
        Object source = event.getSource();

        if (source instanceof Button) {
            Button btn = (Button) source;
            Object data = btn.getUserData();

            if (data instanceof Tache) {
                Tache t = (Tache) data;
                String texte = btn.getText();

                if ("Archiver".equals(texte)) {
                    modele.archiverTache(t);
                }
                else if ("Restaurer".equals(texte)) {
                    modele.restaurerTache(t);
                }
                else if ("supprimer".equals(texte)) {
                    modele.supprimerTache(t);
                }
                else if ("Supprimer".equals(texte)) {
                    modele.supprimerTache(t);
                }
                else if ("+".equals(texte)) {
                    VueFormulaire.afficherFormulaireSousTache(t, this);
                }
            }
        }
    }

    /**
     * Méthodes pour vueFormulaire qui créé une tache
     *
     * @param titre        le titre
     * @param description  la description
     * @param estComposite true si elle peut avoir une sous-tâche, false sinon
     * @param debut        jour de début
     * @param fin          jour de fin
     * @param prio         priorité (AJOUT: leur version)
     */
    public void creerTache(String titre, String description, boolean estComposite, String debut, String fin, String prio) {
        if (estComposite) {
            modele.creerTacheComposite(titre, description, debut, fin, prio);
        } else {
            modele.creerTacheSimple(titre, description, debut, fin, prio);
        }
    }

    // surcharge pour compatibilité (si prio non fournie)
    public void creerTache(String titre, String description, boolean estComposite, String debut, String fin) {
        creerTache(titre, description, estComposite, debut, fin, "Moyenne"); // Priorité par défaut
    }

    /**
     * Méthode pour modifier une tâche
     *
     * @param t           la tâche à modifier
     * @param titre       le titre modifié
     * @param description la description modifié
     * @param prio        priorité (AJOUT: leur version)
     */
    public void modifierTache(Tache t, String titre, String description, String prio) {
        modele.modifierTache(t, titre, description, prio);
    }

    // surcharge pour compatibilité (si prio non fournie)
    public void modifierTache(Tache t, String titre, String description) {
        modifierTache(t, titre, description, t.getPriorite()); // Garder priorité existante
    }

    /**
     * Méthode pour ajouter une tâche
     * à une tâche existante
     *
     * @param parent      la tâche parente
     * @param titre       le titre de la sous-tâche
     * @param description la description de la sous-tâche
     * @param estComposite true si composite
     * @param debut       jour de début
     * @param fin         jour de fin
     * @param prio        priorité (AJOUT: leur version)
     */
    public void ajouterSousTache(Tache parent, String titre, String description, boolean estComposite,
                                 String debut, String fin, String prio) {
        modele.ajouterSousTache(parent, titre, description, estComposite, debut, fin, prio);
    }

    // surcharge pour compatibilité (si prio non fournie)
    public void ajouterSousTache(Tache parent, String titre, String description, boolean estComposite,
                                 String debut, String fin) {
        ajouterSousTache(parent, titre, description, estComposite, debut, fin, "Moyenne"); // Priorité par défaut
    }

    /**
     * Méthode appelée quand on clique sur une tâche
     * pour la déplacer
     *
     * @param t la tâche
     */
    public void debuterDeplacement(Tache t) {
        this.tacheEnDeplacement = t;
    }

    /**
     * Appelé quand on lâche la tâche dans une colonne
     */
    public void finaliserDeplacement(String nouvelEtat) {
        if (tacheEnDeplacement != null) {
            modele.deplacerTache(tacheEnDeplacement, nouvelEtat);
            tacheEnDeplacement = null; // Reset
        }
    }

    /**
     * Déplace une tâche vers un nouveau jour (VOTRE version avancée)
     * Conserve la durée actuelle
     */
    public void deplacerTacheVersJour(Tache t, String nouveauJour) {
        if (t != null && nouveauJour != null) {
            // calculer la durée actuelle
            int duree = calculerDuree(t.getJDebut(), t.getJFin());

            // changer le jour de début
            t.setJDebut(nouveauJour);

            // calculer le nouveau jour de fin en conservant la durée
            String nouveauJFin = calculerJourSuivant(nouveauJour, duree);
            t.setJFin(nouveauJFin);

            // notifier les observateurs
            modele.notifierObservateur();
        }
    }

    /**
     * Méthode pour changer le jour d'une tâche (LEUR version simple)
     * Ne conserve pas la durée, change seulement le début
     *
     * @param t           la tâche à déplacer
     * @param nouveauJour le nouveau jour ("Lundi", "Mardi", etc.)
     */
    public void changerJourTache(Tache t, String nouveauJour) {
        if (t != null && nouveauJour != null) {
            t.setJDebut(nouveauJour);
            modele.notifierObservateur();
        }
    }

    public void ajouterColonne(String titre) {
        modele.ajouterColonne(titre);
    }

    public void supprimerColonne(String titre) {
        modele.supprimerColonne(titre);
    }

    public void devenirSousTacheDe(Tache enfant, Tache parentCible) {
        modele.devenirSousTacheDe(enfant, parentCible);
    }

    /**
     * Extraire une sous-tâche pour en faire une tâche principale
     */
    public void extraireSousTache(Tache sousTache) {
        modele.extraireSousTache(sousTache);
    }

    /**
     * Extraire une sous-tâche vers une colonne spécifique
     */
    public void extraireVersColonne(Tache sousTache, String etat) {
        modele.extraireVersColonne(sousTache, etat);
    }

    /**
     * Extraire une sous-tâche vers un jour spécifique
     */
    public void extraireVersJour(Tache sousTache, String jour) {
        modele.extraireVersJour(sousTache, jour);
    }

    public Tache getTacheEnDeplacement() {
        return tacheEnDeplacement;
    }

    /**
     * Calcule la durée entre deux jours
     */
    private int calculerDuree(String debut, String fin) {
        List<String> jours = Arrays.asList("Lundi", "Mardi", "Mercredi", "Jeudi",
                "Vendredi", "Samedi", "Dimanche");
        int idxDebut = jours.indexOf(debut);
        int idxFin = jours.indexOf(fin);

        if (idxFin >= idxDebut) {
            return idxFin - idxDebut;
        } else {
            return (idxFin + 7) - idxDebut;
        }
    }

    /**
     * Calcule le jour suivant
     */
    private String calculerJourSuivant(String jour, int nbJours) {
        List<String> jours = Arrays.asList("Lundi", "Mardi", "Mercredi", "Jeudi",
                "Vendredi", "Samedi", "Dimanche");
        int idx = jours.indexOf(jour);
        int nouvelIdx = (idx + nbJours) % 7;
        return jours.get(nouvelIdx);
    }
}