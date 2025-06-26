package units;

import game.GameBoard;
import units.Enemy;
import units.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Warrior extends Player{
    private int abilityCooldown;
    private int remainingCooldown;

    public Warrior(String name, int healthPool, int attackPoints, int defensePoints, int abilityCooldown, int x, int y) {
        super(name, healthPool, attackPoints, defensePoints, x, y);
        this.abilityCooldown = abilityCooldown;
        this.remainingCooldown = 0;
    }
    public int getRemainingCooldown(){ return remainingCooldown; }

    @Override
    public String applyLevelUpBonuses() {
        this.remainingCooldown = 0;
        this.health.setHealthPool(this.health.getHealthPool() + 5 * this.level);
        this.attackPoints += (2 * this.level);
        this.defensePoints += this.level;
        return "";
    }

    public void OnCastAbility(GameBoard board) {
        if (remainingCooldown > 0) {
            if (messageCallback != null) messageCallback.send(getName() + " tried to cast Avenger's Shield, but there is a cooldown: " + remainingCooldown + ".\n");
            return;
        }
        remainingCooldown = abilityCooldown;
        int oldHealth = getCurrentHealth();
        health.SetHealthAmount(Math.min(getCurrentHealth() + (10 * defensePoints), getHealthPool()));
        board.sendMessage(name + " used Avenger's Shield, healing for " + (getCurrentHealth() - oldHealth) +".\n");

        // Randomly hits one enemy within range < 3
        List<Enemy> enemiesInRange = board.getEnemiesInRange(this, 3); // Range < 3 (so 0, 1, 2)
        if (enemiesInRange.isEmpty()) { return; }
        Random random = new Random();
        Enemy target = enemiesInRange.get(random.nextInt(enemiesInRange.size()));
        List<Enemy> targetList = Arrays.asList(target);
        int damage = (int) (this.health.getHealthPool() * 0.1); // 10% of max health
        super.castAbility(board, targetList, damage);
    }

    @Override
    public void ProcessTurn(GameBoard board) {
        super.ProcessTurn(board);
        if (remainingCooldown > 0) {
            remainingCooldown--;
        }
    }

    @Override
    public String description() {
        return super.description() + String.format("\t\tCooldown: %d/%d", remainingCooldown, abilityCooldown);
    }

    public void setRemainingCooldown(int remainingCooldown){
        this.remainingCooldown = remainingCooldown;
    }
}