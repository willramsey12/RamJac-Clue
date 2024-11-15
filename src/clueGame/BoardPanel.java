package clueGame;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Color;
import java.util.List;

public class BoardPanel extends JPanel {
    private Board board;  // Reference to the game board

    public BoardPanel() {
        this.board = Board.getInstance();  // Assuming Board is a singleton
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
    }
}

