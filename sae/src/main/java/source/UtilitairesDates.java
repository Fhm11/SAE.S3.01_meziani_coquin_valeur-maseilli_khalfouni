package source;

import java.util.Arrays;
import java.util.List;

/**
 * Classe utilitaire pour les calculs sur les jours de la semaine
 * Centralise toutes les opérations liées aux dates
 */
public class UtilitairesDates {

    // Liste ordonnée des jours de la semaine
    private static final List<String> JOURS_SEMAINE = Arrays.asList(
            "Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche"
    );

    /**
     * Calcule la durée (en jours) entre deux jours
     * @param debut le jour de début
     * @param fin le jour de fin
     * @return le nombre de jours entre les deux dates
     */
    public static int calculerDuree(String debut, String fin) {
        int indexDebut = JOURS_SEMAINE.indexOf(debut);
        int indexFin = JOURS_SEMAINE.indexOf(fin);

        if (indexFin >= indexDebut) {
            return indexFin - indexDebut;
        } else {
            // Cas où on passe au début de la semaine suivante
            return (indexFin + 7) - indexDebut;
        }
    }

    /**
     * Calcule le jour qui suit un jour donné après un certain nombre de jours
     * @param jour le jour de départ
     * @param nbJours le nombre de jours à ajouter
     * @return le jour résultant
     */
    public static String calculerJourSuivant(String jour, int nbJours) {
        int index = JOURS_SEMAINE.indexOf(jour);
        int nouvelIndex = (index + nbJours) % 7;
        return JOURS_SEMAINE.get(nouvelIndex);
    }
}