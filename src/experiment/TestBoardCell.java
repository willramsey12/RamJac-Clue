package experiment;



import java.util.HashSet;

import java.util.Set;



public class TestBoardCell {

private int row, col;

private boolean isRoom = false;

private boolean isOccupied = false;

private Set<TestBoardCell> adjList = new HashSet<>();


public TestBoardCell(int row, int col) {

this.row = row;

this.col = col;

}



public void addAdjacency(TestBoardCell cell) {

adjList.add(cell);

}



public Set<TestBoardCell> getAdjList() {

return adjList;

}



public void setRoom(boolean isRoom) {

this.isRoom = isRoom;

}



public boolean isRoom() {

return isRoom;

}



public void setOccupied(boolean isOccupied) {

this.isOccupied = isOccupied;

}



public boolean getOccupied() {

return isOccupied;

}

}
