package Game;

import onlineThread.OnlineThread;

import java.util.ArrayList;
import java.util.Random;

public class Snake {

    private static Random random = new Random(System.currentTimeMillis());
    //координаты всех клеток змейки
    public ArrayList<Coordinates> coordinates = new ArrayList<Coordinates>();
    //скорость змейки
    public Coordinates speed;

    public Snake(){
        coordinates.add(new Coordinates());
        speed = new Coordinates();
        setSpeed(Math.abs(random.nextInt()) % 4);
    }

    public void setSpeed(int a) {
        switch (a) {
            case(0) : if (speed.y != -1) { speed.x = 0; speed.y = 1; } break;
            case(1) : if (speed.x != -1) { speed.x = 1; speed.y = 0; } break;
            case(2) : if (speed.y != 1) { speed.x = 0; speed.y = -1; } break;
            case(3) : if (speed.x != 1) { speed.x = -1; speed.y = 0; } break;
        }
    }

    public boolean move() {
        coordinates.add(new Coordinates(coordinates.get(coordinates.size() - 1).x + speed.x, coordinates.get(coordinates.size() - 1).y + speed.y));

        for (int i = 0; i < Game.food.size(); i++)
            if ((Game.food.get(i).x.equals(coordinates.get(coordinates.size() - 1).x)) && (Game.food.get(i).y.equals(coordinates.get(coordinates.size() - 1).y))) {
                Game.food.remove(i);
                Game.food.add(new Coordinates());
                return false;
            }
        coordinates.remove(0);
        return false;
    }

}
