package ui.gui;

import logic.Board;
import logic.Move;
import logic.Tile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

/**
 * Class to produce GUI Window with Board
 */
public class BoardPanel extends JPanel implements MouseListener {
    private static final int TILE_GAP = 1;
    private static final TimePanel timePanel = new TimePanel();
    public static int[] clickedTile = new int[2];
    private final TilePanel[][] tilePanels = new TilePanel[Board.BOARD_SIZE][Board.BOARD_SIZE];
    private final Object lock;

    /**
     * Initialize GUI window and mouse listener
     *
     * @param lock        lock to wait for human action
     */
    public BoardPanel(Object lock) {
        super();
        this.lock = lock;
        setupLayout();
        addMouseListener(this);
    }

    /**
     * Create GUI window
     */
    private void setupLayout() {
        GridLayout gridLayout = new GridLayout(Board.BOARD_SIZE + 2, Board.BOARD_SIZE + 2, TILE_GAP, TILE_GAP);
        setupTiles();
        setLayout(gridLayout);
    }

    /**
     * Create board and surrounding tiles with labels
     */
    private void setupTiles() {
        add(timePanel);
        setupHeader();
        for (int i = 0; i < Board.BOARD_SIZE; i++) {
            formatLabel(String.valueOf(Board.BOARD_SIZE - i));
            for (int j = 0; j < Board.BOARD_SIZE; j++) {
                TilePanel tilePanel = new TilePanel();
                tilePanels[i][j] = tilePanel;
                add(tilePanel);
            }
            formatLabel(String.valueOf(Board.BOARD_SIZE - i));
        }
        add(new JLabel());
        setupHeader();
    }

    /**
     * Header with chess notation labels
     */
    private void setupHeader() {
        char header = 'A';
        for (int i = 0; i < Board.BOARD_SIZE; i++) {
            formatLabel(String.valueOf(header));
            header++;
        }
        // "Padding" in last column
        add(new JLabel());
    }

    /**
     * Formatting and adding of chess notation labels
     *
     * @param s String
     */
    private void formatLabel(String s) {
        JLabel label = new JLabel(s, SwingConstants.CENTER);
        label.setFont(getFont().deriveFont(getFont().getSize() * 1.2F));
        add(label);
    }

    /**
     * update GUI with board position
     *
     * @param board current board position
     */
    void updateBoard(Board board) {
        for (int i = 0; i < Board.BOARD_SIZE; i++) {
            for (int j = 0; j < Board.BOARD_SIZE; j++) {
                Tile tile = board.getTile(i, j);
                TilePanel tilePanel = tilePanels[i][j];
                tilePanel.setTile(tile);
                updateTilePanel(tilePanel);
            }
        }
        updateTimePanel();

    }

    void updateTilePanel(TilePanel tilePanel) {
        SwingUtilities.invokeLater(tilePanel::repaint);
    }

    void updateTimePanel() {
        SwingUtilities.invokeLater(timePanel::repaint);
    }

    /**
     * Tag/untag all destination tiles of the moves.
     *
     * @param moves list of possible moves
     * @param b     current board position
     * @param tag   boolean to indicate if it should be tagged or not
     */
    public void tagPossibleMoves(ArrayList<Move> moves, Board b, boolean tag) {
        for (Move m : moves) {
            TilePanel tp = tilePanels[m.getX2()][m.getY2()];
            tp.setPotentialMoveDestination(tag);
        }
        updateBoard(b);
    }

    /**
     * Parse mouse click event: calculate clicked tile and notify human player logic of move
     *
     * @param e Mouseevent e
     */
    private void parseMouseClick(MouseEvent e) {
        int yPoint = e.getY();
        int xPoint = e.getX();
        clickedTile[0] = (yPoint / (FramePresenter.FRAME_HEIGHT / (Board.BOARD_SIZE + 2))) - 1;
        clickedTile[1] = (xPoint / (FramePresenter.FRAME_WIDTH / (Board.BOARD_SIZE + 2))) - 1;
        synchronized (lock) {
            lock.notifyAll();
        }
    }


    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        parseMouseClick(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
