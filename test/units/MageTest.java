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

public class MageTest {

    private Mage mage;
    private List<Enemy> enemies;
    private GameManager game;
    private GameBoard board;
    private Enemy enemyInRange;

    @BeforeEach
    void setUp() {
        mage = new Mage("Melisandre", 150, 20, 2, 100, 20, 30, 3, 5, 0, 0);
        MessageCallback cli = new CLI();
        game = new GameManager("levels_dir", cli);
        board = game.getBoard();
        setPlayer(game, mage);
        game.loadNextLevel();
        enemies = board.getEnemies();
        setEnemiesPosition();
    }

    void setEnemiesPosition() {
        if (!enemies.isEmpty()) {
            enemyInRange = enemies.get(0);
            board.swapPositions(enemyInRange, mage.getX() + 1, mage.getY());
        }
    }

    @Test
    public void testGainExperienceNoLevelUp() {
        mage.gainExperience(25);
        assertEquals(25, mage.getExperience());
        assertEquals(1, mage.getLevel());
    }

    @Test
    void testGainExperienceSingleLevelUp() {
        mage.gainExperience(50);
        mage.levelUp();

        assertTrue(mage.getLevel() == 2);
        assertTrue(mage.getAttackPoints() > 20);
        assertTrue(mage.getDefensePoints() > 2);
        assertTrue(mage.getHealthPool() > 150);
        assertTrue(mage.getCurrentHealth() > 0);
        assertEquals(0, mage.getExperience());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 5})
    void testLevelUpMultipleLevelUp(int level) {
        mage.level = level;
        int oldAttack = mage.getAttackPoints();
        int oldDefense = mage.getDefensePoints();
        int oldHealth = mage.getHealthPool();
        int oldManaPool = mage.getManaPool();
        int oldManaAmount = getCurrentMana(mage);
        int oldSpellPower = getSpellPower(mage);

        // Give enough experience to level up
        mage.experience = 1000;
        mage.levelUp();

        assertTrue(mage.level > level , "Level should increase by 1");
        assertTrue(mage.getAttackPoints() > oldAttack, "Attack should increase");
        assertTrue(mage.getDefensePoints() > oldDefense , "Defense should increase");
        assertTrue(mage.getHealthPool() > oldHealth , "Health pool should increase");
        assertTrue(mage.getCurrentHealth() > oldHealth , "Health amount should increase");
        assertTrue(mage.getExperience() < 1000, "experience should decrease");
        assertTrue(mage.getManaPool() > oldManaPool , "mana pool should increase");
        assertTrue(getCurrentMana(mage) > oldManaAmount , "mana amount should increase");
        assertTrue(getSpellPower(mage) > oldSpellPower , "spell power should increase");
    }

    @Test
    public void testInitialMana() {
        assertEquals(100 / 4, getCurrentMana(mage));
    }

    @Test
    public void testManaRegenerationEachTurn() {
        int initialMana = getCurrentMana(mage);
        mage.setInputProvider(new TestInputProvider("d"));
        mage.ProcessTurn(board);
        int expectedMana = Math.min(mage.getManaPool(), initialMana + mage.getLevel());
        assertEquals(expectedMana, getCurrentMana(mage));
    }
    
    @Test
    public void testCastAbilityNotEnoughMana(){
        setCurrentMana(mage,0);
        mage.OnCastAbility(board);
        assertEquals(0, getCurrentMana(mage)); // make sure mana amount is not negative, meaning the special ability wasn't called.
    }

    @Test
    public void testCastAbilityEnemyInRange() {
        boolean enemyGotDamage = false;
        int enemyOldHealth = enemyInRange.getCurrentHealth();
        int attempts = 20; // Increase attempts for higher confidence

        for (int i = 0; i < attempts && !enemyGotDamage; i++) {
            mage.OnCastAbility(board);
            setCurrentMana(mage, 100); // ensure enough mana
            if (enemyOldHealth > enemyInRange.getCurrentHealth()) {
                enemyGotDamage = true;
            }
        }
        assertTrue(enemyGotDamage, "Warrior's ability should damage the enemy at least once in " + attempts + " attempts.");
    }

    @Test
    public void testCastAbilityNoEnemiesInRange() {
        // Move all enemies out of range
        for (Enemy enemy : enemies) {
            enemy.setPosition(100, 100);
        }
        int manaBefore = getCurrentMana(mage);
        mage.OnCastAbility(board);
        assertEquals(manaBefore, getCurrentMana(mage));
    }


    @Test
    public void testDescription() {
        String desc = mage.description();
        assertTrue(desc.contains("Mana: "));
        assertTrue(desc.contains("Spell Power: "));
    }

    // Helper methods to access private mana fields (if needed, use reflection or add a getter in Mage)
    private int getCurrentMana(Mage mage) {
        try {
            java.lang.reflect.Field field = Mage.class.getDeclaredField("currentMana");
            field.setAccessible(true);
            return field.getInt(mage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private void setCurrentMana(Mage mage, int value) {
        try {
            java.lang.reflect.Field field = Mage.class.getDeclaredField("currentMana");
            field.setAccessible(true);
            field.setInt(mage, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private int getSpellPower(Mage mage) {
        try {
            java.lang.reflect.Field field = Mage.class.getDeclaredField("spellPower");
            field.setAccessible(true);
            return field.getInt(mage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setPlayer(GameManager gameManager, Mage mage) {
        try {
            java.lang.reflect.Field field = GameManager.class.getDeclaredField("currentPlayer");
            field.setAccessible(true);
            field.set(gameManager, mage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
