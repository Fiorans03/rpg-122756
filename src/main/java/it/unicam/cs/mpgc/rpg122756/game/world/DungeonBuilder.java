package it.unicam.cs.mpgc.rpg122756.game.world;

import it.unicam.cs.mpgc.rpg122756.model.entities.Monster;
import it.unicam.cs.mpgc.rpg122756.model.items.Ingredient;
import it.unicam.cs.mpgc.rpg122756.model.world.Direction;
import it.unicam.cs.mpgc.rpg122756.model.world.Dungeon;
import it.unicam.cs.mpgc.rpg122756.model.world.DungeonLevel;
import it.unicam.cs.mpgc.rpg122756.model.world.Room;
import it.unicam.cs.mpgc.rpg122756.model.entities.Alchemist;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class DungeonBuilder {

    // ==========================================
    // SEZIONE: VARIABILI STATICHE E COSTANTI
    // ==========================================

    private static final Random random = new Random();
    private static final int GRID_SIZE = 15;

    // ==========================================
    // SEZIONE: METODO DI COSTRUZIONE PRINCIPALE
    // ==========================================

    public static Dungeon buildFullDungeon(Alchemist alchemist) {
        Dungeon dungeon = new Dungeon(alchemist);
        DungeonLevel level1 = new DungeonLevel(1, "La Foresta Alchemica");

        Room[][] grid = new Room[GRID_SIZE][GRID_SIZE];

        for (int x = 0; x < GRID_SIZE; x++) {
            for (int y = 0; y < GRID_SIZE; y++) {
                grid[x][y] = new Room("R_" + x + "_" + y, "Corridoio del labirinto [" + x + "," + y + "].");
            }
        }

        for (int x = 0; x < GRID_SIZE; x++) {
            for (int y = 0; y < GRID_SIZE; y++) {
                if (x + 1 < GRID_SIZE)
                    connectRooms(grid[x][y], grid[x + 1][y], Direction.EAST, Direction.WEST);
                if (y + 1 < GRID_SIZE)
                    connectRooms(grid[x][y], grid[x][y + 1], Direction.NORTH, Direction.SOUTH);
            }
        }

        for (int x = 0; x < GRID_SIZE; x++) {
            for (int y = 0; y < GRID_SIZE; y++) {
                if ((x == 0 && y == 0) || (x == GRID_SIZE - 1 && y == GRID_SIZE - 1))
                    continue;

                double chance = random.nextDouble();
                if (chance < 0.4) {
                    int numItems = random.nextInt(2) + 1;
                    for (int i = 0; i < numItems; i++) {
                        grid[x][y].addItem(getRandomLevel1Item());
                    }
                } else if (chance < 0.7) {
                    grid[x][y].setMonster(getRandomLevel1Monster());
                }
            }
        }

        Room startRoom = grid[0][0];
        startRoom.setDescription("L'ingresso della foresta. L'aria è fresca e misteriosa.");

        Room bossRoom = grid[GRID_SIZE - 1][GRID_SIZE - 1];
        bossRoom.setDescription("Il cuore della foresta. Un'aura antica e potente ti avvolge.");

        Monster boss = new Monster("Guardiano della Foresta", 150, 20, 10, 200,
                Arrays.asList(new Ingredient("Cuore della Foresta", "Pulsante di energia vitale", 53, 1)),
                "Fuoco", true);
        bossRoom.setMonster(boss);

        level1.addRoom(startRoom);
        level1.addRoom(bossRoom);

        level1.setStartRoom(startRoom);
        level1.setBossRoom(bossRoom);
        dungeon.addLevel(level1);

        return dungeon;
    }

    // ==========================================
    // SEZIONE: METODI DI UTILITÀ E HELPER
    // ==========================================

    private static void connectRooms(Room r1, Room r2, Direction d1, Direction d2) {
        r1.setExit(d1, r2);
        r2.setExit(d2, r1);
    }

    private static Monster getRandomLevel1Monster() {
        List<Monster> pool = Arrays.asList(
                new Monster("Lupo Mannaro", 60, 12, 5, 35,
                        Arrays.asList(new Ingredient("Zanna di Lupo", "Zanna affilata", 23, 1)), "Luce", false),
                new Monster("Ragno Velenoso", 40, 8, 3, 25,
                        Arrays.asList(new Ingredient("Tela di Ragno", "Appiccicosa", 19, 1)), "Fuoco", false),
                new Monster("Folletto Dispettoso", 30, 6, 2, 20,
                        Arrays.asList(new Ingredient("Polvere di Folletto", "Scintillante", 13, 1)), "Luce", false));
        Monster m = pool.get(random.nextInt(pool.size()));
        return new Monster(m.getName(), m.getHp(), m.getAttack(), m.getDefense(), m.getXpReward(), m.getPossibleDrops(),
                m.getWeakness(), false);
    }

    private static Ingredient getRandomLevel1Item() {
        List<Ingredient> pool = Arrays.asList(
                new Ingredient("Erba Lunare", "Erba che brilla.", 11, 1),
                new Ingredient("Lucciola Argentea", "Luce tenue.", 17, 1),
                new Ingredient("Radice Secca", "Materiale base.", 7, 1));
        return pool.get(random.nextInt(pool.size()));
    }
}