package units;

import game.CLI;
import game.GameBoard;
import game.GameManager;
import interfaces.MessageCallback;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MonsterTest {

    private Monster monster;
    private Player player;
    private GameManager game;
    private GameBoard board;

    @BeforeEach
    void setUp() {
        monster = new Monster('M', "Orc", 100, 15, 5, 20, 2, 5, 5);
        player = new Warrior("Hero", 100, 10, 5, 3, 2, 2);
        MessageCallback cli = new CLI();
        monster.setMessageCallback(cli);
        player.setMessageCallback(cli);
        game = new GameManager("levels_dir", cli);
        //game.setPlayer(player);
        setPlayer(game, player);
        game.loadNextLevel();
        board = game.getBoard();
        // Now it's safe to place units
        board.swapPositions(monster, 5, 5);
        board.swapPositions(player, 4, 5);
    }

    @Test
    public void testChasePlayerWhenInVisionRange() {
        // Place player within vision range
        board.swapPositions(player, 4, 5);
        double distance = board.getDistance(monster, player);
        assertTrue(distance < monster.visionRange);

        int oldX = monster.getX();
        int oldY = monster.getY();

        monster.ProcessTurn(board);

        // Monster should move closer to player
        int newX = monster.getX();
        int newY = monster.getY();
        assertTrue(Math.abs(newX - player.getX()) <= Math.abs(oldX - player.getX()) ||
                Math.abs(newY - player.getY()) <= Math.abs(oldY - player.getY()));
    }

    @Test
    public void testRandomMovementWhenPlayerOutOfVisionRange() {
        // Place player far away
        board.swapPositions(player, 2, 2);

        int oldX = monster.getX();
        int oldY = monster.getY();

        monster.ProcessTurn(board);

        // Monster should move to a neighboring tile or stay
        int newX = monster.getX();
        int newY = monster.getY();
        int dx = Math.abs(newX - oldX);
        int dy = Math.abs(newY - oldY);
        assertTrue((dx == 1 && dy == 0) || (dx == 0 && dy == 1) || (dx == 0 && dy == 0));
    }

    @Test
    public void testDescriptionIncludesVisionRange() {
        String desc = monster.description();
        assertTrue(desc.contains("Vision Range: " + monster.visionRange));
    }

    @Test
    public void testMonsterAttacksPlayerIfAdjacent() {
        // Place monster next to player
        player.setPosition(5, 4);
        board.swapPositions(player, 5, 4);
        int playerHealthBefore = player.getCurrentHealth();
        boolean playerGotDamage = false;

        for(int i = 0; i< 20 && !playerGotDamage; i++){
            monster.ProcessTurn(board);
            if(player.getCurrentHealth() < playerHealthBefore)
                playerGotDamage = true;
        }
        assertTrue(playerGotDamage);
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