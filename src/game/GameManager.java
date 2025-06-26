package game;
import interfaces.MessageCallback;
import interfaces.InputProvider;
import units.Enemy;
import units.Player;
import units.Hunter;
import units.Mage;
import units.Rogue;
import units.Warrior;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class GameManager implements InputProvider {
    private GameBoard board;
    private Player currentPlayer;
    private List<String> levelFiles; // List of paths to level files
    private int currentLevelIndex;
    private Scanner scanner;
    private MessageCallback messageCallback;

    public GameManager(String levelsDirectoryPath, MessageCallback messageCallback) {
        this.board = new GameBoard(messageCallback);
        this.scanner = new Scanner(System.in);
        this.currentLevelIndex = 0;
        this.messageCallback = messageCallback;

        try {
            this.levelFiles = Files.list(Paths.get(levelsDirectoryPath))
                    .filter(Files::isRegularFile)
                    .map(p -> p.toAbsolutePath().toString())
                    .sorted() // Ensure levels are loaded in order (e.g., level1.txt, level2.txt)
                    .collect(Collectors.toList());
            if (this.levelFiles.isEmpty()) {
                throw new IllegalArgumentException("No level files found in the specified directory: " + levelsDirectoryPath);
            }
        } catch (IOException e) {
            System.err.println("Error reading level files: " + e.getMessage());
            System.exit(1);
        }
    }

    public void startGame() {
        messageCallback.send("Welcome to Dungeons and Dragons!\n");
        selectPlayer();
        while(currentPlayer.isAlive() && currentLevelIndex < levelFiles.size()){
            loadNextLevel();
            if(!currentPlayer.isAlive()){ // In case loading a level with a trap kills the player
                break;
            }
            playLevel();
//            if (currentPlayer.isAlive() && board.getEnemies().isEmpty()) {
//                messageCallback.send("Level " + (currentLevelIndex) + " completed!");
//                if(currentLevelIndex != levelFiles.size() -1)
//                    messageCallback.send("");
//            }
        }

        if (!currentPlayer.isAlive()) {
            board.printBoard(); // Show final board with 'X'
            messageCallback.send(currentPlayer.description() + "\nGame Over.");
        } else {
            messageCallback.send("Congratulations! You completed all levels!\n");
        }
        scanner.close();
    }


    private void selectPlayer() {
        List<Player> availablePlayers = PlayerFactory.getAvailablePlayers();

        messageCallback.send("Choose your player character:\n");
        for (int i = 0; i < availablePlayers.size(); i++) {
            messageCallback.send((i + 1) + ". " + availablePlayers.get(i).description() + "\n");
        }

        int choice = -1;
        while (choice < 1 || choice > availablePlayers.size()) {
            messageCallback.send("Enter your choice: ");
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                messageCallback.send("Invalid input. Please enter a number.\n");
            }
        }
        currentPlayer = availablePlayers.get(choice - 1);
        currentPlayer.setInputProvider(this);
        messageCallback.send("You have chosen " + currentPlayer.getName() + "!\n");
    }


    public void loadNextLevel() {
        if (currentLevelIndex < levelFiles.size()) {
            String levelPath = levelFiles.get(currentLevelIndex);
            try {
                List<String> levelData = Files.readAllLines(Paths.get(levelPath));
                board.loadLevel(levelData, currentPlayer);
                currentLevelIndex++;
            } catch (IOException e) {
                messageCallback.send("Failed to load level " + levelPath + ": " + e.getMessage() + "\n");
                System.exit(1);
            }
        }
    }

    public void playLevel() {
        while (currentPlayer.isAlive() && !board.getEnemies().isEmpty()) {
            board.printBoard();
            messageCallback.send(currentPlayer.description() + "\n");
            messageCallback.send("Your move (w/a/s/d/e/q): ");
            // Player turn
            currentPlayer.ProcessTurn(board);
            if (!currentPlayer.isAlive()) {
                return; // End level
            }

            List<Enemy> currentEnemies = new ArrayList<>(board.getEnemies());
            for (Enemy enemy : currentEnemies) {
                if (enemy.isAlive()) {
                    enemy.ProcessTurn(board);
                }
                if (!currentPlayer.isAlive()) {
                    return; // End level
                }
            }
            board.getEnemies().removeIf(enemy -> !enemy.isAlive());

            if (!currentPlayer.isAlive() || board.getEnemies().isEmpty()) {
                return;
            }
        }
    }

    public GameBoard getBoard(){ return board; }

    @Override
    public String getInput() {
        return scanner.nextLine().toLowerCase();
    }
}