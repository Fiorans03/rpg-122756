package it.unicam.cs.mpgc.rpg122756.persistence.api;

import it.unicam.cs.mpgc.rpg122756.persistence.GameState;

public interface SaveManager {

    // ==========================================
    // SEZIONE: METODI DI PERSISTENZA
    // ==========================================

    void save(GameState state, String filePath);

    GameState load(String filePath);
}