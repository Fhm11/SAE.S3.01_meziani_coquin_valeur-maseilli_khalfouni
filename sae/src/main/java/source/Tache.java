package source;

import java.io.Serializable;
import java.util.List;

/**
 * Classe abstraite représentant une tâche dans l'application
 * Fait partie du patron Composite (peut être simple ou composite)
 */
public abstract class Tache implements Serializable {

    // Attributs de base d'une tâche
    private String titre;
    private String description;
    private String etat = "afaire";           // état initial : à faire
    private String jDebut;                    // our de début (Lundi, Mardi, etc.)
    private String jFin;                      // Jour de fin
    private String ancienEtat;
    private String priorite = "Moyenne";

    /**
     * Constructeur d'une tâche
     * @param titre le titre de la tâche (non null)
     * @param description la description de la tâche
     */
    public Tache(String titre, String description) {
        this.titre = titre;
        this.description = description;
        this.jDebut = "Lundi";    // Valeur par défaut
        this.jFin = "Lundi";      // Valeur par défaut
    }


    public String getTitre() {
        return titre;
    }

    /**
     * Modifie le titre avec validation
     * @param titre le nouveau titre (ne peut pas être null ou vide)
     */
    public void setTitre(String titre) {
        if (titre == null || titre.trim().isEmpty()) {
            throw new IllegalArgumentException("Le titre est obligatoire");
        }
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getJDebut() {
        return jDebut;
    }

    public void setJDebut(String jDebut) {
        this.jDebut = jDebut;
    }

    public String getJFin() {
        return jFin;
    }

    public void setJFin(String jFin) {
        this.jFin = jFin;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String nouvelEtat) {
        this.etat = nouvelEtat;
    }

    public String getAncienEtat() {
        return ancienEtat;
    }

    public void setAncienEtat(String ancienEtat) {
        this.ancienEtat = ancienEtat;
    }

    public String getPriorite() {
        return priorite;
    }

    public void setPriorite(String priorite) {
        this.priorite = priorite;
    }

    /**
     * Représentation textuelle d'une tâche
     * @return une chaîne décrivant la tâche
     */
    @Override
    public String toString() {
        return "Tâche : " + titre + " : " + description;
    }


    /**
     * Ajoute une sous-tâche à cette tâche
     * @param sousTache la sous-tâche à ajouter
     * @return true si l'ajout a réussi, false sinon
     */
    public abstract boolean ajouterSousTache(Tache sousTache);

    /**
     * Récupère la liste des sous-tâches
     * @return la liste des sous-tâches (jamais null)
     */
    public abstract List<Tache> getSousTaches();

    /**
     * Indique si la tâche est composite (peut contenir des sous-tâches)
     * @return true si la tâche est composite, false si elle est simple
     */
    public abstract boolean estComposite();

    /**
     * Vérifie si cette tâche contient une autre tâche dans sa hiérarchie
     * @param tacheRecherche la tâche à rechercher
     * @return true si la tâche est trouvée dans la hiérarchie
     */
    public boolean contientTache(Tache tacheRecherche) {
        if (this == tacheRecherche) {
            return true;
        }

        if (this.estComposite()) {
            for (Tache sousTache : this.getSousTaches()) {
                if (sousTache.contientTache(tacheRecherche)) {
                    return true;
                }
            }
        }

        return false;
    }
}