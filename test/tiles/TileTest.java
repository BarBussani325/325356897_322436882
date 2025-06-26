package tiles;

import game.GameBoard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import units.Warrior;

import static org.junit.jupiter.api.Assertions.*;

public class TileTest {

    // Simple subclass for testing
    static class TestTile extends Tile {
        public TestTile(char character, int x, int y) {
            super(character, x, y);
        }
        @Override
        public void accept(interfaces.Visitor visitor, game.GameBoard board) {
            // For testing
        }
    }

    private Tile tile;

    @BeforeEach
    void setUp() {
        tile = new TestTile('T', 2, 3);
    }

    @Test
    public void testConstructorAndGetters() {
        assertEquals(2, tile.getX());
        assertEquals(3, tile.getY());
        assertEquals('T', tile.getCharacter());
    }

    @Test
    public void testSetPosition() {
        tile.setPosition(7, 8);
        assertEquals(7, tile.getX());
        assertEquals(8, tile.getY());
    }

    @Test
    public void testToStringReturnsCharacter() {
        assertEquals("T", tile.toString());
    }

    @Test
    public void testEquals() {
        Tile tile2 = new TestTile('T', 2, 3);
        Tile tile3 = new TestTile('X', 2, 3);
        Tile tile4 = new TestTile('T', 5, 6);

        assertEquals(tile.getCharacter(), tile2.getCharacter());
        assertEquals(tile.getX(), tile2.getX());
        assertEquals(tile.getY(), tile2.getY());
    }
}