package tiles;

import game.GameBoard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import units.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EmptyTest {

    private Empty empty;
    private GameBoard board;

    @BeforeEach
    void setUp() {
        empty = new Empty(3, 4);
        board = new GameBoard(message -> {});
        // Minimal level: 5x5 grid of dots (empty tiles)
        List<String> levelData = new ArrayList<>();
        for (int i = 0; i < 5; i++) levelData.add(".....");
        board.loadLevel(levelData, new Warrior("Hero", 100, 10, 5, 3, 0, 0));
    }

    @Test
    public void testConstructorAndGetters() {
        assertEquals(3, empty.getX());
        assertEquals(4, empty.getY());
    }

    @Test
    public void testSetPosition() {
        empty.setPosition(7, 8);
        assertEquals(7, empty.getX());
        assertEquals(8, empty.getY());
    }

    @Test
    public void testToStringIsDot() {
        assertEquals(".", empty.toString());
    }


    @Test
    public void testAcceptVisitorCallsVisit() {
        // Use a Warrior as a Visitor
        Warrior warrior = new Warrior("Hero", 100, 10, 5, 3, 1, 1);
        assertDoesNotThrow(() -> empty.accept(warrior, board));
    }



}