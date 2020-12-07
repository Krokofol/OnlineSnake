package onlineSnake.game.online;

import com.google.protobuf.GeneratedMessageV3;
import me.ippolitov.fit.snakes.SnakesProto;
import onlineSnake.game.Game;
import onlineSnake.game.proto.*;
import onlineSnake.game.snake.Snake;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Sender implements Runnable {
    public static int counter = 0;
    public static ConcurrentLinkedQueue<Packet> packets = new ConcurrentLinkedQueue<>();

    public static void addMessage(GeneratedMessageV3 message, int type) {
        SnakesProto.GameMessage.Builder builder = SnakesProto.GameMessage.newBuilder();
        switch (type) {
            case (2) :
                builder.setPing((SnakesProto.GameMessage.PingMsg) message);
                break;
            case (3) :
                builder.setSteer((SnakesProto.GameMessage.SteerMsg) message);
                break;
            case (4) :
                builder.setAck((SnakesProto.GameMessage.AckMsg) message);
                break;
            case (5) :
                builder.setState((SnakesProto.GameMessage.StateMsg) message);
                break;
            case (6) :
                builder.setAnnouncement((SnakesProto.GameMessage.AnnouncementMsg) message);
                builder.setSenderId(Game.player.getId());
                builder.setMsgSeq(counter++);
                byte[] data = builder.build().toByteArray();
                try {
                    DatagramPacket datagramPacket = new DatagramPacket(data, data.length, new InetSocketAddress("224.0.0.1", 8079));
                    new MulticastSocket(8079).send(datagramPacket);
                    System.out.print(".");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            case(7) :
                builder.setJoin((SnakesProto.GameMessage.JoinMsg) message);
                break;
            case(8) :
                builder.setError((SnakesProto.GameMessage.ErrorMsg) message);
                break;
            case(9) :
                builder.setRoleChange((SnakesProto.GameMessage.RoleChangeMsg) message);
                break;
        }
        builder.setSenderId(Game.player.getId());
        for (Player player : Player.players) {
            if (player.getId() == Game.player.getId()) continue;
            builder.setMsgSeq(counter++);
            builder.setReceiverId(player.getId());
            packets.add(new Packet(builder.build(), new InetSocketAddress(player.getIp_address(), player.getPort())));
        }
    }

    public void run() {
        try {
            DatagramSocket datagramSocket = new DatagramSocket(Game.player.getPort());
            while (true) {
                Packet packet = packets.poll();
                if (packet == null) continue;
                byte[] data = packet.message.toByteArray();
                DatagramPacket datagramPacket = new DatagramPacket(data, 0, data.length, packet.address);
                datagramSocket.send(datagramPacket);
                System.out.print(",");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
