package source;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

/**
 * Classe représentant une tâche composite (conteneur) dans le patron Composite.
 * Une tâche composite peut contenir une liste de sous-tâches (TacheSimple ou TacheComposite).
 * Cette classe correspond au nœud composite dans la structure d'arbre.
 */
public class TacheComposite extends Tache implements Serializable {

    private List<Tache> sousTaches;

    /**
     * Constructeur créant une tâche composite.
     * @param titre le titre de la tâche
     * @param description sa description
     * @throws IllegalArgumentException si le titre est null ou vide
     */
    public TacheComposite(String titre, String description) {
        super(titre, description);
        this.sousTaches = new ArrayList<>();
    }

    /**
     * Ajoute une sous-tâche à cette tâche composite.
     * @param sousTache la sous-tâche à ajouter
     * @return true si la sous-tâche a été ajoutée avec succès
     * @throws NullPointerException si sousTache est null
     */
    @Override
    public boolean ajouterSousTache(Tache sousTache) {
        return sousTaches.add(sousTache);
    }

    /**
     * Retourne la liste des sous-tâches de cette tâche composite.
     * La liste peut être vide si aucune sous-tâche n'a été ajoutée.
     * @return la liste des sous-tâches
     */
    @Override
    public List<Tache> getSousTaches() {
        return sousTaches;
    }

    /**
     * Indique si la tâche est composite.
     * Une tâche composite est toujours composite.
     * @return toujours true
     */
    @Override
    public boolean estComposite() {
        return true;
    }

    /**
     * Retire une sous-tâche d'une tâche composite.
     * @param sousTache la sous-tâche à retirer
     * @return true si la sous-tâche était présente et a été retirée, false sinon
     * @throws NullPointerException si sousTache est null
     */
    public boolean retirerSousTache(Tache sousTache) {
            return sousTaches.remove(sousTache);
    }

    /**
     * Retourne le nombre de sous-tâches contenues dans une tâche composite.
     * @return le nombre de sous-tâches (0 si la liste est vide)
     */
    public int getNombreSousTaches() {
        return sousTaches.size();
    }

}
