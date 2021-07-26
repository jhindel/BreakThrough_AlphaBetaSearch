package pieces;

import logic.Color;

public class Fleetship extends Piece {

    public Fleetship(Color color) {
        super(color);
    }

    @Override
    public String toString() {
        if (getOwner() == Color.GOLD) {
            return "g";
        } else {
            return "s";
        }
    }

    @Override
    public int getScore() {
        if (getOwner() == Color.GOLD) {
            return 3;
        } else {
            return 2;
        }
    }
}
