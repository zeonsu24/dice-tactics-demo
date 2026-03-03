package com.game.dicetactics.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Dice {

    private DiceType type;

    /** Face value rolled, between 1 and 6 inclusive. */
    private int faceValue;
}