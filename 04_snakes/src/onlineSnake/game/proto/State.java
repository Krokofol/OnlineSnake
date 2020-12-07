package onlineSnake.game.proto;

import me.ippolitov.fit.snakes.SnakesProto;
import onlineSnake.game.snake.Food;
import onlineSnake.game.snake.Snake;

public class State {
    private static int stateOrder = 0;

    public static SnakesProto.GameState getState() {
        SnakesProto.GameState.Builder builder = SnakesProto.GameState.newBuilder();
        builder.setStateOrder(stateOrder++);
        Snake.getSnakes(builder);
        Food.getFood(builder);
        builder.setPlayers(Player.getGamePlayers());
        builder.setConfig(Config.getGameConfig());
        return builder.build();
    }

}
