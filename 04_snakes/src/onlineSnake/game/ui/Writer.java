package onlineSnake.game.ui;

import me.ippolitov.fit.snakes.SnakesProto;
import onlineSnake.game.Game;
import onlineSnake.game.proto.Config;
import onlineSnake.game.proto.Player;

import java.util.Scanner;

public class Writer {

    public static void Initialization() {
        Game.player = new Player();
        Game.player.setIp_address("i don't know");
        Scanner in = new Scanner(System.in);
        System.out.print("Enter your name : ");
        Game.player.setName(in.next());
        System.out.print("Enter your port : ");
        Game.player.setPort(in.nextInt());
        Game.player.setRole(SnakesProto.NodeRole.MASTER);
        Player.players.add(Game.player);
        Config.initializeFieldSize();
    }
}
