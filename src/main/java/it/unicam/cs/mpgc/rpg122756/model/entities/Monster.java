package it.unicam.cs.mpgc.rpg122756.model.entities;

import it.unicam.cs.mpgc.rpg122756.model.items.Item;
import it.unicam.cs.mpgc.rpg122756.model.items.PotionEffect;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Monster implements Combatant {

    // ==========================================
    // SEZIONE: VARIABILI DI ISTANZA
    // ==========================================

    private final String name;
    private int hp;
    private final int maxHp;
    private final int attack;
    private final int defense;
    private final int xpReward;
    private final List<Item> possibleDrops;
    private final String weakness;
    private final String resistance;
    private final boolean isBoss;

    private List<ActiveEffect> activeEffects = new ArrayList<>();
    private final Random random;

    // ==========================================
    // SEZIONE: COSTRUTTORI
    // ==========================================

    public Monster(String name, int maxHp, int attack, int defense, int xpReward,
            List<Item> possibleDrops, String weakness, boolean isBoss) {
        this(name, maxHp, attack, defense, xpReward, possibleDrops, weakness, null, isBoss);
    }

    public Monster(String name, int maxHp, int attack, int defense, int xpReward,
            List<Item> possibleDrops, String weakness, String resistance, boolean isBoss) {
        this.name = name;
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.attack = attack;
        this.defense = defense;
        this.xpReward = xpReward;
        this.possibleDrops = possibleDrops;
        this.weakness = weakness;
        this.resistance = resistance;
        this.isBoss = isBoss;
        this.random = new Random();
    }

    // ==========================================
    // SEZIONE: METODI GETTER E CALCOLO STATISTICHE
    // ==========================================

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getHp() {
        return hp;
    }

    @Override
    public int getMaxHp() {
        return maxHp;
    }

    @Override
    public int getAttack() {
        int base = attack;
        for (ActiveEffect e : activeEffects) {
            if (e.getType() == PotionEffect.EffectType.BUFF_ATTACK) {
                base += e.getMagnitude();
            }
            if (e.getType() == PotionEffect.EffectType.DEBUFF_ATTACK) {
                base -= e.getMagnitude();
            }
        }
        return Math.max(1, base);
    }

    @Override
    public int getDefense() {
        int base = defense;
        for (ActiveEffect e : activeEffects) {
            if (e.getType() == PotionEffect.EffectType.BUFF_DEFENSE) {
                base += e.getMagnitude();
            }
        }
        return Math.max(0, base);
    }

    public int getXpReward() {
        return xpReward;
    }

    public List<Item> getPossibleDrops() {
        return possibleDrops;
    }

    public String getWeakness() {
        return weakness;
    }

    public String getResistance() {
        return resistance;
    }

    public boolean isBoss() {
        return isBoss;
    }

    public List<ActiveEffect> getActiveEffects() {
        return activeEffects;
    }

    // ==========================================
    // SEZIONE: GESTIONE EFFETTI E COMBATTIMENTO
    // ==========================================

    public void addEffect(ActiveEffect effect) {
        this.activeEffects.add(effect);
    }

    @Override
    public void takeDamage(int damage) {
        this.hp = Math.max(0, this.hp - damage);
    }

    @Override
    public boolean isDead() {
        return hp <= 0;
    }

    public int tickEffects() {
        int totalDamage = 0;
        Iterator<ActiveEffect> it = activeEffects.iterator();

        while (it.hasNext()) {
            ActiveEffect e = it.next();
            if (e.getType() == PotionEffect.EffectType.POISON) {
                totalDamage += e.getMagnitude();
            }
            e.decrementDuration();
            if (e.isExpired()) {
                it.remove();
            }
        }

        if (totalDamage > 0) {
            takeDamage(totalDamage);
        }
        return totalDamage;
    }

    public List<Item> dropItems() {
        List<Item> drops = new ArrayList<>();
        if (possibleDrops == null || possibleDrops.isEmpty()) {
            return drops;
        }

        int numDrops = random.nextInt(4);
        for (int i = 0; i < numDrops; i++) {
            drops.add(possibleDrops.get(random.nextInt(possibleDrops.size())));
        }
        return drops;
    }

    // ==========================================
    // SEZIONE: METODI DI UTILITÀ
    // ==========================================

    @Override
    public String toString() {
        return String.format("%s [%s] HP: %d/%d | ATT: %d | DEF: %d",
                name, isBoss ? "BOSS" : "Minion", hp, maxHp, attack, defense);
    }
}