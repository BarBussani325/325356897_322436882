package game;

import interfaces.MessageCallback;

public class CLI implements MessageCallback {

    @Override
    public void send(String message) {
        System.out.print(message);
    }

    public static void main(String[] args) {
        String levelsPath;
        if (args.length > 0) {
            levelsPath = args[0];
        } else {
            System.out.println("Error: this program needs a path to the levels directory as an argument.");
            return;
        }
        MessageCallback cli = new CLI();
        GameManager game = new GameManager(levelsPath, cli);
        game.startGame();
    }
} 