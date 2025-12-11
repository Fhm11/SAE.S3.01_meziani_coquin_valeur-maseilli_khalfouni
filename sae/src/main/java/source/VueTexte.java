package main.java.source;

public class VueTexte implements Observateur {

    public void actualiser() {
        System.out.println("toutees les taches faites dans al onsole poru test:");
        for (Tache t : TacheManager.getInstance().getTaches()) {
            System.out.println(t);
        }
    }
}
