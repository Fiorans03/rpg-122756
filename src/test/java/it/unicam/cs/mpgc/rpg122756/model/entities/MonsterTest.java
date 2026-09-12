package it.unicam.cs.mpgc.rpg122756.model.entities;

import it.unicam.cs.mpgc.rpg122756.model.items.Ingredient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

class MonsterTest {

    private Monster monster;

    @BeforeEach
    void setUp() {
        monster = new Monster("Test Monster", 100, 15, 8, 50,
                Arrays.asList(new Ingredient("Drop", "Test", 10, 1)), "Fuoco", false);
    }

    @Test
    void testInitialStats() {
        assertEquals("Test Monster", monster.getName());
        assertEquals(100, monster.getHp());
        assertEquals(100, monster.getMaxHp());
        assertEquals(15, monster.getAttack());
        assertEquals(8, monster.getDefense());
        assertEquals(50, monster.getXpReward());
        assertEquals("Fuoco", monster.getWeakness());
        assertFalse(monster.isBoss());
    }

    @Test
    void testTakeDamage() {
        monster.takeDamage(30);
        assertEquals(70, monster.getHp());
    }

    @Test
    void testTakeDamage_CannotGoBelowZero() {
        monster.takeDamage(200);
        assertEquals(0, monster.getHp());
    }

    @Test
    void testIsDead() {
        assertFalse(monster.isDead());
        monster.takeDamage(100);
        assertTrue(monster.isDead());
    }

    @Test
    void testDropItems() {
        var drops = monster.dropItems();
        assertNotNull(drops);
    }

    @Test
    void testBossMonster() {
        Monster boss = new Monster("Boss", 500, 30, 20, 500,
                Arrays.asList(new Ingredient("Boss Drop", "Rare", 100, 1)), "Acqua", true);
        assertTrue(boss.isBoss());
    }

    @Test
    void testResistance() {
        Monster monsterWithResistance = new Monster("Test", 100, 10, 5, 50,
                Arrays.asList(new Ingredient("Drop", "Test", 10, 1)), "Fuoco", "Acqua", false);
        assertEquals("Acqua", monsterWithResistance.getResistance());
    }
}