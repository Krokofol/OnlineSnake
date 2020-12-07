package onlineSnake.game.online;

import onlineSnake.game.Game;

import java.io.IOException;
import java.net.*;

public class Receiver implements Runnable{

    public void run() {
        MulticastSocket socket = null;
        try {
            socket = new MulticastSocket(8079);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] data = new byte[2048];
        DatagramPacket datagramPacket = new DatagramPacket(data, data.length);
        long timeStart = System.currentTimeMillis();
        while (timeStart > System.currentTimeMillis() - 1000) {
            System.out.print("-");
            try {
                socket.receive(datagramPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.print("+");
        }
        socket.close();
    }
}
