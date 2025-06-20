package units;

import game.GameBoard;
import java.util.List;
import java.util.Comparator;

public class Hunter extends Player/*implements HeroicUnit */{
    private int range; // Shooting range
    private int arrowsCount;
    private int ticksCount;

    public Hunter(String name, int healthPool, int attackPoints, int defensePoints, int range, int x, int y) {
        super(name, healthPool, attackPoints, defensePoints, x, y);
        this.range = range;
        this.arrowsCount = 10 * level; // Starting arrows based on initial level
        this.ticksCount = 0;
    }

    @Override
    public void levelUp() {
        super.levelUp();
        this.arrowsCount += (10 * this.level);
        this.attackPoints += (2 * this.level);
        this.defensePoints += (1 * this.level);
    }

    @Override
    public void castAbility(GameBoard board) {
        if (arrowsCount == 0) {
            System.out.println(getName() + " cannot cast Shoot, no arrows left.");
            return;
        }

        List<Enemy> enemiesInRange = board.getEnemiesInRange(this.position.x, this.position.y, range);
        if (enemiesInRange.isEmpty()) {
            System.out.println(getName() + " cannot cast Shoot, no enemies in range.");
            return;
        }

        // Find the closest enemy
        Enemy closestEnemy = enemiesInRange.stream()
                .min(Comparator.comparingDouble(e -> board.getDistance(this, e)))
                .orElse(null);

        if (closestEnemy == null) {
            System.out.println(getName() + " cannot cast Shoot, no enemies in range.");
            return;
        }

        arrowsCount--;
        closestEnemy.health.increaseHealth(attackPoints);
        System.out.println(getName() + " shot " + closestEnemy.getName() + " for " + attackPoints + " damage.");

        if (!closestEnemy.health.isAlive()) {
            board.removeUnit(closestEnemy);
            gainExperience(closestEnemy.getExperienceValue());
            System.out.println(getName() + " defeated " + closestEnemy.getName() + " and gained " + closestEnemy.getExperienceValue() + " experience.");
        }
    }

    @Override
    public void processTurn(GameBoard board) {
        ticksCount++;
        if (ticksCount == 10) {
            arrowsCount += level;
            ticksCount = 0;
            System.out.println(getName() + " regenerated " + level + " arrow(s). Current arrows: " + arrowsCount);
        }
    }

    @Override
    public String description() {
        return super.description() + String.format("\t\tArrows: %d\t\tRange: %d",
                arrowsCount, range);
    }


}
