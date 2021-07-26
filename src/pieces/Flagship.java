package pieces;

import logic.Color;


public class Flagship extends Piece {

    public Flagship() {
        super(Color.GOLD);
    }

    @Override
    public String toString() {
        return "f";
    }

    @Override
    public int getScore() {
        return 4;
    }
}
