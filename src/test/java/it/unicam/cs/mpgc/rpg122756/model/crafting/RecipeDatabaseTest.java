package it.unicam.cs.mpgc.rpg122756.model.crafting;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RecipeDatabaseTest {

    private RecipeDatabase recipeDatabase;

    @BeforeEach
    void setUp() {
        recipeDatabase = new RecipeDatabase();
    }

    @Test
    void testGetRecipeByValue_ExistingRecipe() {
        Recipe recipe = recipeDatabase.getRecipeByValue(22);
        assertNotNull(recipe);
        assertEquals("Pozione di Cura Minore", recipe.getName());
        assertEquals(22, recipe.getTargetValue());
    }

    @Test
    void testGetRecipeByValue_NonExistingRecipe() {
        Recipe recipe = recipeDatabase.getRecipeByValue(999);
        assertNull(recipe);
    }

    @Test
    void testGetAllRecipes_NotEmpty() {
        assertFalse(recipeDatabase.getAllRecipes().isEmpty());
        assertEquals(16, recipeDatabase.getAllRecipes().size());
    }

    @Test
    void testRecipeHasCorrectPotion() {
        Recipe recipe = recipeDatabase.getRecipeByValue(22);
        assertNotNull(recipe.getResultPotion());
        assertEquals("Pozione di Cura Minore", recipe.getResultPotion().getName());
    }

    @Test
    void testRecipeHasIngredients() {
        Recipe recipe = recipeDatabase.getRecipeByValue(22);
        assertNotNull(recipe.getSuggestedIngredients());
        assertFalse(recipe.getSuggestedIngredients().isEmpty());
    }
}