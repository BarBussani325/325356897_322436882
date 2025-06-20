package units;

import game.GameBoard;


import java.util.List;
import java.util.Random;

public class Mage extends Player /*implements HeroicUnit*/ {
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
        this.currentMana = manaPool;
        this.manaCost = manaCost;
        this.spellPower = spellPower;
        this.hitsCount = hitsCount;
        this.abilityRange = abilityRange;
    }

    @Override
    public void levelUp() {
        super.levelUp();
        this.manaPool += (25 * this.level);
        this.currentMana = Math.min(currentMana + manaPool / 4, manaPool);
        this.spellPower += (10 * this.level);
    }

    @Override
    public void castAbility(GameBoard board) {
        if (currentMana < manaCost) {
            System.out.println(getName() + " cannot cast Blizzard, not enough mana. Current mana: " + currentMana + "/" + manaPool + ".");
            return;
        }

        List<Enemy> enemiesInRange = board.getEnemiesInRange(this.position.x, this.position.y, abilityRange);
        if (enemiesInRange.isEmpty()) {
            System.out.println(getName() + " cannot cast Blizzard, no enemies in range.");
            return;
        }

        currentMana -= manaCost;
        Random random = new Random();
        int actualHits = Math.min(hitsCount, enemiesInRange.size()); // Don't hit more than available enemies

        System.out.println(getName() + " cast Blizzard, hitting " + actualHits + " enemies.");
        for (int i = 0; i < actualHits; i++) {
            Enemy target = enemiesInRange.get(random.nextInt(enemiesInRange.size()));
            target.health.increaseHealth(spellPower);
            System.out.println("  " + target.getName() + " took " + spellPower + " damage.");

            if (!target.health.isAlive()) {
                board.removeUnit(target);
                gainExperience(target.getExperienceValue());
                System.out.println("  " + getName() + " defeated " + target.getName() + " and gained " + target.getExperienceValue() + " experience.");
            }
        }
    }

    @Override
    public void processTurn(GameBoard board) {
        currentMana = Math.min(manaPool, currentMana + (1 * level));
    }

    @Override
    public String description() {
        return super.description() + String.format("\t\tMana: %d/%d\t\tSpell Power: %d\t\tAbility Range: %d",
                currentMana, manaPool, spellPower, abilityRange);
    }
}