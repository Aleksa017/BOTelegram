package api;

import config.ConfigLoader;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CalorieNinjasClient {

    private static final String API_URL =
            "https://api.calorieninjas.com/v1/nutrition?query=";

    public static JSONObject getNutritionalInfo(String alimento) {
        try {
            URL url = new URL(API_URL + alimento.replace(" ", "%20"));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setRequestProperty("X-Api-Key", ConfigLoader.getApiKey());
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(conn.getInputStream()));

            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();

            JSONObject json = new JSONObject(response.toString());
            JSONArray items = json.getJSONArray("items");

            if (items.length() == 0) return null;

            return items.getJSONObject(0);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
