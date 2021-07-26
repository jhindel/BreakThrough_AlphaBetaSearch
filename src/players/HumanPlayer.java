package players;

import logic.*;
import ui.GamePresenter;
import ui.gui.BoardPanel;

import java.util.ArrayList;

/**
 * Class to produce logic of human player
 */
public class HumanPlayer extends Player {
    private final GamePresenter guiPresenter;
    private final Object lock;
    private boolean firstMove=true;

    /**
     * Initialize human player
     * @param color Color representing human player
     * @param guiPresenter connection to game representer (text or GUI), only GUI implemented in the following
     * @param lock lock to wait for user interface actions
     */
    public HumanPlayer(Color color, GamePresenter guiPresenter, Object lock) {
        super(color);
        this.guiPresenter=guiPresenter;
        this.lock=lock;
    }

    /**
     * Generate next Turn t
     * @param b board position
     * @return turn
     */

    @Override
    public Turn getNextTurn(Board b) {
        Move m1=null;
        BoardPanel fp = guiPresenter.getBoardPanel();

        while (true) {
            // wait for GUI to set lock
            try {
                synchronized(lock) {
                    lock.wait();
                }
            } catch (InterruptedException e) {
            }
            // retrieve coordinates of clicked tile
            int x = BoardPanel.clickedTile[0];
            int y = BoardPanel.clickedTile[1];

            if (!b.isOnBoard(x,y)){
                //skip move in first round possible
                if (firstMove) {
                    return null;
                } else {
                    continue;
                }
            }
            firstMove=false;

            Tile t = b.getTile(x,y);

            if (!t.isOccupied() || t.getPiece().getOwner()!=getColor()){
                continue;
            }

            if (m1!=null){
                if(m1.getX2()==x && m1.getY2()==y){
                    continue;
                }
            }
            // generate all moves
            ArrayList<Move> moves = t.getPiece().enumerateAllPossibleMoves(b, x, y);
            if(m1!=null){
                moves.removeIf(Move::isSingleMove);
            }
            fp.tagPossibleMoves(moves, b,true);
            // wait for lock again
            try {
                synchronized(lock) {
                    lock.wait();
                }
            } catch (InterruptedException e) {
            }
            // get clicked Tile
            x = BoardPanel.clickedTile[0];
            y = BoardPanel.clickedTile[1];

            fp.tagPossibleMoves(moves, b,false);

            // if first and second clicked tile represent one legal move, save move
            for (Move m : moves){
                if(m.getX2()==x && m.getY2()==y){
                    if (m.isSingleMove()){
                        return new Turn(m);
                    } else if (m1 != null){
                        return new Turn(m1,m);
                    }
                    else {
                        m1=m;
                        b.makeMove(m1);
                        if (b.getResult()!=Result.NONE){
                            return new Turn(m1);
                        }
                    }

                }
            }
        }
    }

}
