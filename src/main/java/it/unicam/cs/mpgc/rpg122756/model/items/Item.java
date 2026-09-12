package it.unicam.cs.mpgc.rpg122756.model.items;

public class Item {

    // ==========================================
    // SEZIONE: VARIABILI DI ISTANZA
    // ==========================================

    private String name;
    private String description;
    private int value;
    private int quantity;

    // ==========================================
    // SEZIONE: COSTRUTTORE
    // ==========================================

    public Item(String name, String description, int value, int quantity) {
        this.name = name;
        this.description = description;
        this.value = value;
        this.quantity = quantity;
    }

    // ==========================================
    // SEZIONE: METODI GETTER
    // ==========================================

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getValue() {
        return value;
    }

    public int getQuantity() {
        return quantity;
    }

    // ==========================================
    // SEZIONE: METODI SETTER
    // ==========================================

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // ==========================================
    // SEZIONE: METODI DI COMPATIBILITÀ
    // ==========================================

    public int getSlots() {
        return quantity;
    }
}