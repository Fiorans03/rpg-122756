package it.unicam.cs.mpgc.rpg122756.model.entities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Iterator;

import it.unicam.cs.mpgc.rpg122756.model.Inventory;
import it.unicam.cs.mpgc.rpg122756.model.items.PotionEffect;

public class Alchemist {

    private String name;
    private int hp;
    private int maxHp;
    private int ap;
    private int maxAp;
    private int attack;
    private int defense;
    private int level;
    private int experience;
    private Inventory inventory;
    private boolean hasInfiniteHP;
    private boolean hasInfiniteAP;
    private int pendingLevelUpCount = 0;
    private Set<String> discoveredWeaknesses = new HashSet<>();
    private List<ActiveEffect> activeEffects = new ArrayList<>();

    public Alchemist(String name) {
        this.name = name;
        this.level = 1;
        this.experience = 0;
        this.maxHp = 9999;
        this.hp = maxHp;
        this.maxAp = 50;
        this.ap = maxAp;
        this.attack = 10;
        this.defense = 5;
        this.inventory = new Inventory(20);
        this.hasInfiniteHP = false;
        this.hasInfiniteAP = false;
    }

    // --- Getter ---
    public String getName() {
        return name;
    }

    public int getHp() {
        return hasInfiniteHP ? Integer.MAX_VALUE : hp;
    }

    public int getMaxHp() {
        return hasInfiniteHP ? Integer.MAX_VALUE : maxHp;
    }

    public int getAp() {
        return hasInfiniteAP ? Integer.MAX_VALUE : ap;
    }

    public int getMaxAp() {
        return hasInfiniteAP ? Integer.MAX_VALUE : maxAp;
    }

    public int getAttack() {
        int base = attack;
        for (ActiveEffect e : activeEffects) {
            if (e.getType() == PotionEffect.EffectType.BUFF_ATTACK)
                base += e.getMagnitude();
            if (e.getType() == PotionEffect.EffectType.DEBUFF_ATTACK)
                base -= e.getMagnitude();
        }
        return Math.max(1, base); // Minimo 1 di attacco
    }

    public int getDefense() {
        int base = defense;
        for (ActiveEffect e : activeEffects) {
            if (e.getType() == PotionEffect.EffectType.BUFF_DEFENSE)
                base += e.getMagnitude();
        }
        return Math.max(0, base);
    }

    public int getLevel() {
        return level;
    }

    public int getExperience() {
        return experience;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public List<ActiveEffect> getActiveEffects() {
        return activeEffects;
    }

    public void addEffect(ActiveEffect effect) {
        // Se l'effetto esiste già, aggiorna la durata (opzionale, qui lo aggiungiamo e
        // basta)
        this.activeEffects.add(effect);
    }

    public boolean hasInfiniteHP() {
        return hasInfiniteHP;
    }

    public boolean hasInfiniteAP() {
        return hasInfiniteAP;
    }

    public boolean isPendingLevelUp() {
        return pendingLevelUpCount > 0;
    }

    public int getPendingLevelUpCount() {
        return pendingLevelUpCount;
    }

    public Set<String> getDiscoveredWeaknesses() {
        return discoveredWeaknesses;
    }

    public void addDiscoveredWeakness(String monsterName) {
        discoveredWeaknesses.add(monsterName);
    }

    public boolean hasDiscoveredWeakness(String monsterName) {
        return discoveredWeaknesses.contains(monsterName);
    }

    // --- Setter ---
    public void setName(String name) {
        this.name = name;
    }

    public void setHp(int hp) {
        if (!hasInfiniteHP) {
            this.hp = Math.max(0, Math.min(hp, maxHp));
        }
    }

    public void setAp(int ap) {
        if (!hasInfiniteAP) {
            this.ap = Math.max(0, Math.min(ap, maxAp));
        }
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    public void setLevel(int level) {
        this.level = level;
        if (level % 5 == 0) {
            inventory.increaseCapacity(10);
        }
    }

    public void setExperience(int experience) {
        this.experience = experience;
        checkLevelUp();
    }

    public void addExperience(int xp) {
        this.experience += xp;
        System.out.println("XP guadagnati: " + xp + " | XP totali: " + this.experience + " | XP necessari per livello "
                + (level + 1) + ": " + getXpForNextLevel());

        checkLevelUp();
    }

    public void setInfiniteHP(boolean infinite) {
        this.hasInfiniteHP = infinite;
    }

    public void setInfiniteAP(boolean infinite) {
        this.hasInfiniteAP = infinite;
    }

    // --- Sistema di Livellamento ---

    public int getXpForNextLevel() {
        return 50 * level * level;
    }

    private void checkLevelUp() {
        int xpNeeded = getXpForNextLevel();
        while (experience >= xpNeeded && level < 50) {
            level++;
            pendingLevelUpCount++;
            xpNeeded = getXpForNextLevel();
        }
    }

    public void applyLevelBonus(BonusType bonus) {
        int hpBonus, attBonus, defBonus, apBonus, invBonus;

        if (level <= 10) {
            hpBonus = 10;
            attBonus = 3;
            defBonus = 2;
            apBonus = 5;
            invBonus = 2;
        } else if (level <= 20) {
            hpBonus = 15;
            attBonus = 4;
            defBonus = 3;
            apBonus = 7;
            invBonus = 3;
        } else if (level <= 30) {
            hpBonus = 20;
            attBonus = 5;
            defBonus = 4;
            apBonus = 10;
            invBonus = 4;
        } else if (level <= 40) {
            hpBonus = 25;
            attBonus = 6;
            defBonus = 5;
            apBonus = 12;
            invBonus = 5;
        } else {
            hpBonus = 30;
            attBonus = 7;
            defBonus = 6;
            apBonus = 15;
            invBonus = 6;
        }

        switch (bonus) {
            case HP:
                maxHp += hpBonus;
                break;
            case ATTACK:
                attack += attBonus;
                break;
            case DEFENSE:
                defense += defBonus;
                break;
            case AP:
                maxAp += apBonus;
                break;
            case INVENTORY:
                inventory.increaseCapacity(invBonus);
                break;
        }

        // RESET COMPLETO HP e AP al massimo dopo ogni level up
        hp = maxHp;
        ap = maxAp;
    }

    public void consumeOneLevelUp() {
        if (this.pendingLevelUpCount > 0) {
            this.pendingLevelUpCount--;
        }
    }

    // --- Azioni di Gioco ---

    public void takeDamage(int damage) {
        if (!hasInfiniteHP) {
            this.hp = Math.max(0, this.hp - damage);
        }
    }

    public void heal(int amount) {
        if (!hasInfiniteHP) {
            setHp(hp + amount);
        }
    }

    public boolean isDead() {
        return hp <= 0 && !hasInfiniteHP;
    }

    public void onDeath() {
        level = Math.max(1, level / 2);
        inventory.clear();
        hp = maxHp;
        ap = maxAp;
    }

    public enum BonusType {
        HP, ATTACK, DEFENSE, AP, INVENTORY
    }

    public int tickEffects() {
        int totalDamage = 0;
        Iterator<ActiveEffect> it = activeEffects.iterator();

        while (it.hasNext()) {
            ActiveEffect e = it.next();

            // Applica danno da veleno
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

    @Override
    public String toString() {
        return String.format("%s [Alchimista Livello %d] HP: %d/%d | AP: %d/%d | ATT: %d | DEF: %d",
                name, level, hp, maxHp, ap, maxAp, attack, defense);
    }
}