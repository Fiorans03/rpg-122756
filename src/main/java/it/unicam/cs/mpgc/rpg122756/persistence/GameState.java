package it.unicam.cs.mpgc.rpg122756.persistence;

import it.unicam.cs.mpgc.rpg122756.model.entities.Alchemist;

import java.util.HashSet;
import java.util.Set;

/**
 * Contiene tutti i dati necessari per salvare e caricare una partita.
 * È una classe pura, pensata per essere serializzata facilmente.
 */
public class GameState {

    private Alchemist alchemist;
    private Set<String> discoveredIngredients;
    private Set<String> discoveredWeaknesses;
    private int currentLevelIndex;
    private String currentRoomId;

    public GameState() {
        this.discoveredWeaknesses = new HashSet<>();
    }

    public GameState(Alchemist alchemist, Set<String> discoveredIngredients,
            int currentLevelIndex, String currentRoomId) {
        this.alchemist = alchemist;
        this.discoveredIngredients = discoveredIngredients;
        this.currentLevelIndex = currentLevelIndex;
        this.currentRoomId = currentRoomId;
    }

    // Getters e Setters (necessari per Gson)
    public Alchemist getAlchemist() {
        return alchemist;
    }

    public void setAlchemist(Alchemist alchemist) {
        this.alchemist = alchemist;
    }

    public Set<String> getDiscoveredIngredients() {
        return discoveredIngredients;
    }

    public Set<String> getDiscoveredWeaknesses() {
        return discoveredWeaknesses;
    }

    public void setDiscoveredIngredients(Set<String> discoveredIngredients) {
        this.discoveredIngredients = discoveredIngredients;
    }

    public void setDiscoveredWeaknesses(Set<String> discoveredWeaknesses) {
        this.discoveredWeaknesses = discoveredWeaknesses;
    }

    public int getCurrentLevelIndex() {
        return currentLevelIndex;
    }

    public void setCurrentLevelIndex(int currentLevelIndex) {
        this.currentLevelIndex = currentLevelIndex;
    }

    public String getCurrentRoomId() {
        return currentRoomId;
    }

    public void setCurrentRoomId(String currentRoomId) {
        this.currentRoomId = currentRoomId;
    }
}