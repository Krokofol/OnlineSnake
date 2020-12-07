package onlineSnake.game.snake;

import onlineSnake.game.ui.MyFrame;

import java.util.Scanner;

public class Field {
    private static Integer sizeX = 20;
    private static Integer sizeY = 20;

    public static boolean setSize(final Integer sizeX, final Integer sizeY) {
        if (sizeX > 75 || sizeY > 40 || sizeX < 1 || sizeY < 1) {
            System.out.print(".... Field size incorrect, try again please\n");
            return true;
        }
        Field.sizeX = sizeX;
        Field.sizeY = sizeY;
        return false;
    }

    public static Integer getSizeX() {
        return sizeX;
    }

    public static Integer getSizeY() {
        return sizeY;
    }
}
