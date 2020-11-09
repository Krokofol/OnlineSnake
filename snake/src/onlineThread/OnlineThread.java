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
    public static ArrayList<Snake> zombie = new ArrayList<Snake>();

    public static boolean becomeObserver = false;
    public static boolean observe = false;
    public static boolean exit = false;
    public static boolean becomeHost = false;

    public static ArrayList<DataInputStream> dataInputStreams = new ArrayList<>();
    public static ArrayList<DataOutputStream> dataOutputStreams = new ArrayList<>();
    public static ArrayList<Socket> sockets = new ArrayList<>();

    public static ArrayList<DataInputStream> dataInputStreamsObservers = new ArrayList<>();
    public static ArrayList<DataOutputStream> dataOutputStreamsObservers = new ArrayList<>();
    public static ArrayList<Socket> observers = new ArrayList<>();

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
        if (OnlineThread.becomeObserver) {
            if (!OnlineThread.observe) {
                OnlineThread.observe = true;
                OnlineThread.zombie.add(Game.mySnake);
                Game.mySnake = new Snake();
            }
        }
        else {
            if (OnlineThread.observe) {
                OnlineThread.observe = false;
                Game.mySnake = new Snake();
            }
        }

        dataOutputStreams.get(0).writeBoolean(exit);
        if (exit) System.exit(0);
        dataOutputStreams.get(0).writeBoolean(observe);

        int speadX = 0;
        int speadY = 1;

        if (!observe) {
            speadX = Game.mySnake.speed.x;
            speadY = Game.mySnake.speed.y;
        }
        if (!observe) {
            Game.mySnake = new Snake();
            Game.mySnake.coordinates = new ArrayList<>();
            int countCoordinates = dataInputStreams.get(0).readInt();
            for (int j = 0; j < countCoordinates; j++)
                Game.mySnake.coordinates.add(new Coordinates(dataInputStreams.get(0).readInt(), dataInputStreams.get(0).readInt()));
        }
        int countSnakes = dataInputStreams.get(0).readInt();
        System.out.println("I GOT " + countSnakes + "SNAKES");
        snakes = new ArrayList<>();
        readSnakes(countSnakes, snakes);

        int countZombie = dataInputStreams.get(0).readInt();
        System.out.println("I GOT " + countZombie + "ZOMBIE");
        zombie = new ArrayList<Snake>();
        readSnakes(countZombie, zombie);

        if (!observe) {
            Game.mySnake.speed.x = speadX;
            Game.mySnake.speed.y = speadY;
        }

        Game.food = new ArrayList<>();
        for (int i = 0; i < 5; i++)
            Game.food.add(new Coordinates(dataInputStreams.get(0).readInt(), dataInputStreams.get(0).readInt()));

        if (!observe) {
            dataOutputStreams.get(0).writeInt(Game.mySnake.speed.x);
            dataOutputStreams.get(0).writeInt(Game.mySnake.speed.y);
        }

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
                    Thread.sleep(120);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                sockets.add(new Socket(ip, port));
                System.out.println("RECONECTED");
                dataOutputStreams.add(new DataOutputStream(sockets.get(0).getOutputStream()));
                dataInputStreams.add(new DataInputStream(sockets.get(0).getInputStream()));
                dataOutputStreams.get(0).writeBoolean(observe);
            }
            else {
                boolean isServerObservs = dataInputStreams.get(0).readBoolean();
                port = dataInputStreams.get(0).readInt();

                dataInputStreams.get(0).close();
                dataOutputStreams.get(0).close();
                sockets.get(0).close();
                dataInputStreams.remove(0);
                dataOutputStreams.remove(0);
                sockets.remove(0);

                becomeHost = true;
                if (!isServerObservs) {
                    zombie.add(snakes.get(snakes.size() - 1));
                    snakes.remove(snakes.size() - 1);
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                serverSocket = new ServerSocket(port);
                new Thread(new OnlineThread()).start();
            }
        }
    }

    private static void readSnakes(int countZombie, ArrayList<Snake> zombie) throws IOException {
        int countCoordinates;
        for (int i = 0; i < countZombie; i++) {
            zombie.add(new Snake());
            zombie.get(i).coordinates = new ArrayList<>();
            countCoordinates = dataInputStreams.get(0).readInt();
            for (int j = 0; j < countCoordinates; j++) {
                zombie.get(i).coordinates.add(new Coordinates(dataInputStreams.get(0).readInt(), dataInputStreams.get(0).readInt()));
            }
            zombie.get(i).speed = new Coordinates(dataInputStreams.get(0).readInt(), dataInputStreams.get(0).readInt());
        }
    }

    public static void sendDataPlayers(int id) throws IOException {
        if (dataInputStreams.get(id).readBoolean()) {
            dataInputStreams.remove(id);
            dataOutputStreams.remove(id);
            sockets.remove(id);
            zombie.add(snakes.get(id));
            snakes.remove(id);
            Game.decISockets = true;
            return;
        }
        boolean becomeObserver = dataInputStreams.get(id).readBoolean();
        if (!becomeObserver)
            sendSnake(id, snakes.get(id));
        else {
            zombie.add(snakes.get(id));
            snakes.remove(id);
            observers.add(sockets.get(id));
            dataInputStreamsObservers.add(dataInputStreams.get(id));
            dataOutputStreamsObservers.add(dataOutputStreams.get(id));
        }
        if (!becomeObserver) {
            if (!observe)
                dataOutputStreams.get(id).writeInt(snakes.size());
            else
                dataOutputStreams.get(id).writeInt(snakes.size() - 1);
        }
        else {
            if (!observe)
                dataOutputStreams.get(id).writeInt(snakes.size() + 1);
            else
                dataOutputStreams.get(id).writeInt(snakes.size());
        }

        for (int i = 0; i < snakes.size(); i++) {
            if (i == id) continue;
            sendSnake(id, snakes.get(i));
            dataOutputStreams.get(id).writeInt(snakes.get(i).speed.x);
            dataOutputStreams.get(id).writeInt(snakes.get(i).speed.y);
        }
        if (!observe) {
            sendSnake(id, Game.mySnake);
            dataOutputStreams.get(id).writeInt(Game.mySnake.speed.x);
            dataOutputStreams.get(id).writeInt(Game.mySnake.speed.y);
        }

        dataOutputStreams.get(id).writeInt(zombie.size());
        for (int i = 0; i < zombie.size(); i++) {
            sendSnake(id, zombie.get(i));
            dataOutputStreams.get(id).writeInt(zombie.get(i).speed.x);
            dataOutputStreams.get(id).writeInt(zombie.get(i).speed.y);
        }

        for (int i = 0; i < 5; i++) {
            dataOutputStreams.get(id).writeInt(Game.food.get(i).x);
            dataOutputStreams.get(id).writeInt(Game.food.get(i).y);
            dataOutputStreams.get(id).flush();
        }

        if (!becomeObserver) {
            snakes.get(id).speed.x = dataInputStreams.get(id).readInt();
            snakes.get(id).speed.y = dataInputStreams.get(id).readInt();
        }

        dataOutputStreams.get(id).writeBoolean(exit);
        dataOutputStreams.get(id).flush();

        if (becomeObserver) {
            sockets.remove(id);
            dataOutputStreams.remove(id);
            dataInputStreams.remove(id);
            Game.decISockets = true;
        }
    }

    public static void sendDataObservers(int id) throws IOException {
        if (dataInputStreamsObservers.get(id).readBoolean()) {
            dataInputStreamsObservers.remove(id);
            dataOutputStreamsObservers.remove(id);
            observers.remove(id);
            Game.decIObservers = true;
            return;
        }
        boolean becomeObserver = dataInputStreamsObservers.get(id).readBoolean();
        if (!becomeObserver) {
            snakes.add(new Snake());

            sockets.add(observers.get(id));
            dataOutputStreams.add(dataOutputStreamsObservers.get(id));
            dataInputStreams.add(dataInputStreamsObservers.get(id));

            sendSnakeObservers(id, snakes.get(snakes.size() - 1));
        }


        if (!becomeObserver) {
            if (!observe)
                dataOutputStreamsObservers.get(id).writeInt(snakes.size());
            else
                dataOutputStreamsObservers.get(id).writeInt(snakes.size() - 1);
        }
        else {
            if (!observe)
                dataOutputStreamsObservers.get(id).writeInt(snakes.size() + 1);
            else
                dataOutputStreamsObservers.get(id).writeInt(snakes.size());
        }

        for (int i = 0; i < snakes.size(); i++) {
            if (!becomeObserver && i == snakes.size() - 1) continue;
            sendSnakeObservers(id, snakes.get(i));
            dataOutputStreamsObservers.get(id).writeInt(snakes.get(i).speed.x);
            dataOutputStreamsObservers.get(id).writeInt(snakes.get(i).speed.y);
        }
        if (!observe) {
            sendSnakeObservers(id, Game.mySnake);
            dataOutputStreamsObservers.get(id).writeInt(Game.mySnake.speed.x);
            dataOutputStreamsObservers.get(id).writeInt(Game.mySnake.speed.y);
        }

        dataOutputStreamsObservers.get(id).writeInt(zombie.size());
        for (int i = 0; i < zombie.size(); i++) {
            sendSnakeObservers(id, zombie.get(i));
            dataOutputStreamsObservers.get(id).writeInt(zombie.get(i).speed.x);
            dataOutputStreamsObservers.get(id).writeInt(zombie.get(i).speed.y);
        }

        for (int i = 0; i < 5; i++) {
            dataOutputStreamsObservers.get(id).writeInt(Game.food.get(i).x);
            dataOutputStreamsObservers.get(id).writeInt(Game.food.get(i).y);
            dataOutputStreamsObservers.get(id).flush();
        }

        if (!becomeObserver) {
            snakes.get(id).speed.x = dataInputStreamsObservers.get(id).readInt();
            snakes.get(id).speed.y = dataInputStreamsObservers.get(id).readInt();
        }

        dataOutputStreamsObservers.get(id).writeBoolean(exit);
        dataOutputStreamsObservers.get(id).flush();

        if (!becomeObserver) {
            dataInputStreamsObservers.remove(id);
            dataOutputStreamsObservers.remove(id);
            observers.remove(id);
            Game.decIObservers = true;
        }
    }

    private static void sendSnake(int id, Snake snake) throws IOException {
        sendSnakes(id, snake, dataOutputStreams);
    }

    private static void sendSnakeObservers(int id, Snake snake) throws IOException {
        sendSnakes(id, snake, dataOutputStreamsObservers);
    }

    private static void sendSnakes(int id, Snake snake, ArrayList<DataOutputStream> dataOutputStreamsObservers) throws IOException {
        dataOutputStreamsObservers.get(id).writeInt(snake.coordinates.size());
        for (int j = 0; j < snake.coordinates.size(); j++){
            dataOutputStreamsObservers.get(id).writeInt(snake.coordinates.get(j).x);
            dataOutputStreamsObservers.get(id).writeInt(snake.coordinates.get(j).y);
        }
        dataOutputStreamsObservers.get(id).flush();
    }

    public static void addUser() {
        try {
            dataInputStreams.add(new DataInputStream(sockets.get(sockets.size()-1).getInputStream()));
            dataOutputStreams.add(new DataOutputStream(sockets.get(sockets.size()-1).getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("ERROR : CREATING STREAMS");
            System.exit(0);
        }
        try {
            if (dataInputStreams.get(sockets.size() - 1).readBoolean()) {
                observers.add(sockets.get(sockets.size() - 1));
                dataOutputStreamsObservers.add(dataOutputStreams.get(dataOutputStreams.size() - 1));
                dataInputStreamsObservers.add(dataInputStreams.get(dataInputStreams.size() - 1));
                sockets.remove(sockets.size() - 1);
                dataInputStreams.remove(dataInputStreams.size() - 1);
                dataOutputStreams.remove(dataOutputStreams.size() - 1);
            }
            else
                if(snakes.size() < sockets.size()) snakes.add(new Snake());
        } catch (IOException e) {
            e.printStackTrace();
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
            dataOutputStreams.get(i).writeBoolean(observe);
            dataOutputStreams.get(i).writeInt(8080);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendServerExitObserver(int i) {
        try {
            dataOutputStreamsObservers.get(i).writeBoolean(true);
            dataOutputStreamsObservers.get(i).writeBoolean(observe);
            dataOutputStreamsObservers.get(i).writeInt(8080);
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

    public static void sendClientExitObserver(int i) {
        try {
            dataOutputStreamsObservers.get(i).writeBoolean(false);
            dataOutputStreamsObservers.get(i).writeUTF("127.0.0.1");
            dataOutputStreamsObservers.get(i).writeInt(8080);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                if(!exit) sockets.add(serverSocket.accept());
                addUser();
                System.out.println("USER IS ADDED");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
