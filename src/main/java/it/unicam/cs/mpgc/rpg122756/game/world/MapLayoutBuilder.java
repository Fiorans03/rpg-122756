package it.unicam.cs.mpgc.rpg122756.game.world;

import it.unicam.cs.mpgc.rpg122756.TileConstants;
import it.unicam.cs.mpgc.rpg122756.game.engine.GameEngine;
import it.unicam.cs.mpgc.rpg122756.model.entities.Monster;
import it.unicam.cs.mpgc.rpg122756.model.items.Ingredient;
import it.unicam.cs.mpgc.rpg122756.model.items.Item;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

public class MapLayoutBuilder {

    // ==========================================
    // SEZIONE: VARIABILI DI ISTANZA
    // ==========================================

    private final int[][][] currentMap;
    private final Monster[][][] monsters;
    private final String[][][] monsterNames;
    private final Item[][][] itemsOnMap;
    private final String[][][] itemNames;
    private final int GRID_SIZE;
    private final Random random;
    private final GameEngine engine;
    private final java.util.Map<Integer, Long> bossDefeatedTimes;

    // ==========================================
    // SEZIONE: COSTRUTTORE
    // ==========================================

    public MapLayoutBuilder(int[][][] currentMap, Monster[][][] monsters,
            String[][][] monsterNames, Item[][][] itemsOnMap,
            String[][][] itemNames, int GRID_SIZE, Random random,
            GameEngine engine, java.util.Map<Integer, Long> bossDefeatedTimes) {
        this.currentMap = currentMap;
        this.monsters = monsters;
        this.monsterNames = monsterNames;
        this.itemsOnMap = itemsOnMap;
        this.itemNames = itemNames;
        this.GRID_SIZE = GRID_SIZE;
        this.random = random;
        this.engine = engine;
        this.bossDefeatedTimes = bossDefeatedTimes;
    }

    // ==========================================
    // SEZIONE: INIZIALIZZAZIONE STANZE (LIVELLO 1: FORESTA)
    // ==========================================

    public void initializeRoom1() {
        int room = 0;

        for (int i = 0; i < GRID_SIZE; i++) {
            currentMap[room][0][i] = TileConstants.TILE_WALL;
            currentMap[room][GRID_SIZE - 1][i] = TileConstants.TILE_WALL;
            currentMap[room][i][0] = TileConstants.TILE_WALL;
            currentMap[room][i][GRID_SIZE - 1] = TileConstants.TILE_WALL;
        }

        boolean validMap = false;
        while (!validMap) {
            for (int x = 1; x < GRID_SIZE - 1; x++) {
                for (int y = 1; y < GRID_SIZE - 1; y++) {
                    currentMap[room][x][y] = TileConstants.TILE_GRASS;
                }
            }

            for (int i = 0; i < 25; i++) {
                int x, y;
                do {
                    x = 1 + random.nextInt(GRID_SIZE - 2);
                    y = 1 + random.nextInt(GRID_SIZE - 2);
                } while (currentMap[room][x][y] != TileConstants.TILE_GRASS);
                currentMap[room][x][y] = TileConstants.TILE_TREE;
            }

            if (isReachable(room, 7, 13, 7, 1)) {
                validMap = true;
            }
        }

        currentMap[room][7][2] = TileConstants.TILE_GRASS;
        currentMap[room][7][3] = TileConstants.TILE_GRASS;
        currentMap[room][7][1] = TileConstants.TILE_DOOR;
        currentMap[room][6][1] = TileConstants.TILE_TREE;
        currentMap[room][8][1] = TileConstants.TILE_TREE;

        List<Monster> monsterPool = Arrays.asList(
                new Monster("Folletto Dispettoso", 30, 6, 2, 20, Arrays.asList(new Ingredient("Polvere di Folletto", "Scintillante", 13, 1)), "Luce", false),
                new Monster("Lupo Mannaro", 60, 12, 5, 35, Arrays.asList(new Ingredient("Zanna di Lupo", "Affilata", 23, 1)), "Luce", false),
                new Monster("Ragno Velenoso", 40, 8, 3, 25, Arrays.asList(new Ingredient("Tela di Ragno", "Appiccicosa", 19, 1)), "Fuoco", false));

        int numMonstersRoom1 = 1 + random.nextInt(2);
        for (int i = 0; i < numMonstersRoom1; i++) {
            int x, y;
            do {
                x = 1 + random.nextInt(GRID_SIZE - 2);
                y = 1 + random.nextInt(GRID_SIZE - 2);
            } while (currentMap[room][x][y] != TileConstants.TILE_GRASS);

            currentMap[room][x][y] = TileConstants.TILE_MONSTER;
            Monster baseMonster = monsterPool.get(random.nextInt(monsterPool.size()));
            monsters[room][x][y] = new Monster(baseMonster.getName(), baseMonster.getMaxHp(), baseMonster.getAttack(), baseMonster.getDefense(), baseMonster.getXpReward(), baseMonster.getPossibleDrops(), baseMonster.getWeakness(), false);
            monsterNames[room][x][y] = baseMonster.getName();
        }

        List<String[]> availableItems = Arrays.asList(
                new String[] { "Erba Lunare", "Erba che brilla.", "11" },
                new String[] { "Lucciola Argentea", "Luce tenue.", "17" },
                new String[] { "Radice Secca", "Materiale base.", "7" });
        int numItemsRoom1 = 1 + random.nextInt(2);
        for (int i = 0; i < numItemsRoom1; i++) {
            int x, y;
            do {
                x = 1 + random.nextInt(GRID_SIZE - 2);
                y = 1 + random.nextInt(GRID_SIZE - 2);
            } while (currentMap[room][x][y] != TileConstants.TILE_GRASS);

            currentMap[room][x][y] = TileConstants.TILE_ITEM;
            String[] itemData = availableItems.get(random.nextInt(availableItems.size()));
            itemNames[room][x][y] = itemData[0];
            itemsOnMap[room][x][y] = new Ingredient(itemData[0], itemData[1], Integer.parseInt(itemData[2]), 1);
        }

        engine.addMessage("Benvenuto/a nel dungeon giovane coraggioso/a. Ti invito a scoprire ogni segreto di questo posto....");
    }

