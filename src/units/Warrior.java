package units;

import game.GameBoard;
import units.Enemy;
import units.Player;

import java.util.List;
import java.util.Random;

public class Warrior extends Player/*implements HeroicUnit */{
    private int abilityCooldown;
    private int remainingCooldown;

    public Warrior(String name, int healthPool, int attackPoints, int defensePoints, int abilityCooldown, int x, int y) {
        super(name, healthPool, attackPoints, defensePoints, x, y);
        this.abilityCooldown = abilityCooldown;
        this.remainingCooldown = 0;
    }

//    @Override
//    public void levelUp() {
//        super.levelUp();
//        this.remainingCooldown = 0; // Reset cooldown on level up
//        //this.healthPool += (5 * this.level);
//        this.health.setHealthPool(this.health.getHealthPool() + 5 * this.level );
//        //this.currentHealth = this.healthPool; // Re-heal fully
//        this.health.increaseHealth(this.health.getHealthPool());
//        this.attackPoints += (2 * this.level);
//        this.defensePoints += (1 * this.level);
//    }
//
//    @Override
//    public void castAbility(GameBoard board) {
//        if (remainingCooldown > 0) {
//            System.out.println(getName() + " cannot cast Avenger's Shield, remaining cooldown: " + remainingCooldown + ".");
//            return;
//        }
//
//        List<Enemy> enemiesInRange = board.getEnemiesInRange(this.position.x, this.position.y, 3); // Range < 3 (so 0, 1, 2)
//        if (enemiesInRange.isEmpty()) {
//            System.out.println(getName() + " cannot cast Avenger's Shield, no enemies in range.");
//            return;
//        }
//
//        remainingCooldown = abilityCooldown;
//        Random random = new Random();
//        Enemy target = enemiesInRange.get(random.nextInt(enemiesInRange.size()));
//
//        int damage = (int) (this.health.getHealthPool() * 0.1); // 10% of max health
//        target.health.decreaseHealth(damage);
//        System.out.println(getName() + " cast Avenger's Shield on " + target.getName() + ", dealing " + damage + " damage.");
//
//        int healAmount = (10 * defensePoints);
//        this.health.increaseHealth(healAmount);
//        System.out.println(getName() + " healed for " + healAmount + " health.");
//
//        // Check if enemy died
//        if (!target.health.isAlive()) {
//            board.removeUnit(target); // Remove enemy from board
//            gainExperience(target.getExperienceValue()); // Gain experience
//            System.out.println(getName() + " defeated " + target.getName() + " and gained " + target.getExperienceValue() + " experience.");
//        }
//    }

    @Override
    public void processTurn(GameBoard board) {
        if (remainingCooldown > 0) {
            remainingCooldown--;
        }
    }

    @Override
    public String description() {
        return super.description() + String.format("\t\tAbility Cooldown: %d/%d", remainingCooldown, abilityCooldown);
    }
}