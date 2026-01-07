package bot;

import config.ConfigLoader;
import model.Alimento;
import database.Utente;
import service.CalcoloNutrizionaleService;
import service.DietaService;
import service.NutrizioneService;
import database.UtenteDAO;
import database.StatoUtenteDAO;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Optional;

public class NutritionBot extends TelegramLongPollingBot {

    @Override
    public String getBotUsername() {
        return "YOUR_BOT_USERNAME"; // sostituisci con l’username reale del tuo bot
    }

    @Override
    public String getBotToken() {
        return ConfigLoader.getBotToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
            String testo = update.getMessage().getText().trim();

            String risposta = gestisciMessaggio(chatId, testo);
            SendMessage message = new SendMessage();
            message.setChatId(String.valueOf(chatId));
            message.setText(risposta);

            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    private String gestisciMessaggio(long chatId, String testo) {
        String comando = testo.toLowerCase();

        switch (comando) {
            case "/start":
                // Inizia la conversazione per creare profilo
                StatoUtenteDAO.resetStato(chatId);
                StatoUtenteDAO.salvaStato(chatId, "ETA");
                return "Benvenuto! Inserisci la tua età:";

            case "/help":
                return "Comandi disponibili:\n" +
                        "/start - crea o resetta il profilo\n" +
                        "/dieta - genera piano alimentare indicativo\n" +
                        "/profilo - mostra profilo\n" +
                        "/reset - cancella profilo\n" +
                        "/statistiche - numero utenti registrati\n" +
                        "Oppure scrivi il nome di un alimento per ottenere i valori nutrizionali.";

            case "/profilo": {
                Optional<Utente> profiloOpt = UtenteDAO.getUtente(chatId);
                if (profiloOpt.isEmpty()) return "Profilo non trovato, usa /start!";
                Utente p = profiloOpt.get();
                return String.format(
                        "Profilo:\nEtà: %d\nSesso: %s\nAltezza: %d cm\nPeso: %.1f kg\nAttività: %s\nObiettivo: %s",
                        p.eta, p.sesso, p.altezza, p.peso, p.attivita, p.obiettivo
                );
            }

            case "/dieta": {
                Optional<Utente> utenteOpt = UtenteDAO.getUtente(chatId);
                if (utenteOpt.isEmpty()) return "Devi prima completare il profilo con /start!";
                Utente u = utenteOpt.get();
                double calorie = CalcoloNutrizionaleService.calorieFinali(u);
                List<Alimento> dieta = DietaService.generaDietaIndicativa(calorie);

                StringBuilder sb = new StringBuilder();
                sb.append("Esempio piano alimentare (~")
                        .append((int) calorie).append(" kcal)\n\n");

                return sb.toString();
            }

            case "/reset":
                UtenteDAO.rimuoviUtente(chatId);
                StatoUtenteDAO.resetStato(chatId);
                return "Profilo resettato. Usa /start per ricominciare.";

            case "/statistiche":
                int tot = UtenteDAO.countUtenti();
                return "Utenti registrati: " + tot;

            default:
                // **Se siamo in una conversazione profilo** continua quel flusso
                Optional<String> statoOpt = StatoUtenteDAO.getStato(chatId);
                if (statoOpt.isPresent()) {
                    return gestisciConversazione(chatId, testo);
                }
                // **Altrimenti interpreta il testo come nome alimento**
                return cercaAlimento(testo);
        }
    }

    private String gestisciConversazione(long chatId, String testo) {
        String stato = StatoUtenteDAO.getStato(chatId).orElse("");
        Utente u = UtenteDAO.getUtente(chatId).orElse(new Utente());
        u.telegramId = chatId;

        switch (stato) {
            case "ETA":
                try {
                    u.eta = Integer.parseInt(testo);
                } catch (NumberFormatException e) {
                    return "Inserisci un numero valido per l’età!";
                }
                StatoUtenteDAO.salvaStato(chatId, "SESSO");
                UtenteDAO.salvaUtente(u);
                return "Inserisci il sesso (M/F):";

            case "SESSO":
                if (!testo.equalsIgnoreCase("m") && !testo.equalsIgnoreCase("f")) {
                    return "Inserisci M o F!";
                }
                u.sesso = testo.toUpperCase();
                StatoUtenteDAO.salvaStato(chatId, "ALTEZZA");
                UtenteDAO.salvaUtente(u);
                return "Inserisci altezza in cm:";

            case "ALTEZZA":
                try {
                    u.altezza = Integer.parseInt(testo);
                } catch (NumberFormatException e) {
                    return "Inserisci un numero valido per l’altezza!";
                }
                StatoUtenteDAO.salvaStato(chatId, "PESO");
                UtenteDAO.salvaUtente(u);
                return "Inserisci peso in kg:";

            case "PESO":
                try {
                    u.peso = Double.parseDouble(testo);
                } catch (NumberFormatException e) {
                    return "Inserisci un numero valido per il peso!";
                }
                StatoUtenteDAO.salvaStato(chatId, "ATTIVITA");
                UtenteDAO.salvaUtente(u);
                return "Inserisci livello di attività (Sedentario / Moderato / Attivo):";

            case "ATTIVITA":
                if (!testo.equalsIgnoreCase("sedentario") &&
                        !testo.equalsIgnoreCase("moderato") &&
                        !testo.equalsIgnoreCase("attivo")) {
                    return "Valore non valido. Scegli Sedentario, Moderato o Attivo.";
                }
                u.attivita = testo.substring(0,1).toUpperCase() + testo.substring(1);
                StatoUtenteDAO.salvaStato(chatId, "OBIETTIVO");
                UtenteDAO.salvaUtente(u);
                return "Qual è il tuo obiettivo? (Dimagrire / Mantenere / Aumentare massa)";

            case "OBIETTIVO":
                if (!testo.equalsIgnoreCase("dimagrire") &&
                        !testo.equalsIgnoreCase("mantenere") &&
                        !testo.equalsIgnoreCase("aumentare massa")) {
                    return "Valore non valido. Scegli Dimagrire, Mantenere o Aumentare massa.";
                }
                u.obiettivo = testo.substring(0,1).toUpperCase() + testo.substring(1);
                StatoUtenteDAO.resetStato(chatId);
                UtenteDAO.salvaUtente(u);
                return "Profilo completato! Ora puoi usare /dieta o cercare alimenti.";

            default:
                return "Non ho capito, riprova!";
        }
    }

    private String cercaAlimento(String nomeCibo) {
        // Chiama il service che usa CalorieNinjas API :contentReference[oaicite:1]{index=1}
        List<Alimento> lista = NutrizioneService.cercaAlimenti(nomeCibo);

        if (lista.isEmpty()) {
            // Riprova con quantità esplicita (es. “100g banana”)
            lista = NutrizioneService.cercaAlimenti("100g " + nomeCibo);
        }

        if (lista.isEmpty()) {
            return "Alimento non trovato o nessuna info disponibile per \"" + nomeCibo + "\".";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("📊 Valori nutrizionali per \"").append(nomeCibo).append("\":\n\n");

        for (Alimento a : lista) {
            sb.append("- ").append(a.nome)
                    .append(": ").append((int) a.calorie).append(" kcal, ")
                    .append(a.proteine).append("g proteine, ")
                    .append(a.carboidrati).append("g carboidrati, ")
                    .append(a.grassi).append("g grassi\n");
        }
        return sb.toString();
    }
}
