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
    public void ProcessTurn(GameBoard board) {
        visible = ticksCount < visibilityTime;
        if(ticksCount == visibilityTime + invisibilityTime)
            ticksCount = 0;
        else
            ticksCount ++;
        Player player = board.getPlayer();
        if (player != null && board.getDistance(this, player) < 2) {
            this.attack(board, player);
        }
        if (!player.isAlive()) {
            player.onDeath(board, this);
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