package service;

import database.Utente;

public class CalcoloNutrizionaleService {

    /**
     * Calcolo delle calorie giornaliere in base al profilo dell'utente
     * Metodo semplice basato su formule Mifflin-St Jeor
     */
    public static double calorieFinali(Utente u) {
        if (u == null) return 0;

        double bmr; // metabolismo basale

        if (u.sesso.equalsIgnoreCase("M")) {
            bmr = 10 * u.peso + 6.25 * u.altezza - 5 * u.eta + 5;
        } else { // "F"
            bmr = 10 * u.peso + 6.25 * u.altezza - 5 * u.eta - 161;
        }

        // Moltiplica per fattore attività
        double fattoreAttivita;
        switch (u.attivita.toLowerCase()) {
            case "sedentario":
                fattoreAttivita = 1.2;
                break;
            case "moderato":
                fattoreAttivita = 1.55;
                break;
            case "attivo":
                fattoreAttivita = 1.725;
                break;
            default:
                fattoreAttivita = 1.2;
        }

        double calorie = bmr * fattoreAttivita;

        // Adatta le calorie in base all'obiettivo
        switch (u.obiettivo.toLowerCase()) {
            case "dimagrire":
                calorie -= 500; // deficit calorico
                break;
            case "aumentare massa":
                calorie += 500; // surplus calorico
                break;
            case "mantenere":
            default:
                break;
        }

        return calorie;
    }
}
