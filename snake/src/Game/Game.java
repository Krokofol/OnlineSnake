package Game;

import onlineThread.OnlineThread;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class Game {
    public static MyFrame myFrame;
    public static Snake mySnake;
    public static ArrayList<Coordinates> food = new ArrayList<Coordinates>();

    public static void startNewGame(String[] args) {
        OnlineThread.preloadServer(Integer.parseInt(args[0]));
        myFrame = new MyFrame(20, 20);
        mySnake = new Snake();
        for (int i = 0; i < 5; i++)
            food.add(new Coordinates());
        runServer();
    }

    public static void connectToGame(String[] args) {
        myFrame = new MyFrame(20, 20);
        mySnake = new Snake();
        try {
            OnlineThread.sockets.add(new Socket(args[0], Integer.parseInt(args[1])));
            OnlineThread.dataInputStreams.add(new DataInputStream(OnlineThread.sockets.get(0).getInputStream()));
            OnlineThread.dataOutputStreams.add(new DataOutputStream(OnlineThread.sockets.get(0).getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("ERROR : CONNECTING");
            System.exit(0);
        }
        try {
            OnlineThread.receiveData();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("ERROR : RECEIVING DATA");
            System.exit(0);
        }
        runClient();
    }

    private static void runClient() {
        do {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                OnlineThread.receiveData();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("ERROR : RECEIVING DATA");
                System.exit(0);
            }
            if (OnlineThread.becomeHost) break;
            myFrame.draw();
        } while (!OnlineThread.becomeHost);
        runServer();
    };

    public static void runServer() {
        while(true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(mySnake.move()) mySnake = new Snake();
            for (int i = 0; i < OnlineThread.snakes.size(); i++)
                if (OnlineThread.snakes.get(i).move()) OnlineThread.snakes.set(i, new Snake());
            for (int i = 0; i < OnlineThread.sockets.size(); i++)
                try {
                    OnlineThread.sendData(i);
                    if (OnlineThread.exit)
                        if (i == 0) OnlineThread.sendServerExit(i);
                        else OnlineThread.sendClientExit(i);
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("ERROR : SENDING DATA FROM LOOP");
                    System.exit(0);
                }
            if (OnlineThread.exit) OnlineThread.close();
            myFrame.draw();
        }
    }

    public static void main(String[] args) {
        if(args.length == 1) startNewGame(args);
        if(args.length == 2) connectToGame(args);
    }

}
