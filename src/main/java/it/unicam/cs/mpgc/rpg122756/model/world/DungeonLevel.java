package it.unicam.cs.mpgc.rpg122756.model.world;

import java.util.HashMap;
import java.util.Map;

/**
 * Rappresenta un singolo livello (piano) del dungeon.
 * Contiene la mappa delle stanze e definisce la stanza di inizio e quella del boss.
 */
public class DungeonLevel {
    
    private final int levelNumber;
    private final String themeName;
    private final Map<String, Room> rooms;
    private Room startRoom;
    private Room bossRoom;

    public DungeonLevel(int levelNumber, String themeName) {
        this.levelNumber = levelNumber;
        this.themeName = themeName;
        this.rooms = new HashMap<>();
    }

    public void addRoom(Room room) {
        rooms.put(room.getId(), room);
    }

    public Room getRoom(String id) {
        return rooms.get(id);
    }

    public void setStartRoom(Room room) {
        this.startRoom = room;
    }

    public void setBossRoom(Room room) {
        this.bossRoom = room;
    }

    public Room getStartRoom() {
        return startRoom;
    }

    public Room getBossRoom() {
        return bossRoom;
    }

    public int getLevelNumber() { return levelNumber; }
    public String getThemeName() { return themeName; }

    /**
     * Verifica se il livello è stato completato (boss sconfitto).
     */
    public boolean isCompleted() {
        return bossRoom != null && bossRoom.isCleared();
    }
}