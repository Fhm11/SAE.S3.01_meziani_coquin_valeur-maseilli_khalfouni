package source;

/**
 * Classe qui gère les tâches
 */
public class Tache {
    private String titre;
    private String description;

    /**
     * Constructeur créant une tâche
     * @param titre le titre de la tâche
     * @param description sa description
     */
    public Tache(String titre, String description) {
        this.titre = titre;
        this.description = description;
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

    /**
     * public pour afficher la tâche
     * @return la tâche avec son titre et sa description
     */
    @Override
    public String toString() {
        return "tache : " + titre + " | " + description;
    }
}
