package players.alphabeta;

import logic.Board;
import logic.Color;
import logic.Move;
import logic.Turn;
import pieces.Piece;

/**
 * Class to organize transposition tables
 */
public class TT {
    private final int pieces = 3;
    // random number for gold and silver player
    private final long silver;
    private final long gold;
    private final long[][][] randomBoard = new long[Board.BOARD_SIZE][Board.BOARD_SIZE][pieces];
    // 20 bits used as key (2^20 entries)
    private final TTentry[] TT = new TTentry[1048576];

    /**
     * Initialize transposition tables
     */
    public TT() {
        long upperBound = Long.MAX_VALUE;
        silver = (long) (Math.random() * (upperBound));
        gold =  (long) (Math.random() * (upperBound));
        initializeRandomBoard();
    }

    /**
     * Initialize randomBoard which is used for translations from Board b to hash value
     */
    public void initializeRandomBoard() {
        long upperBound = Long.MAX_VALUE;
        for (int i = 0; i < Board.BOARD_SIZE; i++) {
            for (int j = 0; j < Board.BOARD_SIZE; j++) {
                for (int k = 0; k < pieces; k++) {
                    randomBoard[i][j][k] = (long) (Math.random() * (upperBound));
                }
            }
        }
    }

    /**
     * Calculate hash value for board position
     * @param b Board position
     * @param c Color of player for who board will be analyzed
     * @return hash value of board position
     */

    public long getHashBoard(Board b, Color c) {
        long hash = 0;
        for (int i = 0; i < Board.BOARD_SIZE; i++) {
            for (int j = 0; j < Board.BOARD_SIZE; j++) {
                if (b.getTile(i, j).isOccupied()) {
                    int pieceValue = b.getTile(i, j).getPiece().getScore()-2;
                    hash ^= randomBoard[i][j][pieceValue];
                }
            }
        }
        if(c == Color.GOLD) {
            hash ^= gold;
        } else {
            hash ^= silver;
        }
        return hash;
    }

    /**
     * Compute hash value of board after a turn
     * @param hash hash value of previous board position
     * @param t turn to be done
     * @return hash value of new board position
     */

    public long computeHash(long hash, Turn t) {
        Move m1 = t.getFirstMove();
        hash = computeHashMove(hash, m1);
        if (t.hasTwoMoves()) {
            Move m2 = t.getSecondMove();
            hash = computeHashMove(hash, m2);
        }
        hash ^= gold ^ silver;
        return hash;
    }

    /**
     * Compute hash value of board after a move
     * @param hash hash value of previous board position
     * @param  m move to be done
     * @return hash value of new board position
     */

    public long computeHashMove(long hash, Move m) {
        int x1 = m.getX1();
        int x2 = m.getX2();
        int y1 = m.getY1();
        int y2 = m.getY2();
        int movedPieceValue = m.getPiece().getScore()-2;
        hash ^= randomBoard[x1][y1][movedPieceValue] ^ randomBoard[x2][y2][movedPieceValue];
        Piece capturedPiece = m.getCapturedPiece();
        if (capturedPiece != null) {
            int capturedPieceValue = capturedPiece.getScore()-2;
            hash ^= randomBoard[x2][y2][capturedPieceValue];
        }
        return hash;
    }

    /**
     * Save new TT board entry
     * @param hash board position as hash value
     * @param type type of value: upper (1), lower(-1) or exact(0)
     * @param value score of the board position
     * @param depth search depth
     * @param turn best turn in this position
     */

    public void saveTTentry(long hash, int type, int value, int depth, Turn turn) {
        long key = hash & 0xFFFFF;
        TTentry entry = new TTentry(hash, type, value, depth, turn);
        if (TT[(int) key] == null){
            TT[(int) key] = entry;
        }
        else if (TT[(int) key].getDepth() <= depth){
            TT[(int) key] = entry;
        }
    }

    /**
     * get transposition table entry for position board and player
     * @param hash board position as hash value
     * @return transposition table entry
     */

    public TTentry retrieve(long hash){
        long key = hash & 0xFFFFF;
        TTentry entry = TT[(int)key];
        if (entry != null && hash != entry.getHash()){
            return null;
        }
        return entry;
    }

}

