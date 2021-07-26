package players;

import logic.Board;
import logic.Color;
import logic.Turn;


public abstract class Player {

    private final Color color;

    public Player(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public abstract Turn getNextTurn(Board b);

}
