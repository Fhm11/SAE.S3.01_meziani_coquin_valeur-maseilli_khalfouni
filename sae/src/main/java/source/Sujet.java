package source;

/**
 * Interface sujet du patron MVC pour gérer les observateurs
 */
public interface Sujet {

    /**
     * Méthode pour ajouter un observateur
     * @param o l'observateur à ajouter
     */
    public void ajouterObservateur(Observateur o);

    /**
     * Méthode pour supp un observateur
     * @param o l'observateur à supp
     */
    public void supprimerObservateur(Observateur o);

    /**
     * Méthode qui notifie les observateurs
     */
    public void notifierObservateur();
}
