package onlineSnake.game.proto;

import me.ippolitov.fit.snakes.SnakesProto;
import onlineSnake.game.Game;
import onlineSnake.game.online.Scanner;
import onlineSnake.game.online.Sender;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class Player {
    public static Player getPlayer(int id) {
        for (Player player : players) {
            if (player.getId() == id) return player;
        }
        return null;
    }

    public static ArrayList<Player> players = new ArrayList<Player>();

    public static SnakesProto.GamePlayers getGamePlayers() {
        SnakesProto.GamePlayers.Builder builder = SnakesProto.GamePlayers.newBuilder();
        for (Player player : players) {
            builder.addPlayers(player.getGamePlayer());
        }
        return builder.build();
    }

    private String name;
    private int id;
    private String ip_address;
    private int port;
    private SnakesProto.NodeRole role;
    private int score;

    public static void addPlayers(int id) {
        int startSize = players.size();
        SnakesProto.GamePlayers gamePlayers = Scanner.getConfig(id).getPlayers();
        for (int i = 0; i < gamePlayers.getPlayersCount(); i++) {
            Player.players.add(new Player(gamePlayers.getPlayers(i)));
            if (players.get(i + startSize).role == SnakesProto.NodeRole.MASTER) {
                players.get(i + startSize).ip_address = Scanner.getHostIp(id);
                SnakesProto.GameMessage.JoinMsg msg = SnakesProto.GameMessage.JoinMsg.newBuilder()
                        .setName(Player.getPlayer(Game.playerId).name)
                        .build();
                Sender.addMessage(msg, 7);
            }
        }
    }

    public Player(){
        id = Math.abs(new Random().nextInt());
    }

    public Player(SnakesProto.GamePlayer player) {
        name = player.getName();
        id = player.getId();
        role = player.getRole();
        ip_address = player.getIpAddress();
        port = player.getPort();
        score = player.getScore();
    }

    public SnakesProto.GamePlayer getGamePlayer() {
        return SnakesProto.GamePlayer.newBuilder()
                .setId(id)
                .setName(name)
                .setIpAddress(ip_address)
                .setPort(port)
                .setRole(role)
                .setScore(score)
                .build();
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public SnakesProto.NodeRole getRole() {
        return role;
    }

    public int getPort() {
        return port;
    }

    public String getIp_address() {
        return ip_address;
    }

    public int getScore() {
        return score;
    }

    public String getName() {
        return name;
    }

    public void setIp_address(String ip_address) {
        this.ip_address = ip_address;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setRole(SnakesProto.NodeRole role) {
        this.role = role;
    }

    public void setScore(int score) {
        this.score = score;
    }
    public void addScore() {
        score++;
    }
}
