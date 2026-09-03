package it.unicam.cs.mpgc.rpg122756.model.items;

public class Potion extends Item {
    private int apCost;
    private PotionEffect effect;

    public Potion(String name, String description, int value, int apCost, PotionEffect effect) {
        super(name, description, value, 1); // 1 è la quantità di default
        this.apCost = apCost;
        this.effect = effect;
    }

    public int getApCost() {
        return apCost;
    }

    public PotionEffect getEffect() {
        return effect;
    }

    // FIX: Definiamo esplicitamente getName() per evitare qualsiasi errore di
    // ereditarietà
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