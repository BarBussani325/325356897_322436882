package units;

import game.CLI;
import game.GameBoard;
import game.GameManager;
import interfaces.MessageCallback;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HunterTest {

    private Hunter hunter;
    private List<Enemy> enemies;
    private GameManager game;
    private GameBoard board;
    private TestMessageCallback callback;
    private Enemy closestEnemy;

    @BeforeEach
    void setUp() {
        hunter = new Hunter("Ygritte", 220, 30, 2, 2, 0, 0);
        callback = new TestMessageCallback();
        game = new GameManager("levels_dir", callback);
        board = game.getBoard();
        hunter.setMessageCallback(callback);
        setPlayer(game, hunter);
        game.loadNextLevel();
        enemies = board.getEnemies();
        setEnemiesPosition();
    }

    void setEnemiesPosition() {
        if (!enemies.isEmpty()) {
            closestEnemy = enemies.get(0);
            closestEnemy.setPosition(hunter.getX() + 1, hunter.getY()); // Place right next to hunter
            board.swapPositions(closestEnemy, hunter.getX() + 1, hunter.getY());
            setDefensePoints(closestEnemy, 1);
        }
    }

    @Test
    public void testGainExperienceNoLevelUp() {
        hunter.gainExperience(25);
        assertEquals(25, hunter.getExperience());
        assertEquals(1, hunter.getLevel());
    }

    @Test
    public void testGainExperienceAndLevelUp() {
        hunter.gainExperience(50);
        hunter.levelUp();
        assertEquals(2, hunter.getLevel());
        assertTrue(hunter.getAttackPoints() > 34);
        assertTrue(hunter.getDefensePoints() > 2);
        assertTrue(hunter.getHealthPool() > 220);
        assertTrue(hunter.getCurrentHealth() > 0);
        assertEquals(0, hunter.getExperience());
        assertTrue(hunter.getArrowsCount() > 20);
    }

    @Test
    public void testInitialArrowsCount() {
        assertEquals(10 * hunter.getLevel(), getArrowsCount(hunter));
    }

    @Test
    public void testArrowRegenerationEvery10Turns() {
        int initialArrows = getArrowsCount(hunter);
        hunter.setInputProvider(new TestInputProvider("q")); // Do nothing
        for (int i = 0; i <= 10; i++) {
            hunter.ProcessTurn(board);
        }
        assertEquals(initialArrows + hunter.getLevel(), getArrowsCount(hunter));
    }

    @Test
    public void testArrowRegenerationEveryAbilityCast() {
        int initialArrows = getArrowsCount(hunter);
        hunter.setInputProvider(new TestInputProvider("e")); // Do nothing
        hunter.OnCastAbility(board);
        // In setUp we made sure there is a close enemy
        assertEquals(initialArrows - 1 , getArrowsCount(hunter));
    }

    @Test
    public void testDescription() {
        String desc = hunter.description();
        assertTrue(desc.contains("Range: "));
        assertTrue(desc.contains("Arrows: "));
    }

    @Test
    public void testCannotCastAbilityWithNoArrows() {
        setArrowsCount(hunter, 0);
        hunter.OnCastAbility(board);
        // Should not decrease arrows below 0
        assertEquals(0, getArrowsCount(hunter));
        assertTrue(callback.messages.stream().anyMatch(m -> m.contains("no arrows left")));
    }

    @Test
    public void testCastAbilityDamagesClosestEnemy() {
        setArrowsCount(hunter, 10);
        int enemyHealthBefore = closestEnemy.getCurrentHealth();
        hunter.OnCastAbility(board);
        assertEquals(9, getArrowsCount(hunter));
        assertTrue(closestEnemy.getCurrentHealth() < enemyHealthBefore);
    }

    @Test
    public void testCastAbilityNoEnemiesInRange() {
        // Move all enemies out of range
        for (Enemy enemy : enemies) {
            enemy.setPosition(hunter.getX() + 2 , hunter.getY() + 2); // Assuming board is at least 19x19
        }
        int arrowsBefore = getArrowsCount(hunter);
        hunter.OnCastAbility(board);
        assertEquals(arrowsBefore, getArrowsCount(hunter));
        assertTrue(callback.messages.stream().anyMatch(m -> m.contains("no enemies in range")));
    }

    // Helper methods to access private arrowsCount (if needed, use reflection or add a getter in Hunter)
    private int getArrowsCount(Hunter hunter) {
        try {
            java.lang.reflect.Field field = Hunter.class.getDeclaredField("arrowsCount");
            field.setAccessible(true);
            return field.getInt(hunter);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setArrowsCount(Hunter hunter, int value) {
        try {
            java.lang.reflect.Field field = Hunter.class.getDeclaredField("arrowsCount");
            field.setAccessible(true);
            field.setInt(hunter, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Simple message callback for capturing output
    public static class TestMessageCallback implements MessageCallback {
        public final java.util.List<String> messages = new java.util.ArrayList<>();
        public void send(String message) { messages.add(message); }
    }

    private void setPlayer(GameManager gameManager, Hunter hunter) {
        try {
            java.lang.reflect.Field field = GameManager.class.getDeclaredField("currentPlayer");
            field.setAccessible(true);
            field.set(gameManager, hunter);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setDefensePoints(Enemy enemy, int value) {
        try {
            java.lang.reflect.Field field = Unit.class.getDeclaredField("defensePoints");
            field.setAccessible(true);
            field.setInt(enemy, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}