package ui.gui;

import logic.Color;
import logic.Result;

import javax.swing.*;

/**
 * Class to produce dialogue window when the game is finished
 */
public class DialogResultPresenter {
    private final JFrame parent;
    private final Color color;

    /**
     * Initialize with parent and player for whom the game result will be shown
     * @param parent  board window
     * @param color color of human player or player's perspective
     */
    public DialogResultPresenter(JFrame parent, Color color) {
        this.parent = parent;
        this.color = color;
    }

    void presentResult(Result result) {
        switch (result) {
            case TIE:
                presentTie();
                break;
            case GOLD_WON:
                if (color == Color.GOLD) {
                    presentAsWinner();
                } else {
                    presentAsLoser();
                }
                break;
            case SILVER_WON:
                if (color == Color.SILVER) {
                    presentAsWinner();
                } else {
                    presentAsLoser();
                }
                break;
        }
    }

    private void presentAsWinner() {
        JOptionPane.showMessageDialog(
                parent,
                "You won!",
                "Win",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void presentAsLoser() {
        JOptionPane.showMessageDialog(
                parent,
                "You lost.",
                "Loss",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private void presentTie() {
        JOptionPane.showMessageDialog(
                parent,
                "No player was able to make a move in the last round.",
                "Tie",
                JOptionPane.WARNING_MESSAGE);
    }
}
