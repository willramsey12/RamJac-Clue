package clueGame;

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

}
