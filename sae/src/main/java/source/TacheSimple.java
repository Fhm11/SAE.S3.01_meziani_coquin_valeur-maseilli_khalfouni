package source;

import java.util.Collections;
import java.util.List;

public class TacheSimple extends Tache {

    public TacheSimple(String titre, String description) {
        super(titre, description);
    }

    @Override
    public boolean ajouterSousTache(Tache sousTache) {
        return false;
    }

    @Override
    public List<Tache> getSousTache() {
        return Collections.emptyList();
    }

    @Override
    public boolean estComposite() {
        return false;
    }
}
