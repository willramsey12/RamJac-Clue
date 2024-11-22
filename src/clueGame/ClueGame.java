package clueGame;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.ImageIcon;

import java.awt.BorderLayout;

public class ClueGame extends JFrame {
	
    public ClueGame() throws BadConfigFormatException {
    	Board board = Board.getInstance();
        board.setConfigFiles("ClueBoardLayout.csv", "ClueSetup.txt");
        board.initialize();
        setTitle("Clue Game - CSC1306");
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        
        // Initialize the main game components
        BoardPanel boardPanel = BoardPanel.getInstance();  // The game board
        GameControlPanel controlPanel = GameControlPanel.getInstance();  
        CardsPanel cardsPanel = new CardsPanel();  // Shows known cards

        // Add components to the main layout
        add(boardPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
        add(cardsPanel, BorderLayout.EAST);
    }

    public static void main(String[] args) throws BadConfigFormatException {

    	
        ImageIcon icon = new ImageIcon("data/skyrim.png");
        ClueGame game = new ClueGame();
        game.setVisible(true);
        Board board = Board.getInstance();
        System.out.println(board.getPlayers());
        JOptionPane.showMessageDialog(null,"You are Golden Paladin\nCan you find the solution of the murder first?" ,"Welcome to Clue!", JOptionPane.INFORMATION_MESSAGE, icon);
    }
}