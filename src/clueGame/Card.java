package clueGame;

public class Card {
	private String cardName;
	private CardType cardType;

	public Card() {
		// TODO Auto-generated constructor stub
	}
	
	public boolean equals(Card target) {
		return true;
		
	}
	
	public void setCardName(String cN) {
		this.cardName = cN;
	}

	public CardType getCardType() {
		return cardType;
	}

	public void setCardType(CardType cT) {
		this.cardType = cT;
	}

	public String getCardName() {
		return cardName;
	}
}
