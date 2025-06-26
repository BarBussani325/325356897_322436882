package units;

import game.GameBoard;


import java.util.List;
import java.util.Random;

public class Mage extends Player {
    private int manaPool;
    private int currentMana;
    private int manaCost;
    private int spellPower;
    private int hitsCount;
    private int abilityRange;

    public Mage(String name, int healthPool, int attackPoints, int defensePoints,
                int manaPool, int manaCost, int spellPower, int hitsCount, int abilityRange, int x, int y) {
        super(name, healthPool, attackPoints, defensePoints, x, y);
        this.manaPool = manaPool;
        this.currentMana = manaPool/4;
        this.manaCost = manaCost;
        this.spellPower = spellPower;
        this.hitsCount = hitsCount;
        this.abilityRange = abilityRange;
    }

    @Override
    public String applyLevelUpBonuses() {
        this.manaPool += (25 * this.level);
        this.currentMana = Math.min(currentMana + manaPool / 4, manaPool);
        this.spellPower += (10 * this.level);
        return String.format(" +%d maximum mana, +%d spell power.\n", (25*this.level), (10*this.level));
    }

    @Override
    public void OnCastAbility(GameBoard board) {
        if (currentMana < manaCost) {
            if (messageCallback != null)
                messageCallback.send(getName() + " tried to cast Blizzard, but there was not enough mana: " + currentMana + "/" + manaPool + ".\n");
            return;
        }

        List<Enemy> enemiesInRange = board.getEnemiesInRange(this, abilityRange);
        if (enemiesInRange.isEmpty()) {
            if (messageCallback != null)
                messageCallback.send(getName() + " tried to cast Blizzard, but there were no enemies in range.\n");
            return;
        }

        currentMana -= manaCost;
        messageCallback.send(getName() + " cast Blizzard.\n");

        int hits = 0;
        while(hits < hitsCount && !board.getEnemiesInRange(this, abilityRange).isEmpty()){
            List<Enemy> currentTargets = board.getEnemiesInRange(this, abilityRange);
            Random random = new Random();
            Enemy target = currentTargets.get(random.nextInt(currentTargets.size()));
            super.castAbility(board, List.of(target), this.spellPower);
            hits++;
        }
    }

    @Override
    public void ProcessTurn(GameBoard board) {
        super.ProcessTurn(board);
        currentMana = Math.min(manaPool, currentMana + level);
    }

    @Override
    public String description() {
        return super.description() + String.format("\t\tMana: %d/%d\t\tSpell Power: %d",
                currentMana, manaPool, spellPower);
    }

    public int getManaPool(){ return this.manaPool; }
}