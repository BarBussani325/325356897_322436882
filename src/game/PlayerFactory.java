package game;

import interfaces.MessageCallback;
import units.*;

import java.util.ArrayList;
import java.util.List;

public class PlayerFactory {

    public static List<Player> getAvailablePlayers() {
        List<Player> players = new ArrayList<>();
        players.add(new Warrior("Jon Snow", 300, 30, 4, 3, 0, 0));
        players.add(new Warrior("The Hound", 400, 20, 6, 5, 0, 0));
        players.add(new Mage("Melisandre", 100, 5, 1, 300, 30, 15, 5, 6, 0, 0));
        players.add(new Mage("Thoros of Myr", 250, 25, 4, 150, 20, 20, 3, 4, 0, 0));
        players.add(new Rogue("Arya Stark", 150, 40, 2, 20, 0, 0));
        players.add(new Rogue("Bronn", 250, 35, 3, 50, 0, 0));
        players.add(new Hunter("Ygritte", 220, 30, 2, 6, 0, 0));
        return players;
    }
}
