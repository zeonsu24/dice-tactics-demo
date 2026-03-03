package com.game.dicetactics.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Hero {

    private String id;
    private String name;

    private int maxHp;
    private int currentHp;

    /** Accumulated block/armor from Guard dice this turn. Resets each turn. */
    private int block;

    /** The passive skill flavour text or identifier for this hero. */
    private String passiveSkillName;

    /** Dice currently equipped / assigned to this hero from the shared pool. */
    private List<Dice> equippedDice = new ArrayList<>();

    // -------------------------------------------------------------------------
    // Convenience constructor (without pre-built dice list)
    // -------------------------------------------------------------------------
    public Hero(String id, String name, int maxHp, String passiveSkillName) {
        this.id = id;
        this.name = name;
        this.maxHp = maxHp;
        this.currentHp = maxHp;
        this.block = 0;
        this.passiveSkillName = passiveSkillName;
        this.equippedDice = new ArrayList<>();
    }

    // -------------------------------------------------------------------------
    // Domain logic
    // -------------------------------------------------------------------------

    /** @return true if this hero has no remaining HP. */
    public boolean isDead() {
        return currentHp <= 0;
    }

    /**
     * Applies incoming damage, absorbing through block first.
     *
     * @param rawDamage the raw damage value before block reduction
     * @return the overflow damage that was NOT absorbed (used for Breakthrough)
     */
    public int takeDamage(int rawDamage) {
        int absorbed = Math.min(block, rawDamage);
        block -= absorbed;

        int remainder = rawDamage - absorbed;
        currentHp -= remainder;

        // Clamp HP to 0 so isDead() is clean
        if (currentHp < 0) {
            int overflow = Math.abs(currentHp);
            currentHp = 0;
            return overflow;   // Breakthrough damage
        }
        return 0;
    }

    /** Resets per-turn transient state (block, equipped dice). */
    public void resetTurnState() {
        block = 0;
        equippedDice.clear();
    }
}