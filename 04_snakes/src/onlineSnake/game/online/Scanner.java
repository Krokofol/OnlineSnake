package onlineSnake.game.online;

import me.ippolitov.fit.snakes.SnakesProto;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Scanner {
    private static ArrayList<DatagramPacket> uniqMessages = new ArrayList<>();
    private static Map<String, DatagramPacket> messages = new HashMap<>();
    private static int counter = 0;

    private static void addMessage(DatagramPacket packet) {
        if (packet == null) return;
        String address = packet.getAddress().toString();
        try {
            byte[] data1 = new byte[packet.getLength()];
            byte[] data2 = packet.getData();
            System.arraycopy(data2, 0, data1, 0, packet.getLength());
            SnakesProto.GameMessage gotMessage = SnakesProto.GameMessage.parseFrom(data1);
            messages.put(address + ":" + findPort(gotMessage.getAnnouncement()), packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void scan() {
        MulticastSocket multicastSocket = null;
        try {
            multicastSocket = new MulticastSocket(8079);
            NetworkInterface IFC = NetworkInterface.getByInetAddress(InetAddress.getByName("224.0.0.0"));
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
            } catch (IOException ignored) {
            }
            System.out.print("+");
        }
        multicastSocket.close();
        getUniq();
    }

    private static void getUniq() {
        uniqMessages = new ArrayList<>();
        for (Map.Entry<String, DatagramPacket> entry : messages.entrySet()) {
            uniqMessages.add(entry.getValue());
            for (Map.Entry<String, DatagramPacket> entry1 : messages.entrySet()) {
                if (entry1.getKey().equals(entry.getKey())) messages.remove(entry1);
            }
        }
        int i = 0;
        for (DatagramPacket datagramPacket : uniqMessages) {
            try {
                byte[] data1 = new byte[datagramPacket.getLength()];
                byte[] data2 = datagramPacket.getData();
                System.arraycopy(data2, 0, data1, 0, datagramPacket.getLength());
                SnakesProto.GameMessage gotMessage = SnakesProto.GameMessage.parseFrom(data1);
                System.out.print("\n" + ++i + ". " + datagramPacket.getAddress().getHostAddress() + ":" + findPort(gotMessage.getAnnouncement()) +  " " + gotMessage.getAnnouncement().getCanJoin() + " " + gotMessage.getAnnouncement().getPlayers().getPlayersList().size());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.print("\n");
    }

    private static int findPort(SnakesProto.GameMessage.AnnouncementMsg msg) {
        SnakesProto.GamePlayers players = msg.getPlayers();
        for (int i = 0; i < players.getPlayersCount(); i++) {
            if (players.getPlayers(i).getRole() == SnakesProto.NodeRole.MASTER) {
                return players.getPlayers(i).getPort();
            }
        }
        return 0;
    }

    public static int getMessagesCount() {
        return uniqMessages.size();
    }
    public static SnakesProto.GameMessage.AnnouncementMsg getConfig(int id) {
        DatagramPacket datagramPacket = uniqMessages.get(id - 1);
        SnakesProto.GameMessage gotMessage = null;
        try {
            byte[] data1 = new byte[datagramPacket.getLength()];
            byte[] data2 = datagramPacket.getData();
            System.arraycopy(data2, 0, data1, 0, datagramPacket.getLength());
            gotMessage = SnakesProto.GameMessage.parseFrom(data1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (gotMessage == null) return null;
        return gotMessage.getAnnouncement();
    }

    public static String getHostIp(int id) {
        return uniqMessages.get(id - 1).getAddress().getHostAddress();
    }

}
