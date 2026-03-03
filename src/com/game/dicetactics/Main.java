package com.game.dicetactics;

import com.game.dicetactics.controller.GameController;
import com.game.dicetactics.model.*;
import com.game.dicetactics.view.ConsoleView;

import java.util.List;
import java.util.Optional;

/**
 * Main — bootstraps Dice Tactics and runs a scripted demo scenario.
 *
 * Scenario:
 *   • Two heroes placed on the grid  (Aria → Frontline row 0, Borin → Backline row 0)
 *   • 5 dice generated into the shared pool
 *   • A STRIKE die is manually assigned to Aria (Frontline attacker)
 *   • A GUARD  die is manually assigned to Borin (Backline defender)
 *   • Grid is rendered, then the turn resolves
 */
public class Main {

    public static void main(String[] args) {

        // ── 1. Bootstrap Models ───────────────────────────────────────────────
        Grid     grid     = new Grid();
        DicePool dicePool = new DicePool();

        // ── 2. Bootstrap View ─────────────────────────────────────────────────
        ConsoleView view = new ConsoleView();

        // ── 3. Bootstrap Controller ───────────────────────────────────────────
        GameController controller = new GameController(grid, dicePool);

        // ── 4. Create Heroes ──────────────────────────────────────────────────
        //        Hero(id, name, maxHp, passiveSkillName)
        Hero aria  = new Hero("H001", "Aria",  5, "Keen Eye");   // will go Frontline
        Hero borin = new Hero("H002", "Borin", 5, "Iron Skin");  // will go Backline

        // ── 5. Place Heroes on the Grid ───────────────────────────────────────
        //   board[row][col] → row 0, col 1 = Frontline top slot
        //                     row 0, col 0 = Backline  top slot
        grid.placeHero(aria,  0, Grid.FRONTLINE);
        grid.placeHero(borin, 0, Grid.BACKLINE);

        // ── 6. Generate Dice into the Shared Pool ─────────────────────────────
        dicePool.generateDice(5);

        view.printBanner("DICE TACTICS  —  DEMO SCENARIO");
        view.printMessage("Heroes placed. Dice pool generated.");
        view.printSeparator();

        // ── 7. Render initial state ───────────────────────────────────────────
        view.printMessage("► Initial Grid State:");
        view.renderGrid(grid);

        view.printMessage("► Shared Pool (before allocation):");
        view.renderPool(dicePool);

        // ── 8. Allocate Dice ──────────────────────────────────────────────────
        //   We need at least one STRIKE and one GUARD die in the pool.
        //   generateDice() is random, so we guarantee availability by injecting
        //   specific dice directly when the pool lacks what we need for the demo.
        guaranteeDie(dicePool, DiceType.STRIKE);
        guaranteeDie(dicePool, DiceType.GUARD);

        Dice strikeForAria = findFirst(dicePool, DiceType.STRIKE);
        Dice guardForBorin = findFirst(dicePool, DiceType.GUARD);

        view.printSeparator();
        view.printMessage("► Allocating dice to heroes...");

        controller.allocateDice(aria,  strikeForAria);
        controller.allocateDice(borin, guardForBorin);

        view.printMessage("► Pool after allocation:");
        view.renderPool(dicePool);

        view.printMessage("► Hero status after allocation:");
        view.renderHeroStatus(aria);
        view.renderHeroStatus(borin);

        // ── 9. Resolve the Turn ───────────────────────────────────────────────
        view.printSeparator();
        view.printMessage("► Resolving turn...");
        controller.resolveTurn();

        // ── 10. Render final grid state ───────────────────────────────────────
        view.printMessage("► Grid after turn resolution:");
        view.renderGrid(grid);

        view.printMessage("► Hero status after resolution:");
        view.renderHeroStatus(aria);
        view.renderHeroStatus(borin);

        view.printBanner("END OF DEMO");
    }

    // -------------------------------------------------------------------------
    // Demo helpers (Main-only utilities, not part of any reusable layer)
    // -------------------------------------------------------------------------

    /**
     * If the pool has no die of the required type, injects one directly.
     * This is demo scaffolding only — real gameplay relies on generateDice().
     */
    private static void guaranteeDie(DicePool pool, DiceType type) {
        boolean present = pool.getSharedPool()
            .stream()
            .anyMatch(d -> d.getType() == type);

        if (!present) {
            pool.getSharedPool().add(new Dice(type, 4)); // face value 4 is a clear mid-value
            System.out.printf("  [DEMO INJECT] Added a guaranteed %s die (value: 4) for scenario.%n", type);
        }
    }

    /**
     * Returns the first die of the given type from the pool without removing it.
     * Throws if somehow still absent (should never happen after guaranteeDie).
     */
    private static Dice findFirst(DicePool pool, DiceType type) {
        return pool.getSharedPool()
            .stream()
            .filter(d -> d.getType() == type)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException(
                "Expected a " + type + " die in pool but found none."));
    }
}
