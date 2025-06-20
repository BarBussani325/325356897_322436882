package tiles;

import interfaces.Visitor;
import units.Enemy;
import units.Player;
import units.Unit;

public class Empty extends Tile {

    public Empty(int x, int y) {
        super('.', x, y);
    }

    public void accept(Visitor  v){
        v.visit(this);    // Second dispatch -Calling back the receiver,  this time with the
    }
}