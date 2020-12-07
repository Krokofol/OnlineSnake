package onlineSnake.game.ui;

import onlineSnake.game.snake.Field;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;

public class MyFrame extends JFrame {
    public static MyFrame myFrame;

    private BufferStrategy bufferStrategy;
    private Graphics graphics;
    private Integer cellSize = 25;

    public MyFrame() {
        this.setSize(Field.getSizeX() * cellSize + 25, Field.getSizeY() * cellSize + 25);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setBackground(new Color(200, 200, 200));
        this.setVisible(true);
        this.createBufferStrategy(2);
        this.addKeyListener(new MyKeyListener());
        this.setName("SnakeV2");
    }

    public static void createFrame() {
        myFrame = new MyFrame();
    }

    public void startDraw() {
        bufferStrategy = this.getBufferStrategy();
        graphics = bufferStrategy.getDrawGraphics();
        graphics.clearRect(0,0, Field.getSizeX() * cellSize, Field.getSizeY() * cellSize);
    }
    public void draw(int x, int y, int r, int g, int b) {
        graphics.setColor(new Color(r, g, b));
        graphics.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
        graphics.setColor(new Color(r * 2 / 5, g * 2 / 5, b * 2 / 5));
        graphics.fillRect(x * cellSize + 2, y * cellSize + 2, cellSize - 4, cellSize - 4);
    }
    public void endDraw() {
        bufferStrategy.show();
    }

}
