package onlineSnake.game.snake;

import me.ippolitov.fit.snakes.SnakesProto;

import java.util.Random;

public class UserSnake extends Snake {
    private static SnakesProto.Direction direction;

    public UserSnake(final int id) {
        super(id);
        setUserDirection(Math.abs(new Random(System.nanoTime()).nextInt()) % 4 + 1);
    }

    @Override
    public void move() {
        this.setDirection(UserSnake.direction.getNumber());
        super.move();
    }

    public static void setUserDirection(final Integer direction) {
        UserSnake.direction = SnakesProto.Direction.forNumber(direction);
    }
}
