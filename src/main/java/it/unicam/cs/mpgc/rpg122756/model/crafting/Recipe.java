package it.unicam.cs.mpgc.rpg122756.model.crafting;

import it.unicam.cs.mpgc.rpg122756.model.items.Ingredient;
import it.unicam.cs.mpgc.rpg122756.model.items.Potion;
import java.util.List;

public class Recipe {
    private String name;
    private int targetValue;
    private Potion resultPotion;
    private List<Ingredient> suggestedIngredients;

    public Recipe(String name, int targetValue, Potion resultPotion, List<Ingredient> suggestedIngredients) {
        this.name = name;
        this.targetValue = targetValue;
        this.resultPotion = resultPotion;
        this.suggestedIngredients = suggestedIngredients;
    }

    public String getName() {
        return name;
    }

    public int getTargetValue() {
        return targetValue;
    }

    // Questo è il metodo chiamato dal MainFX.java
    public Potion getResultPotion() {
        return resultPotion;
    }

    public List<Ingredient> getSuggestedIngredients() {
        return suggestedIngredients;
    }
}