    public void initializeRoom2() {
        int room = 1;

        for (int i = 0; i < GRID_SIZE; i++) {
            currentMap[room][0][i] = TileConstants.TILE_WALL;
            currentMap[room][GRID_SIZE - 1][i] = TileConstants.TILE_WALL;
            currentMap[room][i][0] = TileConstants.TILE_WALL;
            currentMap[room][i][GRID_SIZE - 1] = TileConstants.TILE_WALL;
        }

        boolean validMap = false;
        while (!validMap) {
            for (int x = 1; x < GRID_SIZE - 1; x++) {
                for (int y = 1; y < GRID_SIZE - 1; y++) {
                    currentMap[room][x][y] = TileConstants.TILE_GRASS;
                }
            }

            for (int i = 0; i < 35; i++) {
                int x, y;
                do {
                    x = 1 + random.nextInt(GRID_SIZE - 2);
                    y = 1 + random.nextInt(GRID_SIZE - 2);
                } while (currentMap[room][x][y] != TileConstants.TILE_GRASS);
                currentMap[room][x][y] = TileConstants.TILE_TREE;
            }

            if (isReachable(room, 7, 13, 7, 1)) {
                validMap = true;
            }
        }

        currentMap[room][7][2] = TileConstants.TILE_GRASS;
        currentMap[room][7][3] = TileConstants.TILE_GRASS;
        currentMap[room][7][12] = TileConstants.TILE_GRASS;
        currentMap[room][7][11] = TileConstants.TILE_GRASS;

        currentMap[room][7][GRID_SIZE - 2] = TileConstants.TILE_DOOR;
        currentMap[room][6][GRID_SIZE - 2] = TileConstants.TILE_TREE;
        currentMap[room][8][GRID_SIZE - 2] = TileConstants.TILE_TREE;

        currentMap[room][7][1] = TileConstants.TILE_DOOR;
        currentMap[room][6][1] = TileConstants.TILE_TREE;
        currentMap[room][8][1] = TileConstants.TILE_TREE;

        List<Monster> monsterPool = Arrays.asList(
                new Monster("Ragno Velenoso", 40, 8, 3, 25, Arrays.asList(new Ingredient("Tela di Ragno", "Appiccicosa", 19, 1)), "Fuoco", false),
                new Monster("Lupo Mannaro", 60, 12, 5, 35, Arrays.asList(new Ingredient("Zanna di Lupo", "Affilata", 23, 1)), "Luce", false),
                new Monster("Folletto Dispettoso", 30, 6, 2, 20, Arrays.asList(new Ingredient("Polvere di Folletto", "Scintillante", 13, 1)), "Luce", false));

        int numMonstersRoom2 = 5 + random.nextInt(2);
        for (int i = 0; i < numMonstersRoom2; i++) {
            int x, y;
            do {
                x = 1 + random.nextInt(GRID_SIZE - 2);
                y = 1 + random.nextInt(GRID_SIZE - 2);
            } while (currentMap[room][x][y] != TileConstants.TILE_GRASS);

            currentMap[room][x][y] = TileConstants.TILE_MONSTER;
            Monster baseMonster = monsterPool.get(random.nextInt(monsterPool.size()));
            monsters[room][x][y] = new Monster(baseMonster.getName(), baseMonster.getMaxHp(), baseMonster.getAttack(), baseMonster.getDefense(), baseMonster.getXpReward(), baseMonster.getPossibleDrops(), baseMonster.getWeakness(), false);
            monsterNames[room][x][y] = baseMonster.getName();
        }

        List<String[]> availableItems = Arrays.asList(
                new String[] { "Erba Lunare", "Erba che brilla.", "11" },
                new String[] { "Lucciola Argentea", "Luce tenue.", "17" },
                new String[] { "Radice Secca", "Materiale base.", "7" });

        int numItemsRoom2 = 6;
        for (int i = 0; i < numItemsRoom2; i++) {
            int x, y;
            do {
                x = 1 + random.nextInt(GRID_SIZE - 2);
                y = 1 + random.nextInt(GRID_SIZE - 2);
            } while (currentMap[room][x][y] != TileConstants.TILE_GRASS);

            currentMap[room][x][y] = TileConstants.TILE_ITEM;
            String[] itemData = availableItems.get(random.nextInt(availableItems.size()));
            itemNames[room][x][y] = itemData[0];
            itemsOnMap[room][x][y] = new Ingredient(itemData[0], itemData[1], Integer.parseInt(itemData[2]), 1);
        }
    }

