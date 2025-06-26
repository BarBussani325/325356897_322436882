package tiles;

import interfaces.Visitor;
import units.Enemy;
import units.Player;
import units.Unit;
import game.GameBoard;

public class Empty extends Tile {

    public Empty(int x, int y) {
        super('.', x, y);
    }

    public void accept(Visitor v, GameBoard board){
        v.visit(this, board);
    }
}