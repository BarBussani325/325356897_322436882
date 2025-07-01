package units;

import game.GameBoard;
import interfaces.HeroicUnit;
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

            board.sendMessage(message);

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
                chasePlayer(board, player);
            }
        } else {
            combatTicks = 0;
            performRandomMovement(board);
        }
    }


    private void chasePlayer(GameBoard board, Player player) {
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

    private void performRandomMovement(GameBoard board) {
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

    public int getVisionRange() {
        return visionRange;
    }

    public int getAbilityFrequency() {
        return abilityFrequency;
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