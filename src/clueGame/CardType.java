package clueGame;

public enum CardType {
	ROOM("Room"), PERSON("Person"), WEAPON("Weapon");
	private final String type;

	CardType(String string) {
		this.type = string;	
	}
	
	public String getType() {
		return type;
	}
	
    @Override
    public String toString() {
        return type;
    }
}
