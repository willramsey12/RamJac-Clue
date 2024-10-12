package clueGame;

public class Room {
	String name;
	BoardCell centerCell;
	BoardCell labelCell;

	public Room() {
		super();
	}
	
	/*
	 * getters and setter for class variables
	 */
	
	public String getName() {
		//return to clear errors for now
		return "";
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public BoardCell getCenterCell() {
		//return to clear error for now
		return null;
	}
	
	public void setCenterCell(BoardCell centerCell) {
		this.centerCell = centerCell;
	}

	public BoardCell getLabelCell() {
		return labelCell;
	}

	public void setLabelCell(BoardCell labelCell) {
		this.labelCell = labelCell;
	}
}
