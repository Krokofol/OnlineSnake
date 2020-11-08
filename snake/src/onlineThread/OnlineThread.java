package onlineThread;

import Game.Coordinates;
import Game.Game;
import Game.Snake;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class OnlineThread implements Runnable {
    public static ArrayList<Snake> snakes = new ArrayList<>();

    public static boolean exit = false;
    public static boolean becomeHost = false;

    public static ArrayList<DataInputStream> dataInputStreams = new ArrayList<>();
    public static ArrayList<DataOutputStream> dataOutputStreams = new ArrayList<>();
    public static ArrayList<Socket> sockets = new ArrayList<>();
    public static ServerSocket serverSocket;

    public static void preloadServer(Integer serverPort) {
        try {
            serverSocket = new ServerSocket(serverPort);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("ERROR : CREATING SERVER SOCKET");
            System.exit(0);
        }
        new Thread(new OnlineThread()).start();
    }

    public static void receiveData() throws IOException {
        int speadX = Game.mySnake.speed.x;
        int speadY = Game.mySnake.speed.y;
        Game.mySnake = new Snake();
        Game.mySnake.coordinates = new ArrayList<>();
        int countCoordinates = dataInputStreams.get(0).readInt();
        for (int j = 0; j < countCoordinates; j++)
            Game.mySnake.coordinates.add(new Coordinates(dataInputStreams.get(0).readInt(), dataInputStreams.get(0).readInt()));
        int countSnakes = dataInputStreams.get(0).readInt();
        snakes = new ArrayList<>();
        for (int i = 0; i < countSnakes; i++) {
            snakes.add(new Snake());
            snakes.get(i).coordinates = new ArrayList<>();
            countCoordinates = dataInputStreams.get(0).readInt();
            for (int j = 0; j < countCoordinates; j++)
                snakes.get(i).coordinates.add(new Coordinates(dataInputStreams.get(0).readInt(), dataInputStreams.get(0).readInt()));
        }
        Game.mySnake.speed.x = speadX;
        Game.mySnake.speed.y = speadY;

        Game.food = new ArrayList<>();
        for (int i = 0; i < 5; i++)
            Game.food.add(new Coordinates(dataInputStreams.get(0).readInt(), dataInputStreams.get(0).readInt()));

        dataOutputStreams.get(0).writeInt(Game.mySnake.speed.x);
        dataOutputStreams.get(0).writeInt(Game.mySnake.speed.y);
        dataOutputStreams.get(0).flush();

        if (dataInputStreams.get(0).readBoolean()) {
            String ip;
            int port;
            boolean amINewHost = dataInputStreams.get(0).readBoolean();
            if (!amINewHost) {
                ip = dataInputStreams.get(0).readUTF();
                port = dataInputStreams.get(0).readInt();

                dataInputStreams.get(0).close();
                dataOutputStreams.get(0).close();
                sockets.get(0).close();
                dataInputStreams.remove(0);
                dataOutputStreams.remove(0);
                sockets.remove(0);

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                sockets.add(new Socket(ip, port));
                dataOutputStreams.add(new DataOutputStream(sockets.get(0).getOutputStream()));
                dataInputStreams.add(new DataInputStream(sockets.get(0).getInputStream()));
            }
            else {
                port = dataInputStreams.get(0).readInt();

                dataInputStreams.get(0).close();
                dataOutputStreams.get(0).close();
                sockets.get(0).close();
                dataInputStreams.remove(0);
                dataOutputStreams.remove(0);
                sockets.remove(0);

                becomeHost = true;
                try {
                    Thread.sleep(120);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                serverSocket = new ServerSocket(port);
                new Thread(new OnlineThread()).start();
            }
        }
    }

    public static void sendData(int id) throws IOException {
        sendSnake(id, snakes.get(id));
        dataOutputStreams.get(id).writeInt(snakes.size());
        //System.out.println("I SEND " + snakes.size() + " SNAKES COUNT");
        for (int i = 0; i < snakes.size(); i++) {
            if (i == id) continue;
            sendSnake(id, snakes.get(i));
        }
        sendSnake(id, Game.mySnake);

        for (int i = 0; i < 5; i++) {
            dataOutputStreams.get(id).writeInt(Game.food.get(i).x);
            dataOutputStreams.get(id).writeInt(Game.food.get(i).y);
            dataOutputStreams.get(id).flush();
        }

        snakes.get(id).speed.x = dataInputStreams.get(id).readInt();
        snakes.get(id).speed.y = dataInputStreams.get(id).readInt();

        dataOutputStreams.get(id).writeBoolean(exit);
        dataOutputStreams.get(id).flush();
    }

    private static void sendSnake(int id, Snake snake) throws IOException {
        dataOutputStreams.get(id).writeInt(snake.coordinates.size());
        //System.out.println("COORDINATES : " + snake.coordinates.size());
        for (int j = 0; j < snake.coordinates.size(); j++){
            dataOutputStreams.get(id).writeInt(snake.coordinates.get(j).x);
            dataOutputStreams.get(id).writeInt(snake.coordinates.get(j).y);
        }
        dataOutputStreams.get(id).flush();
    }

    public static void addUser() {
        snakes.add(new Snake());
        try {
            dataInputStreams.add(new DataInputStream(sockets.get(sockets.size()-1).getInputStream()));
            dataOutputStreams.add(new DataOutputStream(sockets.get(sockets.size()-1).getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("ERROR : CREATING STREAMS");
            System.exit(0);
        }
//        try {
//            sendData(sockets.size()-1);
//        } catch (IOException e) {
//            e.printStackTrace();
//            System.out.println("ERROR : SENDING START INFO");
//            System.exit(0);
//        }
    }

    public static void sendServerExit(int i) {
        try {
            dataOutputStreams.get(i).writeBoolean(true);
            dataOutputStreams.get(i).writeInt(8080);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void close(){
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < sockets.size(); i++) {
            try {
                sockets.get(0).close();
                dataOutputStreams.get(0).close();
                dataInputStreams.get(0).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.exit(0);
    }

    public static void sendClientExit(int i) {
        try {
            dataOutputStreams.get(i).writeBoolean(false);
            dataOutputStreams.get(i).writeUTF("127.0.0.1");
            dataOutputStreams.get(i).writeInt(8080);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                sockets.add(serverSocket.accept());
                addUser();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
