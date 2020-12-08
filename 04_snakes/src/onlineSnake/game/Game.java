package onlineSnake.game;

import me.ippolitov.fit.snakes.SnakesProto;
import onlineSnake.game.online.PingerHost;
import onlineSnake.game.online.Scanner;
import onlineSnake.game.online.Sender;
import onlineSnake.game.proto.Player;
import onlineSnake.game.ui.Writer;

import java.io.IOException;

public class Game {
    public static int playerId;
    public static SnakesProto.GameConfig gameConfig;

    public static void main(String[] args) throws IOException {
        Writer.Initialization();
        new Thread(new Sender()).start();
        new Thread(new PingerHost()).start();
    }

}
