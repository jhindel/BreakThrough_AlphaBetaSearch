package ui.gui;

import logic.Board;
import logic.Color;
import logic.Result;
import ui.GamePresenter;

import javax.swing.*;

/**
 * GUI class to initialize two windows (board and result)
 */
public class FramePresenter extends JFrame implements GamePresenter {
    private static final String FRAME_TITLE = "Breakthru";
    protected static final int FRAME_WIDTH = 800;
    protected static final int FRAME_HEIGHT = 800;
    private final DialogResultPresenter resultPresenter;
    private final BoardPanel boardPanel;

    public FramePresenter(Color color, Object lock) {
        this.resultPresenter = new DialogResultPresenter(this, color);
        this.boardPanel = new BoardPanel(lock);

        setupWindow();
        setupBoard();
        setVisible(true);
    }

    private void setupWindow() {
        setTitle(FRAME_TITLE);
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setResizable(false);
        setLocation(0, 0);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private void setupBoard() {
        getContentPane().add(boardPanel);
    }

    public BoardPanel getBoardPanel() {return this.boardPanel;}

    @Override
    public void presentBoard(Board board) {
        boardPanel.updateBoard(board);
    }

    @Override
    public void presentResult(Result result) {
        resultPresenter.presentResult(result);
    }
}
