package clueGame;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Color;
import java.util.List;
import java.util.Set;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class BoardPanel extends JPanel {
    private Board board;  // Reference to the game board
    private boolean actionTaken = false; // Flag to track if the player has taken their action
    private static BoardPanel boardPanel = new BoardPanel();

    // Public static method to provide access to the single instance
    public static BoardPanel getInstance() {
        return boardPanel;
    }
    

    private BoardPanel() {
        this.board = Board.getInstance();  // Assuming Board is a singleton
        // Add MouseListener to detect clicks
        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleClick(e.getX(), e.getY()); // Handle the click
            }

            @Override
            public void mousePressed(MouseEvent e) {}

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {}
        });
    }
    
    
    
    private void drawAdjacentCells(Graphics g, Player player, int cellSize) {
        g.setColor(new Color(64, 224, 208)); // Turquoise color

        // Get the player's current position
        BoardCell cell = board.getCell(player.getRow(), player.getCol());

        // Get the adjacency list for the player's position
        Set<BoardCell> adjacentCells = board.getTargetsForBoard(cell, board.getRoll());

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
    
    private void handleClick(int mouseX, int mouseY) {
        // Calculate cell size based on panel size and board dimensions
        int cellSize = Math.min(getWidth() / board.getNumColumns(), getHeight() / board.getNumRows());

        // Determine the row and column of the clicked cell
        int col = mouseX / cellSize;
        int row = mouseY / cellSize;

        // Ensure the click is within the grid bounds
        if (row >= 0 && row < board.getNumRows() && col >= 0 && col < board.getNumColumns()) {
            BoardCell clickedCell = board.getCell(row, col);

            // Perform actions based on the clicked cell
            if (clickedCell != null) {
                System.out.println("Clicked on cell at row " + row + ", column " + col);
                // Example: Highlight the cell, move a player, or show details
                processClickedCell(clickedCell);
            }
        }
    }
    
    private void processClickedCell(BoardCell clickedCell) {
        if (actionTaken) {
            System.out.println("Action already taken for this turn!");
            return; // Ignore the click
        }

        Player currentPlayer = board.getCurrentPlayer();
        // Get the player's current position
        BoardCell cell = board.getCell(currentPlayer.getRow(), currentPlayer.getCol());
        

        // Get the adjacency list for the player's position
        Set<BoardCell> adjacentCells = board.getTargetsForBoard(cell, board.getRoll());
        //System.out.println(adjacentCells);
        // Check if the clicked cell is a valid target
        if (adjacentCells.contains(clickedCell) && currentPlayer.isHuman()) {
            // Move the player to the new cell
            currentPlayer.setLocation(clickedCell.getRow(), clickedCell.getCol());
            System.out.println("row " + Integer.toString(currentPlayer.getRow()) + " col " + Integer.toString(currentPlayer.getCol()));
            board.calcTargets(clickedCell, board.getRoll()); // Update targets from the new position
            repaint(); // Redraw the board to reflect changes

            // Mark action as taken
            setActionTaken(true);
        } else if (!currentPlayer.isHuman()) {
        	System.out.println("Invalid move! Not your player");
        } else {
            System.out.println("Invalid move! Cell is not a target.");
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
        // Draw the walkways
        board.drawDoorway(g, cellSize, cellSize);
        
        for (Player player : board.getPlayers()) {
            player.draw(g, cellSize);
            if (board.getCurrentPlayer().isHuman() && !actionTaken) {
                drawAdjacentCells(g, board.getCurrentPlayer(), cellSize);
            }
        }
    }
    
    public void setActionTaken(boolean b){
    	actionTaken = b;  
    	}
    
    public boolean getActionTaken() {
    	return actionTaken;
    }
    
    
    
}

