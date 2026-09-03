package it.unicam.cs.mpgc.rpg122756.model.entities;

/**
 * Interfaccia per tutte le entità che possono partecipare a un combattimento.
 * Rispetta il principio di Interface Segregation (SOLID).
 */
public interface Combatant {
    
    String getName();
    int getHp();
    int getMaxHp();
    int getAttack();
    int getDefense();
    
    void takeDamage(int damage);
    boolean isDead();
}