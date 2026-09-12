package it.unicam.cs.mpgc.rpg122756.model.items;

public class PotionEffect {

    // ==========================================
    // SEZIONE: ENUM
    // ==========================================

    public enum EffectType {
        HEAL,
        DAMAGE,
        AP_RECOVER,
        BUFF_ATTACK,
        BUFF_DEFENSE,
        DEBUFF_ATTACK,
        POISON,
        SLOW,
        REGEN,
        SHIELD,
        ANALYZE,
        SPECIAL
    }

    // ==========================================
    // SEZIONE: VARIABILI DI ISTANZA
    // ==========================================

    private final EffectType type;
    private final int magnitude;
    private final int duration;
    private final String element;

    // ==========================================
    // SEZIONE: COSTRUTTORI
    // ==========================================

    public PotionEffect(EffectType type, int magnitude, int duration) {
        this(type, magnitude, duration, "Neutro");
    }

    public PotionEffect(EffectType type, int magnitude, int duration, String element) {
        this.type = type;
        this.magnitude = magnitude;
        this.duration = duration;
        this.element = element;
    }

    // ==========================================
    // SEZIONE: METODI GETTER
    // ==========================================

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

    // ==========================================
    // SEZIONE: METODI DI UTILITÀ
    // ==========================================

    public boolean isInstantaneous() {
        return duration == 0;
    }
}