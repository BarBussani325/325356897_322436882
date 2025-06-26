package units;

import game.CLI;
import game.GameBoard;
import game.GameManager;
import interfaces.MessageCallback;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tiles.Empty;
import tiles.Wall;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UnitTest {

    private Warrior unit;
    private Monster enemy;
    private GameBoard board;
    private GameManager game;
    private TestMessageCallback callback;

    @BeforeEach
    void setUp() {
        unit = new Warrior("Hero", 100, 10, 5, 3, 2, 2);
        enemy = new Monster('M', "Goblin", 50, 10, 2, 7, 5, 2, 3);
        callback = new TestMessageCallback();
        unit.setMessageCallback(callback);
        enemy.setMessageCallback(callback);
        unit.setInputProvider(() -> "q");
        MessageCallback cli = new CLI();
        game = new GameManager("levels_dir", cli);
        board = game.getBoard();
        setPlayer(game, unit);
        game.loadNextLevel();
        board.swapPositions(enemy, 2, 3);
        board.swapPositions(unit,2,2);
    }



    @Test
    public void testConstructorAndGetters() {
        assertEquals("Hero", unit.getName());
        assertEquals(100, unit.getHealthPool());
        assertEquals(100, unit.getCurrentHealth());
        assertEquals(10, unit.getAttackPoints());
        assertEquals(5, unit.getDefensePoints());
        assertTrue(unit.isAlive());
    }

    @Test
    public void testTakeDamageReducesHealth() {
        unit.takeDamage(20);
        assertEquals(80, unit.getCurrentHealth());
    }

    @Test
    public void testTakeDamageCannotGoBelowZero() {
        unit.takeDamage(200);
        assertEquals(0, unit.getCurrentHealth());
        assertFalse(unit.isAlive());
    }

    @Test
    public void testAttackDealsDamage() {
        int enemyHealthBefore = enemy.getCurrentHealth();
        boolean enemyGotDamage = false;
        int attempts = 20;
        for (int i = 0; i< attempts && !enemyGotDamage; i++){
            unit.attack(board, enemy);
            if(enemy.getCurrentHealth() < enemyHealthBefore)
                enemyGotDamage = true;
        }
        assertTrue(enemyGotDamage);
    }

    @Test
    public void testDescription() {
        String desc = unit.description();
        assertTrue(desc.contains("Hero"));
        assertTrue(desc.contains("Health:"));
        assertTrue(desc.contains("Attack:"));
        assertTrue(desc.contains("Defense:"));
    }

    @Test
    public void testEncounterEmptySwapsPositions() {
        Empty empty = new Empty(4, 2);
        int oldX = unit.getX();
        int oldY = unit.getY();
        unit.encounter(empty, board);
        assertEquals(4, unit.getX());
        assertEquals(2, unit.getY());
        assertSame(board.getTile(oldX, oldY), empty);
    }

    @Test
    public void testEncounterWallDoesNothing() {
        Wall wall = new Wall(4, 2);
        int oldX = unit.getX();
        int oldY = unit.getY();
        unit.encounter(wall, board);
        assertEquals(oldX, unit.getX());
        assertEquals(oldY, unit.getY());
    }

    @Test
    public void testIsAliveAndOnDeath() {
        unit.takeDamage(200);
        assertFalse(unit.isAlive());
    }


    // Simple message callback for capturing output
    public static class TestMessageCallback implements MessageCallback {
        public final List<String> messages = new ArrayList<>();
        public void send(String message) { messages.add(message); }
    }

    private void setPlayer(GameManager gameManager, Unit unit) {
        try {
            java.lang.reflect.Field field = GameManager.class.getDeclaredField("currentPlayer");
            field.setAccessible(true);
            field.set(gameManager, unit);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}