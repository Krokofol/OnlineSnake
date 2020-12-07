package onlineSnake.game.snake;

import me.ippolitov.fit.snakes.SnakesProto;

import java.awt.*;
import java.util.Random;

public class Coordinates {
    private Integer x;
    private Integer y;

    public Coordinates() {
        setX(Math.abs(new Random(System.nanoTime()).nextInt()));
        setY(Math.abs(new Random(System.nanoTime()).nextInt()));
    }

    public Coordinates(Integer x, Integer y) {
        this.setX(x);
        this.setY(y);
    }

    public Coordinates(SnakesProto.Direction direction, Coordinates coordinates) {
        int newX = coordinates.x;
        int newY = coordinates.y;
        switch (direction.getNumber()) {
            case(1) :
                newX--;
                break;
            case(2) :
                newX++;
                break;
            case(3) :
                newY--;
                break;
            case(4) :
                newY++;
                break;
        }
        this.setX(newX);
        this.setY(newY);
    }

    public void setY(Integer y) {
        if (Field.getSizeY() == null) {
            System.out.println("Field size \"Y\" is not initialized\n");
            System.exit(101);
        }
        if (Field.getSizeY() == 0) {
            System.out.println("Field size \"Y\" is 0 (min 1)");
            System.exit(102);
        }
        this.y = (y + Field.getSizeY()) % Field.getSizeY();
    }
    public void setX(Integer x) {
        if (Field.getSizeX() == null) {
            System.out.println("Field size \"X\" is not initialized\n");
            System.exit(101);
        }
        if (Field.getSizeX() == 0) {
            System.out.println("Field size \"X\" is 0 (min 1)");
            System.exit(102);
        }
        this.x = (x + Field.getSizeX()) % Field.getSizeX();
    }

    public boolean checkCollision(Coordinates coordinates) {
        return coordinates.x.equals(this.x) && coordinates.y.equals(this.y);
    }

    public final Integer getX() {
        return x;
    }
    public final Integer getY() {
        return y;
    }

    public SnakesProto.GameState.Coord getCoord() {
        return SnakesProto.GameState.Coord.newBuilder()
                .setX(this.x)
                .setY(this.y)
                .build();
    }

    public SnakesProto.GameState.Coord getCoord(Coordinates coordinates) {
        return SnakesProto.GameState.Coord.newBuilder()
                .setX(this.x - coordinates.x)
                .setY(this.y - coordinates.y)
                .build();
    }
}
