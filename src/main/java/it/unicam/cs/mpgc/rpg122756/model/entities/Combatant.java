package it.unicam.cs.mpgc.rpg122756.model.entities;

public interface Combatant {

    // ==========================================
    // SEZIONE: METODI GETTER (ACCESSORI)
    // ==========================================

    String getName();

    int getHp();

    int getMaxHp();

    int getAttack();

    int getDefense();

    // ==========================================
    // SEZIONE: METODI DI AZIONE E STATO
    // ==========================================

    void takeDamage(int damage);

    boolean isDead();
}