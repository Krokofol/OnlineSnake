package onlineSnake.game.snake;

import me.ippolitov.fit.snakes.SnakesProto;
import onlineSnake.game.ui.MyFrame;

import java.util.ArrayList;
import java.util.Random;

public class Snake {
    private static ArrayList<Snake> snakeList = new ArrayList<>();
    private SnakesProto.GameState.Snake.SnakeState state;
    private Integer id;
    private SnakesProto.Direction direction;
    private ArrayList<Coordinates> body;

    public Snake(final Integer id) {
        state = SnakesProto.GameState.Snake.SnakeState.ALIVE;
        this.id = id;
        body = new ArrayList<>();
        body.add(new Coordinates());
        setDirection(Math.abs(new Random(System.nanoTime()).nextInt()) % 4 + 1);
    }

    public void upDate() {
        this.move();
        this.draw();
    }

    public void move() {
        body.add(0, new Coordinates(direction, body.get(0)));
    }

    private void draw() {
        if (this.state == SnakesProto.GameState.Snake.SnakeState.ALIVE) {
            for (Coordinates coordinates : body) {
                MyFrame.myFrame.draw(coordinates.getX(), coordinates.getY(), 0, 200, 50);
            }
            return;
        }
        for (Coordinates coordinates : body) {
            MyFrame.myFrame.draw(coordinates.getX(), coordinates.getY(), 120, 120, 120);
        }
    }

    public static void getSnakes(SnakesProto.GameState.Builder builder) {
        for (Snake snake : snakeList) {
            builder.addSnakes(snake.getSnake());
        }
    }

    public SnakesProto.GameState.Snake getSnake() {
        SnakesProto.GameState.Snake.Builder builder = SnakesProto.GameState.Snake.newBuilder();
        builder.setPlayerId(this.id);
        builder.addPoints(this.body.get(0).getCoord());
        for (int i = 1; i < this.body.size(); i++) {
            builder.addPoints(this.body.get(i).getCoord(this.body.get(i - 1)));
        }
        builder.setState(this.state);
        builder.setHeadDirection(this.direction);
        return builder.build();
    }

    public void setDirection(final Integer direction) {
        this.direction = SnakesProto.Direction.forNumber(direction);
    }
    public SnakesProto.Direction getDirection() {
        return direction;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getId() {
        return id;
    }

    public Integer getBodySize() {
        return body.size();
    }
    public static Integer getSnakeListSize() {
        return snakeList.size();
    }
    public Coordinates getBodyCoordinates(final int id) {
        return body.get(id);
    }
    public static void addSnake(final Snake snake) {
        snakeList.add(snake);
    }
    public static Snake getSnake(final Integer id) {
        return snakeList.get(id);
    }
    public static void deleteSnake(final Integer id) {
        snakeList.remove(id);
    }
    public static void clearList() {
        snakeList = new ArrayList<>();
    }
}
