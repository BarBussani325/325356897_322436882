package units;

import game.GameBoard;
import units.Enemy;
import units.Player;
import tiles.Empty;

public class Trap extends Enemy {
    private int visibilityTime;
    private int invisibilityTime;
    private int ticksCount;
    private boolean visible;

    public Trap(char character, String name, int healthPool, int attackPoints, int defensePoints,
                int experienceValue, int visibilityTime, int invisibilityTime, int x, int y) {
        super(character, name, healthPool, attackPoints, defensePoints, experienceValue, x, y);
        this.visibilityTime = visibilityTime;
        this.invisibilityTime = invisibilityTime;
        this.ticksCount = 0;
        this.visible = true; // Initially visible
        this.character = character; // Store original character
    }

    @Override
    public void processTurn(GameBoard board) {
        ticksCount++;
        if (visible) {
            if (ticksCount == visibilityTime) {
                visible = false;
                ticksCount = 0;
                // Character should change to '.' when invisible, but the object itself stays in place
                // This means the 'character' field of the Tile base class should be dynamic or overridden
                // For now, let's just change the character for display
                super.character = '.'; // Temporarily change character for display
            }
        } else { // Invisible
            if (ticksCount == invisibilityTime) {
                visible = true;
                ticksCount = 0;
                super.character = character; // Revert to original character
            }
        }

        Player player = board.getPlayer();
        if (player != null && board.getDistance(this, player) < 2) { // range < 2
            attack(player);
        }
    }

    @Override
    public String toString() {
        return String.valueOf(super.character); // Use the potentially changed character
    }

    @Override
    public String description() {
        return super.description() + String.format("\t\tVisibility Time: %d\t\tInvisibility Time: %d\t\tIs Visible: %b",
                visibilityTime, invisibilityTime, visible);
    }
}