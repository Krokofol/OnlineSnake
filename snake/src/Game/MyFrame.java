package Game;

import onlineThread.OnlineThread;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;

public class MyFrame extends JFrame {

    public MyFrame(Integer width, Integer height){
        Field.size.x = width;
        Field.size.y = height;
        this.setSize(Field.size.x * 20, Field.size.y * 20 + 20);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setBackground(new Color(220, 220, 220));
        this.setVisible(true);
        this.createBufferStrategy(2);
        this.addKeyListener(new MyKeyListener());
    }

    public void draw() {
        BufferStrategy bufferStrat = this.getBufferStrategy();
        Graphics g1 = bufferStrat.getDrawGraphics();

        g1.clearRect(0, 0, Field.size.x * 20, Field.size.y * 20 + 20);

        drawFood(g1);
        drawSnake(g1);

        bufferStrat.show();
    }

    private void drawSnake(Graphics g1) {
        if (!OnlineThread.observe) {
            for (int i = 0; i < Game.mySnake.coordinates.size(); i++) {
                int x = Game.mySnake.coordinates.get(i).x * 20;
                int y = Game.mySnake.coordinates.get(i).y * 20 + 20;
                g1.setColor(new Color(0, 80, 0));
                g1.fillRect(x, y, 20, 20);
                g1.setColor(new Color(0, 200, 50));
                g1.fillRect(x + 1, y + 1, 18, 18);
            }
        }
        for (int j = 0; j < OnlineThread.snakes.size(); j++) {
            for (int i = 0; i < OnlineThread.snakes.get(j).coordinates.size(); i++) {
                int x = OnlineThread.snakes.get(j).coordinates.get(i).x * 20;
                int y = OnlineThread.snakes.get(j).coordinates.get(i).y * 20 + 20;
                g1.setColor(new Color(0,0,80));
                g1.fillRect(x, y, 20, 20);
                g1.setColor(new Color(0,50,200));
                g1.fillRect(x + 1, y + 1, 18, 18);
            }
        }
        for (int i = 0; i < OnlineThread.zombie.size(); i++) {
            for (int j = 0; j < OnlineThread.zombie.get(i).coordinates.size(); j++) {
                int x = OnlineThread.zombie.get(i).coordinates.get(j).x * 20;
                int y = OnlineThread.zombie.get(i).coordinates.get(j).y * 20 + 20;
                g1.setColor(new Color(40,40,40));
                g1.fillRect(x, y, 20, 20);
                g1.setColor(new Color(120,120,120));
                g1.fillRect(x + 1, y + 1, 18, 18);

            }
        }
    }

    private void drawFood(Graphics g1) {
        g1.setColor(new Color(255,150,0));
        for(int i = 0; i < Game.food.size(); i++) {
            int x = Game.food.get(i).x * 20;
            int y = Game.food.get(i).y * 20 + 20;
            g1.fillOval(x, y, 20, 20);
        }
    }
}

