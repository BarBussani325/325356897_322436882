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


 public class WarriorTest {

    private Warrior warrior;
    private List<Enemy> enemies;
    private Enemy enemyInRange;
    private GameManager game;
    private GameBoard board;

    @BeforeEach
    void setUp() {
        // Replace with actual subclass if needed, with proper constructor values
        warrior = new Warrior("Jon Snow", 300, 30, 4, 3, 1, 1);
        warrior.setInputProvider(() -> "q");
        MessageCallback cli = new CLI();
        game = new GameManager("levels_dir", cli);
        board = game.getBoard();
        setPlayer(game, warrior);
        game.loadNextLevel();
        enemies = board.getEnemies();
        setEnemiesPosition();
    }

    void setEnemiesPosition(){
        int x = warrior.getX();
        int y = warrior.getY();
        board.swapPositions(enemies.get(0), x+1, y);
        enemyInRange = enemies.get(0);
    }

    @Test
     public void testGainExperienceNoLevelUp() {
        warrior.gainExperience(25);
        assertEquals(25, warrior.getExperience());
        assertEquals(1, warrior.getLevel());
    }

     @Test
     void testGainExperienceSingleLevelUp() {
         warrior.gainExperience(50);
         warrior.levelUp();

         assertTrue(warrior.level == 2);
         assertEquals(42, warrior.getAttackPoints());
         assertEquals(8, warrior.getDefensePoints());
         assertEquals(330, warrior.getHealthPool());
         assertEquals(320, warrior.getCurrentHealth());
         assertEquals(0, warrior.getExperience());
         assertEquals(0, warrior.getRemainingCooldown());

     }

     @ParameterizedTest
     @ValueSource(ints = {1, 2, 5})
     void testLevelUpMultipleLevelUp(int level) {
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
     public void testInitialCooldownIsZero() {
         assertEquals(0, warrior.getRemainingCooldown());
     }

     @Test
     public void testAbilitySetsCooldown() {
         warrior.OnCastAbility(board);
         assertEquals(3, warrior.getRemainingCooldown());
     }

     @Test
     public void testCooldownDecreasesEachTick() {
         warrior.OnCastAbility(board);
         warrior.ProcessTurn(board);
         assertEquals(2, warrior.getRemainingCooldown());

         warrior.ProcessTurn(board);
         assertEquals(1, warrior.getRemainingCooldown());

         warrior.ProcessTurn(board);
         assertEquals(0, warrior.getRemainingCooldown());
     }

     @Test
     public void testCooldownNeverGoesNegative() {
         warrior.OnCastAbility(board);

         for (int i = 0; i < 5; i++) {
             warrior.ProcessTurn(board);
         }

         assertEquals(0, warrior.getRemainingCooldown());
     }

     @Test
     public void testCannotCastAbilityWhenOnCooldown() {
         warrior.OnCastAbility(board); // put on cooldown
         int currentCooldown = warrior.getRemainingCooldown();

         warrior.OnCastAbility(board); // should do nothing
         assertEquals(currentCooldown, warrior.getRemainingCooldown()); // still same cooldown
     }

     @Test
     public void testCanCastAgainAfterCooldownExpires() {
         warrior.OnCastAbility(board);

         for (int i = 0; i < 3; i++) {
             warrior.ProcessTurn(board);
         }

         assertEquals(0, warrior.getRemainingCooldown());

         warrior.OnCastAbility(board); // Should work again
         assertEquals(3, warrior.getRemainingCooldown());
     }

     @Test
     public void testLevelUpResetsCooldown() {
         warrior.gainExperience(50);
         warrior.levelUp();
         assertEquals(0, warrior.getRemainingCooldown());
     }

     @Test
     public void testCastAbility() {
         boolean enemyGotDamage = false;
         int enemyOldHealth = enemyInRange.getCurrentHealth();
         int attempts = 20; // Increase attempts for higher confidence

         for (int i = 0; i < attempts && !enemyGotDamage; i++) {
             warrior.OnCastAbility(board);
             warrior.setRemainingCooldown(0); // Reset cooldown for next attempt
             if (enemyOldHealth > enemyInRange.getCurrentHealth()) {
                 enemyGotDamage = true;
             }
         }
         assertTrue(enemyGotDamage, "Warrior's ability should damage the enemy at least once in " + attempts + " attempts.");
     }

     @Test
     public void testDescription() {
         String desc = warrior.description();
         assertTrue(desc.contains("Cooldown: "));

     }



     private void setPlayer(GameManager gameManager, Warrior warrior) {
         try {
             java.lang.reflect.Field field = GameManager.class.getDeclaredField("currentPlayer");
             field.setAccessible(true);
             field.set(gameManager, warrior);
         } catch (Exception e) {
             throw new RuntimeException(e);
         }
     }



}