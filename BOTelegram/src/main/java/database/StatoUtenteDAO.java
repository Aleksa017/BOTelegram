package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class StatoUtenteDAO {

    // Salva o aggiorna lo stato dell'utente
    public static void salvaStato(long telegramId, String stato) {
        String sql = "INSERT INTO stato_utente(telegram_id, stato) VALUES(?,?) " +
                "ON CONFLICT(telegram_id) DO UPDATE SET stato=excluded.stato;";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, telegramId);
            ps.setString(2, stato);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Recupera lo stato dell'utente
    public static Optional<String> getStato(long telegramId) {
        String sql = "SELECT stato FROM stato_utente WHERE telegram_id=?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, telegramId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(rs.getString("stato"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    // Cancella lo stato dell'utente
    public static void resetStato(long telegramId) {
        String sql = "DELETE FROM stato_utente WHERE telegram_id=?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, telegramId);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
