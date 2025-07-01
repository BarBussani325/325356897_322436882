package units;

import game.GameBoard;
import interfaces.HeroicUnit;
import interfaces.MessageCallback;
import tiles.Empty;
import tiles.Wall;

import java.util.Random;

public class Boss extends Enemy implements HeroicUnit {
    private int visionRange;
    private int abilityFrequency;
    private int combatTicks;

    public Boss(char character, String name, int healthPool, int attackPoints, int defensePoints,
                int experienceValue, int x, int y, int visionRange, int abilityFrequency) {
        super(character, name, healthPool, attackPoints, defensePoints, experienceValue, x, y);
        this.visionRange = visionRange;
        this.abilityFrequency = abilityFrequency;
        this.combatTicks = 0;
    }

    @Override
    public void castAbility(GameBoard board) {
        Player player = board.getPlayer();
        double distance = board.getDistance(this, player);

        if (distance <= visionRange) {
            Random random = new Random();
            int defenseRoll = random.nextInt(player.getDefensePoints() + 1);
            int damage = Math.max(0, this.attackPoints - defenseRoll);

            player.takeDamage(damage);

            String message = String.format("%s casts Shoebodybop on %s!\n",
                    this.getName(), player.getName());
            message += String.format("%s rolled %d defense points.\n",
                    player.getName(), defenseRoll);
            message += String.format("%s hit %s for %d ability damage.\n",
                    this.getName(), player.getName(), damage);

            messageCallback.send(message);

            if (!player.isAlive()) {
                player.onDeath(board, this);
            }
        }
    }
    @Override
    public void ProcessTurn(GameBoard board) {
        Player player = board.getPlayer();
        double distance = board.getDistance(this, player);

        if (distance <= visionRange) {
            if (combatTicks + 1 >= abilityFrequency) {
                combatTicks = 0;
                castAbility(board);
            } else {
                combatTicks++;
                super.chasePlayer(board, player);
            }
        } else {
            combatTicks = 0;
            super.performRandomMovement(board);
        }
    }

    public int getCombatTicks() {
        return combatTicks;
    }

    @Override
    public String description() {
        return super.description() + String.format("\t\tVision Range: %d\t\tAbility Frequency: %d\t\tCombat Ticks: %d",
                visionRange, abilityFrequency, combatTicks);
    }
}