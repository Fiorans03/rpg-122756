package it.unicam.cs.mpgc.rpg122756.game.combat;

import it.unicam.cs.mpgc.rpg122756.model.entities.Alchemist;
import it.unicam.cs.mpgc.rpg122756.model.entities.Monster;
import it.unicam.cs.mpgc.rpg122756.model.items.Ingredient;
import it.unicam.cs.mpgc.rpg122756.model.items.Item;
import it.unicam.cs.mpgc.rpg122756.model.items.Potion;
import it.unicam.cs.mpgc.rpg122756.model.items.PotionEffect;
import it.unicam.cs.mpgc.rpg122756.model.crafting.AlchemyBook;
import it.unicam.cs.mpgc.rpg122756.model.entities.ActiveEffect;
import java.util.List;
import java.util.Random;

public class CombatSystem {

    // ==========================================
    // SEZIONE: VARIABILI DI ISTANZA
    // ==========================================

    private final Random random = new Random();

    // ==========================================
    // SEZIONE: METODI DI ATTACCO BASE
    // ==========================================

    public int alchemistAttack(Alchemist alchemist, Monster monster) {
        int baseDamage = Math.max(1, alchemist.getAttack() - (monster.getDefense() / 2));
        int finalDamage = applyVariance(baseDamage);
        monster.takeDamage(finalDamage);
        return finalDamage;
    }

    public int monsterAttack(Monster monster, Alchemist alchemist) {
        int baseDamage = Math.max(1, monster.getAttack() - (alchemist.getDefense() / 2));
        int finalDamage = applyVariance(baseDamage);
        alchemist.takeDamage(finalDamage);
        return finalDamage;
    }

    // ==========================================
    // SEZIONE: METODI DI COMBATTIMENTO AVANZATO (POZIONI)
    // ==========================================

    public int usePotion(Alchemist alchemist, Potion potion, Monster target) {
        if (alchemist.getAp() < potion.getApCost()) {
            return -1;
        }

        alchemist.setAp(alchemist.getAp() - potion.getApCost());
        PotionEffect effect = potion.getEffect();

        if (effect.getType() == PotionEffect.EffectType.ANALYZE) {
            if (target != null) {
                alchemist.addDiscoveredWeakness(target.getName());
            }
            return 0;
        }

        if (effect.getType() == PotionEffect.EffectType.AP_RECOVER) {
            int recoveredAp = effect.getMagnitude();
            int currentAp = alchemist.getAp();
            int maxAp = alchemist.getMaxAp();
            alchemist.setAp(Math.min(maxAp, currentAp + recoveredAp));
            return recoveredAp;
        }

        int damage = effect.getMagnitude();

        if (target != null && effect.getType() == PotionEffect.EffectType.DAMAGE) {
            String attackElement = effect.getElement();
            String weakness = target.getWeakness();
            String resistance = target.getResistance();

            if (attackElement != null && attackElement.equals(weakness)) {
                damage = (int) (damage * 1.5);
            } else if (attackElement != null && attackElement.equals(resistance)) {
                damage = (int) (damage * 0.5);
            }

            target.takeDamage(damage);
            return damage;
        }

        if (effect.getType() == PotionEffect.EffectType.HEAL) {
            alchemist.heal(effect.getMagnitude());
            return effect.getMagnitude();
        }

        if (effect.getType() == PotionEffect.EffectType.BUFF_ATTACK ||
                effect.getType() == PotionEffect.EffectType.BUFF_DEFENSE ||
                effect.getType() == PotionEffect.EffectType.DEBUFF_ATTACK) {

            alchemist.addEffect(new ActiveEffect(effect.getType(), effect.getMagnitude(), effect.getDuration()));
            return effect.getMagnitude();
        }

        if (effect.getType() == PotionEffect.EffectType.POISON && target != null) {
            target.addEffect(
                    new ActiveEffect(PotionEffect.EffectType.POISON, effect.getMagnitude(), effect.getDuration()));
            return effect.getMagnitude();
        }

        return 0;
    }

    // ==========================================
    // SEZIONE: METODI DI UTILITÀ E VARIANZA
    // ==========================================

    private int applyVariance(int baseDamage) {
        int variance = (int) (baseDamage * 0.2);
        int randomOffset = random.nextInt(variance * 2 + 1) - variance;
        return Math.max(1, baseDamage + randomOffset);
    }

    // ==========================================
    // SEZIONE: GESTIONE TURNI E VITTORIA
    // ==========================================

    public CombatResult resolveVictory(Monster monster, Alchemist alchemist) {
        int xpGained = monster.getXpReward();
        alchemist.addExperience(xpGained);
        List<Item> droppedItems = monster.dropItems();
        return new CombatResult(xpGained, droppedItems);
    }

    public String processStartOfTurn(Alchemist alchemist, Monster monster) {
        StringBuilder log = new StringBuilder();

        int alchDamage = alchemist.tickEffects();
        if (alchDamage > 0) {
            log.append("☠️ ").append(alchemist.getName()).append(" subisce ").append(alchDamage)
                    .append(" danni da veleno! ");
        }

        int monsterDamage = monster.tickEffects();
        if (monsterDamage > 0) {
            log.append("☠️ ").append(monster.getName()).append(" subisce ").append(monsterDamage)
                    .append(" danni da veleno! ");
        }

        return log.toString().trim();
    }

    public List<Item> processVictory(Alchemist alchemist, AlchemyBook book, Monster monster) {
        List<Item> drops = monster.dropItems();

        for (Item drop : drops) {
            alchemist.getInventory().addItem(drop);
            if (drop instanceof Ingredient) {
                book.discoverIngredient(drop.getName());
            }
        }

        alchemist.addExperience(monster.getXpReward());
        return drops;
    }

    // ==========================================
    // SEZIONE: CLASSI INTERNE
    // ==========================================

    public static class CombatResult {
        private final int xpGained;
        private final List<Item> droppedItems;

        public CombatResult(int xpGained, List<Item> droppedItems) {
            this.xpGained = xpGained;
            this.droppedItems = droppedItems;
        }

        public int getXpGained() {
            return xpGained;
        }

        public List<Item> getDroppedItems() {
            return droppedItems;
        }
    }
}