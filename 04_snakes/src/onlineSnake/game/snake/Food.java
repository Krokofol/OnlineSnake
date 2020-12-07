package onlineSnake.game.snake;

import me.ippolitov.fit.snakes.SnakesProto;
import onlineSnake.game.ui.MyFrame;

import java.util.ArrayList;

public class Food extends Coordinates{
    private static ArrayList<Food> foodList;

    public Food() {
        super();
        Food.addFood(this);
    }

    public static void getFood(SnakesProto.GameState.Builder builder) {
        for (Food food : foodList) {
            builder.addFoods(food.getCoord());
        }
    }

    public static void draw() {
        if (foodList.size() < 5) {
            for (int i = foodList.size(); i < 5; i++) {
                foodList.add(new Food());
            }
        }
        for (Food food : foodList) {
            MyFrame.myFrame.draw(food.getX(), food.getY(), 200, 140, 0);
        }
    }
    public static Food getFood(final Integer id) {
        return foodList.get(id);
    }
    public static void addFood(Food food) {
        foodList.add(food);
    }

}
