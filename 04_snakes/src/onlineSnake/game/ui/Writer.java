package onlineSnake.game.ui;

import me.ippolitov.fit.snakes.SnakesProto;
import onlineSnake.game.Game;
import onlineSnake.game.proto.Config;
import onlineSnake.game.proto.Player;

import java.util.Scanner;

import static onlineSnake.game.proto.Player.addPlayers;
import static onlineSnake.game.proto.Player.getPlayer;

public class Writer {

    public static void Initialization() {
        Player.players.add(new Player());
        Game.playerId = Player.players.get(0).getId();
        getPlayer(Game.playerId).setIp_address("i don't know");
        Scanner in = new Scanner(System.in);
        System.out.print("Enter your name : ");
        getPlayer(Game.playerId).setName(in.next());
        System.out.print("Enter your port : ");
        getPlayer(Game.playerId).setPort(in.nextInt());
        onlineSnake.game.online.Scanner.scan();
        System.out.print("If you want to connect enter game number\nOr enter 0 to start new game\n");
        boolean key = true;
        while (key) {
            System.out.print(".... number : ");
            int number = in.nextInt();
            if (number == 0) {
                key = false;
                getPlayer(Game.playerId).setRole(SnakesProto.NodeRole.MASTER);
                Config.initializeFieldSize();
            }
            if (number > 0 && number <= onlineSnake.game.online.Scanner.getMessagesCount()) {
                key = false;
                getPlayer(Game.playerId).setRole(SnakesProto.NodeRole.NORMAL);
                Config.initializeFieldSize(number);
                addPlayers(number);
            }
        }
    }
}
