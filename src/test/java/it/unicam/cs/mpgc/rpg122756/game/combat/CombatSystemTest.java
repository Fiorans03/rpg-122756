package it.unicam.cs.mpgc.rpg122756.game.combat;

import it.unicam.cs.mpgc.rpg122756.model.entities.Alchemist;
import it.unicam.cs.mpgc.rpg122756.model.entities.Monster;
import it.unicam.cs.mpgc.rpg122756.model.items.Ingredient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

class CombatSystemTest {

    private CombatSystem combatSystem;
    private Alchemist alchemist;
    private Monster monster;

    @BeforeEach
    void setUp() {
        combatSystem = new CombatSystem();
        alchemist = new Alchemist("Test");
        monster = new Monster("Test Monster", 100, 10, 5, 50,
                Arrays.asList(new Ingredient("Drop", "Test", 10, 1)), "Fuoco", false);
    }

    @Test
    void testAlchemistAttack_DamageDealt() {
        int initialHp = monster.getHp();
        int damage = combatSystem.alchemistAttack(alchemist, monster);

        assertTrue(damage > 0);
        assertTrue(monster.getHp() < initialHp);
    }

    @Test
    void testMonsterAttack_DamageDealt() {
        int initialHp = alchemist.getHp();
        int damage = combatSystem.monsterAttack(monster, alchemist);

        assertTrue(damage > 0);
        assertTrue(alchemist.getHp() < initialHp);
    }

    @Test
    void testResolveVictory_XpGained() {
        int initialXp = alchemist.getExperience();
        CombatSystem.CombatResult result = combatSystem.resolveVictory(monster, alchemist);

        assertEquals(50, result.getXpGained());
        assertEquals(initialXp + 50, alchemist.getExperience());
    }

    @Test
    void testResolveVictory_DropsReturned() {
        CombatSystem.CombatResult result = combatSystem.resolveVictory(monster, alchemist);

        assertNotNull(result.getDroppedItems());
        assertFalse(result.getDroppedItems().isEmpty());
    }
}