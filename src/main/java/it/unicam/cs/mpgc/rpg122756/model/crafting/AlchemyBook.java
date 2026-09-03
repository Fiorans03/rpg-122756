package it.unicam.cs.mpgc.rpg122756.model.crafting;

import java.util.*;

public class AlchemyBook {
    private List<Recipe> discoveredRecipes;
    private Set<String> discoveredIngredients;

    public AlchemyBook() {
        this.discoveredRecipes = new ArrayList<>();
        this.discoveredIngredients = new HashSet<>();
    }

    public void discoverIngredient(String name) {
        discoveredIngredients.add(name);
    }

    public void discoverRecipe(Recipe recipe) {
        // Evita duplicati
        for (Recipe r : discoveredRecipes) {
            if (r.getName().equals(recipe.getName()))
                return;
        }
        discoveredRecipes.add(recipe);
    }

    public List<Recipe> getDiscoveredRecipes() {
        return discoveredRecipes;
    }

    public Set<String> getDiscoveredIngredients() {
        return discoveredIngredients;
    }

    public boolean isRecipeDiscovered(String name) {
        for (Recipe r : discoveredRecipes) {
            if (r.getName().equals(name))
                return true;
        }
        return false;
    }
}