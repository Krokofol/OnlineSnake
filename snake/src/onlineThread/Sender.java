package onlineThread;

import java.io.IOException;
import java.net.DatagramPacket;

public class Sender implements Runnable {


    public void run() {
        while (true) {
            String message = "message";
            byte[] messByte = message.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(messByte, messByte.length, Finder.multicastAddress);
            try {
                Finder.socketAddress.send(sendPacket);
            } catch (IOException e) {
                e.printStackTrace();
                Finder.errorCode = "Send error";
                continue;
            }
            Finder.cooldownSend = Finder.totalCooldownSend;
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
