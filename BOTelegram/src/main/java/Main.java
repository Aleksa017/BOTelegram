import bot.NutritionBot;
import database.DatabaseManager;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Main {
    public static void main(String[] args) {
        // Inizializza database
        DatabaseManager.initDatabase();

        // Avvio bot
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new NutritionBot());
            System.out.println("Bot avviato correttamente!");
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
