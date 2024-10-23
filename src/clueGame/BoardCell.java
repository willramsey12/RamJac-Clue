package clueGame;

import java.util.HashSet;
import java.util.Set;

public class BoardCell {
    // Row and column variables
    private int row;
    private int col;

    // Cell info
    private char initial;  // Represents the type of room or space
    private DoorDirection doorDirection;
    private char secretPassage;

    // Status flags
    private boolean isRoom;
    private boolean isOccupied;
    private boolean isDoorway;
    private boolean roomLabel;
    private boolean roomCenter;
    Board board = Board.getInstance();

    // Adjacency List
    private Set<BoardCell> adjList = new HashSet<>();

    // Constructor to set row and col
    public BoardCell(int rowNum, int colNum, char init) {
        this.row = rowNum;
        this.col = colNum;
        this.initial = init;
    }
    
    // Get the adjacency list, initializing it if needed
    public Set<BoardCell> getAdjList() {
    	calculateAdjList();
        return adjList;
    }
    
    private void calculateAdjList() {
    	if (row < 24 && !((board.getCell(row+1,col)).isRoom())) {
    		addAdjacency(board.getCell(row+1, col));
    	}
    	if (col < 23 && !((board.getCell(row,col+1)).isRoom())) {
    		addAdjacency(board.getCell(row, col+1));
    	}
    	if (!((board.getCell(row-1,col)).isRoom())) {
    		addAdjacency(board.getCell(row-1, col));
    	}
    	if (!((board.getCell(row,col-1)).isRoom())) {
    		addAdjacency(board.getCell(row, col-1));
    	}
    }

    // Add a cell to the adjacency list
    public void addAdjacency(BoardCell cell) {
        adjList.add(cell);
    }

    // Getters and setters for row and column
    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    // Room initial getter and setter
    public char getInitial() {
        return initial;
    }

    public void setInitial(char initial) {
        this.initial = initial;
    }

    // Room setter
    public void setRoom(boolean roomSet) {
        this.isRoom = roomSet;
    }

    // Is the cell a room?
    public boolean isRoom() {
        return isRoom;
    }

    // Is the cell occupied?
    public boolean isOccupied() {
        return isOccupied;
    }

    // Set the occupied status of the cell
    public void setOccupied(boolean occupied) {
        this.isOccupied = occupied;
    }

    // Getter for secretPassage
    public char getSecretPassage() {
        return secretPassage;
    }

    // Setter for secretPassage
    public void setSecretPassage(char secretPassage) {
        this.secretPassage = secretPassage;
    }

    // Is the cell a room label?
    public boolean isLabel() {
        return roomLabel;
    }

    // Set the cell as a room label
    public void setRoomLabel(boolean roomLabel) {
        this.roomLabel = roomLabel;
    }

    // Is the cell the center of a room?
    public boolean isRoomCenter() {
        return roomCenter;
    }

    // Set the cell as the center of a room
    public void setRoomCenter(boolean roomCenter) {
        this.roomCenter = roomCenter;
    }

    // Is the cell a doorway?
    public boolean isDoorway() {
        return isDoorway;
    }

    // Set the cell as a doorway
    public void setDoorway(boolean doorway) {
        this.isDoorway = doorway;
    }

    // Getter for doorway direction
    public DoorDirection getDoorDirection() {
        return doorDirection;
    }

    // Setter for doorway direction
    public void setDoorDirection(DoorDirection doorDirection) {
        this.doorDirection = doorDirection;
    }
    
    // Override toString to see what the hells going on
    @Override
    public String toString() {
        return "BoardCell [row=" + row + ", col=" + col + ", initial=" + initial + ", isRoom=" + isRoom + 
               ", isDoorway=" + isDoorway + ", roomCenter=" + roomCenter + ", roomLabel=" + roomLabel + "]";
    }
    
}