package it.unicam.cs.mpgc.rpg122756.model.crafting;

import it.unicam.cs.mpgc.rpg122756.model.items.Ingredient;
import it.unicam.cs.mpgc.rpg122756.model.items.Potion;
import it.unicam.cs.mpgc.rpg122756.model.items.PotionEffect;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RecipeDatabase {
    
    private final Map<Integer, Recipe> possibleRecipes;

    public RecipeDatabase() {
        this.possibleRecipes = new HashMap<>();
        initializeRecipes();
    }

    private void initializeRecipes() {
        // ==========================================
        // LIVELLO 1: FORESTA
        // ==========================================
        Potion curaMinore = new Potion("Pozione di Cura Minore", "Cura moderata.", 20, 5,
                new PotionEffect(PotionEffect.EffectType.HEAL, 50, 0, "Neutro"));
        possibleRecipes.put(22, new Recipe("Pozione di Cura Minore", 22, curaMinore, Arrays.asList(
                new Ingredient("Erba Lunare", "", 11, 1), new Ingredient("Erba Lunare", "", 11, 1))));

        Potion tonicoBosco = new Potion("Tonico del Bosco", "Recupera energia.", 30, 0,
                new PotionEffect(PotionEffect.EffectType.AP_RECOVER, 30, 0, "Neutro"));
        possibleRecipes.put(24, new Recipe("Tonico del Bosco", 24, tonicoBosco, Arrays.asList(
                new Ingredient("Erba Lunare", "", 11, 1), new Ingredient("Polvere di Folletto", "", 13, 1))));

        Potion tisana = new Potion("Tisana di Radici", "Cura leggera.", 15, 3,
                new PotionEffect(PotionEffect.EffectType.HEAL, 25, 0, "Neutro"));
        possibleRecipes.put(32, new Recipe("Tisana di Radici", 32, tisana, Arrays.asList(
                new Ingredient("Radice Secca", "", 7, 1), new Ingredient("Radice Secca", "", 7, 1),
                new Ingredient("Radice Secca", "", 7, 1), new Ingredient("Erba Lunare", "", 11, 1))));

        Potion estrattoLuce = new Potion("Estratto di Luce", "Danno luminoso.", 25, 8,
                new PotionEffect(PotionEffect.EffectType.DAMAGE, 60, 0, "Luce"));
        possibleRecipes.put(34, new Recipe("Estratto di Luce", 34, estrattoLuce, Arrays.asList(
                new Ingredient("Lucciola Argentea", "", 17, 1), new Ingredient("Lucciola Argentea", "", 17, 1))));

        Potion analisi = new Potion("Occulus Veritatis", "Rivela debolezze.", 40, 5,
                new PotionEffect(PotionEffect.EffectType.ANALYZE, 0, 0, "Luce"));
        possibleRecipes.put(40, new Recipe("Occulus Veritatis", 40, analisi, Arrays.asList(
                new Ingredient("Zanna di Lupo", "", 23, 1), new Ingredient("Lucciola Argentea", "", 17, 1))));

        Potion sieroRagno = new Potion("Siero del Ragno", "Danno tossico nel tempo.", 30, 10,
                new PotionEffect(PotionEffect.EffectType.POISON, 20, 3, "Veleno"));
        possibleRecipes.put(42, new Recipe("Siero del Ragno", 42, sieroRagno, Arrays.asList(
                new Ingredient("Tela di Ragno", "", 19, 1), new Ingredient("Zanna di Lupo", "", 23, 1))));

        Potion artiglioLupo = new Potion("Artiglio del Lupo", "Aumenta Attacco.", 40, 10,
                new PotionEffect(PotionEffect.EffectType.BUFF_ATTACK, 5, 3, "Neutro"));
        possibleRecipes.put(46, new Recipe("Artiglio del Lupo", 46, artiglioLupo, Arrays.asList(
                new Ingredient("Zanna di Lupo", "", 23, 1), new Ingredient("Zanna di Lupo", "", 23, 1))));

        Potion velenoBase = new Potion("Veleno Base", "Danno tossico.", 30, 8,
                new PotionEffect(PotionEffect.EffectType.POISON, 40, 0, "Veleno"));
        possibleRecipes.put(50, new Recipe("Veleno Base", 50, velenoBase, Arrays.asList(
                new Ingredient("Tela di Ragno", "", 19, 1), new Ingredient("Polvere di Folletto", "", 13, 1),
                new Ingredient("Erba Lunare", "", 11, 1), new Ingredient("Radice Secca", "", 7, 1))));

        Potion elisirGuardiano = new Potion("Elisir del Guardiano", "Cura massiccia.", 80, 15,
                new PotionEffect(PotionEffect.EffectType.HEAL, 150, 0, "Neutro"));
        possibleRecipes.put(60, new Recipe("Elisir del Guardiano", 60, elisirGuardiano, Arrays.asList(
                new Ingredient("Cuore della Foresta", "", 53, 1), new Ingredient("Radice Secca", "", 7, 1))));

        Potion scintilla = new Potion("Scintilla Esplosiva", "Danno da fuoco.", 50, 12,
                new PotionEffect(PotionEffect.EffectType.DAMAGE, 80, 0, "Fuoco"));
        possibleRecipes.put(68, new Recipe("Scintilla Esplosiva", 68, scintilla, Arrays.asList(
                new Ingredient("Lucciola Argentea", "", 17, 1), new Ingredient("Lucciola Argentea", "", 17, 1),
                new Ingredient("Lucciola Argentea", "", 17, 1), new Ingredient("Lucciola Argentea", "", 17, 1))));

        // ==========================================
        // LIVELLO 2: CAVERNE
        // ==========================================
        Potion tossina = new Potion("Tossina della Caverna", "Danno tossico forte.", 60, 15,
                new PotionEffect(PotionEffect.EffectType.POISON, 80, 0, "Veleno"));
        possibleRecipes.put(78, new Recipe("Tossina della Caverna", 78, tossina, Arrays.asList(
                new Ingredient("Fungo Velenoso", "", 37, 1), new Ingredient("Guano di Pipistrello", "", 41, 1))));

        Potion corazza = new Potion("Corazza di Muschio", "Aumenta Difesa.", 50, 10,
                new PotionEffect(PotionEffect.EffectType.BUFF_DEFENSE, 5, 3, "Terra"));
        possibleRecipes.put(80, new Recipe("Corazza di Muschio", 80, corazza, Arrays.asList(
                new Ingredient("Fungo Velenoso", "", 37, 1), new Ingredient("Dente di Ratto", "", 43, 1))));

        Potion forzaRatto = new Potion("Forza del Ratto", "Aumenta Attacco.", 60, 12,
                new PotionEffect(PotionEffect.EffectType.BUFF_ATTACK, 8, 3, "Neutro"));
        possibleRecipes.put(90, new Recipe("Forza del Ratto", 90, forzaRatto, Arrays.asList(
                new Ingredient("Dente di Ratto", "", 43, 1), new Ingredient("Scheggia di Pietra", "", 47, 1))));

        Potion pellePietra = new Potion("Pelle di Pietra", "Aumenta Difesa.", 80, 15,
                new PotionEffect(PotionEffect.EffectType.BUFF_DEFENSE, 10, 4, "Terra"));
        possibleRecipes.put(100, new Recipe("Pelle di Pietra", 100, pellePietra, Arrays.asList(
                new Ingredient("Nucleo di Pietra", "", 71, 1), new Ingredient("Muschio Luminescente", "", 29, 1))));

        Potion furiaGolem = new Potion("Furia del Golem", "Danno devastante.", 100, 20,
                new PotionEffect(PotionEffect.EffectType.DAMAGE, 120, 0, "Terra"));
        possibleRecipes.put(118, new Recipe("Furia del Golem", 118, furiaGolem, Arrays.asList(
                new Ingredient("Nucleo di Pietra", "", 71, 1), new Ingredient("Scheggia di Pietra", "", 47, 1))));

        Potion cuoreTerra = new Potion("Cuore della Terra", "Cura Totale.", 200, 25,
                new PotionEffect(PotionEffect.EffectType.HEAL, 9999, 0, "Neutro"));
        possibleRecipes.put(124, new Recipe("Cuore della Terra", 124, cuoreTerra, Arrays.asList(
                new Ingredient("Cuore della Foresta", "", 53, 1), new Ingredient("Nucleo di Pietra", "", 71, 1))));
    }

    public Recipe getRecipeByValue(int value) {
        return possibleRecipes.get(value);
    }

    public Map<Integer, Recipe> getAllRecipes() {
        return possibleRecipes;
    }
}