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

    public void performRandomMovement(GameBoard board) {
        Random random = new Random();
        int direction = random.nextInt(5); // 0-4, where 4 means stay in place

        int newX = this.getX();
        int newY = this.getY();

        switch (direction) {
            case 0: // Move up
                newY = this.getY() - 1;
                break;
            case 1: // Move down
                newY = this.getY() + 1;
                break;
            case 2: // Move left
                newX = this.getX() - 1;
                break;
            case 3: // Move right
                newX = this.getX() + 1;
                break;
            case 4: // Stay in place
                return;
        }

        board.tryMoveUnit(this, newX, newY);
    }


    public void chasePlayer(GameBoard board, Player player) {
        int dx = this.getX() - player.getX();
        int dy = this.getY() - player.getY();

        int newX = this.getX();
        int newY = this.getY();

        if (Math.abs(dx) > Math.abs(dy)) {
            if (dx > 0) {
                newX = this.getX() - 1; // Move left
            } else {
                newX = this.getX() + 1; // Move right
            }
        } else {
            if (dy > 0) {
                newY = this.getY() - 1; // Move up
            } else {
                newY = this.getY() + 1; // Move down
            }
        }

        board.tryMoveUnit(this, newX, newY);
    }



}