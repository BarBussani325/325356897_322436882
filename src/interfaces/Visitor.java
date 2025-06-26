package interfaces;

import tiles.Empty;
import tiles.Wall;
import units.Enemy;
import units.Player;
import game.GameBoard;

public interface Visitor {
    void visit(Empty empty, GameBoard board);
    void visit(Wall wall, GameBoard board);
    void visit(Player player, GameBoard board);
    void visit(Enemy enemy, GameBoard board);
}