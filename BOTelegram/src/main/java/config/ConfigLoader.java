package config;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;

public class ConfigLoader {

    private static Configuration config;

    static {
        try {
            Configurations configs = new Configurations();
            config = configs.properties("config.properties");
        } catch (Exception e) {
            System.err.println("Errore nel caricamento config.properties");
            e.printStackTrace();
        }
    }

    public static String getBotToken() {
        return config.getString("BOT_TOKEN");
    }

    public static String getApiKey() {
        return config.getString("API_KEY_CALORIENINJAS");
    }
}
