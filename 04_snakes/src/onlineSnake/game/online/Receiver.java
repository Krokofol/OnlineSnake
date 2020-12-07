package onlineSnake.game.online;

import onlineSnake.game.Game;

import java.io.IOException;
import java.net.*;

public class Receiver {

    public static void run() {
        MulticastSocket multicastSocket = null;
        try {
            multicastSocket = new MulticastSocket(8079);
            NetworkInterface IFC = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
            multicastSocket.setSoTimeout(300);
            multicastSocket.joinGroup(new InetSocketAddress("224.0.0.0", 8079), IFC);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] data = new byte[2048];
        DatagramPacket datagramPacket = new DatagramPacket(data, data.length);
        long timeStart = System.currentTimeMillis();
        while (timeStart > System.currentTimeMillis() - 300) {
            System.out.print("-");
            try {
                multicastSocket.receive(datagramPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.print("+");
        }
        multicastSocket.close();
    }
}
