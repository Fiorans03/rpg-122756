package it.unicam.cs.mpgc.rpg122756.model.items;

public class Ingredient extends Item {

    // ==========================================
    // SEZIONE: COSTRUTTORE
    // ==========================================

    public Ingredient(String name, String description, int value, int quantity) {
        super(name, description, value, quantity);
    }

    // ==========================================
    // SEZIONE: METODI GETTER (OVERRIDE)
    // ==========================================

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public String getDescription() {
        return super.getDescription();
    }

    @Override
    public int getValue() {
        return super.getValue();
    }

    @Override
    public int getSlots() {
        return super.getQuantity();
    }

    @Override
    public int getQuantity() {
        return super.getQuantity();
    }
}