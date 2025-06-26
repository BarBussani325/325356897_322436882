package units;

import game.GameBoard;

import java.util.Arrays;
import java.util.List;
import java.util.Comparator;

public class Hunter extends Player{
    private int range; // Shooting range
    private int arrowsCount;
    private int ticksCount;

    public Hunter(String name, int healthPool, int attackPoints, int defensePoints, int range, int x, int y) {
        super(name, healthPool, attackPoints, defensePoints, x, y);
        this.range = range;
        this.arrowsCount = 10 * level;
        this.ticksCount = 0;
    }

    @Override
    public String applyLevelUpBonuses() {
        this.arrowsCount += (10 * this.level);
        this.attackPoints += (2 * this.level);
        this.defensePoints +=  this.level;
        return "";
    }


    public void OnCastAbility(GameBoard board) {
        if (arrowsCount == 0) {
            if (messageCallback != null)
                messageCallback.send(getName() + " cannot cast Shoot, no arrows left.\n");
            return;
        }

        List<Enemy> enemiesInRange = board.getEnemiesInRange(this, range);
        if (enemiesInRange.isEmpty()) {
            if (messageCallback != null)
                messageCallback.send(getName() + " tried to shoot an arrow but there were no enemies in range.\n");
            return;
        }

        // Find the closest enemy
        Enemy closestEnemy = enemiesInRange.stream()
                .min(Comparator.comparingDouble(e -> board.getDistance(this, e)))
                .orElse(null);

        if (closestEnemy == null) {
            if (messageCallback != null)
                messageCallback.send(getName() + " cannot cast Shoot, no enemies in range.\n");
            return;
        }

        arrowsCount--;
        if (messageCallback != null)
            messageCallback.send(getName() + " fired an arrow at " + closestEnemy.getName()+".\n");
        List<Enemy> targetList = Arrays.asList(closestEnemy);
        super.castAbility(board,targetList,attackPoints);
    }


    @Override
    public void ProcessTurn(GameBoard board) {
        super.ProcessTurn(board);
        if (ticksCount == 10) {
            arrowsCount += level;
            ticksCount = 0;
        }
        else
            ticksCount++;
    }

    @Override
    public String description() {
        return super.description() + String.format("\t\tArrows: %d\t\tRange: %d",
                arrowsCount, range);
    }


    public int getArrowsCount() {
        return arrowsCount;
    }
}
