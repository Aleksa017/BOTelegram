package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:data/bot_database.db";

    // Restituisce connessione
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    // Crea tabelle se non esistono
    public static void initDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            String creaUtenti = "CREATE TABLE IF NOT EXISTS utenti (" +
                    "telegram_id INTEGER PRIMARY KEY," +
                    "eta INTEGER," +
                    "sesso TEXT," +
                    "altezza INTEGER," +
                    "peso REAL," +
                    "attivita TEXT," +
                    "obiettivo TEXT" +
                    ");";

            String creaStato = "CREATE TABLE IF NOT EXISTS stato_utente (" +
                    "telegram_id INTEGER PRIMARY KEY," +
                    "stato TEXT" +
                    ");";

            stmt.execute(creaUtenti);
            stmt.execute(creaStato);

            System.out.println("Database inizializzato correttamente!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
