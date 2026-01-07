package service;

import model.Alimento;
import java.util.ArrayList;
import java.util.List;

public class DietaService {

    // Genera un piano alimentare semplice (colazione, pranzo, cena)
    public static List<Alimento> generaDietaIndicativa(double calorieTotali) {

        List<Alimento> dieta = new ArrayList<>();

        // Prova a cercare i cibi base
        List<Alimento> colazioneResults = NutrizioneService.cercaAlimenti("100g uova");
        List<Alimento> pranzoResults   = NutrizioneService.cercaAlimenti("100g pollo");
        List<Alimento> cenaResults     = NutrizioneService.cercaAlimenti("100g riso");

        // Se il service ha trovato risultati, aggiungili
        if (!colazioneResults.isEmpty()) {
            dieta.add(colazioneResults.get(0));
        }
        if (!pranzoResults.isEmpty()) {
            dieta.add(pranzoResults.get(0));
        }
        if (!cenaResults.isEmpty()) {
            dieta.add(cenaResults.get(0));
        }

        // Se per qualche motivo il servizio non ha trovato nulla,
        // puoi anche aggiungere un default o lasciare vuoto.
        // Esempio:
        // if (dieta.isEmpty()) {
        //     dieta.add(new Alimento("Nutrizione non disponibile", 0, 0, 0, 0));
        // }

        return dieta;
    }
}