    public void initializeRoom3() {
        int room = 2;

        for (int i = 0; i < GRID_SIZE; i++) {
            currentMap[room][0][i] = TileConstants.TILE_WALL;
            currentMap[room][GRID_SIZE - 1][i] = TileConstants.TILE_WALL;
            currentMap[room][i][0] = TileConstants.TILE_WALL;
            currentMap[room][i][GRID_SIZE - 1] = TileConstants.TILE_WALL;
        }

        for (int x = 1; x < GRID_SIZE - 1; x++) {
            for (int y = 1; y < GRID_SIZE - 1; y++) {
                currentMap[room][x][y] = TileConstants.TILE_GRASS;
            }
        }

        for (int x = 1; x < GRID_SIZE - 1; x++) {
            if (x != 7) currentMap[room][x][3] = TileConstants.TILE_TREE;
        }
        for (int x = 1; x < GRID_SIZE - 1; x++) {
            if (x != 5 && x != 9) currentMap[room][x][6] = TileConstants.TILE_TREE;
        }
        for (int x = 1; x < GRID_SIZE - 1; x++) {
            if (x != 3 && x != 7) currentMap[room][x][9] = TileConstants.TILE_TREE;
        }
        for (int x = 1; x < GRID_SIZE - 1; x++) {
            if (x != 7) currentMap[room][x][12] = TileConstants.TILE_TREE;
        }

        currentMap[room][9][4] = TileConstants.TILE_TREE;
        currentMap[room][9][5] = TileConstants.TILE_TREE;
        currentMap[room][9][6] = TileConstants.TILE_TREE;
        currentMap[room][9][7] = TileConstants.TILE_TREE;
        currentMap[room][9][8] = TileConstants.TILE_TREE;

        currentMap[room][3][7] = TileConstants.TILE_TREE;
        currentMap[room][3][8] = TileConstants.TILE_TREE;
        currentMap[room][3][9] = TileConstants.TILE_TREE;
        currentMap[room][3][10] = TileConstants.TILE_TREE;
        currentMap[room][3][11] = TileConstants.TILE_TREE;

        currentMap[room][5][10] = TileConstants.TILE_TREE;
        currentMap[room][5][11] = TileConstants.TILE_TREE;

        currentMap[room][2][4] = TileConstants.TILE_TREE;
        currentMap[room][12][4] = TileConstants.TILE_TREE;
        currentMap[room][2][10] = TileConstants.TILE_TREE;
        currentMap[room][12][10] = TileConstants.TILE_TREE;
        currentMap[room][4][5] = TileConstants.TILE_TREE;
        currentMap[room][10][5] = TileConstants.TILE_TREE;

        currentMap[room][7][GRID_SIZE - 2] = TileConstants.TILE_HIDDEN_DOOR;
        currentMap[room][6][GRID_SIZE - 2] = TileConstants.TILE_TREE;
        currentMap[room][8][GRID_SIZE - 2] = TileConstants.TILE_TREE;

        currentMap[room][7][1] = TileConstants.TILE_HIDDEN_DOOR;
        currentMap[room][6][1] = TileConstants.TILE_TREE;
        currentMap[room][8][1] = TileConstants.TILE_TREE;

        currentMap[room][7][4] = TileConstants.TILE_BOSS;
        monsterNames[room][7][4] = "Guardiano della Foresta";
        monsters[room][7][4] = new Monster("Guardiano della Foresta", 150, 20, 10, 200,
                Arrays.asList(new Ingredient("Cuore della Foresta", "Pulsante", 53, 1)), "Fuoco", true);
    }

    // ==========================================
    // SEZIONE: INIZIALIZZAZIONE STANZE (LIVELLO 2: CAVERNE)
    // ==========================================

    public void initializeRoom4() {
        int room = 3;

        for (int i = 0; i < GRID_SIZE; i++) {
            currentMap[room][0][i] = TileConstants.TILE_WALL;
            currentMap[room][GRID_SIZE - 1][i] = TileConstants.TILE_WALL;
            currentMap[room][i][0] = TileConstants.TILE_WALL;
            currentMap[room][i][GRID_SIZE - 1] = TileConstants.TILE_WALL;
        }

        for (int x = 1; x < GRID_SIZE - 1; x++) {
            for (int y = 1; y < GRID_SIZE - 1; y++) {
                currentMap[room][x][y] = TileConstants.TILE_GRASS;
            }
        }

        boolean validMap = false;
        while (!validMap) {
            for (int x = 1; x < GRID_SIZE - 1; x++) {
                for (int y = 1; y < GRID_SIZE - 1; y++) {
                    currentMap[room][x][y] = TileConstants.TILE_GRASS;
                }
            }

            for (int i = 0; i < 25; i++) {
                int x, y;
                do {
                    x = 1 + random.nextInt(GRID_SIZE - 2);
                    y = 1 + random.nextInt(GRID_SIZE - 2);
                } while (currentMap[room][x][y] != TileConstants.TILE_GRASS);
                currentMap[room][x][y] = TileConstants.TILE_TREE;
            }

            if (isReachable(room, 7, 13, 7, 1)) {
                validMap = true;
            }
        }

        currentMap[room][7][2] = TileConstants.TILE_GRASS;
        currentMap[room][7][3] = TileConstants.TILE_GRASS;
        currentMap[room][7][12] = TileConstants.TILE_GRASS;
        currentMap[room][7][11] = TileConstants.TILE_GRASS;

        currentMap[room][7][1] = TileConstants.TILE_DOOR;
        currentMap[room][6][1] = TileConstants.TILE_TREE;
        currentMap[room][8][1] = TileConstants.TILE_TREE;

        currentMap[room][7][13] = TileConstants.TILE_DOOR;
        currentMap[room][6][13] = TileConstants.TILE_TREE;
        currentMap[room][8][13] = TileConstants.TILE_TREE;

        List<Monster> monsterPool = Arrays.asList(
                new Monster("Pipistrello delle Caverne", 50, 10, 4, 35, Arrays.asList(new Ingredient("Guano di Pipistrello", "Appiccicoso", 41, 1)), "Luce", false),
                new Monster("Ratto Gigante", 45, 9, 3, 30, Arrays.asList(new Ingredient("Dente di Ratto", "Affilato", 43, 1)), "Veleno", false),
                new Monster("Golem di Pietra Minore", 80, 12, 8, 45, Arrays.asList(new Ingredient("Scheggia di Pietra", "Tagliente", 47, 1)), "Fuoco", false));

        int numMonsters = 1 + random.nextInt(2);
        for (int i = 0; i < numMonsters; i++) {
            int x, y;
            do {
                x = 1 + random.nextInt(GRID_SIZE - 2);
                y = 1 + random.nextInt(GRID_SIZE - 2);
            } while (currentMap[room][x][y] != TileConstants.TILE_GRASS);

            currentMap[room][x][y] = TileConstants.TILE_MONSTER;
            Monster baseMonster = monsterPool.get(random.nextInt(monsterPool.size()));
            monsters[room][x][y] = new Monster(baseMonster.getName(), baseMonster.getMaxHp(), baseMonster.getAttack(), baseMonster.getDefense(), baseMonster.getXpReward(), baseMonster.getPossibleDrops(), baseMonster.getWeakness(), false);
            monsterNames[room][x][y] = baseMonster.getName();
        }

        List<String[]> availableItems = Arrays.asList(
                new String[] { "Muschio Luminescente", "Brilla nel buio", "29" },
                new String[] { "Cristallo Grezzo", "Pietra semipreziosa", "31" },
                new String[] { "Fungo Velenoso", "Pericoloso ma utile", "37" });

        int numItems = 1 + random.nextInt(2);
        for (int i = 0; i < numItems; i++) {
            int x, y;
            do {
                x = 1 + random.nextInt(GRID_SIZE - 2);
                y = 1 + random.nextInt(GRID_SIZE - 2);
            } while (currentMap[room][x][y] != TileConstants.TILE_GRASS);

            currentMap[room][x][y] = TileConstants.TILE_ITEM;
            String[] itemData = availableItems.get(random.nextInt(availableItems.size()));
            itemNames[room][x][y] = itemData[0];
            itemsOnMap[room][x][y] = new Ingredient(itemData[0], itemData[1], Integer.parseInt(itemData[2]), 1);
        }
    }

