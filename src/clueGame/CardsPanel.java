package clueGame;
import javax.swing.*;
import java.awt.*;

public class CardsPanel extends JPanel {

    private JPanel peoplePanel;
    private JPanel roomPanel;
    private JPanel weaponPanel;

    private JPanel peopleSeenPanel;
    private JPanel roomSeenPanel;
    private JPanel weaponSeenPanel;
    private static CardsPanel cardsPanel = new CardsPanel();
    private Board board;
    
    
    
    // Public static method to provide access to the single instance
    public static CardsPanel getInstance() {
        return cardsPanel;
    }

    public CardsPanel() {
    	board = Board.getInstance();
        setLayout(new GridLayout(3, 1)); // Three main sections for people, rooms, and weapons

        // Set up each main panel with hand and seen sub-panels
        if (board.getCurrentPlayer().isHuman) {
        	for (Card card : board.getCurrentPlayer().getHand()) {
        		if (card.getCardType() == CardType.PERSON) {
                    peoplePanel = createPanel(card);
                    add(peoplePanel);
        		} else if (card.getCardType() == CardType.ROOM) {
                    roomPanel = createPanel(card);
                    add(roomPanel);
        		} else {
        			weaponPanel = createPanel(card);
        			add(weaponPanel);
        		}
        	}
        }
    }

    // Helper method to create each section panel
    private JPanel createPanel(Card card) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(card.getCardType().getType()));

        // Panel for the hand cards (static)
        JPanel handPanel = new JPanel(new BorderLayout());
        handPanel.add(new JLabel("In Hand: "), BorderLayout.WEST);
        JTextField handField = new JTextField(card.getCardName());
        handField.setEditable(false);
        handPanel.add(handField, BorderLayout.CENTER);

        // Panel for seen cards (where new cards will be added below each other)
        JPanel seenPanel = new JPanel();
        seenPanel.setLayout(new BoxLayout(seenPanel, BoxLayout.Y_AXIS));
        seenPanel.add(new JLabel("Seen:"));
        
        // Save the seen panel based on type to update later
        switch (card.getCardType()) {
            case PERSON -> peopleSeenPanel = seenPanel;
            case ROOM -> roomSeenPanel = seenPanel;
            case WEAPON -> weaponSeenPanel = seenPanel;
        }

        // Add sub-panels to the main panel
        panel.add(handPanel, BorderLayout.NORTH);
        panel.add(seenPanel, BorderLayout.CENTER);

        return panel;
    }

    // Method to add a new seen card to the appropriate panel
    public void addSeenCard(Card card) {
        JTextField newSeenField = new JTextField(card.getCardName());
        newSeenField.setEditable(false);

        switch (card.getCardType()) {
            case PERSON -> peopleSeenPanel.add(newSeenField);
            case ROOM -> roomSeenPanel.add(newSeenField);
            case WEAPON -> weaponSeenPanel.add(newSeenField);
        }

        revalidate(); // Refresh layout to show the new field
        repaint(); // Redraw panel to display the new field
    }

    // Test main method to display the panel and simulate adding seen cards
    public static void main(String[] args) {
        JFrame frame = new JFrame("Player Cards");
        CardsPanel panel = new CardsPanel();
        frame.setContentPane(panel);
        frame.setSize(300, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
