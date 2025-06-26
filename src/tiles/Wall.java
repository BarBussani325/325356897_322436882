package tiles;

import interfaces.Visitor;
import units.Enemy;
import units.Player;
import game.GameBoard;

public class Wall extends Tile {

    public Wall(int x, int y) {
        super('#', x, y);
    }

    public void accept(Visitor v, GameBoard board){
        v.visit(this, board);
    }
}