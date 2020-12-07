package onlineSnake.game.online;

import me.ippolitov.fit.snakes.SnakesProto;
import onlineSnake.game.Game;
import onlineSnake.game.proto.Config;
import onlineSnake.game.proto.Player;

import java.net.InetSocketAddress;
import java.util.concurrent.Semaphore;

public class PingerHost implements Runnable {
    public void run() {
        while (true) {
            Sender.addMessage(SnakesProto.GameMessage.PingMsg.newBuilder().build(), 2);
            Sender.addMessage(SnakesProto.GameMessage.AnnouncementMsg.newBuilder().setPlayers(Player.getGamePlayers()).setConfig(Config.getGameConfig()).setCanJoin(true).build(), 6);
            //Sender.packets.add(new Packet(, new InetSocketAddress("224.0.0.0", 12001)));
            try {
                Thread.sleep(Config.ping_delay_ms);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
