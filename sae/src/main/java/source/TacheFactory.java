package source;

public class TacheFactory {

    public static Tache creerTache(String titre, String description) {
        if (titre == null || titre.trim().isEmpty()) {
            throw new IllegalArgumentException("titre obliger");
        }

        return new Tache(titre, description);
    }
}
