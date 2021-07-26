package players;

import logic.Board;
import logic.Color;
import logic.Turn;

import java.util.ArrayList;
import java.util.Random;

/**
 * Class to produce random agents
 */
public class RandomPlayer extends Player {

    public RandomPlayer(Color color) {
        super(color);
    }
    @Override
    public Turn getNextTurn(Board b) {
        ArrayList<Turn> turns = b.getAllPossibleTurns(getColor());
        int numberTurns = turns.size();
        if (numberTurns == 0)
            return null;
        int pos = (int) (Math.random() * (numberTurns));
        return turns.get(pos);
    }
}
