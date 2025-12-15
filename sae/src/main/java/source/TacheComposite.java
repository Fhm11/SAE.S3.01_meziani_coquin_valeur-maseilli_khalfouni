package source;

import java.util.List;
import java.util.ArrayList;

public class TacheComposite extends Tache {

    private List<Tache> sousTaches;

    public TacheComposite(String titre, String description) {
        super(titre, description);
        this.sousTaches = new ArrayList<>()
    }

    @Override
    public boolean ajouterSousTache(Tache sousTache) {
        return sousTaches.add(sousTache);
    }

    @Override
    public List<Tache> getSousTache() {
        return sousTaches;
    }

    @Override
    public boolean estComposite() {
        return true;
    }

    public boolean retirerSousTache(Tache sousTache) {
            return sousTaches.remove(sousTache);
    }

    public int getNombreSousTaches() {
        return sousTaches.size();
    }
}
