package clueGame;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;

public class GameControlPanel extends JPanel {
    private JButton nextPlayerButton;
    private JButton accusationButton;
    private JTextField dieRollField;
    private JTextField turnField;
    private JTextField guessField;
    private JTextField guessResultField;
    private Board board;

    // Static instance for the singleton pattern
    private static GameControlPanel controlPanel = new GameControlPanel();

    // Private constructor to prevent external instantiation
    private GameControlPanel() {
    	board = Board.getInstance();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); // Vertical layout for main panel

        // --- Top Panel (1x4) ---
        JPanel topPanel = new JPanel(new GridLayout(1, 4));

        // Sub-panel 1: for display of die roll
        JPanel diePanel = new JPanel();
        diePanel.add(new JLabel("Roll: "));
        dieRollField = new JTextField(5);
        dieRollField.setEditable(false);
        dieRollField.setText(Integer.toString(board.getRoll()));
        diePanel.add(dieRollField);
        topPanel.add(diePanel);

        // Sub-panel 2: for display of whose turn it is
        JPanel turnPanel = new JPanel();
        turnPanel.add(new JLabel("Turn: "));
        turnField = new JTextField(10);
        turnField.setEditable(false);
        turnField.setPreferredSize(new Dimension(500, 30));
        turnPanel.add(turnField);
        topPanel.add(turnPanel);
        turnField.setText(board.getCurrentPlayer().getName());

        // Button 1: "Next Player"
        nextPlayerButton = new JButton("Next Player");
        topPanel.add(nextPlayerButton);
        nextPlayerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Logic to execute when button is clicked
                board.diceRoll();
                board.nextPlayer();
                BoardPanel.getInstance().setActionTaken(false);
                updateControlPanel();
                BoardPanel.getInstance().repaint();
            }
        });

        // Button 2: "Make an Accusation"
        accusationButton = new JButton("Make an Accusation");
        topPanel.add(accusationButton);

        // Add topPanel to main panel
        add(topPanel);

        // --- Bottom Panel (0x2) ---
        JPanel bottomPanel = new JPanel(new GridLayout(0, 2));

        // Sub-panel 3: for displaying guess
        JPanel guessPanel = new JPanel();
        guessPanel.add(new JLabel("Guess: "));
        guessField = new JTextField(20);
        guessField.setEditable(false);
        guessPanel.add(guessField);
        bottomPanel.add(guessPanel);

        // Sub-panel 4: for displaying guess result
        JPanel guessResultPanel = new JPanel();
        guessResultPanel.add(new JLabel("Result: "));
        guessResultField = new JTextField(20);
        guessResultField.setEditable(false);
        guessResultPanel.add(guessResultField);
        bottomPanel.add(guessResultPanel);

        // Add bottomPanel to main panel
        add(bottomPanel);
    }

    // Public static method to provide access to the single instance
    public static GameControlPanel getInstance() {
        return controlPanel;
    }

    public void setTurn(Player player, Integer i) {
        turnField.setText(player.getName());
        dieRollField.setText(Integer.toString(i));
    }

    public void setGuess(String guess) {
        guessField.setText(guess);
    }

    public void setGuessResult(String result) {
        guessResultField.setText(result);
    }
    
    public void updateControlPanel() {
    	dieRollField.setText(Integer.toString(board.getRoll()));
    	turnField.setText(board.getCurrentPlayer().getName());
    }

    // Test main method to run this panel independently
    public static void main(String[] args) {
        JFrame frame = new JFrame("Clue Game Control");
        GameControlPanel panel = GameControlPanel.getInstance(); // Use the singleton instance

        frame.setContentPane(panel);
        frame.setSize(750, 180);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Test filling in the data
        panel.setTurn(new ComputerPlayer("Col. Mustard", "orange", 0, 0), 5);
        panel.setGuess("I have no guess!");
        panel.setGuessResult("So you have nothing?");
    }
}
