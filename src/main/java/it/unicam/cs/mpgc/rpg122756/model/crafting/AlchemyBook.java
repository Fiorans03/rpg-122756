package it.unicam.cs.mpgc.rpg122756.model.crafting;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AlchemyBook {

    // ==========================================
    // SEZIONE: VARIABILI DI ISTANZA
    // ==========================================

    private List<Recipe> discoveredRecipes;
    private Set<String> discoveredIngredients;

    // ==========================================
    // SEZIONE: COSTRUTTORE
    // ==========================================

    public AlchemyBook() {
        this.discoveredRecipes = new ArrayList<>();
        this.discoveredIngredients = new HashSet<>();
    }

    // ==========================================
    // SEZIONE: METODI DI MODIFICA
    // ==========================================

    public void discoverIngredient(String name) {
        discoveredIngredients.add(name);
    }

    public void discoverRecipe(Recipe recipe) {
        for (Recipe r : discoveredRecipes) {
            if (r.getName().equals(recipe.getName())) {
                return;
            }
        }
        discoveredRecipes.add(recipe);
    }

    // ==========================================
    // SEZIONE: METODI GETTER E CONSULTAZIONE
    // ==========================================

    public List<Recipe> getDiscoveredRecipes() {
        return discoveredRecipes;
    }

    public Set<String> getDiscoveredIngredients() {
        return discoveredIngredients;
    }

    public boolean isRecipeDiscovered(String name) {
        for (Recipe r : discoveredRecipes) {
            if (r.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
}