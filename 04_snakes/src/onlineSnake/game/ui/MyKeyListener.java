package onlineSnake.game.ui;

import onlineSnake.game.online.Scanner;
import onlineSnake.game.snake.UserSnake;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class MyKeyListener implements KeyListener {
    @Override
    public void keyTyped(KeyEvent keyEvent) {
        switch (keyEvent.getKeyChar()) {
            case ('w') :
            case ('ц') :
                UserSnake.setUserDirection(1);
                break;
            case ('a') :
            case ('ф') :
                UserSnake.setUserDirection(3);
                break;
            case ('s') :
            case ('ы') :
                UserSnake.setUserDirection(2);
                break;
            case ('d') :
            case ('в') :
                UserSnake.setUserDirection(4);
                break;
            case ('l') :
            case ('д') :
                Scanner.scan();
        }
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {

    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {

    }
}
