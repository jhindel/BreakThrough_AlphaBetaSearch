package players.alphabeta;

import logic.Turn;

/**
 * Produce transposition table entries
 */
public class TTentry {
    private final long hash;
    private final int type;
    private final int value;
    private final int depth;
    private final Turn turn;

    /**
     * Initialize transposition table element
     * @param hash hash value
     * @param type type of value: upper (1), lower(-1) or exact(0)
     * @param value score of the board position
     * @param depth search depth
     * @param turn best turn in this position
     */
    public TTentry(long hash, int type, int value, int depth, Turn turn){
        this.hash=hash;
        this.type=type;
        this.value=value;
        this.depth=depth;
        this.turn=turn;
    }

    public int getDepth() {
        return depth;
    }

    public int getType() {
        return type;
    }

    public int getValue() {
        return value;
    }

    public Turn getTurn() {
        return turn;
    }

    public long getHash(){
        return hash;
    }

}
