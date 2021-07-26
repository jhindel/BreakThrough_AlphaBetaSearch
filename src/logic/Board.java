package logic;

import pieces.Flagship;
import pieces.Fleetship;
import pieces.Piece;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * The board class implements the logic of the board.
 */
public class Board implements Cloneable {
    public static final int BOARD_SIZE = 11;
    private final Tile[][] board;

    /**
     * Initialize board with array of tiles
     * @param tiles tiles of new board
     */
    public Board(Tile[][] tiles) {
        this.board = tiles;
    }

    /**
     * Initialize board with original game position (flagship in center, each color has 2 vertical and
     * 2 horizontal rows of small ships)
     */
    public Board() {
        // initialize board
        board = new Tile[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = new Tile();
            }
        }
        // flagship
        board[5][5].setPiece(new Flagship());

        // silver fleet
        for (int i = Columns.D.col; i < Columns.I.col; i++) {
            for (Columns j : Arrays.asList(Columns.B, Columns.J)) {
                board[i][j.col].setPiece(new Fleetship(Color.SILVER));
                board[j.col][i].setPiece(new Fleetship(Color.SILVER));
            }
        }
        // gold fleet
        for (int i = Columns.E.col; i < Columns.H.col; i++) {
            for (Columns j : Arrays.asList(Columns.D, Columns.H)) {
                board[i][j.col].setPiece(new Fleetship(Color.GOLD));
                board[j.col][i].setPiece(new Fleetship(Color.GOLD));
            }
        }
    }

    public String toString() {
        String str = "a b c d e f g h i j k \n";
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                str += board[i][j] + " ";
            }
            str += (BOARD_SIZE - i) + "\n";
        }
        str += "a b c d e f g h i j k \n";
        return str;
    }

    /**
     * Get tile on board
     * @param i 1st coordinate of tile
     * @param j 2nd coordinate of tile
     * @return tile
     */
    public Tile getTile(int i, int j) {
        return board[i][j];
    }

    /**
     * Check whether the coordinates are contained in the board
     * @param x 1st coordinate of tile
     * @param y 2nd coordinate of tile
     * @return boolean value
     */
    public boolean isOnBoard(int x, int y) {
        return x < BOARD_SIZE && y < BOARD_SIZE && x >= 0 && y >= 0;
    }

    /**
     * Compute result of board position
     * @return SILVER_WON, GOLD_WON or NONE
     */
    public Result getResult() {
        int[] flagship = findFlagship();
        if (flagship == null) {
            return Result.SILVER_WON;
        } else if (isAtEdge(flagship)) {
            return Result.GOLD_WON;
        }
        return Result.NONE;
    }

    /**
     * Check if position is at the edge of the board
     * @param coordinate coordinates stored in int-array [y-axis, x-axis]
     * @return boolean value
     */
    private boolean isAtEdge(int[] coordinate) {
        return coordinate[0] == 0 || coordinate[1] == 0 ||
                coordinate[0] == BOARD_SIZE - 1 || coordinate[1] == BOARD_SIZE - 1;
    }

    /**
     * Check whether tile is occupied by player
     * @param tile tile to be checked
     * @param owner color of player
     * @return boolean value
     */
    private boolean isPieceOwnedBy(Tile tile, Color owner) {
        return tile.isOccupied() && tile.getPiece().getOwner() == owner;
    }

    /**
     * Retrieve coordinates of the flagship piece
     * @return coordinates stored in int-array [y-axis, x-axis]
     */
    public int[] findFlagship() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j].isOccupied() &&
                        board[i][j].getPiece().getOwner() == Color.GOLD
                        && board[i][j].getPiece() instanceof Flagship) {
                    return new int[]{i, j};
                }
            }
        }
        return null;
    }

    /**
     * Get all possible turns a player can do in this board position
     * @param player player whose turns are regarded
     * @return ArrayList of all possible moves
     */
    public ArrayList<Turn> getAllPossibleTurns(Color player) {
        ArrayList<Turn> turns = new ArrayList<>();
        ArrayList<Move> movesOne = getAllPossibleMoves(player);
        for (Move one : movesOne) {
            if (one.isSingleMove()) {
                turns.add(new Turn(one));
            } else {
                makeMove(one);
                if (getResult() != Result.NONE) {
                    turns.add(new Turn(one));
                    undoMove(one);
                    continue;
                }
                ArrayList<Move> movesTwo = getAllPossibleMoves(player);
                // remove one-move turns and moves that involve the same ship as in move 1 again
                movesTwo.removeIf(Move::isSingleMove);
                movesTwo.removeIf(s -> (s.getX1() == one.getX2() && s.getY1() == one.getY2()));
                for (Move two : movesTwo) {
                    turns.add(new Turn(one, two));
                }
                undoMove(one);
            }
        }
        return turns;
    }

    /**
     * Get all possible moves a player can do in this board position
     * @param player player whose moves are regarded
     * @return ArrayList of all possible moves
     */

    public ArrayList<Move> getAllPossibleMoves(Color player) {
        ArrayList<Move> moves = new ArrayList<>();
        for (int i = 0; i < BOARD_SIZE; i++)
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (isPieceOwnedBy(board[i][j], player)) {
                    moves.addAll(board[i][j].getPiece().enumerateAllPossibleMoves(this, i, j));
                }
            }
        return moves;
    }

    /**
     * Make a turn by making all moves that are incorporated in this turn
     * @param t turn to be done
     */

    public void makeTurn(Turn t) {
        makeMove(t.getFirstMove());
        if (t.hasTwoMoves()) {
            makeMove(t.getSecondMove());
        }
    }

    /**
     * Undo a turn by undoing all moves that are incorporated in this turn
     * @param t turn to be undone
     */

    public void undoTurn(Turn t) {
        if (t.hasTwoMoves()) {
            undoMove(t.getSecondMove());
        }
        undoMove(t.getFirstMove());
    }

    /**
     * Make a move
     * @param m move to be done
     */
    public void makeMove(Move m) {
        Piece piece = m.getPiece();
        board[m.getX2()][m.getY2()].setPiece(piece);
        board[m.getX1()][m.getY1()].emptyTile();
    }

    /**
     * Undo a move
     * @param m move to be undone
     */
    public void undoMove(Move m) {
        Piece previouslyCaptured = m.getCapturedPiece();
        Piece previouslyMoved = m.getPiece();
        board[m.getX1()][m.getY1()].setPiece(previouslyMoved);
        board[m.getX2()][m.getY2()].setPiece(previouslyCaptured);
    }

    /**
     * Count all tiles which could be captured if a piece is present (ab: evaluation function feature)
     * @param player player whose pieces are regarded
     * @return number of controlled tiles
     */

    public int countAllControlledSquares(Color player) {
        int count = 0;
        for (int i = 0; i < BOARD_SIZE; i++)
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (isPieceOwnedBy(board[i][j], player)) {
                    count += board[i][j].getPiece().countControlledSquares(this, i, j);
                }
            }
        return count;
    }

    /**
     * Calculate the average Manhattan distance to the flagship for silver ships (
     * ab: evaluation function feature)
     * @param locFlagship coordinates of the flagship
     * @return normalized average distance to flagship
     */
    public int distanceToFlagship(int[] locFlagship) {
        float dist=0;
        int silver_pieces=0;
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (isPieceOwnedBy(board[i][j], Color.SILVER)) {
                    dist += Math.abs(i-locFlagship[0]) + Math.abs(j-locFlagship[1]);
                    silver_pieces+=1;
                }
            }
        }
        // maximum distance for every piece -> 10+10=20 (100: highest score, 0:lowest score)
        return (int)(100-((dist/silver_pieces)*5));
    }

    /**
     * Calculate the silver neighboring pieces and number of pieces of all color blocking the way to the boarder
     * for the flagship (ab: evaluation function feature)
     * @param locFlagship coordinates of the flagship
     * @return normalized score
     */

    public int piecesToBoarder(int[] locFlagship) {
        int pieces_x = -1;
        int pieces_y = -1;
        int countNeighbors=0;
        int x = locFlagship[0];
        int y = locFlagship[1];
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (board[x][i].isOccupied()) {
                pieces_x +=1;
            }
            if (board[i][y].isOccupied()) {
                pieces_y +=1;
            }
        }
        for (int i=0;i<2;i++){
            if (isPieceOwnedBy(board[x+i][y], Color.SILVER)){
                countNeighbors +=1;
            }
            if (isPieceOwnedBy(board[x][y+1], Color.SILVER)){
                countNeighbors +=1;
            }
        }
        return 6*Math.min(pieces_x, pieces_y) + 10*(4-countNeighbors);

    }

    /**
     * Calculate the number of tiles that no silver ship is encountered from the flagship
     * (ab: evaluation function feature)
     * @param locFlagship coordinates of the flagship
     * @return normalized score
     */

    public int flagshipFreedom (int [] locFlagship){
        int x = locFlagship[0];
        int y = locFlagship[1];
        int freedom=0;
        for (int i = x; i >= 0; i--) {
            if (isPieceOwnedBy(board[i][y], Color.SILVER)) {
                break;
            }
            freedom+= 1;
        }
        for (int i = x; i < BOARD_SIZE; i++) {
            if (isPieceOwnedBy(board[i][y], Color.SILVER)) {
                break;
            }
            freedom+= 1;
        }
        for (int i = y; i >= 0; i--) {
            if (isPieceOwnedBy(board[x][i], Color.SILVER)) {
                break;
            }
            freedom+= 1;
        }
        for (int i = y; i < BOARD_SIZE; i++) {
            if (isPieceOwnedBy(board[x][i], Color.SILVER)) {
                break;
            }
            freedom+= 1;
        }
        // normalized score maximum freedom 10+10=20 (100: best, 0:worst)
        return 5*freedom;
    }

    /**
     * Clone board
     * @return new board having same position as original board
     */
    public Board clone() {
        Tile[][] tilesCopy = new Tile[BOARD_SIZE][BOARD_SIZE];
        for (int x = 0; x < BOARD_SIZE; x++) {
            for (int y = 0; y < BOARD_SIZE; y++) {
                tilesCopy[x][y] = cloneTile(board[x][y]);
            }
        }
        return new Board(tilesCopy);
    }

    /**
     * Clone tiles
     * @param original tile to be cloned
     * @return new tile having same attributes as original tile
     */
    private Tile cloneTile(Tile original) {
        Tile copy = new Tile();
        copy.setPiece(original.getPiece());
        return copy;
    }
}