    public void initializeRoom5() {
        int room = 4;

        for (int i = 0; i < GRID_SIZE; i++) {
            currentMap[room][0][i] = TileConstants.TILE_WALL;
            currentMap[room][GRID_SIZE - 1][i] = TileConstants.TILE_WALL;
            currentMap[room][i][0] = TileConstants.TILE_WALL;
            currentMap[room][i][GRID_SIZE - 1] = TileConstants.TILE_WALL;
        }

        for (int x = 1; x < GRID_SIZE - 1; x++) {
            for (int y = 1; y < GRID_SIZE - 1; y++) {
                currentMap[room][x][y] = TileConstants.TILE_GRASS;
            }
        }

        boolean validMap = false;
        while (!validMap) {
            for (int x = 1; x < GRID_SIZE - 1; x++) {
                for (int y = 1; y < GRID_SIZE - 1; y++) {
                    currentMap[room][x][y] = TileConstants.TILE_GRASS;
                }
            }

            for (int i = 0; i < 35; i++) {
                int x, y;
                do {
                    x = 1 + random.nextInt(GRID_SIZE - 2);
                    y = 1 + random.nextInt(GRID_SIZE - 2);
                } while (currentMap[room][x][y] != TileConstants.TILE_GRASS);
                currentMap[room][x][y] = TileConstants.TILE_TREE;
            }

            if (isReachable(room, 7, 13, 7, 1)) {
                validMap = true;
            }
        }

        currentMap[room][7][2] = TileConstants.TILE_GRASS;
        currentMap[room][7][3] = TileConstants.TILE_GRASS;
        currentMap[room][7][12] = TileConstants.TILE_GRASS;
        currentMap[room][7][11] = TileConstants.TILE_GRASS;

        currentMap[room][7][1] = TileConstants.TILE_DOOR;
        currentMap[room][6][1] = TileConstants.TILE_TREE;
        currentMap[room][8][1] = TileConstants.TILE_TREE;

        currentMap[room][7][13] = TileConstants.TILE_DOOR;
        currentMap[room][6][13] = TileConstants.TILE_TREE;
        currentMap[room][8][13] = TileConstants.TILE_TREE;

        List<Monster> monsterPool = Arrays.asList(
                new Monster("Pipistrello delle Caverne", 50, 10, 4, 35, Arrays.asList(new Ingredient("Guano di Pipistrello", "Appiccicoso", 41, 1)), "Luce", false),
                new Monster("Ratto Gigante", 45, 9, 3, 30, Arrays.asList(new Ingredient("Dente di Ratto", "Affilato", 43, 1)), "Veleno", false),
                new Monster("Golem di Pietra Minore", 80, 12, 8, 45, Arrays.asList(new Ingredient("Scheggia di Pietra", "Tagliente", 47, 1)), "Fuoco", false));

        int numMonsters = 5 + random.nextInt(2);
        for (int i = 0; i < numMonsters; i++) {
            int x, y;
            do {
                x = 1 + random.nextInt(GRID_SIZE - 2);
                y = 1 + random.nextInt(GRID_SIZE - 2);
            } while (currentMap[room][x][y] != TileConstants.TILE_GRASS);

            currentMap[room][x][y] = TileConstants.TILE_MONSTER;
            Monster baseMonster = monsterPool.get(random.nextInt(monsterPool.size()));
            monsters[room][x][y] = new Monster(baseMonster.getName(), baseMonster.getMaxHp(), baseMonster.getAttack(), baseMonster.getDefense(), baseMonster.getXpReward(), baseMonster.getPossibleDrops(), baseMonster.getWeakness(), false);
            monsterNames[room][x][y] = baseMonster.getName();
        }

        List<String[]> availableItems = Arrays.asList(
                new String[] { "Muschio Luminescente", "Brilla nel buio", "29" },
                new String[] { "Cristallo Grezzo", "Pietra semipreziosa", "31" },
                new String[] { "Fungo Velenoso", "Pericoloso ma utile", "37" });

        int numItems = 6;
        for (int i = 0; i < numItems; i++) {
            int x, y;
            do {
                x = 1 + random.nextInt(GRID_SIZE - 2);
                y = 1 + random.nextInt(GRID_SIZE - 2);
            } while (currentMap[room][x][y] != TileConstants.TILE_GRASS);

            currentMap[room][x][y] = TileConstants.TILE_ITEM;
            String[] itemData = availableItems.get(random.nextInt(availableItems.size()));
            itemNames[room][x][y] = itemData[0];
            itemsOnMap[room][x][y] = new Ingredient(itemData[0], itemData[1], Integer.parseInt(itemData[2]), 1);
        }
    }

