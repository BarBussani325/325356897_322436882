package tiles; // Recommended package structure based on your screenshots

import game.Position;
import interfaces.Visited;
import interfaces.Visitor;
import game.GameBoard;


public abstract class Tile implements Visited{
    protected char character; // To store the character representation of the tile
    protected Position position;

    public Tile(char character, int x, int y) {
        this.character = character;
        this.position = new Position(x,y);
    }

    // Getters for coordinates and character
    public char getCharacter() {
        return character;
    }

    public int getX() {
        return position.x;
    }

    public int getY() {
        return position.y;
    }

    public void setPosition(int x, int y) {
        position.setX(x);
        position.setY(y);
    }

    public Position getPosition() {
        return this.position;
    }

    public abstract void accept(Visitor visitor, GameBoard board);

    @Override
    public String toString() {
        return String.valueOf(character); // Returns the tile character as required
    }
}