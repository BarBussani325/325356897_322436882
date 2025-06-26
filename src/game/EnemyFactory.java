package game;
import interfaces.MessageCallback;
import units.Enemy;
import units.Monster;
import units.Trap;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class EnemyFactory {
    private static final Map<Character, BiFunction<Integer, Integer, Enemy>> enemyMap = new HashMap<>();

    static {
        // Monsters
        enemyMap.put('s', (x, y) -> new Monster('s', "Lannister Soldier", 80, 8, 3, 25, 3, x, y));
        enemyMap.put('k', (x, y) -> new Monster('k', "Lannister Knight", 200, 14, 8, 50, 4, x, y));
        enemyMap.put('q', (x, y) -> new Monster('q', "Queen's Guard", 400, 20, 15, 100, 5, x, y));
        enemyMap.put('z', (x, y) -> new Monster('z', "Wright", 600, 30, 15, 100, 3, x, y));
        enemyMap.put('b', (x, y) -> new Monster('b', "Bear-Wright", 1000, 75, 30, 250, 4, x, y));
        enemyMap.put('g', (x, y) -> new Monster('g', "Giant-Wright", 1500, 100, 40, 500, 5, x, y));
        enemyMap.put('w', (x, y) -> new Monster('w', "WhiteWalker", 2000, 150, 50, 1000, 6, x, y));
        enemyMap.put('M', (x, y) -> new Monster('M', "The Mountain", 1000, 60, 25, 500, 6, x, y));
        enemyMap.put('C', (x, y) -> new Monster('C', "Queen Cersei", 100, 10, 10, 1000, 1, x, y));
        enemyMap.put('K', (x, y) -> new Monster('K', "Night's King", 5000, 300, 150, 5000, 8, x, y));
        // Traps
        enemyMap.put('Q', (x, y) -> new Trap('Q', "Queen's Trap", 250, 50, 10, 100, 3, 7, x, y));
        enemyMap.put('D', (x, y) -> new Trap('D', "Death Trap", 500, 100, 20, 250, 1, 10, x, y));
        enemyMap.put('B', (x, y) -> new Trap('B', "Bonus Trap", 1, 1, 1, 250, 1, 5, x, y));
    }

    public static Enemy createEnemy(char symbol, int x, int y, MessageCallback messageCallback) {
        BiFunction<Integer, Integer, Enemy> constructor = enemyMap.get(symbol);
        if (constructor != null) {
            Enemy enemy = constructor.apply(x, y);
            enemy.setMessageCallback(messageCallback);
            return enemy;
        }
        return null;
    }
}
