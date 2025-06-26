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

public class EnemyTest {

    private Monster enemy;
    private Player player;
    private GameManager game;
    private GameBoard board;
    private TestMessageCallback callback;

    @BeforeEach
    void setUp() {
        // Monster(char character, String name, int healthPool, int attackPoints, int defensePoints, int experienceValue, int visionRange, int x, int y)
        enemy = new Monster('M', "Goblin", 50, 10, 2, 7, 5, 5, 5);
        player = new Warrior("Hero", 100, 10, 5, 3, 2, 2);
        callback = new TestMessageCallback();
        enemy.setMessageCallback(callback);
        player.setMessageCallback(callback);
        game = new GameManager("levels_dir", callback);
        setPlayer(game,player);
        game.loadNextLevel();
        board = game.getBoard();
        board.swapPositions(enemy, 5, 5);
        board.swapPositions(player, 5, 4);
    }

    @Test
    public void testGetExperienceValue() {
        assertEquals(7, enemy.getExperienceValue());
    }

    @Test
    public void testDescriptionIncludesExperienceValue() {
        String desc = enemy.description();
        assertTrue(desc.contains("Experience Value: 7"));
    }

    @Test
    public void testVisitEmptySwapsPositions() {
        Empty empty = new Empty(6, 5);
        int oldX = enemy.getX();
        int oldY = enemy.getY();
        enemy.visit(empty, board);
        // After swap, enemy should be at empty's position
        assertEquals(6, enemy.getX());
        assertEquals(5, enemy.getY());
        // The old position should now be Empty
        assertTrue(board.getTile(oldX, oldY) == empty);
    }

    @Test
    public void testVisitWallDoesNothing() {
        Wall wall = new Wall(6, 5);
        int oldX = enemy.getX();
        int oldY = enemy.getY();
        enemy.visit(wall, board);
        // Enemy should not move
        assertEquals(oldX, enemy.getX());
        assertEquals(oldY, enemy.getY());
    }

    @Test
    public void testVisitEnemyDoesNothing() {
        Monster other = new Monster('M', "Orc", 40, 8, 1, 5, 5, 6, 5);
        int oldX = enemy.getX();
        int oldY = enemy.getY();
        enemy.visit(other, board);
        // Enemy should not move
        assertEquals(oldX, enemy.getX());
        assertEquals(oldY, enemy.getY());
    }

    @Test
    public void testVisitPlayerAttacksAndCanKill() {
        // Lower player health so enemy can kill in one hit
        player.health.SetHealthAmount(1);
        for (int i = 0; i< 20 && player.isAlive(); i++)
        {
            enemy.visit(player, board);
        }
        assertFalse(player.isAlive());
        // Check that onDeath was called (message sent)
        assertTrue(callback.messages.stream().anyMatch(m -> m.contains("was killed by")));
    }

    @Test
    public void testOnDeathSendsMessageAndRemovesEnemy() {
        enemy.onDeath(board, player);
        assertTrue(callback.messages.stream().anyMatch(m -> m.contains("Goblin died. Hero gained 7 experience")));
        assertFalse(board.getEnemies().contains(enemy));
    }

    // Simple message callback for capturing output
    public static class TestMessageCallback implements MessageCallback {
        public final List<String> messages = new ArrayList<>();
        public void send(String message) { messages.add(message); }
    }

    private void setPlayer(GameManager gameManager, Player player) {
        try {
            java.lang.reflect.Field field = GameManager.class.getDeclaredField("currentPlayer");
            field.setAccessible(true);
            field.set(gameManager, player);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}