package clueGame;

import java.util.ArrayList;

public abstract class Player {
	private String name;
	private String color;
	private int row, col;
	private ArrayList<Card> hand;
	protected boolean isHuman;
	

	public Player() {
		// TODO Auto-generated constructor stub
		hand = new ArrayList<Card>();
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
