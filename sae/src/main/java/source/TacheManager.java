package source;

import java.util.ArrayList;

/**
 * Classe représentant le modèle de MVC
 */
public class TacheManager implements Sujet {

    private static TacheManager instance;
    private ArrayList<Observateur> observateurs;
    private ArrayList<Tache> listeTaches;

    /**
     * Constructeur privé de Singleton
     */
    private TacheManager() {

        observateurs = new ArrayList<>();
        listeTaches = new ArrayList<>();
    }

    /**
     * Méthode getInstance de Singleton qui permet de s'assurer qu'une
     * seule instance soit lancé
     * @return
     */
    public static synchronized TacheManager getInstance() {
        if (instance == null) {
            instance  = new TacheManager();
        }
        return instance;
    }

    /**
     * Méthode pour ajouter un observateur à la liste
     * @param o l'observateur à ajouter
     */
    @Override
    public void ajouterObservateur(Observateur o) {
        if (o != null && !observateurs.contains(o)) {
            observateurs.add(o);
        }
    }

    /**
     * Méthode pour supprimer un observateur
     * @param o l'observateur à supprimer
     */
    @Override
    public void supprimerObservateur(Observateur o) {
        observateurs.remove(o);
    }

    /**
     * Méthode permettant de notifier les observateurs pour qu'ils
     * lancent la méthode actualiser sur les vues
     */
    @Override
    public void notifierObservateur() {
        for (Observateur o : observateurs) {
            o.actualiser();
        }
    }

    /**
     * Méthode qui permet d'ajouter la tâche a la liste
     * @param t la tâche à ajouter
     */
    public void ajouterTache(Tache t) {
        if (t != null) {
            listeTaches.add(t);
            notifierObservateur();
        }
    }

    /**
     * Getter pour voir la liste des tâches
     * @return la liste de tâches
     */
    public ArrayList<Tache> getTaches() {
        return listeTaches;
    }


}
