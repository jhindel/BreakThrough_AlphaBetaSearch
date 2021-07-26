package pieces;

import logic.Board;
import logic.Color;
import logic.Move;
import logic.Tile;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Abstract class defining pieces and all of their possible movements
 */
public abstract class Piece {
    private final Color owner;

    public Piece(Color owner) {
        this.owner = owner;
    }

    public Color getOwner() {
        return owner;
    }

    protected Piece getPieceOnBoard(Board board, int x, int y) {
        return board.getTile(x, y).getPiece();
    }

    /**
     * Produce list of all possible moves of a piece
     *
     * @param b current board position
     * @param x piece 1st coordinate
     * @param y piece 2nd coordinate
     * @return list of moves
     */
    public ArrayList<Move> enumerateAllPossibleMoves(Board b, int x, int y) {
        ArrayList<Move> moves = enumerateAllCaptureMoves(b, x, y);
        moves.addAll(enumerateAllMotionMoves(b, x, y));
        return moves;
    }

    /**
     * Get list of all motion moves for a piece
     *
     * @param b current board position
     * @param x piece 1st coordinate
     * @param y piece 2nd coordinate
     * @return list of moves
     */
    protected ArrayList<Move> enumerateAllMotionMoves(Board b, int x, int y) {
        ArrayList<Move> moves = new ArrayList<>();
        // move in all directions: add motion moves as long as no other piece is encountered
        //motion move left
        int i = x - 1;
        while (addMotionMove(moves, b, x, y, i, y)) {
            i--;
        }

        //motion move right
        i = x + 1;
        while (addMotionMove(moves, b, x, y, i, y)) {
            i++;
        }

        //motion move down
        i = y + 1;
        while (addMotionMove(moves, b, x, y, x, i)) {
            i++;
        }

        //motion move up
        i = y - 1;
        while (addMotionMove(moves, b, x, y, x, i)) {
            i--;
        }

        return moves;
    }

    /**
     * Check if tile new is free, if yes add motion move from original position to new position to move list
     *
     * @param moves list of already produced moves
     * @param b     current board position
     * @param x1    original position: 1st coordinate
     * @param y1    original position: 2nd coordinate
     * @param x2    new position: 1st coordinate
     * @param y2    new position: 2nd coordinate
     * @return boolean value if a valid motion move was added to the list
     */
    private boolean addMotionMove(ArrayList<Move> moves, Board b, int x1, int y1, int x2, int y2) {
        if (b.isOnBoard(x2, y2)
                && !b.getTile(x2, y2).isOccupied()) {
            if (this instanceof Flagship) {
                moves.add(new Move(this, x1, y1, null, x2, y2, true));
            } else {
                moves.add(new Move(this, x1, y1, x2, y2));
            }
            return true;
        }
        return false;
    }

    /**
     * Get list of all caption moves for a piece
     *
     * @param b current board position
     * @param x piece 1st coordinate
     * @param y piece 2nd coordinate
     * @return list of all caption moves
     */
    private ArrayList<Move> enumerateAllCaptureMoves(Board b, int x, int y) {
        ArrayList<Move> moves = new ArrayList<>();

        for (Integer i : Arrays.asList(x - 1, x + 1)) {
            for (Integer j : Arrays.asList(y - 1, y + 1)) {
                if (isCapturable(b, i, j)) {
                    moves.add(new Move(this, x, y, getPieceOnBoard(b, i, j), i, j, true));
                }
            }
        }
        return moves;
    }

    /**
     * Check whether piece can capture piece on tile (x,y)
     *
     * @param board current board position
     * @param x     tile 1st coordinate
     * @param y     tile 2nd coordinate
     * @return boolean value
     */
    public boolean isCapturable(Board board, int x, int y) {
        if (!board.isOnBoard(x, y)) {
            return false;
        }
        Tile tile = board.getTile(x, y);
        return tile.isOccupied() && tile.getPiece().getOwner() != getOwner();
    }

    /**
     * Count tiles which could be captured if a piece is present for a piece (ab: evaluation function feature)
     *
     * @param b current board position
     * @param x piece 1st coordinate
     * @param y piece 2nd coordinate
     * @return counter (1-4)
     */
    public int countControlledSquares(Board b, int x, int y) {
        int count = 0;
        for (Integer i : Arrays.asList(x - 1, x + 1)) {
            for (Integer j : Arrays.asList(y - 1, y + 1)) {
                if (b.isOnBoard(i, j) && (isCapturable(b, i, j) || !b.getTile(i, j).isOccupied())) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Get material score for piece
     *
     * @return game heuristic material value for piece
     */
    public abstract int getScore();

}