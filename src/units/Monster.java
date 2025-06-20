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
    public void processTurn(GameBoard board) {
        Player player = board.getPlayer();
        if (player == null) return; // Should not happen in a single-player game, but for safety

        double distance = board.getDistance(this, player);

        if (distance < visionRange) {
            // Chase player
            int dx = this.position.x - player.getX();
            int dy = this.position.y - player.getY();

            int nextX = this.position.x;
            int nextY = this.position.y;

            if (Math.abs(dx) > Math.abs(dy)) {
                if (dx > 0) {
                    nextX = nextX - 1; // Move left
                } else {
                    nextX = nextX + 1; // Move right
                }
            } else {
                if (dy > 0) {
                    nextY = nextY - 1; // Move up
                } else {
                    nextY = nextY + 1; // Move down
                }
            }
            // Attempt to move to the new position
            board.tryMoveUnit(this, nextX, nextY);

        } else {
            // Random movement
            Random random = new Random();
            int moveDirection = random.nextInt(5); // 0: Up, 1: Down, 2: Left, 3: Right, 4: Stay

            int nextX = this.position.x;
            int nextY = this.position.y;

            switch (moveDirection) {
                case 0: // Up
                    nextY--;
                    break;
                case 1: // Down
                    nextY++;
                    break;
                case 2: // Left
                    nextX--;
                    break;
                case 3: // Right
                    nextX++;
                    break;
                case 4: // Stay
                    break;
            }
            board.tryMoveUnit(this, nextX, nextY);
        }
    }

    @Override
    public String description() {
        return super.description() + String.format("\t\tVision Range: %d", visionRange);
    }
}