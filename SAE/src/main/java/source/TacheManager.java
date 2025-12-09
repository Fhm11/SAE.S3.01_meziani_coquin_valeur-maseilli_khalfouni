package source;

import java.util.ArrayList;

public class TacheManager implements Sujet {

    private static TacheManager instance;
    private ArrayList<Observateur> observateurs;

    private TacheManager() {
        observateurs = new ArrayList<>();
    }

    public static synchronized TacheManager getInstance() {
        if (instance == null) {
            instance  = new TacheManager();
        }
        return instance;
    }

    @Override
    public void ajouterObservateur(Observateur o) {
        if (o != null && !observateurs.contains(o)) {
            observateurs.add(o);
        }
    }

    @Override
    public void supprimerObservateur(Observateur o) {
        observateurs.remove(o);
    }

    @Override
    public void notifierObservateur() {
        for (Observateur o : observateurs) {
            o.actualiser();
        }
    }
}
