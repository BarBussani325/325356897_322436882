package game;

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

public class GameManager {
    private GameBoard board;
    private Player currentPlayer;
    private List<String> levelFiles; // List of paths to level files
    private int currentLevelIndex;
    private Scanner scanner;

    public GameManager(String levelsDirectoryPath) {
        this.board = new GameBoard();
        this.scanner = new Scanner(System.in);
        this.currentLevelIndex = 0;

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
        System.out.println("Welcome to Dungeons and Dragons!");
        selectPlayer();
        while(currentPlayer.isAlive() && currentLevelIndex < levelFiles.size()){
            loadNextLevel();
            if (currentPlayer.isAlive() && board.getEnemies().isEmpty()) {
                System.out.println("Level " + (currentLevelIndex) + " completed! Moving to next level...");
            }
        }

        if (!currentPlayer.isAlive()) {
            System.out.println("Game Over! " + currentPlayer.getName() + " died.");
            board.printBoard(); // Show final board with 'X'
        } else {
            System.out.println("Congratulations! You completed all levels!");
        }
        scanner.close();
        System.out.println(board.toString());
    }

    private void selectPlayer() {
        List<Player> availablePlayers = new ArrayList<>();
        // Add pre-defined players
        availablePlayers.add(new Warrior("Jon Snow", 300, 30, 4, 3, 0, 0));
        availablePlayers.add(new Warrior("The Hound", 400, 20, 6, 5, 0, 0));
        availablePlayers.add(new Mage("Melisandre", 100, 5, 1, 300, 30, 15, 5, 6, 0, 0));
        availablePlayers.add(new Mage("Thoros of Myr", 250, 25, 4, 150, 20, 20, 3, 4, 0, 0));
        availablePlayers.add(new Rogue("Arya Stark", 150, 40, 2, 20, 0, 0));
        availablePlayers.add(new Rogue("Bronn", 250, 35, 3, 50, 0, 0));
        availablePlayers.add(new Hunter("Ygritte", 220, 30, 2, 6, 0, 0)); // Bonus Hunter

        System.out.println("Choose your player character:");
        for (int i = 0; i < availablePlayers.size(); i++) {
            System.out.println((i + 1) + ". " + availablePlayers.get(i).description());
        }

        int choice = -1;
        while (choice < 1 || choice > availablePlayers.size()) {
            System.out.print("Enter your choice: ");
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
        this.currentPlayer = availablePlayers.get(choice - 1);
        System.out.println("You have chosen " + currentPlayer.getName() + "!");
    }

    private void loadNextLevel() {
        if (currentLevelIndex < levelFiles.size()) {
            String levelPath = levelFiles.get(currentLevelIndex);
            System.out.println("\nLoading level from: " + levelPath);
            try {
                List<String> levelData = Files.readAllLines(Paths.get(levelPath));
                board.loadLevel(levelData, currentPlayer);
                currentLevelIndex++;
            } catch (IOException e) {
                System.err.println("Failed to load level " + levelPath + ": " + e.getMessage());
                // Handle error, maybe skip level or end game
                System.exit(1);
            }
        }
    }

    private void playLevel() {
        while (currentPlayer.isAlive() && !board.getEnemies().isEmpty()) {
            board.printBoard();
            System.out.println(currentPlayer.description());
            System.out.print("Your move (w/a/s/d/e/q): ");
            String input = scanner.nextLine().toLowerCase();

            // Player turn
            int playerPrevX = currentPlayer.getX();
            int playerPrevY = currentPlayer.getY();

            switch (input) {
                case "w": // Move up
                    board.tryMoveUnit(currentPlayer, currentPlayer.getX(), currentPlayer.getY() - 1);
                    break;
                case "s": // Move down
                    board.tryMoveUnit(currentPlayer, currentPlayer.getX(), currentPlayer.getY() + 1);
                    break;
                case "a": // Move left
                    board.tryMoveUnit(currentPlayer, currentPlayer.getX() - 1, currentPlayer.getY());
                    break;
                case "d": // Move right
                    board.tryMoveUnit(currentPlayer, currentPlayer.getX() + 1, currentPlayer.getY());
                    break;
                case "e": // Cast ability
                    currentPlayer.castAbility(board);
                    break;
                case "q": // Do nothing
                    System.out.println(currentPlayer.getName() + " did nothing.");
                    break;
                default:
                    System.out.println("Invalid input. Please use w, a, s, d, e, or q.");
                    continue; // Skip enemy turns if input is invalid
            }

            // If player moved, update player's position on the board if needed (handled by tryMoveUnit)
            // If player was killed by a trap, the game over should be handled.
            if (!currentPlayer.isAlive()) {
                System.out.println("Player was defeated!");
                return; // End level
            }

            currentPlayer.processTurn(board); // Apply passive effects like mana/energy regen or cooldown reduction

            // Enemies' turns
            // Create a copy to avoid ConcurrentModificationException if enemies are removed
            List<Enemy> currentEnemies = new ArrayList<>(board.getEnemies());
            for (Enemy enemy : currentEnemies) {
                if (enemy.isAlive()) {
                    enemy.processTurn(board); // Move or cast ability
                }
                if (!currentPlayer.isAlive()) { // Check if player died during enemy turn
                    System.out.println("Player was defeated by an enemy!");
                    return; // End level
                }
            }
            // After all enemy turns, clean up dead enemies (if not already handled in combat)
            board.getEnemies().removeIf(enemy -> !enemy.isAlive());

            // Check if player still alive and if all enemies are defeated
            if (!currentPlayer.isAlive() || board.getEnemies().isEmpty()) {
                return; // Level ends
            }
        }
    }

//        public static void main(String[] args) {
//            if (args.length != 1) {
//                System.out.println("Usage: java -jar hw3.jar <path_to_levels_directory>");
//                return;
//            }
//            GameManager game = new GameManager(args[0]);
//            game.startGame();
//        }
}