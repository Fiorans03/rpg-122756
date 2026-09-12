package it.unicam.cs.mpgc.rpg122756.model.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AlchemistTest {

    private Alchemist alchemist;

    @BeforeEach
    void setUp() {
        alchemist = new Alchemist("Test");
    }

    @Test
    void testInitialStats() {
        assertEquals("Test", alchemist.getName());
        assertEquals(1, alchemist.getLevel());
        assertEquals(100, alchemist.getHp());
        assertEquals(100, alchemist.getMaxHp());
        assertEquals(50, alchemist.getAp());
        assertEquals(50, alchemist.getMaxAp());
        assertEquals(10, alchemist.getAttack());
        assertEquals(5, alchemist.getDefense());
    }

    @Test
    void testTakeDamage() {
        alchemist.takeDamage(30);
        assertEquals(70, alchemist.getHp());
    }

    @Test
    void testTakeDamage_CannotGoBelowZero() {
        alchemist.takeDamage(200);
        assertEquals(0, alchemist.getHp());
    }

    @Test
    void testHeal() {
        alchemist.takeDamage(50);
        alchemist.heal(30);
        assertEquals(80, alchemist.getHp());
    }

    @Test
    void testHeal_CannotExceedMaxHp() {
        alchemist.takeDamage(20);
        alchemist.heal(50);
        assertEquals(100, alchemist.getHp());
    }

    @Test
    void testIsDead() {
        assertFalse(alchemist.isDead());
        alchemist.takeDamage(100);
        assertTrue(alchemist.isDead());
    }

    @Test
    void testAddExperience_LevelUp() {
        alchemist.addExperience(50);
        assertEquals(2, alchemist.getLevel());
        assertTrue(alchemist.isPendingLevelUp());
    }

    @Test
    void testGetXpForNextLevel() {
        assertEquals(50, alchemist.getXpForNextLevel());
        alchemist.setLevel(2);
        assertEquals(200, alchemist.getXpForNextLevel());
    }

    @Test
    void testInfiniteHP() {
        alchemist.setInfiniteHP(true);
        alchemist.takeDamage(1000);
        assertEquals(Integer.MAX_VALUE, alchemist.getHp());
        assertFalse(alchemist.isDead());
    }

    @Test
    void testDiscoveredWeakness() {
        assertFalse(alchemist.hasDiscoveredWeakness("Monster"));
        alchemist.addDiscoveredWeakness("Monster");
        assertTrue(alchemist.hasDiscoveredWeakness("Monster"));
    }
}