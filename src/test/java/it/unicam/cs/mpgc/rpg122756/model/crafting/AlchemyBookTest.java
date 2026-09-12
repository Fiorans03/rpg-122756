package it.unicam.cs.mpgc.rpg122756.model.crafting;

import it.unicam.cs.mpgc.rpg122756.model.items.Ingredient;
import it.unicam.cs.mpgc.rpg122756.model.items.Potion;
import it.unicam.cs.mpgc.rpg122756.model.items.PotionEffect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

class AlchemyBookTest {

    private AlchemyBook alchemyBook;

    @BeforeEach
    void setUp() {
        alchemyBook = new AlchemyBook();
    }

    @Test
    void testInitialState() {
        assertTrue(alchemyBook.getDiscoveredRecipes().isEmpty());
        assertTrue(alchemyBook.getDiscoveredIngredients().isEmpty());
    }

    @Test
    void testDiscoverIngredient() {
        alchemyBook.discoverIngredient("Erba Lunare");
        assertTrue(alchemyBook.getDiscoveredIngredients().contains("Erba Lunare"));
    }

    @Test
    void testDiscoverRecipe() {
        Potion potion = new Potion("Test Potion", "Desc", 20, 5,
                new PotionEffect(PotionEffect.EffectType.HEAL, 50, 0, "Neutro"));
        Recipe recipe = new Recipe("Test Recipe", 20, potion,
                Arrays.asList(new Ingredient("Ing", "Desc", 10, 1)));

        alchemyBook.discoverRecipe(recipe);

        assertEquals(1, alchemyBook.getDiscoveredRecipes().size());
        assertTrue(alchemyBook.isRecipeDiscovered("Test Recipe"));
    }

    @Test
    void testDiscoverRecipe_NoDuplicates() {
        Potion potion = new Potion("Test Potion", "Desc", 20, 5,
                new PotionEffect(PotionEffect.EffectType.HEAL, 50, 0, "Neutro"));
        Recipe recipe = new Recipe("Test Recipe", 20, potion,
                Arrays.asList(new Ingredient("Ing", "Desc", 10, 1)));

        alchemyBook.discoverRecipe(recipe);
        alchemyBook.discoverRecipe(recipe);

        assertEquals(1, alchemyBook.getDiscoveredRecipes().size());
    }

    @Test
    void testIsRecipeDiscovered_NotDiscovered() {
        assertFalse(alchemyBook.isRecipeDiscovered("Non Existent"));
    }
}