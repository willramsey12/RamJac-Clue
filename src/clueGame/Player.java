package clueGame;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class Player {
	private String name;
	private String color;
	private int row;
	private int  col;
	private ArrayList<Card> hand = new ArrayList<>();
	protected boolean isHuman;
	
	protected Player(String name, String color, int row, int  col, boolean isHuman) {
		this.name = name;
		this.color = color;
		this.row = row;
		this.col = col;
		this.isHuman = isHuman;
	}
	protected Player() {
	}
	
	public Card disproveSuggestion(Card person, Card room, Card weapon) {
        List<Card> matchingCards = new ArrayList<>();

        // Check each card in hand to see if it matches suggestion
        for (Card card : hand) {
            if (card.equals(person) || card.equals(room) || card.equals(weapon)) {
                matchingCards.add(card);
            }
        }

        // If no matching cards
        if (matchingCards.isEmpty()) {
            return null;
        }

        // If one matching card
        if (matchingCards.size() == 1) {
            return matchingCards.get(0);
        }

        // If multiple matching cards return randomly
        Random random = new Random();
        return matchingCards.get(random.nextInt(matchingCards.size()));
    }
	
	public void updateHand(Card card) {
		hand.add(card);
	}
	
	public void setLocation(int newRow, int newCol) {
		this.row = newRow;
		this.col = newCol;
	}

	public String getName() {
		return name;
	}

	public ArrayList<Card> getHand() {
		return hand;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}
	
	public boolean isHuman() {
		return isHuman;
	}
	private Color getColorFromName(String colorName) {
	    return switch (colorName.toLowerCase()) {
	        case "silver" -> new Color(192, 192, 192);
	        case "purple" -> new Color(128, 0, 128); // RGB for purple
	        case "red" -> Color.RED;
	        case "green" -> Color.GREEN;
	        case "brown" -> new Color(139, 69, 19); // RGB for brown
	        case "white" -> Color.WHITE;
	        default -> Color.LIGHT_GRAY; // Default color for unexpected input
	    };
	}
	
	public void draw(Graphics g, int cellSize) {
	    // Convert the color string to an actual Color object
	    Color playerColor = getColorFromName(color);

	    // Set the drawing color
	    g.setColor(playerColor);

	    // Calculate the top-left corner of the circle based on row and col
	    int x = col * cellSize;
	    int y = row * cellSize;

	    // Draw the circle (slightly smaller than the cell to fit nicely inside)
	    int circleSize = (int) (cellSize * 0.8); // Circle is 80% of the cell size
	    int xOffset = (cellSize - circleSize) / 2; // Center the circle horizontally
	    int yOffset = (cellSize - circleSize) / 2; // Center the circle vertically

	    g.fillOval(x + xOffset, y + yOffset, circleSize, circleSize);

	    // Optionally draw the player's initials or name inside the circle
	    g.setColor(Color.BLACK); // Text color
	    
	}

}