    public void initializeRoom6() {
        int room = 5;

        for (int i = 0; i < GRID_SIZE; i++) {
            currentMap[room][0][i] = TileConstants.TILE_WALL;
            currentMap[room][GRID_SIZE - 1][i] = TileConstants.TILE_WALL;
            currentMap[room][i][0] = TileConstants.TILE_WALL;
            currentMap[room][i][GRID_SIZE - 1] = TileConstants.TILE_WALL;
        }

        for (int x = 1; x < GRID_SIZE - 1; x++) {
            for (int y = 1; y < GRID_SIZE - 1; y++) {
                currentMap[room][x][y] = TileConstants.TILE_GRASS;
            }
        }

        currentMap[room][7][13] = TileConstants.TILE_HIDDEN_DOOR;
        currentMap[room][7][1] = TileConstants.TILE_HIDDEN_DOOR;

        for (int x = 1; x < GRID_SIZE - 1; x++) {
            if (x != 7) currentMap[room][x][3] = TileConstants.TILE_TREE;
        }
        for (int x = 1; x < GRID_SIZE - 1; x++) {
            if (x != 5 && x != 9) currentMap[room][x][6] = TileConstants.TILE_TREE;
        }
        for (int x = 1; x < GRID_SIZE - 1; x++) {
            if (x != 3 && x != 7) currentMap[room][x][9] = TileConstants.TILE_TREE;
        }
        for (int x = 1; x < GRID_SIZE - 1; x++) {
            if (x != 7) currentMap[room][x][12] = TileConstants.TILE_TREE;
        }

        currentMap[room][9][4] = TileConstants.TILE_TREE;
        currentMap[room][9][5] = TileConstants.TILE_TREE;
        currentMap[room][9][6] = TileConstants.TILE_TREE;
        currentMap[room][9][7] = TileConstants.TILE_TREE;
        currentMap[room][9][8] = TileConstants.TILE_TREE;

        currentMap[room][3][7] = TileConstants.TILE_TREE;
        currentMap[room][3][8] = TileConstants.TILE_TREE;
        currentMap[room][3][9] = TileConstants.TILE_TREE;
        currentMap[room][3][10] = TileConstants.TILE_TREE;
        currentMap[room][3][11] = TileConstants.TILE_TREE;

        currentMap[room][5][10] = TileConstants.TILE_TREE;
        currentMap[room][5][11] = TileConstants.TILE_TREE;

        currentMap[room][2][4] = TileConstants.TILE_TREE;
        currentMap[room][12][4] = TileConstants.TILE_TREE;
        currentMap[room][2][10] = TileConstants.TILE_TREE;
        currentMap[room][12][10] = TileConstants.TILE_TREE;
        currentMap[room][4][5] = TileConstants.TILE_TREE;
        currentMap[room][10][5] = TileConstants.TILE_TREE;

        int bossX = 7;
        int bossY = 4;
        currentMap[room][bossX][bossY] = TileConstants.TILE_BOSS;
        monsters[room][bossX][bossY] = new Monster("Golem di Pietra", 250, 25, 15, 400,
                Arrays.asList(new Ingredient("Nucleo di Pietra", "Energia tellurica", 71, 1)), "Fuoco", true);
        monsterNames[room][bossX][bossY] = "Golem di Pietra";
    }

    // ==========================================
    // SEZIONE: INIZIALIZZAZIONE STANZE (LIVELLO 3: PALUDE)
    // ==========================================

    public void initializeRoom7() {
        int room = 6;

        for (int i = 0; i < GRID_SIZE; i++) {
            currentMap[room][0][i] = TileConstants.TILE_WALL;
            currentMap[room][GRID_SIZE - 1][i] = TileConstants.TILE_WALL;
            currentMap[room][i][0] = TileConstants.TILE_WALL;
            currentMap[room][i][GRID_SIZE - 1] = TileConstants.TILE_WALL;
        }

        boolean validMap = false;
        while (!validMap) {
            for (int x = 1; x < GRID_SIZE - 1; x++) {
                for (int y = 1; y < GRID_SIZE - 1; y++) {
                    currentMap[room][x][y] = TileConstants.TILE_GRASS;
                }
            }

            currentMap[room][7][1] = TileConstants.TILE_DOOR;
            currentMap[room][7][GRID_SIZE - 2] = TileConstants.TILE_DOOR;

            currentMap[room][6][1] = TileConstants.TILE_TREE;
            currentMap[room][8][1] = TileConstants.TILE_TREE;
            currentMap[room][6][GRID_SIZE - 2] = TileConstants.TILE_TREE;
            currentMap[room][8][GRID_SIZE - 2] = TileConstants.TILE_TREE;

            for (int i = 0; i < 35; i++) {
                int x, y;
                do {
                    x = 1 + random.nextInt(GRID_SIZE - 2);
                    y = 1 + random.nextInt(GRID_SIZE - 2);
                } while (currentMap[room][x][y] != TileConstants.TILE_GRASS);

                currentMap[room][x][y] = TileConstants.TILE_TREE;
            }

            if (isReachable(room, 7, 1, 7, GRID_SIZE - 2)) {
                validMap = true;
            }
        }

        List<Monster> monsterPool = Arrays.asList(
                new Monster("Rospo Velenoso", 45, 12, 4, 25, Arrays.asList(new Ingredient("Occhio di Rospo", "", 35, 1), new Ingredient("Occhio di Rospo", "", 35, 1), new Ingredient("Occhio di Rospo", "", 35, 1), new Ingredient("Fango Tossico", "", 30, 1), new Ingredient("Fango Tossico", "", 30, 1)), "Fuoco", "Veleno", false),
                new Monster("Serpente di Palude", 70, 18, 8, 45, Arrays.asList(new Ingredient("Zanna di Serpente", "", 45, 1), new Ingredient("Zanna di Serpente", "", 45, 1), new Ingredient("Zanna di Serpente", "", 45, 1), new Ingredient("Fango Tossico", "", 30, 1), new Ingredient("Fango Tossico", "", 30, 1)), "Terra", "Veleno", false),
                new Monster("Spettro del Loto", 90, 22, 12, 70, Arrays.asList(new Ingredient("Loto Marcio", "", 40, 1), new Ingredient("Loto Marcio", "", 40, 1), new Ingredient("Loto Marcio", "", 40, 1), new Ingredient("Occhio di Rospo", "", 35, 1), new Ingredient("Occhio di Rospo", "", 35, 1)), "Luce", "Veleno", false));

        int numMonsters = 3 + random.nextInt(2);
        for (int i = 0; i < numMonsters; i++) {
            int x, y;
            do {
                x = 1 + random.nextInt(GRID_SIZE - 2);
                y = 1 + random.nextInt(GRID_SIZE - 2);
            } while (currentMap[room][x][y] != TileConstants.TILE_GRASS);

            currentMap[room][x][y] = TileConstants.TILE_MONSTER;
            Monster baseMonster = monsterPool.get(random.nextInt(monsterPool.size()));
            monsters[room][x][y] = new Monster(baseMonster.getName(), baseMonster.getMaxHp(), baseMonster.getAttack(), baseMonster.getDefense(), baseMonster.getXpReward(), baseMonster.getPossibleDrops(), baseMonster.getWeakness(), baseMonster.getResistance(), false);
            monsterNames[room][x][y] = baseMonster.getName();
        }

        List<String[]> availableItems = Arrays.asList(
                new String[] { "Fango Tossico", "Fango maleodorante.", "30" },
                new String[] { "Occhio di Rospo", "Occhio viscido.", "35" },
                new String[] { "Loto Marcio", "Fiore appassito.", "40" });

        int numItems = 3 + random.nextInt(2);
        for (int i = 0; i < numItems; i++) {
            int x, y;
            do {
                x = 1 + random.nextInt(GRID_SIZE - 2);
                y = 1 + random.nextInt(GRID_SIZE - 2);
            } while (currentMap[room][x][y] != TileConstants.TILE_GRASS);

            currentMap[room][x][y] = TileConstants.TILE_ITEM;
            String[] itemData = availableItems.get(random.nextInt(availableItems.size()));
            itemNames[room][x][y] = itemData[0];
            itemsOnMap[room][x][y] = new Ingredient(itemData[0], itemData[1], Integer.parseInt(itemData[2]), 1);
        }
    }

