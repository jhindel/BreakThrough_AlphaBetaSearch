package logic;

import pieces.Piece;

/**
 * Class produces tile objects which represent one square on the board.
 */
public class Tile {
    private Piece piece;

    public Tile() {
    }

    public boolean isOccupied() {
        return piece != null;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    public void emptyTile() {
        this.piece = null;
    }

    @Override
    public String toString() {
        if (isOccupied()) {
            return piece.toString();
        }
        return "_";
    }

}