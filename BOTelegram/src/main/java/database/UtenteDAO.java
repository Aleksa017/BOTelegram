package database;

import java.sql.*;
import java.util.Optional;

public class UtenteDAO {

    public static void salvaUtente(Utente u) {
        String sql = "INSERT INTO utenti(telegram_id, eta, sesso, altezza, peso, attivita, obiettivo) " +
                "VALUES(?,?,?,?,?,?,?) " +
                "ON CONFLICT(telegram_id) DO UPDATE SET " +
                "eta=excluded.eta, sesso=excluded.sesso, altezza=excluded.altezza, " +
                "peso=excluded.peso, attivita=excluded.attivita, obiettivo=excluded.obiettivo;";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, u.telegramId);
            ps.setInt(2, u.eta);
            ps.setString(3, u.sesso);
            ps.setInt(4, u.altezza);
            ps.setDouble(5, u.peso);
            ps.setString(6, u.attivita);
            ps.setString(7, u.obiettivo);

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Optional<Utente> getUtente(long telegramId) {
        String sql = "SELECT * FROM utenti WHERE telegram_id=?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, telegramId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Utente u = new Utente(
                        rs.getLong("telegram_id"),
                        rs.getInt("eta"),
                        rs.getString("sesso"),
                        rs.getInt("altezza"),
                        rs.getDouble("peso"),
                        rs.getString("attivita"),
                        rs.getString("obiettivo")
                );
                return Optional.of(u);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public static void rimuoviUtente(long telegramId) {
        String sql = "DELETE FROM utenti WHERE telegram_id=?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, telegramId);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int countUtenti() {
        String sql = "SELECT COUNT(*) as tot FROM utenti";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) return rs.getInt("tot");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
