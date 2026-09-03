package it.unicam.cs.mpgc.rpg122756.model.world;

import it.unicam.cs.mpgc.rpg122756.model.entities.Alchemist;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestisce l'intero dungeon, i suoi livelli e la posizione dell'Alchimista.
 * Funge da "Game State" per l'esplorazione.
 */
public class Dungeon {
    
    private final List<DungeonLevel> levels;
    private int currentLevelIndex;
    private Room currentRoom;
    private final Alchemist alchemist;

    public Dungeon(Alchemist alchemist) {
        this.alchemist = alchemist;
        this.levels = new ArrayList<>();
        this.currentLevelIndex = 0;
    }

    public void addLevel(DungeonLevel level) {
        levels.add(level);
    }

    /**
     * Inizia l'esplorazione dal primo livello.
     */
    public void start() {
        if (!levels.isEmpty()) {
            enterLevel(0);
        }
    }

    /**
     * Entra in un livello specifico e posiziona l'alchimista nella stanza di inizio.
     */
    public void enterLevel(int levelIndex) {
        if (levelIndex >= 0 && levelIndex < levels.size()) {
            this.currentLevelIndex = levelIndex;
            this.currentRoom = levels.get(levelIndex).getStartRoom();
        }
    }

    /**
     * Tenta di muovere l'alchimista nella direzione specificata.
     * @param direction La direzione in cui muoversi
     * @return true se il movimento è avvenuto, false se bloccato
     */
    public boolean move(Direction direction) {
        Room nextRoom = currentRoom.getExit(direction);
        if (nextRoom != null) {
            // Qui potremmo aggiungere controlli (es. porta chiusa se c'è un mostro)
            this.currentRoom = nextRoom;
            return true;
        }
        return false;
    }

    /**
     * Passa al livello successivo (dopo aver sconfitto il boss).
     * @return true se c'è un livello successivo, false se il gioco è finito
     */
    public boolean goToNextLevel() {
        if (currentLevelIndex + 1 < levels.size()) {
            enterLevel(currentLevelIndex + 1);
            return true;
        }
        return false; // Ultimo livello
    }

    // --- Getters ---

    public Room getCurrentRoom() { return currentRoom; }
    public DungeonLevel getCurrentLevel() { return levels.get(currentLevelIndex); }
    public int getCurrentLevelNumber() { return currentLevelIndex + 1; }
    public Alchemist getAlchemist() { return alchemist; }
    public int getTotalLevels() { return levels.size(); }
    public boolean isGameCompleted() { 
        return currentLevelIndex == levels.size() - 1 && getCurrentLevel().isCompleted(); 
    }
}