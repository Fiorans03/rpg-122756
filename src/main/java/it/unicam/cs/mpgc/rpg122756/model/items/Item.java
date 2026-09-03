package it.unicam.cs.mpgc.rpg122756.model.items;

public class Item {
    private String name;
    private String description;
    private int value;
    private int quantity;

    public Item(String name, String description, int value, int quantity) {
        this.name = name;
        this.description = description;
        this.value = value;
        this.quantity = quantity;
    }

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

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // Alias per compatibilità con il codice che usa "getSlots"
    public int getSlots() {
        return quantity;
    }
}