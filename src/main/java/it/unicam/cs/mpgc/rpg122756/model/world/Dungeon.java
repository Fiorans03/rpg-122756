package it.unicam.cs.mpgc.rpg122756.model.world;

import it.unicam.cs.mpgc.rpg122756.model.entities.Alchemist;
import java.util.ArrayList;
import java.util.List;

public class Dungeon {

    // ==========================================
    // SEZIONE: VARIABILI DI ISTANZA
    // ==========================================

    private final List<DungeonLevel> levels;
    private int currentLevelIndex;
    private Room currentRoom;
    private final Alchemist alchemist;

    // ==========================================
    // SEZIONE: COSTRUTTORE
    // ==========================================

    public Dungeon(Alchemist alchemist) {
        this.alchemist = alchemist;
        this.levels = new ArrayList<>();
        this.currentLevelIndex = 0;
    }

    // ==========================================
    // SEZIONE: METODI DI GESTIONE DEL DUNGEON
    // ==========================================

    public void addLevel(DungeonLevel level) {
        levels.add(level);
    }

    public void start() {
        if (!levels.isEmpty()) {
            enterLevel(0);
        }
    }

    public void enterLevel(int levelIndex) {
        if (levelIndex >= 0 && levelIndex < levels.size()) {
            this.currentLevelIndex = levelIndex;
            this.currentRoom = levels.get(levelIndex).getStartRoom();
        }
    }

    public boolean move(Direction direction) {
        Room nextRoom = currentRoom.getExit(direction);
        if (nextRoom != null) {
            this.currentRoom = nextRoom;
            return true;
        }
        return false;
    }

    public boolean goToNextLevel() {
        if (currentLevelIndex + 1 < levels.size()) {
            enterLevel(currentLevelIndex + 1);
            return true;
        }
        return false;
    }

    // ==========================================
    // SEZIONE: METODI GETTER E CONSULTAZIONE
    // ==========================================

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public DungeonLevel getCurrentLevel() {
        return levels.get(currentLevelIndex);
    }

    public int getCurrentLevelNumber() {
        return currentLevelIndex + 1;
    }

    public Alchemist getAlchemist() {
        return alchemist;
    }

    public int getTotalLevels() {
        return levels.size();
    }

    public boolean isGameCompleted() {
        return currentLevelIndex == levels.size() - 1 && getCurrentLevel().isCompleted();
    }
}