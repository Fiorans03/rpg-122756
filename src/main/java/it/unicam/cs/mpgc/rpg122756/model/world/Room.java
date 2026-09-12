package it.unicam.cs.mpgc.rpg122756.model.world;

import it.unicam.cs.mpgc.rpg122756.model.entities.Monster;
import it.unicam.cs.mpgc.rpg122756.model.items.Item;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Room {

    // ==========================================
    // SEZIONE: VARIABILI DI ISTANZA
    // ==========================================

    private final String id;
    private String description;
    private final List<Item> itemsOnFloor;
    private Monster monster;
    private final Map<Direction, Room> exits;
    private boolean isCleared;

    // ==========================================
    // SEZIONE: COSTRUTTORE
    // ==========================================

    public Room(String id, String description) {
        this.id = id;
        this.description = description;
        this.itemsOnFloor = new ArrayList<>();
        this.exits = new HashMap<>();
        this.isCleared = true;
    }

    // ==========================================
    // SEZIONE: GESTIONE USCITE
    // ==========================================

    public void setExit(Direction direction, Room room) {
        exits.put(direction, room);
    }

    public Room getExit(Direction direction) {
        return exits.get(direction);
    }

    public Map<Direction, Room> getExits() {
        return new HashMap<>(exits);
    }

    // ==========================================
    // SEZIONE: GESTIONE OGGETTI
    // ==========================================

    public void addItem(Item item) {
        itemsOnFloor.add(item);
    }

    public List<Item> getItemsOnFloor() {
        return new ArrayList<>(itemsOnFloor);
    }

    public void clearItems() {
        itemsOnFloor.clear();
    }

    // ==========================================
    // SEZIONE: GESTIONE MOSTRI
    // ==========================================

    public void setMonster(Monster monster) {
        this.monster = monster;
        this.isCleared = false;
    }

    public Monster getMonster() {
        return monster;
    }

    public boolean hasMonster() {
        return monster != null && !monster.isDead();
    }

    public void clearMonster() {
        this.monster = null;
        this.isCleared = true;
    }

    public boolean isCleared() {
        return isCleared;
    }

    // ==========================================
    // SEZIONE: INFO E SETTERS
    // ==========================================

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // ==========================================
    // SEZIONE: METODI DI UTILITÀ
    // ==========================================

    @Override
    public String toString() {
        return "Stanza: " + id + "\n" + description;
    }
}