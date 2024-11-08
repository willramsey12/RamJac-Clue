package clueGame;

import java.util.ArrayList;

public class Solution {
	private Card room;
	private Card weapon;
	private Card person;
	private ArrayList<Card> solutionDeck = new ArrayList<>();
	
	Solution(){
	}
	
	public Card getRoom() {
		return room;
	}
	public void setRoom(Card room) {
		this.room = room;
		solutionDeck.add(room);
	}
	public Card getWeapon() {
		return weapon;
	}
	public void setWeapon(Card weapon) {
		this.weapon = weapon;
		solutionDeck.add(weapon);
	}
	public Card getPerson() {
		return person;
	}
	public void setPerson(Card person) {
		this.person = person;
		solutionDeck.add(person);
	}
	public ArrayList<Card> getSolutionDeck() {
		return solutionDeck;
	}
	

}
