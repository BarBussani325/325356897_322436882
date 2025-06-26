package units;

import game.GameBoard;
import interfaces.Visitor;
import tiles.Empty;
import tiles.Wall;

import java.util.List;
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

    @Override
    public String description() {
        return super.description() + String.format("\t\tExperience Value: %d",experienceValue);
    }

    // Visitor pattern implementation for Enemy
    @Override
    public void accept(Visitor visitor, GameBoard board) {
        visitor.visit(this, board);
    }

    @Override
    public void visit(Empty empty, GameBoard board) {
        board.swapPositions(this, empty);
    }

    @Override
    public void visit(Wall wall, GameBoard board) {
        // Enemy is blocked by wall, do nothing
    }

    @Override
    public void visit(Player player, GameBoard board) {
        this.attack(board,player);
        if (!player.isAlive()) {
            player.onDeath(board, this);
        }
    }

    @Override
    public void visit(Enemy enemy, GameBoard board) {
        // Enemy meets another Enemy, do nothing
    }

    public void onDeath(GameBoard board, Unit unit){
        messageCallback.send(String.format("%s died. %s gained %d experience.%n", name, unit.getName(), getExperienceValue()));
        board.removeEnemy(this);
    }



}