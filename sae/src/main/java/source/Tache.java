package source;

import java.io.Serializable;
import java.util.List;

/**
 * Classe abstraite qui gère les tâches
 */
public abstract class Tache implements Serializable {

    private String titre;
    private String description;
    private String etat = "afaire";
    private String jDebut;
    private String jFin;

    /**
     * Constructeur créant une tâche
     * @param titre le titre de la tâche
     * @param description sa description
     */
    public Tache(String titre, String description) {
        this.titre = titre;
        this.description = description;
        this.jDebut = "Lundi";
        this.jFin = "Lundi";
    }

    /**
     * Getter pour le titre
     * @return le titre
     */ 
    public String getTitre() {
        return titre;
    }

    /**
     * Getter pour la description
     * @return la description
     */
    public String getDescription() {
        return description;
    }

    public String getJDebut() {
        return jDebut;
    }

    public String getJFin() {
        return jFin;
    }

    public void setTitre(String titre) {
        if (titre == null || titre.trim().isEmpty()) {
            throw new IllegalArgumentException("titre obligé");
        }
        this.titre = titre;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setJDebut(String jDebut) {
        this.jDebut = jDebut;
    }

    public void setJFin(String jFin) {
        this.jFin = jFin;
    }

    /**
     * public pour afficher la tâche
     * @return la tâche avec son titre et sa description
     */
    @Override
    public String toString() {
        return "tache : " + titre + " : "  + description;
    }

    /**
     * Méthode pour ajouter une sous-tâche.
     * Dans une TacheSimple, cette méthode échouera toujours.
     * Dans une TacheComposite, cette méthode ajoutera la sous-tâche à la liste.
     * @param sousTache la sous-tâche à ajouter
     * @return true si l'ajout a réussi, false sinon
     */
    public abstract boolean ajouterSousTache(Tache sousTache);

    /**
     * Retourne la liste des sous-tâches.
     * Pour une TacheSimple, retourne une liste vide.
     * Pour une TacheComposite, retourne la liste des sous-tâches.
     * @return la liste des sous-tâches (jamais null)
     */
    public abstract List<Tache> getSousTaches();

    /**
     * Indique si la tâche est composite.
     * @return true si la tâche est composite, false si elle est simple
     */
    public abstract boolean estComposite();

    public String getEtat() {
        return etat;
    }

    public void setEtat(String nouvelEtat) {
        this.etat = nouvelEtat;
    }
}
