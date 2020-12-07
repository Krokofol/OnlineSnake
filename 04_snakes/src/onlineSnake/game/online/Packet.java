package onlineSnake.game.online;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Message;

import java.net.InetSocketAddress;

public class Packet {
    com.google.protobuf.GeneratedMessageV3 message;
    InetSocketAddress address;

    public Packet(GeneratedMessageV3 message, InetSocketAddress address) {
        this.message = message;
        this.address = address;
    }
}
