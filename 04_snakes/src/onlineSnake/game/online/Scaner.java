package onlineSnake.game.online;

import com.google.protobuf.GeneratedMessageV3;
import me.ippolitov.fit.snakes.SnakesProto;
import onlineSnake.game.Game;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Scaner {
    private static Map<String, DatagramPacket> messages = new HashMap<>();
    private static int counter = 0;

    private static void addMessage(DatagramPacket packet) {
        if (packet == null) return;
        String address = packet.getAddress().toString();
        messages.put(address + ":" + packet.getPort(), packet);
    }

    public static void scan() {
        MulticastSocket multicastSocket = null;
        try {
            multicastSocket = new MulticastSocket(8079);
            NetworkInterface IFC = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
            multicastSocket.setSoTimeout(300);
            multicastSocket.joinGroup(new InetSocketAddress("224.0.0.0", 8079), IFC);
        } catch (IOException e) {
            e.printStackTrace();
        }
        long timeStart = System.currentTimeMillis();
        while (timeStart > System.currentTimeMillis() - 300) {
            DatagramPacket datagramPacket = new DatagramPacket(new byte[8192], 8192);
            System.out.print("-");
            try {
                multicastSocket.receive(datagramPacket);
                addMessage(datagramPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.print("+");
        }
        multicastSocket.close();
        getUniq();
    }

    private static void getUniq() {
        ArrayList<DatagramPacket> uniqMessages = new ArrayList<>();
        for (Map.Entry<String, DatagramPacket> entry : messages.entrySet()) {
            uniqMessages.add(entry.getValue());
            for (Map.Entry<String, DatagramPacket> entry1 : messages.entrySet()) {
                if (entry1.getKey().equals(entry.getKey())) messages.remove(entry1);
            }
        }
        for (DatagramPacket datagramPacket : uniqMessages) {
            try {
                byte[] data1 = new byte[datagramPacket.getLength()];
                byte[] data2 = datagramPacket.getData();
                System.arraycopy(data2, 0, data1, 0, datagramPacket.getLength());
                SnakesProto.GameMessage gotMessage = SnakesProto.GameMessage.parseFrom(data1);
                System.out.print(datagramPacket.getLength() + "\n");
                System.out.print(datagramPacket.getAddress() +  " " + gotMessage.getAnnouncement().getCanJoin() + " " + gotMessage.getAnnouncement().getPlayers().getPlayersList().size() + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
