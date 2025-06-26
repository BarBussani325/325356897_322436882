package units;

import game.Health;
import interfaces.MessageCallback;
import interfaces.Visitor;
import tiles.Empty;
import tiles.Tile;
import game.GameBoard; // Assuming you'll have a GameBoard class
import tiles.Wall;

import java.util.Random;

public abstract class Unit extends Tile implements Visitor {
    protected String name;
    protected Health health;
    protected int attackPoints;
    protected int defensePoints;
    protected MessageCallback messageCallback;

    protected Unit(char character, String name, int healthPool, int attackPoints, int defensePoints, int x, int y) {
        super(character, x, y);
        this.name = name;
        this.health = new Health(healthPool);
        this.attackPoints = attackPoints;
        this.defensePoints = defensePoints;
    }

    public void setMessageCallback(MessageCallback callback) {
        this.messageCallback = callback;
    }

    public String getName() {
        return name;
    }

    public int getHealthPool() {
        return health.getHealthPool();
    }

    public int getCurrentHealth() {
        return health.getHealthAmount();
    }

    public int getAttackPoints() {
        return attackPoints;
    }

    public int getDefensePoints() {
        return defensePoints;
    }

    public boolean isAlive(){return this.health.isAlive();}

    public void attack(GameBoard board, Unit attacked) {
        board.sendMessage(name + " engaged in combat with " +  attacked.name + ".\n");
        board.sendMessage(description() + "\n");
        board.sendMessage(attacked.description()+ "\n");
        Random random = new Random();
        int attackRoll = random.nextInt(attackPoints + 1);
        int defenseRoll = random.nextInt(attacked.defensePoints + 1);
        int damage = Math.max(0, attackRoll - defenseRoll);
        attacked.takeDamage(damage);
        messageCallback.send(String.format("%s rolled %d attack points.\n%s rolled %d defence points.\n%s dealt %d damage to %s.\n",
                name ,attackRoll, attacked.getName(), defenseRoll, name, damage, attacked.getName()));
    }

    public String description(){
        return String.format("%s\t\tHealth: %d/%d\t\tAttack: %d\t\tDefense: %d",
                name, health.getHealthAmount(), health.getHealthPool(), attackPoints, defensePoints);
    }

    public void encounter(Tile tile, GameBoard board){
        tile.accept(this, board);
    }

    public void takeDamage(int damage){
        this.health.decreaseHealth(damage);
    }

    public abstract void visit(Empty empty, GameBoard board);
    public abstract void visit(Wall wall, GameBoard board);
    public abstract void visit(Player player, GameBoard board);
    public abstract void visit(Enemy enemy, GameBoard board);

    public abstract void ProcessTurn(GameBoard board);

    public abstract void onDeath(GameBoard board, Unit unit);
}