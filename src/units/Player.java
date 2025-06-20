package units;

import game.GameBoard;
import interfaces.Visited;
import interfaces.Visitor;
import tiles.Empty;
import tiles.Wall;

import java.util.Random; // For rolling attack/defense

public abstract class Player extends Unit {
    protected int experience;
    protected int level;
    protected int maxExperienceForLevel;

    public Player(String name, int healthPool, int attackPoints, int defensePoints, int x, int y) {
        super('@', name, healthPool, attackPoints, defensePoints, x, y);
        this.experience = 0;
        this.level = 1;
        this.maxExperienceForLevel = 50; // Initial requirement for level 1
    }

    public int getExperience() {
        return experience;
    }

    public int getLevel() {
        return level;
    }

    public void gainExperience(int expGained) {
        this.experience += expGained;
        while (this.experience >= maxExperienceForLevel * this.level) {
            levelUp();
        }
    }

    protected void levelUp() {
        this.experience -= (maxExperienceForLevel * this.level);
        this.level++;
        //this.healthPool += (10 * this.level);
        this.health.setHealthPool(this.health.getHealthPool() + 10 * this.level);
        //this.currentHealth = this.healthPool; // Restore health
        this.health.increaseHealth(this.health.getHealthPool());
        this.attackPoints += (4 * this.level);
        this.defensePoints += (1 * this.level);
        System.out.println(getName() + " leveled up to level " + getLevel() + "!");
        // You'll need to notify the UI here, possibly through an observer pattern
        // Example: gameBoard.notifyPlayerLeveledUp(this);
    }

    // Combat logic for player attacking an enemy
    public void attack(Enemy enemy) {
        Random random = new Random();
        int attackRoll = random.nextInt(attackPoints + 1);
        int defenseRoll = random.nextInt(enemy.getDefensePoints() + 1);
        int damage = Math.max(0, attackRoll - defenseRoll);
        enemy.health.decreaseHealth(damage);
        System.out.println(getName() + " attacked " + enemy.getName() + ". Attack roll: " + attackRoll + ", " + enemy.getName() + "'s defense roll: " + defenseRoll + ", Damage dealt: " + damage + ".");
        // Notify UI about combat details
    }

    @Override
    public String description() {
        return String.format("%s\t\tHealth: %d/%d\t\tAttack: %d\t\tDefense: %d\t\tLevel: %d\t\tExperience: %d/%d",
                name, health.getHealthAmount(), health.getHealthPool(), attackPoints, defensePoints, level, experience, (maxExperienceForLevel * level));
    }

    // Visitor pattern implementation for Player
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void visit(Empty empty) {
        System.out.println("Player moves to Empty.");
    }

    public void visit(Wall wall) {
        System.out.println("Player hits Wall. Blocked.");
    }

    public void visit(Player player) {
        System.out.println("Player meets Player. Nothing happens.");
    }

    public void visit(Enemy enemy) {
        System.out.println("Combat: Player fights Enemy!");
    }

    // This method will be called by the game manager to process player's input
    // The actual movement/ability logic will be handled by the game controller based on player input.
    @Override
    public void processTurn(GameBoard board) {
        // Player turn logic will be handled externally by user input
        // This method might be empty or used for passive effects if any
    }

    // Placeholder for special ability, to be implemented by subclasses
    public abstract void castAbility(GameBoard board);
}