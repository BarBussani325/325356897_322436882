package units;

import game.GameBoard;
import game.GameManager;
import game.Position;
import interfaces.MessageCallback;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import tiles.Empty;
import tiles.Wall;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {

    private Warrior player;
    private Monster enemy;
    private GameManager game;
    private GameBoard board;
    private TestMessageCallback callback;

    @BeforeEach
    void setUp() {
        player = new Warrior("Hero", 100, 10, 5, 3, 4, 5);
        enemy = new Monster('M', "Goblin", 50, 10, 2, 7, 5, 5, 5);
        callback = new TestMessageCallback();
        player.setMessageCallback(callback);
        enemy.setMessageCallback(callback);
        game = new GameManager("levels_dir", callback);
        setPlayer(game,player);
        game.loadNextLevel();
        board = game.getBoard();
        board.swapPositions(player, 4, 5);
        board.swapPositions(enemy, 5, 5);
    }

    @Test
    public void testGainExperienceNoLevelUp() {
        player.gainExperience(25);
        assertEquals(25, player.getExperience());
        assertEquals(1, player.getLevel());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 5})
    void testLevelUpIncreasesStats(int level) {
        Warrior player = new Warrior("Test", 100, 10, 5, 3, 0, 0);
        player.level = level;
        int oldAttack = player.getAttackPoints();
        int oldDefense = player.getDefensePoints();
        int oldHealth = player.getHealthPool();

        // Give enough experience to level up
        player.experience = 1000;
        player.levelUp();

        assertTrue(player.level > level , "Level should increase by 1");
        assertTrue(player.getAttackPoints() > oldAttack, "Attack should increase");
        assertTrue(player.getDefensePoints() > oldDefense , "Defense should increase");
        assertTrue(player.getHealthPool() > oldHealth , "Health pool should increase");
        assertTrue(player.getCurrentHealth() > oldHealth , "Health amount should increase");
        assertTrue(player.getExperience() < 1000, "experience should decrease");
    }

    @Test
    public void testDescriptionIncludesLevelAndExperience() {
        String desc = player.description();
        assertTrue(desc.contains("Level: 1"));
        assertTrue(desc.contains("Experience: 0/50"));
    }

    @Test
    public void testOnDeathSetsCharacterAndSendsMessage() {
        player.onDeath(board, enemy);
        assertEquals('X', player.getCharacter());
        assertTrue(callback.messages.stream().anyMatch(m -> m.contains("was killed by")));
    }

    @Test
    public void testVisitEmptySwapsPositions() {
        Empty empty = new Empty(3, 2);
        int oldX = player.getX();
        int oldY = player.getY();
        player.visit(empty, board);
        // After swap, player should be at empty's position
        assertEquals(3, player.getX());
        assertEquals(2, player.getY());
        // In the old position should now be the empty instance
        assertSame(board.getTile(oldX, oldY), empty);
    }

    @Test
    public void testVisitWallDoesNothing() {
        Wall wall = new Wall(3, 2);
        int oldX = player.getX();
        int oldY = player.getY();
        player.visit(wall, board);
        // Player should not move
        assertEquals(oldX, player.getX());
        assertEquals(oldY, player.getY());
    }

    @Test
    public void testVisitPlayerDoesNothing() {
        Warrior other = new Warrior("Other", 100, 10, 5, 3, 3, 2);
        int oldX = player.getX();
        int oldY = player.getY();
        player.visit(other, board);
        // Player should not move
        assertEquals(oldX, player.getX());
        assertEquals(oldY, player.getY());
    }

    @Test
    public void TestProcessTurnRightDirection(){
        List<Character> directions = Arrays.asList('w', 's', 'd', 'q', 'a', 'e','b');
        Random random = new Random();
        String randomDirection = directions.get(random.nextInt(directions.size())) + "";
        int oldX = player.getX();
        int oldY = player.getY();
        player.setInputProvider(() -> randomDirection);
        player.ProcessTurn(board);
        Position position = player.getPosition();

        switch (randomDirection) {
            case "w":
                assertEquals(new Position(oldX, oldY - 1),position);
                break;
            case "s":
                assertEquals(new Position(oldX, oldY + 1),position);
                break;
            case "a":
                assertEquals(new Position(oldX - 1, oldY),position);
                break;
            case "d":
                assertEquals(new Position(oldX + 1, oldY),position);
                break;
            case "e", "q":
                assertEquals(new Position(oldX, oldY),position);
                break;
            default:
                assertTrue(callback.messages.stream().anyMatch(m -> m.contains("Invalid input.")));
        }
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






