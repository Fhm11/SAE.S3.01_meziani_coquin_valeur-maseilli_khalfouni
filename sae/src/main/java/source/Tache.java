package source;

public class Tache {
    private String titre;
    private String description;

    public Tache(String titre, String description) {
        this.titre = titre;
        this.description = description;
    }

    public String getTitre() {
        return titre;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "tache : " + titre + " | " + description;
    }
}
