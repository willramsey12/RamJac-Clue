package clueGame;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.ImageIcon;

import java.awt.BorderLayout;

public class ClueGame extends JFrame {
    public ClueGame() throws BadConfigFormatException {
        setTitle("Clue Game - CSC1306");
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        
        // Initialize the main game components
        BoardPanel boardPanel = new BoardPanel();  // The game board
        GameControlPanel controlPanel = GameControlPanel.getInstance();  // Controls (e.g., "Next", "Make Accusation")
        CardsPanel cardsPanel = new CardsPanel();  // Shows known cards

        // Add components to the main layout
        add(boardPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
        add(cardsPanel, BorderLayout.EAST);
    }

    public static void main(String[] args) throws BadConfigFormatException {
    	Board board = Board.getInstance();
        board.setConfigFiles("ClueBoardLayout.csv", "ClueSetup.txt");
        board.initialize();
        ImageIcon icon = new ImageIcon("data/skyrim.png");
        ClueGame game = new ClueGame();
        game.setVisible(true);
        JOptionPane.showMessageDialog(null,"You are Golden Paladin\nCan you find the solution of the murder first?" ,"Welcome to Clue!", JOptionPane.INFORMATION_MESSAGE, icon);
    }
}