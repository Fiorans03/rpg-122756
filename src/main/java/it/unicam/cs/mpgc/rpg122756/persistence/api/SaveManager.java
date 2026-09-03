package it.unicam.cs.mpgc.rpg122756.persistence.api;

import it.unicam.cs.mpgc.rpg122756.persistence.GameState;

/**
 * Interfaccia per la gestione della persistenza dei dati.
 * Segue il principio di Dependency Inversion (SOLID).
 */
public interface SaveManager {
    
    /**
     * Salva lo stato corrente del gioco.
     * @param state Lo stato del gioco da salvare
     * @param filePath Il percorso del file dove salvare
     */
    void save(GameState state, String filePath);

    /**
     * Carica lo stato del gioco da un file.
     * @param filePath Il percorso del file da cui caricare
     * @return Lo stato del gioco caricato
     */
    GameState load(String filePath);
}