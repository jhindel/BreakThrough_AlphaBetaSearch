package ui.gui;

import logic.Tile;
import pieces.Piece;

import javax.swing.*;
import java.awt.*;

/**
 * GUI of Tiles
 */
class TilePanel extends JPanel {
    private Tile tile;
    // boolean value defining if tile is part of potentialMoveDestination or not
    private boolean potentialMoveDestination = false;
    private final Color silver = new Color(192, 192, 192);
    private final Color gold = new Color(255, 215, 0);
    private final Color light_green = new Color (199,242,59);
    private final Color green = new Color(173,209,53);

    TilePanel() {}

    void setTile(Tile tile) {
        this.tile = tile;
    }

    public Tile getTile() {
        return this.tile;
    }

    void setPotentialMoveDestination(boolean potentialMoveDestination) {
        this.potentialMoveDestination = potentialMoveDestination;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        paintTile(g);
        if (isOccupied()) {
            paintPiece(g);
        }
    }

    private void paintTile(Graphics g) {
        g.setColor(getBackgroundColor());
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, getWidth(), getHeight());
    }

    private void paintPiece(Graphics g) {
        Piece piece = tile.getPiece();
        useLargerFont(g);
        g.setColor(Color.BLACK);
        g.drawString(piece.toString(), 23, 25);
    }

    private boolean isOccupied() {
        return tile != null && tile.getPiece() != null;
    }

    private void useLargerFont(Graphics g) {
        Font currentFont = g.getFont();
        Font largerFont = currentFont.deriveFont(currentFont.getSize() * 2F);
        g.setFont(largerFont);
    }

    /**
     * Get color of tile (color code: light green for possible capture moves, green for motion moves otherwise pieces
     *         have a color representing the player, all other have a white background color
     * @return defined color
     */
    private Color getBackgroundColor() {
        if (tile != null) {
            if (tile.isOccupied() && potentialMoveDestination) {
                return light_green;
            }
            else if (potentialMoveDestination) {
                return green;
            }
            else if (tile.isOccupied()) {
                return getPieceColor(tile.getPiece());
            }
        }
            return Color.WHITE;
    }

    private Color getPieceColor(Piece piece) {
        if (piece.getOwner() == logic.Color.GOLD) {
            return gold;
        } else {
            return silver;
        }
    }
}
