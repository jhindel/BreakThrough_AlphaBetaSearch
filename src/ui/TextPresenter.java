package ui;

import logic.Board;
import logic.Result;
import ui.gui.BoardPanel;

/**
 * Class to represent game as text in terminal (no user interaction implemented)
 */

public class TextPresenter implements GamePresenter {
    @Override
    public void presentBoard(Board board) {
        System.out.println(board.toString());
    }

    @Override
    public void presentResult(Result result) {
        switch (result) {
            case SILVER_WON:
                System.out.println("Silver won!");
                break;
            case GOLD_WON:
                System.out.println("Gold won!");
                break;
            case TIE:
                System.out.println("No player made a turn, tie!");
                break;
        }
    }
    @Override
    public BoardPanel getBoardPanel(){
        return null;
    }
}
