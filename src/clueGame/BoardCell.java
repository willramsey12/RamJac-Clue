package clueGame;

import java.util.HashSet;
import java.util.Set;

public class BoardCell{
	//Row and column variables
	private int row;
	private int col;
	
	//Cell info
	private char initial;
	private DoorDirection doorDirection;
	private char secretPassage;

	//Boolean status checkers
	private boolean isRoom;
	private boolean isOccupied;
	private boolean doorway;
	private boolean roomLabel;
	private boolean roomCenter;

	//Adjacency List
	private Set<BoardCell> adjList = new HashSet<BoardCell>();
	
	//constructor to set row and col
	public BoardCell(int rowNum, int colNum) {
		row = rowNum;
		col = colNum;
	}
	
	//add cell to adjacency list
	public void addAdjacency(BoardCell cell) {
		if(!cell.isRoom)
			adjList.add(cell);
	}
	
	//getter for adjList
	public Set<BoardCell> getAdjList() {
		return adjList;
	}
	
	//row getter
	public int getRow() {
		return row;
	}

	//row setter
	public void setRow(int row) {
		this.row = row;
	}

	//column getter
	public int getCol() {
		return col;
	}

	//column setter
	public void setCol(int col) {
		this.col = col;
	}

	//room setter (sets to T or F)
	public void setRoom(boolean roomSet) {
		isRoom = roomSet;
	}
	
	//returns T or F if a cell is a room or not
	public boolean getRoom() {
		if(isRoom) {
			return true;
		}
		return false;
	}
	
	//occupied setter (T or F)
	public void setOccupied(boolean playerSet) {
		isOccupied = playerSet;
	}
	
	//if cell occupied, T; else F.
	public boolean getOccupied() {
		if(isOccupied) {
			return true;
		}
		return false;
	}
	
	//getter for secretPassage
	public char getSecretPassage() {
		return secretPassage;
	}

	//setter for secretPassage
	public void setSecretPassage(char secretPassage) {
		this.secretPassage = secretPassage;
	}

	//getter for if a cell is a room label
	public boolean isLabel() {
		return roomLabel;
	}

	//setter for roomLabel
	public void setRoomLabel(boolean roomLabel) {
		this.roomLabel = roomLabel;
	}

	//getter for roomCenter
	public boolean isRoomCenter() {
		return roomCenter;
	}

	//setter for roomCenter
	public void setRoomCenter(boolean roomCenter) {
		this.roomCenter = roomCenter;
	}
	
	//getter for doorway
	public boolean isDoorway() {
		return doorway;
	}

	//setter for roomCenter
	public void setDoorway(boolean doorway) {
		this.doorway = roomCenter;
	}

	//getter for doorway direction
	public Object getDoorDirection() {
		return doorDirection;
	}
	
	//setter for doorway direction
	public void setDoorDirection(DoorDirection doorDirection) {
		this.doorDirection = doorDirection;
	}
}
