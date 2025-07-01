package units;

import game.GameBoard;
import interfaces.MessageCallback;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BossTest {

    private Boss boss;
    private Player player;
    private GameBoard board;
    private TestMessageCallback callback;

    @BeforeEach
    public void setUp() {
        callback = new TestMessageCallback();
        board = new GameBoard(callback);

        // קונסטרקטור של Warrior דורש גם x ו־y
        player = new Warrior("Jon Snow", 1000, 150, 100, 5, 5, 5);
        player.setPosition(5, 5);

        // Boss מקבל x, y, visionRange, abilityFrequency
        boss = new Boss('B', "The Mountain", 300, 80, 40, 100, 5, 7, 3, 3);

        board.loadLevel(generateEmptyLevel(10, 10), player);
        board.getEnemies().add(boss);
        board.swapPositions(boss, 5, 7);
    }

    @Test
    public void testCastAbilityWithinRange() {
        boss.castAbility(board);
        assertTrue(callback.messages.stream().anyMatch(m -> m.contains("Shoebodybop")));
    }

    @Test
    public void testCastAbilityOutOfRange() {
        board.swapPositions(boss, 0, 0);
        callback.messages.clear();
        boss.castAbility(board);
        assertTrue(callback.messages.stream().noneMatch(m -> m.contains("Shoebodybop")));
    }

    @Test
    public void testProcessTurnCastsAbilityAtRightTick() {
        board.swapPositions(boss, 5, 8); // distance = 3 (in range)

        boss.ProcessTurn(board); // tick 1
        boss.ProcessTurn(board); // tick 2
        boss.ProcessTurn(board); // tick 3 => should cast

        assertTrue(callback.messages.stream().anyMatch(m -> m.contains("Shoebodybop")));
        assertEquals(0, boss.getCombatTicks());
    }

    @Test
    public void testDescriptionIncludesBossStats() {
        String desc = boss.description();
        assertTrue(desc.contains("The Mountain"));
        assertTrue(desc.contains("Vision Range"));
        assertTrue(desc.contains("Ability Frequency"));
    }

    @Test
    public void testResetCombatTicksWhenOutOfRange() {
        board.swapPositions(boss, 0, 0);
        boss.ProcessTurn(board);
        assertEquals(0, boss.getCombatTicks());
    }

    // Helper for creating a test board
    private List<String> generateEmptyLevel(int rows, int cols) {
        List<String> level = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            StringBuilder row = new StringBuilder();
            for (int j = 0; j < cols; j++) {
                row.append((i == 5 && j == 5) ? '@' : '.');
            }
            level.add(row.toString());
        }
        return level;
    }

    // Basic callback to capture messages
    static class TestMessageCallback implements MessageCallback {
        List<String> messages = new ArrayList<>();
        @Override
        public void send(String message) {
            messages.add(message);
        }
    }
}
