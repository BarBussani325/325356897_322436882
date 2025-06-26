package interfaces;

import game.GameBoard;

public interface Visited {
    void accept(Visitor visitor, GameBoard board);
}
