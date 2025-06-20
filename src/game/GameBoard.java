package game;

import tiles.Empty;
import tiles.Tile;
import tiles.Wall;
import units.*;

import interfaces.Visitor;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GameBoard {
    private Tile[][] board;
    private Player player;
    private List<Enemy> enemies;
    private int rows;
    private int cols;

    public GameBoard() {
        enemies = new ArrayList<>();
    }


    public void loadLevel(List<String> levelData, Player selectedPlayer) {
        this.rows = levelData.size();
        this.cols = levelData.get(0).length();
        this.board = new Tile[rows][cols];
        this.enemies.clear();
        this.player = selectedPlayer;

        for (int i = 0; i < rows; i++) {
            String row = levelData.get(i);
            for (int j = 0; j < cols; j++) {
                char charTile = row.charAt(j);
                Tile tile;

                switch (charTile) {
                    case '@':
                        player.setPosition(j, i);
                        tile = new Empty(j, i);  // overwritten later
                        break;
                    case '#':
                        tile = new Wall(j, i);
                        break;
                    case '.':
                        tile = new Empty(j, i);
                        break;
                    default:
                        Enemy enemy = EnemyFactory.createEnemy(charTile, j, i);
                        if (enemy != null) {
                            enemies.add(enemy);
                            tile = enemy;
                        } else {
                            tile = new Empty(j, i);  // fallback
                        }
                        break;
                }

                board[i][j] = tile;
            }
        }
        // Put player after parsing
        board[player.getY()][player.getX()] = player;
    }

//    // You'll need to parse the level.txt files to populate the boahis method will be responsible for loading levels from filesrd
//    public void loadLevel(List<String> levelData, Player selectedPlayer) {
//        this.rows = levelData.size();
//        this.cols = levelData.get(0).length();
//        this.board = new Tile[rows][cols];
//        this.enemies.clear(); // Clear enemies from previous level
//
//        this.player = selectedPlayer; // Set the player for this level
//
//        for (int i = 0; i < rows; i++) {
//            String row = levelData.get(i);
//            for (int j = 0; j < cols; j++) {
//                char charTile = row.charAt(j);
//                Tile newTile;
//
//                switch (charTile) {
//                    case '@':
//                        player.setPosition(j, i); // Set player's position
//                        newTile = new Empty(j, i); // Player starts on an empty tile
//                        break;
//                    case '#':
//                        newTile = new Wall(j, i);
//                        break;
//                    case 'u':
//                        newTile = new Empty(j, i);
//                        break;
//                    // Add cases for different enemy types here based on their characters
//                    // Example:
//                    case 's': // Lannister Soldier
//                        Enemy soldier = new Monster('s', "Lannister Soldier", 80, 8, 3, 25, 3, j, i);
//                        enemies.add(soldier);
//                        newTile = soldier;
//                        break;
//                    case 'k': // Lannister Knight
//                        Enemy knight = new Monster('k', "Lannister Knight", 200, 14, 8, 50, 4, j, i);
//                        enemies.add(knight);
//                        newTile = knight;
//                        break;
//                    // ... and so on for all other enemies including traps and bosses
//                    case 'B': // Bonus Trap
//                        Enemy bonusTrap = new Trap('B', "Bonus Trap", 1, 1, 1, 250, 1, 5, j, i);
//                        enemies.add(bonusTrap);
//                        newTile = bonusTrap;
//                        break;
//                    case 'M': // The Mountain (now a Boss)
//                        Enemy mountain = new org.example.units.enemies.Boss('M', "The Mountain", 1000, 60, 25, 500, 6, 5, j, i);
//                        enemies.add(mountain);
//                        newTile = mountain;
//                        break;
//                    // ... add Queen Cersei and Night's King as Bosses too
//                    default:
//                        newTile = new Empty(j, i); // Fallback
//                        break;
//                }
//                board[i][j] = newTile;
//            }
//        }
//        // Place player on the board after initialization (overwriting the '.' where '@' was)
//        board[player.getY()][player.getX()] = player;
//    }

    //Method to try and move a unit
    public void tryMoveUnit(Unit unit, int newX, int newY) {
        // Check bounds
        if (newX < 0 || newX >= cols || newY < 0 || newY >= rows) {
            System.out.println(unit.getName() + " cannot move out of bounds.");
            return;
        }

        Tile targetTile = board[newY][newX];
        Tile currentTile = board[unit.getY()][unit.getX()];

        // Use the visitor pattern for interaction
        unit.accept(targetTile);

        // If the unit moved, update the board
        if (unit.getX() == newX && unit.getY() == newY) { // Means the accept method updated the unit's position
            board[newY][newX] = unit; // Place unit in new spot
            board[currentTile.getY()][currentTile.getX()] = new Empty(currentTile.getX(), currentTile.getY()); // Old spot becomes empty
        }
    }

    public void removeUnit(Unit unit) {
        if (unit instanceof Enemy) {
            enemies.remove((Enemy) unit);
            // Replace the enemy's position with an empty tile
            board[unit.getY()][unit.getX()] = new Empty(unit.getX(), unit.getY());
        }
        // If player is removed, game over
        if (unit instanceof Player) {
            board[unit.getY()][unit.getX()] = new Tile('X', unit.getX(), unit.getY()); // Mark player's death spot
            // Trigger game over state
        }
    }

    // getEnemiesInRange boardGame
    // Utility method to get enemies within a certain range
    public List<Enemy> getEnemiesInRange(int centerX, int centerY, int range) {
        return enemies.stream()
                .filter(enemy -> getDistance(new Tile(' ', centerX, centerY), enemy) < range)
                .collect(Collectors.toList());
    }

    public Player getPlayer() {
        return player;
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public Tile getTile(int x, int y) {
        if (x >= 0 && x < cols && y >= 0 && y < rows) {
            return board[y][x];
        }
        return null; // Out of bounds
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }



    // Utility method to calculate distance
    public double getDistance(Tile t1, Tile t2) {
        return Math.sqrt(Math.pow(t1.getX() - t2.getX(), 2) + Math.pow(t1.getY() - t2.getY(), 2));
    }



    public void printBoard() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                System.out.print(board[i][j].toString());
            }
            System.out.println();
        }
    }
}