    public void initializeRoom8() {
        int room = 7;

        for (int i = 0; i < GRID_SIZE; i++) {
            currentMap[room][0][i] = TileConstants.TILE_WALL;
            currentMap[room][GRID_SIZE - 1][i] = TileConstants.TILE_WALL;
            currentMap[room][i][0] = TileConstants.TILE_WALL;
            currentMap[room][i][GRID_SIZE - 1] = TileConstants.TILE_WALL;
        }

        boolean validMap = false;
        while (!validMap) {
            for (int x = 1; x < GRID_SIZE - 1; x++) {
                for (int y = 1; y < GRID_SIZE - 1; y++) {
                    currentMap[room][x][y] = TileConstants.TILE_GRASS;
                }
            }

            currentMap[room][7][1] = TileConstants.TILE_DOOR;
            currentMap[room][7][GRID_SIZE - 2] = TileConstants.TILE_DOOR;

            currentMap[room][6][1] = TileConstants.TILE_TREE;
            currentMap[room][8][1] = TileConstants.TILE_TREE;
            currentMap[room][6][GRID_SIZE - 2] = TileConstants.TILE_TREE;
            currentMap[room][8][GRID_SIZE - 2] = TileConstants.TILE_TREE;

            for (int i = 0; i < 40; i++) {
                int x, y;
                do {
                    x = 1 + random.nextInt(GRID_SIZE - 2);
                    y = 1 + random.nextInt(GRID_SIZE - 2);
                } while (currentMap[room][x][y] != TileConstants.TILE_GRASS);

                currentMap[room][x][y] = TileConstants.TILE_TREE;
            }

            if (isReachable(room, 7, 1, 7, GRID_SIZE - 2)) {
                validMap = true;
            }
        }

        List<Monster> monsterPool = Arrays.asList(
                new Monster("Rospo Velenoso", 45, 12, 4, 25, Arrays.asList(new Ingredient("Occhio di Rospo", "", 35, 1), new Ingredient("Occhio di Rospo", "", 35, 1), new Ingredient("Occhio di Rospo", "", 35, 1), new Ingredient("Fango Tossico", "", 30, 1), new Ingredient("Fango Tossico", "", 30, 1)), "Fuoco", "Veleno", false),
                new Monster("Serpente di Palude", 70, 18, 8, 45, Arrays.asList(new Ingredient("Zanna di Serpente", "", 45, 1), new Ingredient("Zanna di Serpente", "", 45, 1), new Ingredient("Zanna di Serpente", "", 45, 1), new Ingredient("Fango Tossico", "", 30, 1), new Ingredient("Fango Tossico", "", 30, 1)), "Terra", "Veleno", false),
                new Monster("Spettro del Loto", 90, 22, 12, 70, Arrays.asList(new Ingredient("Loto Marcio", "", 40, 1), new Ingredient("Loto Marcio", "", 40, 1), new Ingredient("Loto Marcio", "", 40, 1), new Ingredient("Occhio di Rospo", "", 35, 1), new Ingredient("Occhio di Rospo", "", 35, 1)), "Luce", "Veleno", false));

        int numMonsters = 4 + random.nextInt(2);
        for (int i = 0; i < numMonsters; i++) {
            int x, y;
            do {
                x = 1 + random.nextInt(GRID_SIZE - 2);
                y = 1 + random.nextInt(GRID_SIZE - 2);
            } while (currentMap[room][x][y] != TileConstants.TILE_GRASS);

            currentMap[room][x][y] = TileConstants.TILE_MONSTER;
            Monster baseMonster = monsterPool.get(random.nextInt(monsterPool.size()));
            monsters[room][x][y] = new Monster(baseMonster.getName(), baseMonster.getMaxHp(), baseMonster.getAttack(), baseMonster.getDefense(), baseMonster.getXpReward(), baseMonster.getPossibleDrops(), baseMonster.getWeakness(), baseMonster.getResistance(), false);
            monsterNames[room][x][y] = baseMonster.getName();
        }

        List<String[]> availableItems = Arrays.asList(
                new String[] { "Fango Tossico", "Fango maleodorante.", "30" },
                new String[] { "Occhio di Rospo", "Occhio viscido.", "35" },
                new String[] { "Loto Marcio", "Fiore appassito.", "40" },
                new String[] { "Zanna di Serpente", "Zanna affilata.", "45" });

        int numItems = 4;
        for (int i = 0; i < numItems; i++) {
            int x, y;
            do {
                x = 1 + random.nextInt(GRID_SIZE - 2);
                y = 1 + random.nextInt(GRID_SIZE - 2);
            } while (currentMap[room][x][y] != TileConstants.TILE_GRASS);

            currentMap[room][x][y] = TileConstants.TILE_ITEM;
            String[] itemData = availableItems.get(random.nextInt(availableItems.size()));
            itemNames[room][x][y] = itemData[0];
            itemsOnMap[room][x][y] = new Ingredient(itemData[0], itemData[1], Integer.parseInt(itemData[2]), 1);
        }
    }

