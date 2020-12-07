package onlineSnake.game.online;

import onlineSnake.game.Game;
import onlineSnake.game.proto.Config;

import java.io.IOException;
import java.net.*;

public class Receiver {

    public static void run() {
        MulticastSocket multicastSocket = null;
        try {
            multicastSocket = new MulticastSocket(8079);
            NetworkInterface IFC = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
            multicastSocket.setSoTimeout(Config.ping_delay_ms * 5);
            multicastSocket.joinGroup(new InetSocketAddress("224.0.0.0", 8079), IFC);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] data = new byte[2048];
        DatagramPacket datagramPacket = new DatagramPacket(data, data.length);
        long timeStart = System.currentTimeMillis();
        while (timeStart > System.currentTimeMillis() - Config.ping_delay_ms * 5) {
            System.out.print("-");
            try {
                multicastSocket.receive(datagramPacket);
            } catch (IOException ignored) {}
            System.out.print("+");
        }
        multicastSocket.close();
    }
}
