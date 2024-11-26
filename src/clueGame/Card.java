package clueGame;

public class Card {
	private String cardName;
	private CardType cardType;

	public Card() {
	
	}
	public Card(String cardName, CardType cardType) {
		this.cardName = cardName;
		this.cardType = cardType;
	}
	@Override
	public boolean equals(Object obj) {
	    // Check if the object passed is of type Card
	    if (this == obj) return true;
	    if (obj == null || getClass() != obj.getClass()) return false;

	    Card target = (Card) obj;

	    return this.getCardType() == target.getCardType() &&
	           this.getCardName().equals(target.getCardName());
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
	@Override
	public String toString() {
	    return cardName;
	}
}
