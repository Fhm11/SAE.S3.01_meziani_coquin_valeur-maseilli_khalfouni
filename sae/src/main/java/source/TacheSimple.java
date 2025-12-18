package source;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * Classe représentant une tâche simple.
 * Une tâche simple ne peut pas contenir de sous-tâches.
 */
public class TacheSimple extends Tache implements Serializable {

    /**
     * Constructeur créant une tâche simple.
     * @param titre le titre de la tâche
     * @param description sa description
     * @throws IllegalArgumentException si le titre est null ou vide
     */
    public TacheSimple(String titre, String description) {
        super(titre, description);
    }

    /**
     * Tente d'ajouter une sous-tâche. Cette opération échoue toujours
     * car une tâche simple ne peut pas contenir de sous-tâches.
     * @param sousTache la sous-tâche à ajouter (ignorée)
     * @return toujours false
     */
    @Override
    public boolean ajouterSousTache(Tache sousTache) {
        return false;
    }

    /**
     * Retourne la liste des sous-tâches.
     * Pour une tâche simple, cette liste est toujours vide.
     * @return une liste vide immuable
     */
    @Override
    public List<Tache> getSousTaches() {
        return Collections.emptyList();
    }

    /**
     * Indique si la tâche est composite.
     * Une tâche simple n'est jamais composite.
     * @return toujours false
     */
    @Override
    public boolean estComposite() {
        return false;
    }
}
