package units;

import game.Health;
import interfaces.Visitor;
import tiles.Empty;
import tiles.Tile;
import game.GameBoard; // Assuming you'll have a GameBoard class
import tiles.Wall;

public abstract class Unit extends Tile implements Visitor {
    protected String name;
    protected Health health;
    protected int attackPoints;
    protected int defensePoints;

    protected Unit(char character, String name, int healthPool, int attackPoints, int defensePoints, int x, int y) {
        super(character, x, y);
        this.name = name;
        this.health = new Health(healthPool);
        this.attackPoints = attackPoints;
        this.defensePoints = defensePoints;
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

    public abstract String description();


    public void encounter(Tile tile){
        tile.accept(this);
    }

    public abstract void visit(Empty empty);
    public abstract void visit(Wall wall);
    public abstract void visit(Player player);
    public abstract void visit(Enemy enemy);


//    // Visitor pattern methods for interaction
//    public abstract void accept(Unit unit); // For unit attacking unit
//    public abstract void accept(Player player); // For unit interacting with player
//    public abstract void accept(Enemy enemy); // For unit interacting with enemy
//    public abstract void accept(Empty empty); // For unit moving to empty tile
//    public abstract void accept(Wall wall); // For unit trying to move into wall

    // Abstract method for unit turn logic (movement/abilities)
    public abstract void processTurn(GameBoard board);
}