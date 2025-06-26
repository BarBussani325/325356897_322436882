package game;

import interfaces.MessageCallback;
import tiles.Empty;
import tiles.Tile;
import tiles.Wall;
import units.*;

import java.util.ArrayList;
import java.util.List;

public class GameBoard {
    private Tile[][] board;
    private Player player;
    private List<Enemy> enemies;
    private int rows;
    private int cols;
    private MessageCallback messageCallback;

    public GameBoard(MessageCallback callback) {
        this.enemies = new ArrayList<>();
        this.messageCallback = callback;
    }

    public void loadLevel(List<String> levelData, Player selectedPlayer) {
        this.rows = levelData.size();
        this.cols = levelData.get(0).length();
        this.board = new Tile[rows][cols];
        this.enemies.clear();
        this.player = selectedPlayer;
        this.player.setMessageCallback(this.messageCallback);

        for (int i = 0; i < rows; i++) {
            String row = levelData.get(i);
            for (int j = 0; j < cols; j++) {
                char charTile = row.charAt(j);
                Tile tile;

                switch (charTile) {
                    case '@':
                        player.setPosition(j, i);
                        tile = player; // Place the player directly
                        break;
                    case '#':
                        tile = new Wall(j, i);
                        break;
                    case '.':
                        tile = new Empty(j, i);
                        break;
                    default:
                        Enemy enemy = EnemyFactory.createEnemy(charTile, j, i, this.messageCallback);
                        if (enemy != null) {
                            enemies.add(enemy);
                            tile = enemy;
                        } else {
                            tile = new Empty(j, i); // Fallback for unknown characters
                        }
                        break;
                }
                board[i][j] = tile;
            }
        }
    }

    public void tryMoveUnit(Unit unit, int newX, int newY) {
        if (newX < 0 || newX >= this.cols || newY < 0 || newY >= this.rows) {
            return;
        }
        Tile destinationTile = getTile(newX, newY);
        unit.encounter(destinationTile, this);
    }

    public void swapPositions(Unit unit, Empty emptyTile) {
        int unitOldX = unit.getX();
        int unitOldY = unit.getY();
        int emptyX = emptyTile.getX();
        int emptyY = emptyTile.getY();

        board[unitOldY][unitOldX] = emptyTile;
        board[emptyY][emptyX] = unit;

        unit.setPosition(emptyX, emptyY);
        emptyTile.setPosition(unitOldX, unitOldY);
    }

    public void swapPositions(Unit unit1, int x, int y) {
        int unit1OldX = unit1.getX();
        int unit1OldY = unit1.getY();

        Tile tile = board[y][x];

        board[unit1OldY][unit1OldX] = tile;
        board[y][x] = unit1;

        unit1.setPosition(x, y);
        tile.setPosition(unit1OldX, unit1OldY);
    }
    
    public void removeEnemy(Enemy enemy) {
        enemies.remove(enemy);
        board[enemy.getY()][enemy.getX()] = new Empty(enemy.getX(), enemy.getY());
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
        return null;
    }

    public void sendMessage(String message) {
        if(messageCallback != null) {
            messageCallback.send(message);
        }
    }

    public void printBoard() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                sb.append(board[i][j].toString());
            }
            sb.append("\n");
        }
        sendMessage(sb.toString());
    }

    public double getDistance(Unit u1, Unit u2) {
        return Math.sqrt(Math.pow(u1.getX() - u2.getX(), 2) + Math.pow(u1.getY() - u2.getY(), 2));
    }

    public List<Enemy> getEnemiesInRange(Unit unit, int range) {
        List<Enemy> enemiesInRange = new ArrayList<>();
        for (Enemy enemy : enemies) {
            if (getDistance(unit, enemy) <= range) {
                enemiesInRange.add(enemy);
            }
        }
        return enemiesInRange;
    }
}