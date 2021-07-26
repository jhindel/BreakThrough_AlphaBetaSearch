package logic;

/**
 * Used to create "turn" objects which represent one round for one player (2 motion moves, 1 capture or flagship move)
 */
public class Turn implements Comparable<Turn> {
    private final Move m1;
    private Move m2;
    private boolean twoMoves = false;

    /**
     * Initialize turn made up of 2 motion moves
     *
     * @param m1 first move
     * @param m2 second move
     */
    public Turn(Move m1, Move m2) {
        this.m1 = m1;
        this.m2 = m2;
        this.twoMoves = true;
    }

    /**
     * Initialize turn made up of 1 capture or flagship move
     *
     * @param m1 move
     */
    public Turn(Move m1) {
        this.m1 = m1;
    }

    public Move getFirstMove() {
        return m1;
    }

    public Move getSecondMove() {
        return m2;
    }

    public boolean hasTwoMoves() {
        return twoMoves;
    }

    @Override
    public String toString() {
        if (hasTwoMoves()) {
            return m1.toString() + " " + m2.toString();
        }
        return m1.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || o.getClass() != this.getClass()) {
            return false;
        }
        // turns are equal if they contain the same moves
        if (((Turn) o).getFirstMove().equals(getFirstMove())) {
            if (!((Turn) o).hasTwoMoves() && !this.hasTwoMoves()) {
                return true;
            }
            return ((Turn) o).getSecondMove().equals(getSecondMove());
        }
        return false;

    }

    /**
     * Compare turns for move ordering: turns consisting of single moves are better than turns consisting
     * of two moves and vice versa
     *
     * @param o second regarded turn
     * @return value indicating which turn is better
     */
    @Override
    public int compareTo(Turn o) {
        if (o.hasTwoMoves()) {
            if (this.hasTwoMoves()) {
                return 0;
            }
            return -1;
        }
        if (this.hasTwoMoves()) {
            return 1;
        }
        return 0;
    }
}
