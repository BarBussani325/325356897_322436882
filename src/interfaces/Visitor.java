package interfaces;

import tiles.Empty;
import tiles.Wall;
import units.Enemy;
import units.Player;

public interface Visitor {
    void visit(Empty empty);
    void visit(Wall wall);
    void visit(Player empty);
    void visit(Enemy wall);
}