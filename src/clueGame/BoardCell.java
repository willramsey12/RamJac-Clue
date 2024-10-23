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
    private boolean isWalkway;
    private boolean isRoom;
    private boolean isOccupied;
    private boolean isDoorway;
    private boolean roomLabel;
    private boolean roomCenter;

    // Adjacency List
    private Set<BoardCell> adjList = new HashSet<>();

    // Constructor to set row and col
    public BoardCell(int rowNum, int colNum, char init) {
        this.row = rowNum;
        this.col = colNum;
        this.initial = init;
    }

    // Add a cell to the adjacency list
    public void addAdjacency(BoardCell cell) {
        adjList.add(cell);
    }
    
    public void setAdjacencyList(int row, int col) {
        Board board = Board.getInstance();
        BoardCell[][] grid = board.getGrid();
        BoardCell cell = grid[row][col];

        // If the cell is a walkway, add adjacent walkways and doorways
        if (isWalkway) {
            addAdjWalkDoor(grid, row - 1, col);  // Above
            addAdjWalkDoor(grid, row + 1, col);  // Below
            addAdjWalkDoor(grid, row, col - 1);  // Left
            addAdjWalkDoor(grid, row, col + 1);  // Right
        }

        // If the cell is the center of a room and has a secret passage, add the passage
        if (isRoom && secretPassage != '\0') {
            BoardCell secretRoomCenter = board.getRoom(secretPassage).getCenterCell();
            adjList.add(secretRoomCenter);  // Add the center of the secret passage destination room
        }

        // If the cell is a doorway, add the adjacent walkway based on the door's direction
        if (isDoorway) {
            switch (doorDirection) {
                case UP:
                    addAdjacentWalkway(grid, row - 1, col);
                    break;
                case DOWN:
                    addAdjacentWalkway(grid, row + 1, col);
                    break;
                case LEFT:
                    addAdjacentWalkway(grid, row, col - 1);
                    break;
                case RIGHT:
                    addAdjacentWalkway(grid, row, col + 1);
                    break;
            }
        }
        
        // Print adjacency list for debugging
        System.out.println(cell.getAdjList());
    }

    private void addAdjWalkDoor(BoardCell[][] grid, int row, int col) {
        // Ensure that row and col are within bounds
        if (row >= 0 && row < grid.length && col >= 0 && col < grid[0].length) {
            BoardCell adjacentCell = grid[row][col];
            // Add the cell if it's a walkway or a doorway
            if (adjacentCell.iswalk() || adjacentCell.isDoorway()) {
                adjList.add(adjacentCell);
            }
        }
    }

    private void addAdjacentWalkway(BoardCell[][] grid, int row, int col) {
        // Ensure that row and col are within bounds
        if (row >= 0 && row < grid.length && col >= 0 && col < grid[0].length) {
            BoardCell adjacentCell = grid[row][col];
            // Only add the cell if it's a walkway
            if (adjacentCell.iswalk()) {
                adjList.add(adjacentCell);
            }
        }
    }
    // Getter for adjList
    public Set<BoardCell> getAdjList() {
        return adjList;
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
    
    public boolean iswalk() {
    	return isWalkway;
    }
    
    public void setWalk(boolean set) {
    	this.isWalkway = set;
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
               ", isDoorway=" + isDoorway + ", roomCenter=" + roomCenter + 
               ", roomLabel=" + roomLabel + ", isWalkway=" + isWalkway +
               ", secretPassage="+ secretPassage +"]";
    }
}