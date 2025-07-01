package units;

import game.GameBoard;

import units.Enemy;
import units.Player;

import java.util.List;

public class Rogue extends Player {
    private int cost; // Energy cost for Fan of Knives
    private int currentEnergy;

    public Rogue(String name, int healthPool, int attackPoints, int defensePoints, int cost, int x, int y) {
        super(name, healthPool, attackPoints, defensePoints, x, y);
        this.cost = cost;
        this.currentEnergy = 100; // Starting energy is 100
    }

    @Override
    public String applyLevelUpBonuses() {
        this.currentEnergy = 100; // Reset energy on level up
        this.attackPoints += (3 * this.level);
        return "";
    }

    public void OnCastAbility(GameBoard board) {
        if (currentEnergy < cost) {
            if(messageCallback != null) messageCallback.send(getName() + " tried to cast Fan of Knives, but there was not enough energy: " + currentEnergy + ".\n");
            return;
        }

        List<Enemy> enemiesInRange = board.getEnemiesInRange(this, 2); // Range < 2 (so 0, 1)
        if (enemiesInRange.isEmpty()) {
            if(messageCallback != null) messageCallback.send(getName() + " tried to cast Fan of Knives, but there were no enemies in range.\n");
            return;
        }
        
        currentEnergy -= cost;
        if(messageCallback != null)
            messageCallback.send(getName() + " cast Fan of Knives.\n");

        super.castAbility(board, enemiesInRange, attackPoints);
    }

    @Override
    public void ProcessTurn(GameBoard board) {
        super.ProcessTurn(board);
        currentEnergy = Math.min(currentEnergy + 10, 100);
    }

    @Override
    public String description() {
        return super.description() + String.format("\t\tEnergy: %d/%d",
                currentEnergy, 100);
    }
}