    public void initializeRoom9() {
        int room = 8;

        for (int i = 0; i < GRID_SIZE; i++) {
            currentMap[room][0][i] = TileConstants.TILE_WALL;
            currentMap[room][GRID_SIZE - 1][i] = TileConstants.TILE_WALL;
            currentMap[room][i][0] = TileConstants.TILE_WALL;
            currentMap[room][i][GRID_SIZE - 1] = TileConstants.TILE_WALL;
        }

        boolean validMap = false;
        while (!validMap) {
            for (int x = 1; x < GRID_SIZE - 1; x++) {
                for (int y = 1; y < GRID_SIZE - 1; y++) {
                    currentMap[room][x][y] = TileConstants.TILE_GRASS;
                }
            }

            currentMap[room][7][GRID_SIZE - 2] = TileConstants.TILE_DOOR;

            currentMap[room][6][1] = TileConstants.TILE_TREE;
            currentMap[room][8][1] = TileConstants.TILE_TREE;
            currentMap[room][6][GRID_SIZE - 2] = TileConstants.TILE_TREE;
            currentMap[room][8][GRID_SIZE - 2] = TileConstants.TILE_TREE;

            for (int i = 0; i < 60; i++) {
                int x, y;
                do {
                    x = 1 + random.nextInt(GRID_SIZE - 2);
                    y = 1 + random.nextInt(GRID_SIZE - 2);
                } while (currentMap[room][x][y] != TileConstants.TILE_GRASS || (x == 7 && y == 4));

                currentMap[room][x][y] = TileConstants.TILE_TREE;
            }

            if (isReachable(room, 7, GRID_SIZE - 2, 7, 4)) {
                validMap = true;
            }
        }

        currentMap[room][7][4] = TileConstants.TILE_BOSS;

        List<Item> dropsBasilisco = Arrays.asList(
                new Ingredient("Zanna di Serpente", "", 45, 1),
                new Ingredient("Zanna di Serpente", "", 45, 1),
                new Ingredient("Zanna di Serpente", "", 45, 1),
                new Ingredient("Fango Tossico", "", 30, 1),
                new Ingredient("Fango Tossico", "", 30, 1),
                new Ingredient("Cuore della Palude", "", 80, 1));

        monsters[room][7][4] = new Monster("Basilisco della Palude", 400, 32, 18, 700, dropsBasilisco, "Fuoco", "Veleno", true);
        monsterNames[room][7][4] = "Basilisco della Palude";
    }

    // ==========================================
    // SEZIONE: ALGORITMI DI UTILITÀ
    // ==========================================

    private boolean isReachable(int room, int startX, int startY, int endX, int endY) {
        boolean[][] visited = new boolean[GRID_SIZE][GRID_SIZE];
        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[] { startX, startY });
        visited[startX][startY] = true;

        int[] dx = { 0, 0, 1, -1 };
        int[] dy = { 1, -1, 0, 0 };

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            if (current[0] == endX && current[1] == endY) return true;

