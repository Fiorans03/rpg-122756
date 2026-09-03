package it.unicam.cs.mpgc.rpg122756.persistence.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.unicam.cs.mpgc.rpg122756.persistence.GameState;
import it.unicam.cs.mpgc.rpg122756.persistence.api.SaveManager;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Implementazione di SaveManager che utilizza il formato JSON.
 */
public class JsonSaveManager implements SaveManager {

    private final Gson gson;

    public JsonSaveManager() {
        // GsonBuilder per formattare il JSON in modo leggibile
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    @Override
    public void save(GameState state, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(state, writer);
            System.out.println("Partita salvata con successo in: " + filePath);
        } catch (IOException e) {
            System.err.println("Errore durante il salvataggio: " + e.getMessage());
        }
    }

    @Override
    public GameState load(String filePath) {
        try (FileReader reader = new FileReader(filePath)) {
            GameState state = gson.fromJson(reader, GameState.class);
            System.out.println("Partita caricata con successo da: " + filePath);
            return state;
        } catch (IOException e) {
            System.err.println("Errore durante il caricamento: " + e.getMessage());
            return null;
        }
    }
}