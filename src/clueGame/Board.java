package clueGame;

import java.util.HashSet;
//import java.util.Map;
import java.util.Set;

public class Board {
	private BoardCell[][] grid;
	private Set<BoardCell> targets = new HashSet<BoardCell>();
	private Set<BoardCell> visited = new HashSet<BoardCell>();
	private Set<BoardCell> adjList = new HashSet<BoardCell>();

	final static int COLS = 24;
	final static int ROWS = 25;
	
	//private String layConFile;
	//private String setupConFile;
	private String layConFile;
	private String setupConFile;
	//private Map<Charecter, Room> roomMap;
	
	private static Board theInstance = new Board();
	
	//calculate adjacency lists
	//constructor is private so only one board can be made
	private Board() {
		super();
		initialize();
	}
	
	//returns the only board
	public static Board getInstance() {
		return theInstance;
	}
	
	public void initialize() {
		//initialize board
		grid = new BoardCell[ROWS][COLS];
		for(int i = 0; i < ROWS; i++) {
			for(int j = 0; j < COLS; j++) {
				grid[i][j] = new BoardCell(i,j);
			}
		}
		for(int i = 0; i < ROWS; i++) {
			for(int j = 0; j < COLS; j++) {
				if (i == 0) {
					
					grid[i][j].addAdjacency(grid[i+1][j]);
				} else if (i == ROWS-1) {
					grid[i][j].addAdjacency(grid[i-1][j]);
				} else {
					grid[i][j].addAdjacency(grid[i+1][j]);
					grid[i][j].addAdjacency(grid[i-1][j]);
				}
				if (j == 0) {
					grid[i][j].addAdjacency(grid[i][j+1]);
				} else if (j == COLS-1) {
					grid[i][j].addAdjacency(grid[i][j-1]);
				} else {
					grid[i][j].addAdjacency(grid[i][j+1]);
					grid[i][j].addAdjacency(grid[i][j-1]);
				}
			}
		}
	}
	
	public void calcTargets(BoardCell startCell, int pathlength) {
		//calculates legal moves from startCell a distance of pathLength away
		targets.clear();
		visited.clear();
		visited.add(startCell);
		calcTargetsHelper(startCell, pathlength);
	}
	
	public void calcTargetsHelper(BoardCell startCell, int pathlength) {
		for(BoardCell adjCell : startCell.getAdjList()) {
			if(!visited.contains(adjCell)) {
				visited.add(adjCell);
				if(pathlength == 1) {
					if(!adjCell.getOccupied())
						targets.add(adjCell);
				} else {
					if(adjCell.getRoom())
						targets.add(adjCell);
					calcTargetsHelper(adjCell, pathlength-1);
				}
				visited.remove(adjCell);
			}
		}
	}
	
	public void setConfigFiles(String layout, String setUp) {
		layConFile = layout;
		setupConFile = setUp;
	}
	
	public void loadLayoutConfig() {
		
	}
	
	public void loadSetupConfig() {
		
	}
	
	//get room based on a cell
	public Room getRoom(BoardCell cell) {
		//return to clear error
		return new Room();
	}
	
	//get room based on a char
	public Room getRoom(char c) {
		return new Room();
	}
	
	public int getNumRows() {
		//returning a final for now
		return ROWS;
	}
	
	public int getNumColumns() {
		//returning a final for now
		return COLS;
	}
	
	public BoardCell getCell(int row, int col) {
		//will give us the cell from the board at row, col
		return grid[row][col];
	}
	
	public Set<BoardCell> getTargets() {
		//gets the targets created by calcTargets
		return targets;
	}

	public Set<BoardCell> getAdjList(int row, int col) {
		// TODO Auto-generated method stub
		return adjList;
		// need to actually the implemented adjacency list in this code for the next assignment. will work for C15A-2
	}

}
