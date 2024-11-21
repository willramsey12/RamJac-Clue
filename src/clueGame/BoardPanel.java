package clueGame;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Color;
import java.util.List;
import java.util.Set;

public class BoardPanel extends JPanel {
    private Board board;  // Reference to the game board

    public BoardPanel() {
        this.board = Board.getInstance();  // Assuming Board is a singleton
    }
    
    private void drawAdjacentCells(Graphics g, Player player, int cellSize) {
        g.setColor(new Color(64, 224, 208)); // Turquoise color

        // Get the player's current position
        BoardCell cell = board.getCell(player.getRow(), player.getCol());

        // Get the adjacency list for the player's position
        Set<BoardCell> adjacentCells = board.getTargetsForBoard(cell, 3);

        // Draw a turquoise circle in each adjacent cell
        for (BoardCell cell1 : adjacentCells) {
            int x = cell1.getCol() * cellSize;
            int y = cell1.getRow() * cellSize;

            // Draw a smaller circle in the center of the cell
            int circleSize = (int) (cellSize * 0.5); // Circle is 50% of the cell size
            int xOffset = (cellSize - circleSize) / 2;
            int yOffset = (cellSize - circleSize) / 2;

            g.fillOval(x + xOffset, y + yOffset, circleSize, circleSize);
        }
    }

  
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Calculate cell size based on panel size and board dimensions
        int cellSize = Math.min(getWidth() / board.getNumColumns(), getHeight() / board.getNumRows());

        // Draw each cell in the grid
        for (int row = 0; row < board.getNumRows(); row++) {
            for (int col = 0; col < board.getNumColumns(); col++) {
                BoardCell cell = board.getCell(row, col);

                // Determine the cell color based on its type
                Color cellColor;
                if (cell.getInitial() == 'W') {
                    cellColor = Color.YELLOW; // Walkway
                } else if (cell.getInitial() == 'X') {
                    cellColor = Color.BLACK; // Inaccessible area
                } else {
                    cellColor = Color.LIGHT_GRAY; // Room
                }

                // Use the draw method of the BoardCell class
                cell.draw(g, cellSize, cellColor);
            }
        }

        // Draw the room names
        board.drawRoomNames(g, cellSize, cellSize); // Use cellSize for both width and height
        for (Player player : board.getPlayers()) {
            player.draw(g, cellSize);
            if (player.isHuman()) {
                drawAdjacentCells(g, player, cellSize);
            }
        }
    }
    
}

