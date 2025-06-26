package units;

import game.CLI;
import game.GameBoard;
import game.GameManager;
import interfaces.MessageCallback;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TrapTest {

    private Trap trap;
    private Player player;
    private GameManager game;
    private GameBoard board;
    private TestMessageCallback callback;

    @BeforeEach
    void setUp() {
        trap = new Trap('T', "Pitfall", 50, 10, 2, 15, 3, 2, 5, 5);
        player = new Warrior("Hero", 100, 10, 5, 3, 2, 2);
        callback = new TestMessageCallback();
        trap.setMessageCallback(callback);
        player.setMessageCallback(callback);
        game = new GameManager("levels_dir", callback);
        //game.setPlayer(player);
        setPlayer(game, player);
        game.loadNextLevel();
        board = game.getBoard();
        board.swapPositions(trap, 5, 5);
        board.swapPositions(player, 5, 4); // Place player adjacent to trap
    }

    @Test
    public void testInitialVisibility() {
        assertTrue(getVisible(trap), "Trap should be visible at start");
    }

    @Test
    public void testVisibilitySwitchesAfterVisibilityTime() {
        for (int i = 0; i <= trapVisibilityTime(trap); i++) {
            trap.ProcessTurn(board);
        }
        assertFalse(getVisible(trap), "Trap should be invisible after visibilityTime ticks");
    }

    @Test
    public void testVisibilityCyclesBackToVisible() {
        int totalTicks = trapVisibilityTime(trap) + trapInvisibilityTime(trap);
        for (int i = 0; i <= totalTicks + 1; i++) {
            trap.ProcessTurn(board);
        }
        assertTrue(getVisible(trap), "Trap should be visible again after full cycle");
    }

    @Test
    public void testTicksCountResetsAfterFullCycle() {
        int totalTicks = trapVisibilityTime(trap) + trapInvisibilityTime(trap);
        for (int i = 0; i <= totalTicks; i++) {
            trap.ProcessTurn(board);
        }
        assertEquals(0, getTicksCount(trap), "Ticks count should reset after full cycle");
    }

    @Test
    public void testTrapAttacksPlayerIfInRange() {
        int playerHealthBefore = player.getCurrentHealth();
        boolean playerGotDamage = false;
        assertTrue(board.getDistance(player, trap) < 2);
        for(int i = 0; i <20 && !playerGotDamage; i++){
            trap.ProcessTurn(board);
            if(player.getCurrentHealth() < playerHealthBefore)
                playerGotDamage = true;
        }
        assertTrue(playerGotDamage, "Trap should attack player if in range");
        //the probability that the player will have higher defense points then the Trap's attack points
        // - 20 times in a row, is very low.
    }

    @Test
    public void testTrapDoesNotAttackPlayerIfOutOfRange() {
        board.swapPositions(player, trap.getX() + 2, trap.getX() + 2);
        int playerHealthBefore = player.getCurrentHealth();
        trap.ProcessTurn(board);
        assertEquals(playerHealthBefore, player.getCurrentHealth(), "Trap should not attack player if out of range");
    }

    @Test
    public void testDescriptionIncludesVisibilityAndInvisibility() {
        String desc = trap.description();
        assertTrue(desc.contains("Visibility Time: " + trapVisibilityTime(trap)));
        assertTrue(desc.contains("Invisibility Time: " + trapInvisibilityTime(trap)));
    }

    // Helper to access private fields via reflection
    private boolean getVisible(Trap trap) {
        try {
            java.lang.reflect.Field field = Trap.class.getDeclaredField("visible");
            field.setAccessible(true);
            return field.getBoolean(trap);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private int getTicksCount(Trap trap) {
        try {
            java.lang.reflect.Field field = Trap.class.getDeclaredField("ticksCount");
            field.setAccessible(true);
            return field.getInt(trap);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private int trapVisibilityTime(Trap trap) {
        try {
            java.lang.reflect.Field field = Trap.class.getDeclaredField("visibilityTime");
            field.setAccessible(true);
            return field.getInt(trap);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private int trapInvisibilityTime(Trap trap) {
        try {
            java.lang.reflect.Field field = Trap.class.getDeclaredField("invisibilityTime");
            field.setAccessible(true);
            return field.getInt(trap);
        } catch (Exception e) {
            throw new RuntimeException(e);
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