            for (int i = 0; i < 4; i++) {
                int nx = current[0] + dx[i];
                int ny = current[1] + dy[i];

                if (nx >= 0 && nx < GRID_SIZE && ny >= 0 && ny < GRID_SIZE && !visited[nx][ny]) {
                    int tile = currentMap[room][nx][ny];
                    if (tile != TileConstants.TILE_WALL && tile != TileConstants.TILE_TREE) {
                        visited[nx][ny] = true;
                        queue.add(new int[] { nx, ny });
                    }
                }
            }
        }
        return false;
    }

    // ==========================================
    // SEZIONE: SISTEMA DI RESPAWN
    // ==========================================

    public void respawnAllRooms() {
        for (int roomIndex = 0; roomIndex < 9; roomIndex++) {
            respawnRoom(roomIndex);
        }
    }

    private void respawnRoom(int roomIndex) {
        if (roomIndex == 2 || roomIndex == 5 || roomIndex == 8) {
            respawnBoss(roomIndex);
            return;
        }

        List<Monster> monsterPool = getMonsterPoolForRoom(roomIndex);
        List<String[]> availableItems = getItemPoolForRoom(roomIndex);

        if (monsterPool.isEmpty() || availableItems.isEmpty()) {
            return;
        }

        int existingMonsters = 0;
        int existingItems = 0;
        for (int x = 1; x < GRID_SIZE - 1; x++) {
            for (int y = 1; y < GRID_SIZE - 1; y++) {
                if (currentMap[roomIndex][x][y] == TileConstants.TILE_MONSTER) existingMonsters++;
                if (currentMap[roomIndex][x][y] == TileConstants.TILE_ITEM) existingItems++;
            }
        }

        boolean isMonsterRoom = (roomIndex == 1 || roomIndex == 4 || roomIndex == 7);
        int targetMonsters = isMonsterRoom ? 6 : 2;
        int targetItems = isMonsterRoom ? 6 : 2;

        while (existingMonsters < targetMonsters) {
            int x, y;
            do {
                x = 2 + random.nextInt(GRID_SIZE - 4);
                y = 2 + random.nextInt(GRID_SIZE - 4);
            } while (currentMap[roomIndex][x][y] != TileConstants.TILE_GRASS);

            currentMap[roomIndex][x][y] = TileConstants.TILE_MONSTER;
            Monster baseMonster = monsterPool.get(random.nextInt(monsterPool.size()));
            monsters[roomIndex][x][y] = new Monster(baseMonster.getName(), baseMonster.getMaxHp(), baseMonster.getAttack(), baseMonster.getDefense(), baseMonster.getXpReward(), baseMonster.getPossibleDrops(), baseMonster.getWeakness(), false);
            monsterNames[roomIndex][x][y] = baseMonster.getName();
            existingMonsters++;
        }

        while (existingItems < targetItems) {
            int x, y;
            do {
                x = 2 + random.nextInt(GRID_SIZE - 4);
                y = 2 + random.nextInt(GRID_SIZE - 4);
            } while (currentMap[roomIndex][x][y] != TileConstants.TILE_GRASS);

            currentMap[roomIndex][x][y] = TileConstants.TILE_ITEM;
            String[] itemData = availableItems.get(random.nextInt(availableItems.size()));
            itemNames[roomIndex][x][y] = itemData[0];
            itemsOnMap[roomIndex][x][y] = new Ingredient(itemData[0], itemData[1], Integer.parseInt(itemData[2]), 1);
            existingItems++;
        }
    }

    private void respawnBoss(int bossRoomIndex) {
        int bossRoom = bossRoomIndex + 1;
        int bossX = 7;
        int bossY = 4;

        if (monsters[bossRoomIndex][bossX][bossY] != null && !monsters[bossRoomIndex][bossX][bossY].isDead()) {
            return;
        }

        Long defeatTime = bossDefeatedTimes.get(bossRoom);
        if (defeatTime != null) {
            long currentTime = System.currentTimeMillis();
            long respawnTimeMillis = getBossRespawnMinutes(bossRoom) * 60 * 1000;
            long timeSinceDefeat = currentTime - defeatTime;

            if (timeSinceDefeat < respawnTimeMillis) {
                return;
            }
        }

        Monster boss = createBossForRoom(bossRoomIndex);
        if (boss != null) {
            monsters[bossRoomIndex][bossX][bossY] = boss;
            monsterNames[bossRoomIndex][bossX][bossY] = boss.getName();
            currentMap[bossRoomIndex][bossX][bossY] = TileConstants.TILE_BOSS;
            bossDefeatedTimes.remove(bossRoom);
        }
    }

    private Monster createBossForRoom(int bossRoomIndex) {
        switch (bossRoomIndex) {
            case 2:
                return new Monster("Guardiano della Foresta", 150, 20, 10, 200, Arrays.asList(new Ingredient("Cuore della Foresta", "Pulsante di energia vitale", 53, 1)), "Fuoco", true);
            case 5:
                return new Monster("Golem di Pietra", 250, 30, 20, 400, Arrays.asList(new Ingredient("Nucleo di Pietra", "Cuore di roccia", 71, 1)), "Acqua", true);
            case 8:
                return new Monster("Basilisco della Palude", 350, 40, 25, 600, Arrays.asList(new Ingredient("Occhio di Basilisco", "Pietrifica", 89, 1)), "Luce", true);
            default:
                return null;
        }
    }

    public static int getBossRespawnMinutes(int bossRoom) {
        switch (bossRoom) {
            case 3: return 30;
            case 6: return 60;
            case 9: return 120;
            default: return 30;
        }
    }

    // ==========================================
    // SEZIONE: POOL DI DATI (MOSTRI E ITEM)
    // ==========================================

    private List<Monster> getMonsterPoolForRoom(int roomIndex) {
        if (roomIndex == 0 || roomIndex == 1) {
            return Arrays.asList(
                    new Monster("Ragno Velenoso", 40, 8, 3, 25, Arrays.asList(new Ingredient("Tela di Ragno", "Appiccicosa", 19, 1)), "Fuoco", false),
                    new Monster("Lupo Mannaro", 60, 12, 5, 35, Arrays.asList(new Ingredient("Zanna di Lupo", "Affilata", 23, 1)), "Luce", false),
                    new Monster("Folletto Dispettoso", 30, 6, 2, 20, Arrays.asList(new Ingredient("Polvere di Folletto", "Scintillante", 13, 1)), "Luce", false));
        }
        if (roomIndex == 3 || roomIndex == 4) {
            return Arrays.asList(
                    new Monster("Pipistrello Gigante", 50, 10, 4, 30, Arrays.asList(new Ingredient("Guano di Pipistrello", "Puzzolente", 41, 1)), "Luce", false),
                    new Monster("Ratto delle Fogne", 35, 8, 3, 20, Arrays.asList(new Ingredient("Dente di Ratto", "Affilato", 43, 1)), "Fuoco", false),
                    new Monster("Golem di Pietra Minore", 70, 14, 8, 40, Arrays.asList(new Ingredient("Scheggia di Pietra", "Dura", 47, 1)), "Acqua", false));
        }
        if (roomIndex == 6 || roomIndex == 7) {
            return Arrays.asList(
                    new Monster("Rana Velenosa", 45, 9, 4, 28, Arrays.asList(new Ingredient("Fungo Velenoso", "Tossico", 37, 1)), "Terra", false),
                    new Monster("Serpente Palustre", 55, 11, 5, 32, Arrays.asList(new Ingredient("Bile di Serpente", "Viscida", 39, 1)), "Fuoco", false),
                    new Monster("Non-morto Affogato", 65, 13, 6, 38, Arrays.asList(new Ingredient("Osso Corrotto", "Infetto", 45, 1)), "Luce", false));
        }
        return Arrays.asList();
    }

    private List<String[]> getItemPoolForRoom(int roomIndex) {
        if (roomIndex == 0 || roomIndex == 1) {
            return Arrays.asList(
                    new String[] { "Erba Lunare", "Erba che brilla.", "11" },
                    new String[] { "Lucciola Argentea", "Luce tenue.", "17" },
                    new String[] { "Radice Secca", "Materiale base.", "7" });
        }
        if (roomIndex == 3 || roomIndex == 4) {
            return Arrays.asList(
                    new String[] { "Fungo Velenoso", "Fungo tossico.", "37" },
                    new String[] { "Guano di Pipistrello", "Sostanza puzzolente.", "41" },
                    new String[] { "Dente di Ratto", "Dente affilato.", "43" },
                    new String[] { "Scheggia di Pietra", "Pietra dura.", "47" });
        }
        if (roomIndex == 6 || roomIndex == 7) {
            return Arrays.asList(
                    new String[] { "Fungo Velenoso", "Fungo tossico.", "37" },
                    new String[] { "Bile di Serpente", "Liquido viscido.", "39" },
                    new String[] { "Osso Corrotto", "Osso infetto.", "45" },
                    new String[] { "Liquido Instabile", "Sostanza volatile.", "49" });
        }
        return Arrays.asList();
    }
}