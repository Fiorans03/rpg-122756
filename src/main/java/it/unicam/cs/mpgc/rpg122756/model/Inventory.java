package it.unicam.cs.mpgc.rpg122756.model;

import it.unicam.cs.mpgc.rpg122756.model.items.Item;
import java.util.ArrayList;
import java.util.List;

public class Inventory {

    // ==========================================
    // SEZIONE: VARIABILI DI ISTANZA
    // ==========================================

    private final List<Item> items;
    private int maxSlots;

    // ==========================================
    // SEZIONE: COSTRUTTORE
    // ==========================================

    public Inventory(int initialMaxSlots) {
        this.items = new ArrayList<>();
        this.maxSlots = initialMaxSlots;
    }

    // ==========================================
    // SEZIONE: METODI DI MODIFICA
    // ==========================================

    public boolean addItem(Item item) {
        if (getUsedSlots() + item.getSlots() > maxSlots) {
            return false;
        }
        items.add(item);
        return true;
    }

    public boolean removeItem(Item item) {
        return items.remove(item);
    }

    public void clear() {
        items.clear();
    }

    public void increaseCapacity(int additionalSlots) {
        this.maxSlots += additionalSlots;
    }

    // ==========================================
    // SEZIONE: METODI GETTER E CONSULTAZIONE
    // ==========================================

    public List<Item> getItems() {
        return new ArrayList<>(items);
    }

    public int getUsedSlots() {
        return items.stream().mapToInt(Item::getSlots).sum();
    }

    public int getAvailableSlots() {
        return maxSlots - getUsedSlots();
    }

    public int getMaxSlots() {
        return maxSlots;
    }

    public boolean containsItem(String itemName) {
        return items.stream().anyMatch(item -> item.getName().equals(itemName));
    }

    // ==========================================
    // SEZIONE: METODI DI UTILITÀ
    // ==========================================

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Inventario (").append(getUsedSlots()).append("/").append(maxSlots).append(" slot):\n");
        for (Item item : items) {
            sb.append("  - ").append(item.toString()).append("\n");
        }
        return sb.toString();
    }
}