package onlineSnake.game.ui;

import me.ippolitov.fit.snakes.SnakesProto;
import onlineSnake.game.Game;
import onlineSnake.game.proto.Config;
import onlineSnake.game.proto.Player;

import java.util.Scanner;

public class Writer {

    public static void Initialization() {
        Player.players.add(new Player());
        Game.playerId = Player.players.get(0).getId();
        Player.getPlayer(Game.playerId).setIp_address("i don't know");
        Scanner in = new Scanner(System.in);
        System.out.print("Enter your name : ");
        Player.getPlayer(Game.playerId).setName(in.next());
        System.out.print("Enter your port : ");
        Player.getPlayer(Game.playerId).setPort(in.nextInt());
        onlineSnake.game.online.Scanner.scan();
        System.out.print("If you want to connect enter game number\nOr enter 0 to start new game\n.... number : ");
        int number = in.nextInt();
        boolean key = true;
        while (key) {
            if (number == 0) {
                key = false;
                Player.getPlayer(Game.playerId).setRole(SnakesProto.NodeRole.MASTER);
                Config.initializeFieldSize();
            }
            if (number > 0 && number <= onlineSnake.game.online.Scanner.getMessagesCount()) {
                key = false;
                Player.getPlayer(Game.playerId).setRole(SnakesProto.NodeRole.NORMAL);
                Config.initializeFieldSize(number);
            }
        }
    }
}
