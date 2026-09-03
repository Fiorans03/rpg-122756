package it.unicam.cs.mpgc.rpg122756.model.items;

/**
 * Rappresenta l'effetto di una pozione.
 * Usa il pattern Strategy per permettere effetti diversi.
 */
public class PotionEffect {
    
    private final EffectType type;
    private final int magnitude;
    private final int duration;
    private final String element;
    
    public enum EffectType {
        HEAL,           // Cura HP
        DAMAGE,         // Danno al nemico
        AP_RECOVER,     // Recupera Punti Azione
        BUFF_ATTACK,    // Aumenta ATT
        BUFF_DEFENSE,   // Aumenta DEF
        DEBUFF_ATTACK,  // Riduce ATT nemico
        POISON,         // Danno nel tempo (DoT)
        SLOW,           // Rallenta nemico
        REGEN,          // Rigenerazione HP
        SHIELD,         // Schiva prossimo attacco
        ANALYZE,        // Rivela debolezze
        SPECIAL         // Effetti speciali (es. Elisir)
    }
    
    public PotionEffect(EffectType type, int magnitude, int duration) {
        this(type, magnitude, duration, "Neutro");
    }

    public PotionEffect(EffectType type, int magnitude, int duration, String element) {
        this.type = type;
        this.magnitude = magnitude;
        this.duration = duration;
        this.element = element;
    }
    
    public EffectType getType() {
        return type;
    }
    
    public int getMagnitude() {
        return magnitude;
    }
    
    public int getDuration() {
        return duration;
    }

    public String getElement() { 
        return element; 
    }
    
    public boolean isInstantaneous() {
        return duration == 0;
    }
}