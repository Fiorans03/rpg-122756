package it.unicam.cs.mpgc.rpg122756.game.combat;

import it.unicam.cs.mpgc.rpg122756.model.entities.Alchemist;
import it.unicam.cs.mpgc.rpg122756.model.entities.Monster;
import it.unicam.cs.mpgc.rpg122756.model.items.Item;
import it.unicam.cs.mpgc.rpg122756.model.items.Potion;
import it.unicam.cs.mpgc.rpg122756.model.items.PotionEffect;
import it.unicam.cs.mpgc.rpg122756.model.entities.ActiveEffect;
import java.util.List;
import java.util.Random;

public class CombatSystem {

    private final Random random = new Random();

    /**
     * Attacco base dell'Alchimista
     */
    public int alchemistAttack(Alchemist alchemist, Monster monster) {
        // Formula: Attacco - (Difesa / 2) + Variazione casuale ±20%
        int baseDamage = Math.max(1, alchemist.getAttack() - (monster.getDefense() / 2));
        int finalDamage = applyVariance(baseDamage);
        monster.takeDamage(finalDamage);
        return finalDamage;
    }

    /**
     * Attacco del Mostro
     */
    public int monsterAttack(Monster monster, Alchemist alchemist) {
        int baseDamage = Math.max(1, monster.getAttack() - (alchemist.getDefense() / 2));
        int finalDamage = applyVariance(baseDamage);
        alchemist.takeDamage(finalDamage);
        return finalDamage;
    }

    /**
     * Usa una pozione in combattimento.
     * 
     * @return Il danno/effect generato, o 0 se fallisce.
     */
    public int usePotion(Alchemist alchemist, Potion potion, Monster target) {
        if (alchemist.getAp() < potion.getApCost()) {
            return -1;
        }

        alchemist.setAp(alchemist.getAp() - potion.getApCost());
        PotionEffect effect = potion.getEffect();

        // 1. GESTIONE ANALISI (BESTIARIO)
        if (effect.getType() == PotionEffect.EffectType.ANALYZE) {
            if (target != null) {
                alchemist.addDiscoveredWeakness(target.getName());
            }
            return 0;
        }

        // 2. GESTIONE RECUPERO AP
        if (effect.getType() == PotionEffect.EffectType.AP_RECOVER) {
            int recoveredAp = effect.getMagnitude();
            int currentAp = alchemist.getAp();
            int maxAp = alchemist.getMaxAp();
            alchemist.setAp(Math.min(maxAp, currentAp + recoveredAp));
            return recoveredAp;
        }

        int damage = effect.getMagnitude();

        // 3. GESTIONE DANNO ELEMENTALE
        if (target != null && effect.getType() == PotionEffect.EffectType.DAMAGE) {
            String attackElement = effect.getElement();
            String weakness = target.getWeakness();
            String resistance = target.getResistance();

            if (attackElement != null && attackElement.equals(weakness)) {
                damage = (int) (damage * 1.5); // Debolezza: +50% danno
            } else if (attackElement != null && attackElement.equals(resistance)) {
                damage = (int) (damage * 0.5); // Resistenza: -50% danno
            }

            target.takeDamage(damage);
            return damage;
        }

        // 4. GESTIONE CURA
        if (effect.getType() == PotionEffect.EffectType.HEAL) {
            alchemist.heal(effect.getMagnitude());
            return effect.getMagnitude();
        }

        // 5. GESTIONE BUFF / DEBUFF / VELENO
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

    private int applyVariance(int baseDamage) {
        // Variazione casuale tra -20% e +20%
        int variance = (int) (baseDamage * 0.2);
        int randomOffset = random.nextInt(variance * 2 + 1) - variance;
        return Math.max(1, baseDamage + randomOffset);
    }

    public CombatResult resolveVictory(Monster monster, Alchemist alchemist) {
        int xpGained = monster.getXpReward();
        alchemist.addExperience(xpGained);
        List<Item> droppedItems = monster.dropItems();
        return new CombatResult(xpGained, droppedItems);
    }

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

    public String processStartOfTurn(Alchemist alchemist, Monster monster) {
        StringBuilder log = new StringBuilder();

        // 1. Effetti sull'Alchimista
        int alchDamage = alchemist.tickEffects();
        if (alchDamage > 0) {
            log.append("☠️ ").append(alchemist.getName()).append(" subisce ").append(alchDamage).append(" danni da veleno! ");
        }

        // 2. Effetti sul Mostro
        int monsterDamage = monster.tickEffects();
        if (monsterDamage > 0) {
            log.append("☠️ ").append(monster.getName()).append(" subisce ").append(monsterDamage).append(" danni da veleno! ");
        }

        return log.toString().trim();
    }
}