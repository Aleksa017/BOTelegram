package service;

import api.CalorieNinjasClient;
import model.Alimento;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NutrizioneService {

    public static List<Alimento> cercaAlimenti(String query) {
        List<Alimento> risultati = new ArrayList<>();

        try {
            JSONObject json = CalorieNinjasClient.getNutritionalInfo(query);
            if (json == null) return risultati;

            // Caso 1: risposta con array "items"
            if (json.has("items")) {
                JSONArray items = json.getJSONArray("items");
                for (int i = 0; i < items.length(); i++) {
                    JSONObject obj = items.getJSONObject(i);
                    risultati.add(creaAlimentoDaJson(obj));
                }
                return risultati;
            }

            // Caso 2: risposta senza array, ma con campi diretti (come nel tuo log)
            if (json.has("name") && json.has("calories")) {
                risultati.add(creaAlimentoDaJson(json));
                return risultati;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return risultati;
    }

    private static Alimento creaAlimentoDaJson(JSONObject obj) {
        String nome = obj.optString("name", "Unknown");
        double calorie = obj.optDouble("calories", 0);
        double proteine = obj.optDouble("protein_g", 0);
        double carboidrati = obj.optDouble("carbohydrates_total_g", 0);
        double grassi = obj.optDouble("fat_total_g", 0);
        return new Alimento(nome, calorie, proteine, carboidrati, grassi);
    }
}
