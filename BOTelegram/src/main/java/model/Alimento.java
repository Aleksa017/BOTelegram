package model;

public class Alimento {

    public String nome;
    public double calorie;
    public double proteine;
    public double carboidrati;
    public double grassi;

    public Alimento(String nome, double calorie, double proteine,
                    double carboidrati, double grassi) {
        this.nome = nome;
        this.calorie = calorie;
        this.proteine = proteine;
        this.carboidrati = carboidrati;
        this.grassi = grassi;
    }
}
