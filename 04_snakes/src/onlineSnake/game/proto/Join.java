package onlineSnake.game.proto;

import me.ippolitov.fit.snakes.SnakesProto;
import onlineSnake.game.Game;

public class Join {

    public static SnakesProto.GameMessage.JoinMsg getJoin() {
        return SnakesProto.GameMessage.JoinMsg.newBuilder()
                .setPlayerType(SnakesProto.PlayerType.HUMAN)
                .setOnlyView(false)
                .setName(Game.player.getName())
                .build();
    }
}
