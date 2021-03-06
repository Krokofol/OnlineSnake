package Game;

import onlineThread.OnlineThread;
import onlineThread.Finder;
import onlineThread.Sender;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Game {
    public static boolean decIObservers = false;
    public static boolean decISockets = false;

    public static MyFrame myFrame;
    public static Snake mySnake;
    public static ArrayList<Coordinates> food = new ArrayList<Coordinates>();

    public static void startNewGame(int port, String observe) {
        OnlineThread.preloadServer(port);
        myFrame = new MyFrame(20, 20);
        mySnake = new Snake();
        for (int i = 0; i < 5; i++)
            food.add(new Coordinates());
        runServer();
    }

    public static void connectToGame(String ip, int port) {
        myFrame = new MyFrame(20, 20);
        mySnake = new Snake();
        try {
            OnlineThread.sockets.add(new Socket(ip, port));
            OnlineThread.dataInputStreams.add(new DataInputStream(OnlineThread.sockets.get(0).getInputStream()));
            OnlineThread.dataOutputStreams.add(new DataOutputStream(OnlineThread.sockets.get(0).getOutputStream()));
            OnlineThread.dataOutputStreams.get(0).writeBoolean(OnlineThread.observe);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("ERROR : CONNECTING");
            System.exit(0);
        }
    }

    private static void runClient() {
        System.out.println(OnlineThread.observe);
        int timeStart = 0;
        int timeEnd = 0;
        do {
            if (timeEnd - timeStart < 100)
                try {
                    Thread.sleep(100 - (timeEnd - timeStart));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            timeStart = (int) System.currentTimeMillis();
            try {
                OnlineThread.receiveData();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("ERROR : RECEIVING DATA");
                System.exit(0);
            }
            if (OnlineThread.becomeHost) break;
            myFrame.draw();
            timeEnd = (int) System.currentTimeMillis();
        } while (!OnlineThread.becomeHost);
        runServer();
    };

    public static void runServer() {
        int timeStart = 0;
        int timeEnd = 0;
        while(true) {
            if (timeEnd - timeStart < 100)
                try {
                    Thread.sleep(100 - (timeEnd - timeStart));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            timeStart = (int) System.currentTimeMillis();

            if (!OnlineThread.observe)
                if (mySnake.move()) mySnake = new Snake();
            for (int i = 0; i < OnlineThread.snakes.size(); i++)
                if (OnlineThread.snakes.get(i).moveSnakes(i))
                    OnlineThread.snakes.set(i, new Snake());
            for (int i = 0; i < OnlineThread.zombie.size(); i++)
                if (OnlineThread.zombie.get(i).moveZombie(i)) {
                    OnlineThread.zombie.remove(i);
                    i--;
                }

            for (int i = 0; i < OnlineThread.sockets.size(); i++)
                try {
                    decISockets = false;
                    OnlineThread.sendDataPlayers(i);
                    if (decISockets) i--;
                    if (OnlineThread.exit)
                        if (i == 0) OnlineThread.sendServerExit(i);
                        else OnlineThread.sendClientExit(i);
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("ERROR : SENDING DATA FROM LOOP");
                    System.exit(0);
                }
            for (int i = 0; i < OnlineThread.observers.size(); i++) {
                try {
                    decIObservers = false;
                    OnlineThread.sendDataObservers(i);
                    if (decIObservers) i--;

                    if (OnlineThread.exit)
                        if (i == 0 && OnlineThread.sockets.size() == 0) OnlineThread.sendServerExitObserver(i);
                        else OnlineThread.sendClientExitObserver(i);
                }
                catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("ERROR : SENDING DATA FROM LOOP TO OBSERVERS");
                    System.exit(0);
                }
            }
            if (OnlineThread.exit) OnlineThread.close();
            myFrame.draw();
            if (OnlineThread.becomeObserver) {
                if (!OnlineThread.observe) {
                    OnlineThread.observe = true;
                    OnlineThread.zombie.add(mySnake);
                    mySnake = new Snake();
                }
            }
            else {
                if (OnlineThread.observe) {
                    OnlineThread.observe = false;
                    mySnake = new Snake();
                }
            }
            timeEnd = (int) System.currentTimeMillis();
        }
    }

    public static void main(String[] args) {

        System.out.print("Enter port : ");
        Scanner in = new Scanner(System.in);
        int port = in.nextInt();
        Finder.port_ = port;

        if (Finder.standartInitialization()) {
            System.out.println(Finder.errorCode);
            System.exit(1);
        }
        Finder finder = new Finder();
        finder.run();
        new Thread (new Sender()).start();

        String ip = "0";

        if (!Finder.iFindSmth) {
            System.out.println("Enter observe or play");
            ip = in.next();
            OnlineThread.observe = "observe".equals(ip);
            OnlineThread.becomeObserver = OnlineThread.observe;
            startNewGame(port, ip);
        }
        else {
            System.out.println("Enter ip to connect or 0 to create a game");
            ip = in.next();
            if ("0".equals(ip)) {
                System.out.println("Enter observe or play");
                ip = in.next();
                OnlineThread.observe = "observe".equals(ip);
                OnlineThread.becomeObserver = OnlineThread.observe;
                startNewGame(port, ip);
            }
            else {
                System.out.println("Enter observe or play");
                String observe = in.next();
                OnlineThread.observe = "observe".equals(observe);
                OnlineThread.becomeObserver = OnlineThread.observe;
                connectToGame(ip, port);
                runClient();
            }
        }
//        OnlineThread.observe = Boolean.parseBoolean(args[args.length - 1]);
//        OnlineThread.becomeObserver = OnlineThread.observe;
//        if(args.length == 2) startNewGame(args);
//        if(args.length == 3) {
//            connectToGame(args);
//            runClient();
//        }
    }

}
