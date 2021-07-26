package logic;

import pieces.Piece;

/**
 * Used to create "move" objects which save information of one piece moving from one location to another
 */
public class Move {
    private final Piece piece;
    private final int x1;
    private final int y1;
    private final int x2;
    private final int y2;
    private boolean singleMove = false;
    private Piece capturedPiece = null;

    /**
     * Initialize capture or flagship moves
     *
     * @param piece         piece which is moved
     * @param x1            original position: 1st coordinate
     * @param y1            original position: 2nd coordinate
     * @param capturedPiece piece which is captured (if any)
     * @param x2            new position: 1st coordinate
     * @param y2            new position: 2nd coordinate
     * @param singleMove    boolean value
     */
    public Move(Piece piece, int x1, int y1, Piece capturedPiece, int x2, int y2, boolean singleMove) {
        this.piece = piece;
        this.capturedPiece = capturedPiece;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.singleMove = singleMove;
    }

    /**
     * Initialize motion move
     *
     * @param piece piece which is moved
     * @param x1    original position: 1st coordinate
     * @param y1    original position: 2nd coordinate
     * @param x2    new position: 1st coordinate
     * @param y2    new position: 2nd coordinate
     */
    public Move(Piece piece, int x1, int y1, int x2, int y2) {
        this.piece = piece;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public int getX1() {
        return x1;
    }

    public int getX2() {
        return x2;
    }

    public int getY1() {
        return y1;
    }

    public int getY2() {
        return y2;
    }

    public boolean isSingleMove() {
        return singleMove;
    }

    public Piece getPiece() {
        return piece;
    }

    public Piece getCapturedPiece() {
        return capturedPiece;
    }

    @Override
    public String toString() {
        return Columns.values()[this.y1] + Integer.toString(Board.BOARD_SIZE - this.x1)
                + Columns.values()[this.y2] + (Board.BOARD_SIZE - this.x2);
    }

    @Override
    public boolean equals(Object m) {
        if (m == this) {
            return true;
        }
        if (m == null || m.getClass() != this.getClass()) {
            return false;
        }
        // moves are equal if all of their attributes are equal
        return (((((Move) m).getX1() == this.x1) && (((Move) m).getX2() == this.x2) && (((Move) m).getY1() == this.y1) && (((Move) m).getY2() == this.y2) &&
                (((Move) m).isSingleMove() == this.isSingleMove()) && (((Move) m).getPiece() == getPiece())
                && (((Move) m).getCapturedPiece() == getCapturedPiece())));
    }

}