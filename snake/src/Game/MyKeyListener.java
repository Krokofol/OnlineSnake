package Game;

import onlineThread.OnlineThread;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class MyKeyListener implements KeyListener {

    public MyKeyListener(){}

    @Override
    public void keyTyped(KeyEvent keyEvent) {
        switch (keyEvent.getKeyChar()) {
            case('s') :
            case('ы') :
                Game.mySnake.setSpeed(0); break;
            case('d') :
            case('в') :
                Game.mySnake.setSpeed(1); break;
            case('w') :
            case('ц') :
                Game.mySnake.setSpeed(2); break;
            case('a') :
            case('ф') :
                Game.mySnake.setSpeed(3); break;
            case('q') :
            case('й') :
                OnlineThread.exit = true; break;
            case ('o') :
            case ('щ') :
                if (!OnlineThread.observe) {
                    OnlineThread.observe = true;
                    OnlineThread.zombie.add(Game.mySnake);
                    Game.mySnake = null;
                }
                break;
            case ('p') :
            case ('з') :
                OnlineThread.observe = false;
                Game.mySnake = new Snake();
                break;
        }
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {

    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {

    }
}
