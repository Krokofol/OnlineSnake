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

    public boolean moveSnakes(int id) {
        if (moveHead()) return true;
        for (int i = 0; i < OnlineThread.snakes.size(); i++) {
            if (i == id) {
                if (compareSnakes(OnlineThread.snakes.get(i), 1))
                    return true;
            }
            else
            if (compareSnakes(OnlineThread.snakes.get(i), 0))
                return true;
        }
        for (int i = 0; i < OnlineThread.zombie.size(); i++) {
            if (compareSnakes(OnlineThread.zombie.get(i), 0))
                return true;
        }
        return false;
    }

    private boolean moveHead() {
        coordinates.add(new Coordinates(coordinates.get(coordinates.size() - 1).x + speed.x, coordinates.get(coordinates.size() - 1).y + speed.y));

        eatFood();

        if (!OnlineThread.observe) {
            return compareSnakes(Game.mySnake, 0);
        }
        return false;
    }

    public boolean moveZombie(int id) {
        if (moveHead()) return true;
        for (int i = 0; i < OnlineThread.snakes.size(); i++) {
            if (compareSnakes(OnlineThread.snakes.get(i), 0)) return true;
        }
        for (int i = 0; i < OnlineThread.zombie.size(); i++) {
            if (i == id) {
                if (compareSnakes(OnlineThread.zombie.get(i), 1)) return true;
            }
            else
                if (compareSnakes(OnlineThread.zombie.get(i), 0)) return true;
        }

        return false;
    }

    private void eatFood() {
        for (int i = 0; i < Game.food.size(); i++)
            if ((Game.food.get(i).x.equals(coordinates.get(coordinates.size() - 1).x)) && (Game.food.get(i).y.equals(coordinates.get(coordinates.size() - 1).y))) {
                Game.food.remove(i);
                Game.food.add(new Coordinates());
                return;
            }
        coordinates.remove(0);
    }

    public boolean compareSnakes(Snake a, int iLast) {
        for (int i = 0; i < a.coordinates.size() - iLast; i++) {
            if (a.coordinates.get(i).equals(coordinates.get(coordinates.size() - 1))) return true;
        }
        return false;
    }

    public boolean move() {
        coordinates.add(new Coordinates(coordinates.get(coordinates.size() - 1).x + speed.x, coordinates.get(coordinates.size() - 1).y + speed.y));

        eatFood();

        for (int i = 0; i < coordinates.size() - 1; i++) {
            if (coordinates.get(i).equals(coordinates.get(coordinates.size() - 1))) return true;
        }
        for (int i = 0; i < OnlineThread.snakes.size(); i++) {
            if (compareSnakes(OnlineThread.snakes.get(i), 0)) return true;
        }
        for (int i = 0; i < OnlineThread.zombie.size(); i++) {
            if (compareSnakes(OnlineThread.zombie.get(i), 0)) return true;
        }

        return false;
    }

}
