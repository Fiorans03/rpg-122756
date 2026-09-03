package it.unicam.cs.mpgc.rpg122756.model.items;

// FIX: Usa "extends" perché Item è una classe, non un'interfaccia!
public class Ingredient extends Item {

    public Ingredient(String name, String description, int value, int quantity) {
        // Chiama il costruttore della classe padre (Item)
        super(name, description, value, quantity);
    }

    // Questi override sono opzionali se Item li ha già pubblici,
    // ma li mettiamo per sicurezza e per soddisfare il compilatore
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

    // FIX: Mappa getSlots() a getQuantity() (o viceversa, a seconda di come l'hai
    // chiamato in Item)
    @Override
    public int getSlots() {
        return super.getQuantity();
    }

    @Override
    public int getQuantity() {
        return super.getQuantity();
    }
}