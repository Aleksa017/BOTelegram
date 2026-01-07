package database;

public class Utente {

    public long telegramId;
    public int eta;
    public String sesso;
    public int altezza;
    public double peso;
    public String attivita;
    public String obiettivo;

    public Utente() {}

    public Utente(long telegramId, int eta, String sesso, int altezza, double peso,
                  String attivita, String obiettivo) {
        this.telegramId = telegramId;
        this.eta = eta;
        this.sesso = sesso;
        this.altezza = altezza;
        this.peso = peso;
        this.attivita = attivita;
        this.obiettivo = obiettivo;
    }
}
