package it.unicam.cs.mpgc.rpg122756.model;

import it.unicam.cs.mpgc.rpg122756.model.items.Item;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestisce l'inventario dell'alchimista.
 * Ha uno spazio limitato che aumenta con i livelli.
 */
public class Inventory {
    
    private final List<Item> items;
    private int maxSlots;
    
    public Inventory(int initialMaxSlots) {
        this.items = new ArrayList<>();
        this.maxSlots = initialMaxSlots;
    }
    
    /**
     * Aggiunge un item all'inventario se c'è spazio.
     * @param item L'item da aggiungere
     * @return true se l'item è stato aggiunto, false se non c'è spazio
     */
    public boolean addItem(Item item) {
        if (getUsedSlots() + item.getSlots() > maxSlots) {
            return false; // Inventario pieno
        }
        items.add(item);
        return true;
    }
    
    /**
     * Rimuove un item dall'inventario.
     * @param item L'item da rimuovere
     * @return true se l'item è stato rimosso, false se non era presente
     */
    public boolean removeItem(Item item) {
        return items.remove(item);
    }
    
    /**
     * @return Lista di tutti gli item nell'inventario
     */
    public List<Item> getItems() {
        return new ArrayList<>(items);
    }
    
    /**
     * @return Numero di slot attualmente utilizzati
     */
    public int getUsedSlots() {
        return items.stream().mapToInt(Item::getSlots).sum();
    }
    
    /**
     * @return Numero di slot disponibili
     */
    public int getAvailableSlots() {
        return maxSlots - getUsedSlots();
    }
    
    /**
     * @return Capacità massima dell'inventario
     */
    public int getMaxSlots() {
        return maxSlots;
    }
    
    /**
     * Aumenta la capacità dell'inventario.
     * @param additionalSlots Slot da aggiungere
     */
    public void increaseCapacity(int additionalSlots) {
        this.maxSlots += additionalSlots;
    }
    
    /**
     * Svuota completamente l'inventario (usato alla morte).
     */
    public void clear() {
        items.clear();
    }
    
    /**
     * Verifica se l'inventario contiene un item specifico.
     * @param itemName Nome dell'item da cercare
     * @return true se l'item è presente
     */
    public boolean containsItem(String itemName) {
        return items.stream().anyMatch(item -> item.getName().equals(itemName));
    }
    
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