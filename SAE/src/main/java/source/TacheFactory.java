package source;

public class TacheFactory {

    public static Tache creerTache(String titre, String description) {
        return new Tache(titre, description);
    }
}
