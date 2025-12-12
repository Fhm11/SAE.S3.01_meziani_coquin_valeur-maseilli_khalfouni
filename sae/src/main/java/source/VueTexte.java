package source;

/**
 * Classe permettant de vérifier si la création de tâche fonctionne, sans javaFx
 */
public class VueTexte implements Observateur {

    /**
     * méthode qui permet de mettre à jour la vue
     */
    public void actualiser() {
        System.out.println("toutes les taches faites dans la console pour test:");
        for (Tache t : TacheManager.getInstance().getTaches()) {
            System.out.println(t);
        }
    }
}
