package onlineSnake.game.proto;

import me.ippolitov.fit.snakes.SnakesProto;
import onlineSnake.game.snake.Field;
import onlineSnake.game.ui.MyFrame;

import java.util.Scanner;

public class Config {
    public static SnakesProto.GameConfig gameConfig;

    public static int height = 20;
    public static int width = 20;
    public static int food_static = 5;
    public static float food_per_player = 0;
    public static int state_delay_ms = 200;
    public static float dead_food_prob = 0;
    public static int ping_delay_ms = 100;
    public static int node_timeout_ms = 800;

    public static void initializeFieldSize() {
        int sizeX;
        int sizeY;
        System.out.print("Field initialization, please enter field size (only numbers) : \n");
        Scanner in = new Scanner(System.in);
        do {
            System.out.print(".... x : ");
            sizeX = in.nextInt();
            System.out.print(".... y : ");
            sizeY = in.nextInt();
        } while (Field.setSize(sizeX, sizeY));
        in.close();
        width = sizeX;
        height = sizeY;
        System.out.print("Field was initialized successfully;\n");
        gameConfig = SnakesProto.GameConfig.newBuilder()
                .setWidth(width)
                .setHeight(height)
                .setFoodStatic(food_static)
                .setFoodPerPlayer(food_per_player)
                .setStateDelayMs(state_delay_ms)
                .setDeadFoodProb(dead_food_prob)
                .setPingDelayMs(ping_delay_ms)
                .setNodeTimeoutMs(node_timeout_ms)
                .build();
        MyFrame.createFrame();
    }

    public static SnakesProto.GameConfig getGameConfig() {
        return gameConfig;
    }
}
