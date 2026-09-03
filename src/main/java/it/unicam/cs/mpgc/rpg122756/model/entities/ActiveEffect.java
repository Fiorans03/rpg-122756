package it.unicam.cs.mpgc.rpg122756.model.entities;

import it.unicam.cs.mpgc.rpg122756.model.items.PotionEffect;

public class ActiveEffect {
    private final PotionEffect.EffectType type;
    private final int magnitude;
    private int duration;

    public ActiveEffect(PotionEffect.EffectType type, int magnitude, int duration) {
        this.type = type;
        this.magnitude = magnitude;
        this.duration = duration;
    }

    public PotionEffect.EffectType getType() { return type; }
    public int getMagnitude() { return magnitude; }
    public int getDuration() { return duration; }

    public void decrementDuration() {
        this.duration--;
    }

    public boolean isExpired() {
        return duration <= 0;
    }

    @Override
    public String toString() {
        String icon = switch (type) {
            case BUFF_ATTACK -> "⚔️";
            case BUFF_DEFENSE -> "🛡️";
            case POISON -> "☠️";
            case REGEN -> "💚";
            default -> "✨";
        };
        return String.format("%s %s (%d turni)", icon, type.name(), duration);
    }
}