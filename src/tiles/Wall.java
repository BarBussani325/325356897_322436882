package tiles;

import interfaces.Visitor;
import units.Enemy;
import units.Player;

public class Wall extends Tile {

    public Wall(int x, int y) {
        super('#', x, y);
    }

    public void accept(Visitor v){
        v.visit(this);
    }
}