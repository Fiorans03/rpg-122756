package it.unicam.cs.mpgc.rpg122756.game.world;

import it.unicam.cs.mpgc.rpg122756.model.entities.Monster;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

class NavigationManagerTest {

    private NavigationManager navigationManager;
    private Monster[][][] monsters;
    private Map<Integer, Long> bossDefeatedTimes;

    @BeforeEach
    void setUp() {
        navigationManager = new NavigationManager();
        monsters = new Monster[30][15][15];
        bossDefeatedTimes = new HashMap<>();
    }

    @Test
    void testHandleDoor_Room1ToRoom2_Nord() {
        NavigationManager.MoveResult result = navigationManager.handleDoor(1, "nord", monsters, bossDefeatedTimes);
        assertEquals(NavigationManager.MoveResult.ROOM_CHANGE, result);
        assertEquals(2, navigationManager.getTargetRoom());
        assertEquals(7, navigationManager.getTargetX());
        assertEquals(12, navigationManager.getTargetY());
    }

    @Test
    void testHandleDoor_Room2ToRoom1_Sud() {
        NavigationManager.MoveResult result = navigationManager.handleDoor(2, "sud", monsters, bossDefeatedTimes);
        assertEquals(NavigationManager.MoveResult.ROOM_CHANGE, result);
        assertEquals(1, navigationManager.getTargetRoom());
    }

    @Test
    void testHandleDoor_Room2ToRoom3_Nord_BossConfirmation() {
        NavigationManager.MoveResult result = navigationManager.handleDoor(2, "nord", monsters, bossDefeatedTimes);
        assertEquals(NavigationManager.MoveResult.BOSS_CONFIRMATION, result);
        assertEquals(3, navigationManager.getTargetRoom());
    }

    @Test
    void testGetBossRespawnMinutes_Room3() {
        assertEquals(30, NavigationManager.getBossRespawnMinutes(3));
    }

    @Test
    void testGetBossRespawnMinutes_Room6() {
        assertEquals(60, NavigationManager.getBossRespawnMinutes(6));
    }

    @Test
    void testGetBossRespawnMinutes_Room9() {
        assertEquals(120, NavigationManager.getBossRespawnMinutes(9));
    }

    @Test
    void testGetBossRespawnMinutes_Default() {
        assertEquals(30, NavigationManager.getBossRespawnMinutes(99));
    }
}