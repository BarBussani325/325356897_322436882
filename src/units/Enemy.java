package units;

import game.GameBoard;
import interfaces.Visitor;
import tiles.Empty;
import tiles.Wall;

import java.util.Random;

public abstract class Enemy extends Unit {
    protected int experienceValue;

    public Enemy(char character, String name, int healthPool, int attackPoints, int defensePoints, int experienceValue, int x, int y) {
        super(character, name, healthPool, attackPoints, defensePoints, x, y);
        this.experienceValue = experienceValue;
    }

    public int getExperienceValue() {
        return experienceValue;
    }

//    // Combat logic for enemy attacking a player
//    public void attack(Player player) {
//        Random random = new Random();
//        int attackRoll = random.nextInt(attackPoints + 1);
//        int defenseRoll = random.nextInt(player.getDefensePoints() + 1);
//        int damage = Math.max(0, attackRoll - defenseRoll);
//        player.health.decreaseHealth(damage);
//        System.out.println(getName() + " attacked " + player.getName() + ". Attack roll: " + attackRoll + ", " + player.getName() + "'s defense roll: " + defenseRoll + ", Damage dealt: " + damage + ".");
//        // Notify UI about combat details
//    }

    @Override
    public String description() {
        return String.format("%s\t\tHealth: %d/%d\t\tAttack: %d\t\tDefense: %d\t\tExperience Value: %d",
                name,health.getHealthAmount(), health.getHealthPool(), attackPoints, defensePoints, experienceValue);
    }

    // Visitor pattern implementation for Enemy
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void visit(Empty empty) {
        System.out.println("Enemy moves to Empty.");
    }

    public void visit(Wall wall) {
        System.out.println("Enemy hits Wall. Blocked.");
    }

    public void visit(Player player) {
        System.out.println("Combat: Enemy fights Player!");
    }

    public void visit(Enemy enemy) {
        System.out.println("Enemy meets Enemy. Nothing happens.");
    }


    // Abstract method for enemy movement logic
    @Override
    public abstract void processTurn(GameBoard board);
}