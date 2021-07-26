package ui;

import logic.Board;
import logic.Result;
import ui.gui.BoardPanel;

/**
 * Class to define different ways to represent game (text, GUI)
 */

public interface GamePresenter {
    void presentBoard(Board board);

    void presentResult(Result result);

    BoardPanel getBoardPanel();
}
