package onlineSnake.game.online;

import com.google.protobuf.GeneratedMessageV3;
import me.ippolitov.fit.snakes.SnakesProto;
import onlineSnake.game.Game;
import onlineSnake.game.proto.*;
import onlineSnake.game.snake.Snake;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Sender implements Runnable {
    public static int counter = 0;
    public static ConcurrentLinkedQueue<Packet> packets = new ConcurrentLinkedQueue<>();
    private static MulticastSocket multicastSocket;
    static {
        try {
            multicastSocket = new MulticastSocket(8079);
            NetworkInterface IFC = NetworkInterface.getByName("224.0.0.0");
            multicastSocket.joinGroup(new InetSocketAddress("224.0.0.0", 8079), IFC);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
                builder.setSenderId(Game.playerId);
                builder.setMsgSeq(counter++);
                byte[] data = builder.buildPartial().toByteArray();
                try {
                    SnakesProto.GameMessage gameMessage = SnakesProto.GameMessage.parseFrom(data);
                    DatagramPacket datagramPacket = new DatagramPacket(gameMessage.toByteArray(), gameMessage.toByteArray().length, new InetSocketAddress("224.0.0.0", 8079));
                    multicastSocket.send(datagramPacket);
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
        builder.setSenderId(Game.playerId);
        for (Player player : Player.players) {
            if (player.getId() == Game.playerId) continue;
            builder.setMsgSeq(counter++);
            builder.setReceiverId(player.getId());
            packets.add(new Packet(builder.build(), new InetSocketAddress(player.getIp_address(), player.getPort())));
        }
    }

    public void run() {
        try {
            DatagramSocket datagramSocket = new DatagramSocket(Player.getPlayer(Game.playerId).getPort());
            while (true) {
                Packet packet = packets.poll();
                if (packet == null) continue;
                byte[] data = packet.message.toByteArray();
                DatagramPacket datagramPacket = new DatagramPacket(data, 0, data.length, packet.address);
                datagramSocket.send(datagramPacket);
                System.out.print("-> ");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
