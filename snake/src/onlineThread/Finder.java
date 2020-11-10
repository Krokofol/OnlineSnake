package onlineThread;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Finder implements Runnable{
    static public String errorCode;

    static public int cooldownSend;
    static public int totalCooldownSend;
    static public int cooldownReceive;
    static public int totalCooldownReceive;

    static public int port_ = 8080;

    static public String message;

    static public SocketAddress multicastAddress;
    static public MulticastSocket socketAddress;

    static public boolean standartInitialization(){
        return initialization("224.0.0.0", port_, 500,2500, "message");
    }

    static public boolean initialization(String address, int port, int totalCooldownSend, int totalCooldownReceive, String message) {
        Finder.message = message;
        if (Finder.initializationAddresses(address, port, totalCooldownSend)) return true;
        return initializationCooldown(totalCooldownSend, totalCooldownReceive);
    }

    static private boolean initializationAddresses(String address, int port, int totalCooldownSend) {
        Finder.multicastAddress = new InetSocketAddress(address, port);
        return Finder.initializationSocketAddress(port, totalCooldownSend);
    }

    public void run(){
        Map<String, Long> copies = new HashMap<>();
        LinkedList<String> defferedCopies = new LinkedList<>();
        long time = System.currentTimeMillis();
        long time1 = System.currentTimeMillis() + 3000;
        while (time1 > System.currentTimeMillis()) {
            updateTime(time);
            time = System.currentTimeMillis();
            String localIp = recieve();
            if (localIp != null)
                copies.put(localIp, System.currentTimeMillis());
            for (Map.Entry<String, Long> entry : copies.entrySet())
                if (System.currentTimeMillis() - entry.getValue() > totalCooldownReceive)
                    defferedCopies.add(entry.getKey());
            for (var value : defferedCopies)
                copies.remove(value);
            if (Finder.cooldownReceive < 0) {
                Finder.cooldownReceive += totalCooldownReceive;
                defferedCopies.clear();
            }
            if (Finder.cooldownSend <= 0)
                if (send(Finder.message)) return;
        }
        boolean iFindSmth = false;
        for (Map.Entry<String, Long> entry : copies.entrySet()) {
            try {
                if (!("LAP" + entry.getKey()).equals(InetAddress.getLocalHost().toString() + ":" + port_)) {
                    System.out.println(entry.getKey());
                    iFindSmth = true;
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
        if (!iFindSmth) System.out.println("NO GAMES");
    }

    static private boolean send(String message) {
        byte[] messByte = message.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(messByte, messByte.length, multicastAddress);
        try {
            socketAddress.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
            Finder.errorCode = "Send error";
            return true;
        }
        Finder.cooldownSend = Finder.totalCooldownSend;
        return false;
    }

    static private String recieve() {
        byte[] messByte = new byte[128];
        DatagramPacket recvPacket = new DatagramPacket(messByte, messByte.length);
        try {
            socketAddress.receive(recvPacket);
        } catch (IOException e) {
            return null;
        }
        return recvPacket.getSocketAddress().toString();
    }

    static private void updateTime(long time) {
        long delta = - time + System.currentTimeMillis();
        Finder.cooldownReceive -= delta;
        Finder.cooldownSend -= delta;
    }

    static private boolean initializationSocketAddress(int port, int totalCooldownSend) {
        try {
            socketAddress = new MulticastSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
            Finder.errorCode = "Error in MulticastSocket initialization";
            return true;
        }
        NetworkInterface networkInterface = null;
        try {
            networkInterface = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
            //System.out.println(InetAddress.getLocalHost().toString());
        } catch (SocketException e) {
            e.printStackTrace();
            Finder.errorCode = "Error in connecting to address";
            return true;
        } catch (UnknownHostException e) {
            e.printStackTrace();
            Finder.errorCode = "Error in getting LocalHost address";
            return true;
        }
        try {
            socketAddress.setSoTimeout(totalCooldownSend);
        } catch (SocketException e) {
            e.printStackTrace();
            Finder.errorCode = "Error in setting timer";
            return true;
        }
        try {
            socketAddress.joinGroup(Finder.multicastAddress, networkInterface);
        } catch (IOException e) {
            e.printStackTrace();
            Finder.errorCode = "Error in getting group";
            return true;
        }
        return false;
    }

    static private boolean initializationCooldown(int totalCooldownSend, int totalCooldownReceive) {
        Finder.cooldownReceive = totalCooldownReceive;
        Finder.totalCooldownReceive = totalCooldownReceive;
        Finder.cooldownSend = 0;
        Finder.totalCooldownSend = totalCooldownSend;
        return false;
    }

}