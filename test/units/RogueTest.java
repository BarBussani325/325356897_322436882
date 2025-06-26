package units;

import game.CLI;
import game.GameBoard;
import game.GameManager;
import interfaces.MessageCallback;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RogueTest {

    private Rogue rogue;
    private List<Enemy> enemies;
    private GameManager game;
    private GameBoard board;
    private Enemy enemyInRange;

    @BeforeEach
    void setUp() {
        rogue = new Rogue("Arya", 120, 18, 4, 20, 0, 0); // name, health, attack, defense, cost, x, y
        MessageCallback cli = new CLI();
        game = new GameManager("levels_dir", cli);
        board = game.getBoard();
        setPlayer(game, rogue);
        game.loadNextLevel();
        enemies = board.getEnemies();
        setEnemiesPosition();
    }

    void setEnemiesPosition() {
        if (!enemies.isEmpty()) {
            enemyInRange = enemies.get(0);
            board.swapPositions(enemyInRange, rogue.getX() + 1, rogue.getY());
        }
    }

    @Test
    public void testGainExperienceNoLevelUp() {
        rogue.gainExperience(25);
        assertEquals(25, rogue.getExperience());
        assertEquals(1, rogue.getLevel());
    }

    @Test
    void testGainExperienceSingleLevelUp() {
        rogue.gainExperience(50);
        rogue.levelUp();

        assertTrue(rogue.getLevel() == 2);
        assertTrue(rogue.getAttackPoints() > 18);
        assertTrue(rogue.getDefensePoints() > 4);
        assertTrue(rogue.getHealthPool() > 120);
        assertTrue(rogue.getCurrentHealth() > 0);
        assertEquals(0, rogue.getExperience());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 5})
    void testLevelUpMultipleLevelUp(int level) {
        rogue.level = level;
        int oldAttack = rogue.getAttackPoints();
        int oldDefense = rogue.getDefensePoints();
        int oldHealth = rogue.getHealthPool();

        // Give enough experience to level up
        rogue.experience = 1000;
        rogue.levelUp();

        assertTrue(rogue.level > level , "Level should increase by 1");
        assertTrue(rogue.getAttackPoints() > oldAttack, "Attack should increase");
        assertTrue(rogue.getDefensePoints() > oldDefense , "Defense should increase");
        assertTrue(rogue.getHealthPool() > oldHealth , "Health pool should increase");
        assertTrue(rogue.getCurrentHealth() > oldHealth , "Health amount should increase");
        assertTrue(rogue.getExperience() < 1000, "experience should decrease");
        assertEquals(100, getCurrentEnergy(rogue), "energy should be set to 100");
    }

    @Test
    public void testInitialEnergy() {
        assertEquals(100, getCurrentEnergy(rogue));
    }

    @Test
    public void testEnergyRegenerationEachTurn() {
        setCurrentEnergy(rogue, 50);
        rogue.setInputProvider(new TestInputProvider("q"));
        rogue.ProcessTurn(board);
        assertEquals(60, getCurrentEnergy(rogue));
    }

    @Test
    public void testCannotCastAbilityWithNotEnoughEnergy() {
        setCurrentEnergy(rogue, 0);
        rogue.OnCastAbility(board);
        assertEquals(0, getCurrentEnergy(rogue));
    }

    @Test
    public void testCastAbilityDamagesEnemiesInRange() {
        setCurrentEnergy(rogue, 100); // Ensure enough energy
        int enemyHealthBefore = enemyInRange.getCurrentHealth();
        int energyBefore = getCurrentEnergy(rogue);
        rogue.OnCastAbility(board);
        assertTrue(enemyInRange.getCurrentHealth() < enemyHealthBefore);
        assertEquals(energyBefore - getCost(rogue), getCurrentEnergy(rogue));
    }

    @Test
    public void testCastAbilityNoEnemiesInRange() {
        // Move all enemies out of range
        for (Enemy enemy : enemies) {
            enemy.setPosition(100, 100);
        }
        int energyBefore = getCurrentEnergy(rogue);
        rogue.OnCastAbility(board);
        assertEquals(energyBefore, getCurrentEnergy(rogue));
    }

    @Test
    public void testDescription() {
        String desc = rogue.description();
        assertTrue(desc.contains("Energy: "));
    }

    // Helper methods to access private energy fields (if needed, use reflection or add a getter in Rogue)
    private int getCurrentEnergy(Rogue rogue) {
        try {
            java.lang.reflect.Field field = Rogue.class.getDeclaredField("currentEnergy");
            field.setAccessible(true);
            return field.getInt(rogue);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private int getCost(Rogue rogue) {
        try {
            java.lang.reflect.Field field = Rogue.class.getDeclaredField("cost");
            field.setAccessible(true);
            return field.getInt(rogue);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setCurrentEnergy(Rogue rogue, int value) {
        try {
            java.lang.reflect.Field field = Rogue.class.getDeclaredField("currentEnergy");
            field.setAccessible(true);
            field.setInt(rogue, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setPlayer(GameManager gameManager, Rogue rogue) {
        try {
            java.lang.reflect.Field field = GameManager.class.getDeclaredField("currentPlayer");
            field.setAccessible(true);
            field.set(gameManager, rogue);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

