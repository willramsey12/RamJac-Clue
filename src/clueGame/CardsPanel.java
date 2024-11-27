package clueGame;
import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class CardsPanel extends JPanel {
	private Set<Card> seenCards = new HashSet<>();

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

        // Always create the three main panels
        peoplePanel = createPanel("People");
        roomPanel = createPanel("Rooms");
        weaponPanel = createPanel("Weapons");

        // Add the panels to the main layout
        add(peoplePanel);
        add(roomPanel);
        add(weaponPanel);

        // Populate the "In Hand" sections with the player's cards
        if (board.getCurrentPlayer().isHuman) {
            for (Card card : board.getCurrentPlayer().getHand()) {
                switch (card.getCardType()) {
                    case PERSON -> addCardToHandPanel(card, peoplePanel);
                    case ROOM -> addCardToHandPanel(card, roomPanel);
                    case WEAPON -> addCardToHandPanel(card, weaponPanel);
                }
            }
        }
    }



    private JPanel createPanel(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));

        // Panel for the hand cards (static)
        JPanel handPanel = new JPanel();
        handPanel.setLayout(new BoxLayout(handPanel, BoxLayout.Y_AXIS));
        handPanel.add(new JLabel("In Hand:"));

        // Panel for seen cards (where new cards will be added below each other)
        JPanel seenPanel = new JPanel();
        seenPanel.setLayout(new BoxLayout(seenPanel, BoxLayout.Y_AXIS));
        seenPanel.add(new JLabel("Seen:"));

        // Save the seen panel for later updates
        switch (title) {
            case "People" -> peopleSeenPanel = seenPanel;
            case "Rooms" -> roomSeenPanel = seenPanel;
            case "Weapons" -> weaponSeenPanel = seenPanel;
        }

        // Add sub-panels to the main panel
        panel.add(handPanel, BorderLayout.NORTH);
        panel.add(seenPanel, BorderLayout.CENTER);

        return panel;
    }


    
    private void addCardToHandPanel(Card card, JPanel panel) {
        JPanel handPanel = (JPanel) panel.getComponent(0); // Get the hand panel (first sub-panel)
        
        // Create a new text field for the card
        JTextField handField = new JTextField(card.getCardName());
        handField.setEditable(false);
        
        // Add the new field to the hand panel
        handPanel.add(handField);

        revalidate(); // Refresh layout to show the new field
        repaint();    // Redraw the panel to display the new field
    }





    // Method to add a new seen card to the appropriate panel
    public void addSeenCard(Card card) {
    	if (seenCards.contains(card)) {
            // Card already added; skip
            return;
        }

        // Add card to the tracking set
        seenCards.add(card);

        // Create a new text field for the card
        JTextField newSeenField = new JTextField(card.getCardName());
        newSeenField.setEditable(false);

        // Add to the appropriate panel based on card type
        switch (card.getCardType()) {
            case PERSON -> peopleSeenPanel.add(newSeenField);
            case ROOM -> roomSeenPanel.add(newSeenField);
            case WEAPON -> weaponSeenPanel.add(newSeenField);
        }

        revalidate(); // Refresh layout to show the new field
        repaint(); // Redraw panel to display the new field
    }
}
