package units;

import game.GameBoard;

import units.Enemy;
import units.Player;

import java.util.List;

public class Rogue extends Player/*implements HeroicUnit*/ {
    private int cost; // Energy cost for Fan of Knives
    private int currentEnergy;

    public Rogue(String name, int healthPool, int attackPoints, int defensePoints, int cost, int x, int y) {
        super(name, healthPool, attackPoints, defensePoints, x, y);
        this.cost = cost;
        this.currentEnergy = 100; // Starting energy is 100
    }

    @Override
    public void levelUp() {
        super.levelUp();
        this.currentEnergy = 100; // Reset energy on level up
        this.attackPoints += (3 * this.level);
    }

//    @Override
//    public void castAbility(GameBoard board) {
//        if (currentEnergy < cost) {
//            System.out.println(getName() + " cannot cast Fan of Knives, not enough energy. Current energy: " + currentEnergy + ".");
//            return;
//        }
//
//        List<Enemy> enemiesInRange = board.getEnemiesInRange(this.position.x, this.position.y, 2); // Range < 2 (so 0, 1)
//        if (enemiesInRange.isEmpty()) {
//            System.out.println(getName() + " cannot cast Fan of Knives, no enemies in range.");
//            return;
//        }
//
//        currentEnergy -= cost;
//        System.out.println(getName() + " cast Fan of Knives!");
//        for (Enemy enemy : enemiesInRange) {
//            enemy.health.decreaseHealth(attackPoints); // Damage equals rogue's attack points
//            System.out.println("  " + enemy.getName() + " took " + attackPoints + " damage.");
//
//            if (!enemy.health.isAlive()) {
//                board.removeUnit(enemy);
//                gainExperience(enemy.getExperienceValue());
//                System.out.println("  " + getName() + " defeated " + enemy.getName() + " and gained " + enemy.getExperienceValue() + " experience.");
//            }
//        }
//    }

    @Override
    public void processTurn(GameBoard board) {
        currentEnergy = Math.min(currentEnergy + 10, 100);
    }

    @Override
    public String description() {
        return super.description() + String.format("\t\tEnergy: %d/%d\t\tAbility Cost: %d",
                currentEnergy, 100, cost);
    }
}