package units;
import game.GameBoard;
import interfaces.HeroicUnit;
import interfaces.InputProvider;
import interfaces.Visitor;
import tiles.Empty;
import tiles.Wall;
import java.util.List;
import java.util.Random;

public abstract class Player extends Unit {
    protected int experience;
    protected int level;
    protected int maxExperienceForLevel;
    protected InputProvider inputProvider;


    public Player(String name, int healthPool, int attackPoints, int defensePoints, int x, int y) {
        super('@', name, healthPool, attackPoints, defensePoints, x, y);
        this.experience = 0;
        this.level = 1;
        this.maxExperienceForLevel = 50;
    }

    public void setInputProvider(InputProvider provider) {
        this.inputProvider = provider;
    }

    public int getLevel(){ return level; }

    public int getExperience(){ return experience;}

    public void gainExperience(int addition){
        this.experience += addition;
    }

    protected String levelUp() {
        String levelUpMessage = "";
        int oldHealth = this.getHealthPool();
        int oldAttack = this.getAttackPoints();
        int oldDefense = this.getDefensePoints();
        while (this.experience >= maxExperienceForLevel * this.level) {
            this.experience -= (maxExperienceForLevel * this.level);
            this.level++;
            this.health.setHealthPool(this.health.getHealthPool() + 10 * this.level);
            this.health.increaseHealth(this.health.getHealthPool());
            this.attackPoints += (4 * this.level);
            this.defensePoints += this.level;
            levelUpMessage += String.format("%s reached level %d: ", name, level);
            String bonusAddition = applyLevelUpBonuses();
            levelUpMessage += String.format("+%d Health, +%d Attack, +%d Defense\n",
                    getHealthPool() - oldHealth, getAttackPoints() - oldAttack, getDefensePoints() - oldDefense);
            levelUpMessage += bonusAddition;
        }
        return levelUpMessage;
    }

    public abstract String applyLevelUpBonuses();

    @Override
    public String description() {
        return super.description() + String.format("\t\tLevel: %d\t\tExperience: %d/%d",
                 level, experience, (maxExperienceForLevel * level));
    }

    // Visitor pattern implementation for Player
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
        // Player is blocked by a wall, do nothing.
    }

    @Override
    public void visit(Player player, GameBoard board) {
        // Player meets another Player, do nothing.
    }

    @Override
    public void visit(Enemy enemy, GameBoard board) {
        this.attack(board, enemy);
        if (!enemy.isAlive()) {
            enemy.onDeath(board,this);
            this.experience += enemy.getExperienceValue();
            String levelUpResult = levelUp();
            if (!levelUpResult.isEmpty()) {
                board.sendMessage(levelUpResult);
            }
        }
    }

    public void onDeath(GameBoard board, Unit unit){
        board.sendMessage(name +" was killed by " + unit.getName() +".\nYou lost.\n" );
        this.character = 'X';
    }

    public void castAbility(GameBoard board, List<Enemy> selectedEnemies, int attackPoints){
        for (Enemy target: selectedEnemies){
            Random random = new Random();
            int defensePoints = random.nextInt(target.defensePoints + 1);
            int damage = Math.max(0, attackPoints - defensePoints);
            target.health.decreaseHealth(damage);
            messageCallback.send(String.format("%s rolled %d defense points.%n%s hit %s for %d ability damage.\n",
                    target.getName(), defensePoints, name, target.getName(), damage));
            // Check if enemy died
            if (!target.isAlive()) {
                board.removeEnemy(target); // Remove enemy from board
                messageCallback.send(target.getName() + " died. " + name + " gained " + target.getExperienceValue() + " experience\n" );
                this.experience += target.getExperienceValue();
                String levelUpResult = levelUp();
                if (!levelUpResult.isEmpty()) {
                    messageCallback.send(levelUpResult);
                }
            }
        }
    }

    public abstract void OnCastAbility(GameBoard board);

    @Override
    public void ProcessTurn(GameBoard board) {
        String input = inputProvider.getInput();
        switch (input) {
            case "w": // Move up
                board.tryMoveUnit(this, this.getX(), this.getY() - 1);
                break;
            case "s": // Move down
                board.tryMoveUnit(this, this.getX(), this.getY() + 1);
                break;
            case "a": // Move left
                board.tryMoveUnit(this, this.getX() - 1, this.getY());
                break;
            case "d": // Move right
                board.tryMoveUnit(this, this.getX() + 1, this.getY());
                break;
            case "e": // Cast ability
                OnCastAbility(board);
                break;
            case "q": // Do nothing
                break;
            default:
                if(messageCallback != null)
                    messageCallback.send("Invalid input. Please use w, a, s, d, e, or q.\n");
        }
    }
}