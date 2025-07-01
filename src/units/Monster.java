package units;

import game.GameBoard;
import units.Enemy;
import units.Player;
import game.Position; // You'll need a Position utility class
import tiles.Empty;

import java.util.Random;

public class Monster extends Enemy {
    protected int visionRange;

    public Monster(char character, String name, int healthPool, int attackPoints, int defensePoints, int experienceValue, int visionRange, int x, int y) {
        super(character, name, healthPool, attackPoints, defensePoints, experienceValue, x, y);
        this.visionRange = visionRange;
    }

    @Override
    public void ProcessTurn(GameBoard board) {
        Player player = board.getPlayer();
        if (player == null) return; // Should not happen in a single-player game, but for safety
        double distance = board.getDistance(this, player);
        if (distance < visionRange) {
            super.chasePlayer(board, player);
        } else {
            super.performRandomMovement(board);
        }
    }

    @Override
    public String description() {
        return super.description() + String.format("\t\tVision Range: %d", visionRange);
    }
}