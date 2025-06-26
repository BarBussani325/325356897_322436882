package tiles;

import game.GameBoard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import units.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

public class WallTest {

    private Wall wall;
    private GameBoard board;

    @BeforeEach
    void setUp() {
        wall = new Wall(2, 3);
        board = new GameBoard(message -> {});
        // Minimal level: 5x5 grid of dots (empty tiles)
        List<String> levelData = new ArrayList<>();
        for (int i = 0; i < 5; i++) levelData.add(".....");
        board.loadLevel(levelData, new Warrior("Hero", 100, 10, 5, 3, 0, 0));
    }

    @Test
    public void testConstructorAndGetters() {
        assertEquals(2, wall.getX());
        assertEquals(3, wall.getY());
    }

    @Test
    public void testSetPosition() {
        wall.setPosition(7, 8);
        assertEquals(7, wall.getX());
        assertEquals(8, wall.getY());
    }

    @Test
    public void testToStringIsHash() {
        assertEquals("#", wall.toString());
    }

    @Test
    public void testAcceptVisitorCallsVisit() {
        Warrior warrior = new Warrior("Hero", 100, 10, 5, 3, 1, 1);
        // Should not throw
        assertDoesNotThrow(() -> wall.accept(warrior, board));
    }
}