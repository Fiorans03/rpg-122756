package it.unicam.cs.mpgc.rpg122756.game.engine;

import it.unicam.cs.mpgc.rpg122756.game.combat.CombatSystem;
import it.unicam.cs.mpgc.rpg122756.model.crafting.AlchemyBook;
import it.unicam.cs.mpgc.rpg122756.model.entities.Alchemist;
import it.unicam.cs.mpgc.rpg122756.model.world.Dungeon;
import it.unicam.cs.mpgc.rpg122756.persistence.GameState;
import it.unicam.cs.mpgc.rpg122756.persistence.api.SaveManager;

import java.util.LinkedList;
import java.util.Queue;

public class GameEngine {

    // ==========================================
    // SEZIONE: VARIABILI DI ISTANZA
    // ==========================================

    private final Alchemist alchemist;
    private final Dungeon dungeon;
    private final AlchemyBook alchemyBook;
    private final CombatSystem combatSystem;
    private final SaveManager saveManager;

    private final Queue<String> messageLog;
    private boolean isGameOver;
    private boolean isGameWon;

    // ==========================================
    // SEZIONE: COSTRUTTORE
    // ==========================================

    public GameEngine(Alchemist alchemist, Dungeon dungeon, AlchemyBook alchemyBook,
            CombatSystem combatSystem, SaveManager saveManager) {
        this.alchemist = alchemist;
        this.dungeon = dungeon;
        this.alchemyBook = alchemyBook;
        this.combatSystem = combatSystem;
        this.saveManager = saveManager;
        this.messageLog = new LinkedList<>();
        this.isGameOver = false;
        this.isGameWon = false;
    }

    // ==========================================
    // SEZIONE: METODI GETTER E ACCESSORI
    // ==========================================

    public Alchemist getAlchemist() {
        return alchemist;
    }

    public Dungeon getDungeon() {
        return dungeon;
    }

    public AlchemyBook getAlchemyBook() {
        return alchemyBook;
    }

    public CombatSystem getCombatSystem() {
        return combatSystem;
    }

    public Queue<String> getMessageLog() {
        return messageLog;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public boolean isGameWon() {
        return isGameWon;
    }

    // ==========================================
    // SEZIONE: LOGICA DI GIOCO E GESTIONE
    // ==========================================

    public boolean isCombatActive() {
        return dungeon.getCurrentRoom().hasMonster();
    }

    public void addMessage(String message) {
        messageLog.add(message);
    }

    public void saveGame(String filePath) {
        GameState state = new GameState(alchemist, alchemyBook.getDiscoveredIngredients(),
                dungeon.getCurrentLevelNumber() - 1, dungeon.getCurrentRoom().getId());
        saveManager.save(state, filePath);
    }

    public void handlePlayerDeath() {
        int currentLevel = alchemist.getLevel();
        int newLevel = Math.max(1, currentLevel / 2);

        alchemist.setLevel(newLevel);
        alchemist.setExperience(0);
        alchemist.consumeOneLevelUp();

        alchemist.getInventory().clear();
        alchemist.setHp(alchemist.getMaxHp());
        alchemist.setAp(alchemist.getMaxAp());

        addMessage("☠️ SEI STATO SCONFITTO! ☠️");
        addMessage("Ti risvegli all'ingresso del dungeon, stordito ma vivo.");
        addMessage("Hai perso tutti i tuoi oggetti e il tuo livello è stato dimezzato (Livello " + newLevel + ").");
        addMessage("I tuoi poteri appresi sono intatti, ma la tua esperienza è azzerata. Riconquista la tua forza.");
    }
}