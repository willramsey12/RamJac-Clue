package clueGame;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Board {
    private BoardCell[][] grid = new BoardCell[ROWS][COLS];
    private Set<BoardCell> targets = new HashSet<>();
    private Set<BoardCell> visited = new HashSet<>();
    private Map<Character, Room> roomMap = new HashMap<>();
    private Map<Character, BoardCell> centerMap = new HashMap<>();
    private ArrayList<BoardCell> doors = new ArrayList<>();
    private Map<Character, ArrayList<BoardCell>> doorsToCenter = new HashMap<>();
    private Map<Character, Character> secretPassages = new HashMap<>();
    

    private static final int COLS = 24;
    private static final int ROWS = 25;

    private String layConFile;
    private String setupConFile;

    private static Board theInstance = new Board();
    private static final String CONFIG_DIRECTORY = "data/";

    // Private constructor to enforce singleton pattern
    private Board() {
        // No initialization in constructor
    }
    
    public void setConfigFiles(String layoutFileName, String setupFileName) {
        // Combine the relative directory and file name to create the path
        this.layConFile = CONFIG_DIRECTORY + layoutFileName;
        this.setupConFile = CONFIG_DIRECTORY + setupFileName;
    }

    // Singleton pattern return the only instance
    public static Board getInstance() {
        return theInstance;
    }

    // Initialize the board after setting config files
    public void initialize() throws BadConfigFormatException {
        if (layConFile == null || setupConFile == null) {
            throw new IllegalStateException("Configuration files are not set!");
        }
        
        loadSetupConfig();  // Load the setup configuration first
        loadLayoutConfig();
        initializeAdjacencyLists();

        // Initialize adjacency lists for the grid
        //initializeAdjacencyLists();
        
    }

    // Load the layout configuration (e.g., ClueLayout.csv)
    public void loadLayoutConfig() throws BadConfigFormatException {
        try (BufferedReader reader = new BufferedReader(new FileReader(layConFile))) {
            String line;
            int row = 0;

            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");
                // Check if the number of columns matches the expected number
                //System.out.println(tokens.length);
                if (tokens.length != COLS) {
                	
                    throw new BadConfigFormatException("Number of columns does not match expected value at row " + row);
                }
               
                //room validation
                for (int col = 0; col < tokens.length; col++) {
                    String cellData = tokens[col].trim();
                    //System.out.println("Raw cellData: '" + cellData + "' with length: " + cellData.length());
                    char initial = cellData.charAt(0);
                    
                    if (!roomMap.containsKey(initial)) {
                    	//System.out.println("initial"+initial+"bitch");
                        throw new BadConfigFormatException("Room initial " + initial + " in layout not found in setup configuration.");
                    }

                    BoardCell cell = new BoardCell(row, col, initial);

                    // Handle room labels and center cells
                    if (cellData.contains("*")) {
                        cell.setRoomCenter(true);
                        Room room = getRoom(initial);
                        room.setCenterCell(cell);
                        centerMap.put(initial, cell);
                        //System.out.println(cell);
                    }
                    if (cellData.contains("#")) {
                        cell.setRoomLabel(true);
                        Room room = getRoom(initial);
                        room.setLabelCell(cell);
                        //System.out.println(cell);
                    }
                    if (cellData.contains("W") && cellData.length() == 1) {
                        cell.setWalk(true);
                        
                        //System.out.println(cell);
                        
                    }

                    // Handle secret passages
                    if (cellData.length() == 2 && Character.isLetter(cellData.charAt(1))) {
                        char secondChar = cellData.charAt(1);
                        if (roomMap.containsKey(secondChar)) {
                            // If the second character is a valid room initial, it's a secret passage
                            cell.setSecretPassage(secondChar);
                        }
                        secretPassages.put(initial, secondChar);
                    }
                    
                    // Handle rooms
                    if (roomMap.containsKey(initial) && initial != 'X' && initial != 'W') {
                    	cell.setRoom(true);
                    }
                    // Handle door directions
                    if (cellData.length() > 1 && cellData.contains("W")) {
                        char doorChar = cellData.charAt(1);
                        doors.add(cell);
                        switch (doorChar) {
                            case '^':
                                cell.setDoorDirection(DoorDirection.UP);
                                cell.setDoorway(true);
                       
                                //System.out.println("Door at " + row + ", " + col + ": UP");
                                break;
                            case 'v':
                                cell.setDoorDirection(DoorDirection.DOWN);
                                cell.setDoorway(true);

                               // System.out.println("Door at " + row + ", " + col + ": DOWN");
                                break;
                            case '<':
                                cell.setDoorDirection(DoorDirection.LEFT);
                                cell.setDoorway(true);

                                //System.out.println("Door at " + row + ", " + col + ": LEFT");
                                break;
                            case '>':
                                cell.setDoorDirection(DoorDirection.RIGHT);
                                cell.setDoorway(true);

                                //System.out.println("Door at " + row + ", " + col + ": RIGHT");
                                break;
                            default:
                                //System.out.println("Not a door at " + row + ", " + col);
                                break;
                        }
                    }
                    

                    grid[row][col] = cell;
                    //System.out.println(cell);
                    
                    
                }
                row++;
            }
        } catch (IOException e) {
            System.err.println("Error loading layout configuration: " + e.getMessage());
        }
    }
    


    // Load the setup configuration
    public void loadSetupConfig() throws BadConfigFormatException {
        try (BufferedReader reader = new BufferedReader(new FileReader(setupConFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                
            	
            	if (line.startsWith("//") || line.trim().isEmpty()) {
                    continue;  // Skip comments and empty lines
                }

                String[] tokens = line.split(", ");
                
                if (tokens.length != 3) {
                    throw new BadConfigFormatException("Bad format in setup configuration: " + line);
                }
                
                if (tokens[0].equals("Room") || tokens[0].equals("Space")) {
                    char roomInitial = tokens[2].charAt(0);  
                    String roomName = tokens[1];             
                    Room room = new Room();
                    room.setName(roomName);
                    roomMap.put(roomInitial, room);
                }
                else {
                	throw new BadConfigFormatException("Unexpected room type in setup configuration: " + tokens[0]);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading setup configuration: " + e.getMessage());
        }
    }

    
    // Calculate targets from a starting cell and a given path length
    public void calcTargets(BoardCell startCell, int pathlength) {
        targets.clear();
        visited.clear();
        visited.add(startCell);
        calcTargetsHelper(startCell, pathlength);
    }

    // Recursive helper function to calculate targets
    private void calcTargetsHelper(BoardCell startCell, int pathlength) {
        for (BoardCell adjCell : startCell.getAdjList()) {
            if (!visited.contains(adjCell)) {
                visited.add(adjCell);
                if (pathlength == 1) {
                    if (!adjCell.isOccupied()) targets.add(adjCell);
                } else {
                    if (adjCell.isRoom()) targets.add(adjCell);
                    calcTargetsHelper(adjCell, pathlength - 1);
                }
                visited.remove(adjCell);
            }
        }
    }

    private void initializeAdjacencyLists() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                setAdjacencyList(grid[row][col]);
            }
        }
    }
    
    private void setAdjacencyList(BoardCell cell) {
        // adding adjacency list for a walkway
        if (cell.iswalk()) {
        	walkAdjacency(cell.getRow(), cell.getCol());
        }
        // adding adjacency list for a doorway
        else if (cell.isDoorway()) {
        	doorwayAdjacency(cell.getRow(), cell.getCol());
        }
        // adding adjacency for a center of the room
        else if (cell.isRoomCenter()) {
        	centerRoomAdjacency(cell.getRow(), cell.getCol());
        }
    }
    
    private void walkAdjacency(int r, int c) {
    	// to check cell below
    	if ((r+1 < ROWS - 1) && (grid[r+1][c].isDoorway() || grid[r+1][c].iswalk())) {
    		grid[r][c].addAdjacency(grid[r+1][c]);
    	}
    	// to check cell below
    	if ((r-1 > 0) && (grid[r-1][c].isDoorway() || grid[r-1][c].iswalk())) {
    		grid[r][c].addAdjacency(grid[r-1][c]);
    	}
    	// to check cell right
    	if ((c+1 < COLS - 1) && (grid[r][c+1].isDoorway() || grid[r][c+1].iswalk())) {
    		grid[r][c].addAdjacency(grid[r][c+1]);
    	}
    	// to check cell left
    	if ((c-1 > 0) && (grid[r][c-1].isDoorway() || grid[r][c-1].iswalk())) {
    		grid[r][c].addAdjacency(grid[r][c-1]);
    	}
    }
    
    private void doorwayAdjacency(int r, int c) {
    	// for a door direction going left
    	if (grid[r][c].getDoorDirection() == DoorDirection.LEFT) {
    		Character roomInitial = grid[r][c-1].getInitial();
    		grid[r][c].addAdjacency(centerMap.get(roomInitial));
    		walkAdjacency(r, c);
    	}
    	// for a door direction going right
    	else if (grid[r][c].getDoorDirection() == DoorDirection.RIGHT) {
    		Character roomInitial = grid[r][c+1].getInitial();
    		grid[r][c].addAdjacency(centerMap.get(roomInitial));
    		walkAdjacency(r, c);
    	}
    	else if (grid[r][c].getDoorDirection() == DoorDirection.UP) {
    		Character roomInitial = grid[r-1][c].getInitial();
    		grid[r][c].addAdjacency(centerMap.get(roomInitial));
    		walkAdjacency(r, c);
    	}
    	else if (grid[r][c].getDoorDirection() == DoorDirection.DOWN) {
    		Character roomInitial = grid[r+1][c].getInitial();
    		grid[r][c].addAdjacency(centerMap.get(roomInitial));
    		walkAdjacency(r, c);
    	}
    }
     
    private void centerRoomAdjacency(int r, int c) {
		System.out.println(doors);
		//System.out.println(doors.size());
    	for (int i = 0; i < doors.size(); i++) {
    		DoorDirection dir = doors.get(i).getDoorDirection();
    		if (dir == DoorDirection.RIGHT) {
    			if (grid[doors.get(i).getRow()][doors.get(i).getCol()+1].getInitial() == grid[r][c].getInitial()) {
    				grid[r][c].addAdjacency(doors.get(i));
    			}
    		}
    		else if (dir == DoorDirection.LEFT) {
    			if (grid[doors.get(i).getRow()][doors.get(i).getCol()-1].getInitial() == grid[r][c].getInitial()) {
    				grid[r][c].addAdjacency(doors.get(i));
    			}
    		}
    		else if (dir == DoorDirection.UP) {
    			if (grid[doors.get(i).getRow()-1][doors.get(i).getCol()].getInitial() == grid[r][c].getInitial()) {
    				grid[r][c].addAdjacency(doors.get(i));
    			}
    		}
    		else if (dir == DoorDirection.DOWN) {
    			if (grid[doors.get(i).getRow()+1][doors.get(i).getCol()].getInitial() == grid[r][c].getInitial()) {
    				grid[r][c].addAdjacency(doors.get(i));
    			}
    		}
    	}
    	
    	
		
		  if (secretPassages.containsKey(grid[r][c].getInitial())) { 
			  char jit = secretPassages.get(grid[r][c].getInitial()); 
			  BoardCell adjCell = centerMap.get(jit); grid[r][c].addAdjacency(adjCell); }
		 
    }

    // Retrieve the room associated with a BoardCell
    public Room getRoom(BoardCell cell) {
        return roomMap.get(cell.getInitial());
    }

    // Retrieve the room based on a character initial
    public Room getRoom(char c) {
        return roomMap.get(c);
    }

    // Get number of rows
    public  int getNumRows() {
        return ROWS;
    }

    // Get number of columns
    public  int getNumColumns() {
        return COLS;
    }
    
    public BoardCell[][] getGrid() {
    	return grid;
    }

    // Get a specific cell from the board
    public BoardCell getCell(int row, int col) {
        return grid[row][col];
    }

    // Get the set of calculated targets
    public Set<BoardCell> getTargets() {
        return targets;
    }
    
    public Set<BoardCell> getAdjList(int row, int col){
    	return grid[row][col].getAdjList();
    }
}
