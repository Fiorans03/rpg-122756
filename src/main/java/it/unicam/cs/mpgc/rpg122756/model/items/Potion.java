package it.unicam.cs.mpgc.rpg122756.model.items;

public class Potion extends Item {

    // ==========================================
    // SEZIONE: VARIABILI DI ISTANZA
    // ==========================================

    private int apCost;
    private PotionEffect effect;

    // ==========================================
    // SEZIONE: COSTRUTTORE
    // ==========================================

    public Potion(String name, String description, int value, int apCost, PotionEffect effect) {
        super(name, description, value, 1);
        this.apCost = apCost;
        this.effect = effect;
    }

    // ==========================================
    // SEZIONE: METODI GETTER SPECIFICI
    // ==========================================

    public int getApCost() {
        return apCost;
    }

    public PotionEffect getEffect() {
        return effect;
    }

    // ==========================================
    // SEZIONE: METODI GETTER (OVERRIDE)
    // ==========================================

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public int getValue() {
        return super.getValue();
    }

    @Override
    public String getDescription() {
        return super.getDescription();
    }
}