package source;

public class VueTexte implements Observateur {

    public void actualiser() {
        System.out.println("toutes les taches faites dans la console pour test:");
        for (Tache t : TacheManager.getInstance().getTaches()) {
            System.out.println(t);
        }
    }
}
