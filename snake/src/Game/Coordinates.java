package Game;

import java.util.Random;

public class Coordinates {

    private static Random random = new Random(System.currentTimeMillis());

    public Integer x;
    public Integer y;

    public Coordinates() {
        x = Math.abs(random.nextInt()) % 20;
        y = Math.abs(random.nextInt()) % 20;
    }

    public boolean equals (Coordinates a) {
        if (a.x == x && a.y == y) return true;
        return false;
    }

    public Coordinates(Integer x_, Integer y_) {
        x =
                (x_
                        +
                Field.size.x) %
                Field.size.x;
        y = (y_ + Field.size.y) % Field.size.y;
    }
}
