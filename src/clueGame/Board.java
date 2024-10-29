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
    }

    // Load the layout configuration (e.g., ClueLayout.csv)
    public void loadLayoutConfig() throws BadConfigFormatException {
        try (BufferedReader reader = new BufferedReader(new FileReader(layConFile))) {
            String line;
            int row = 0;

            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");
                // Check if the number of columns matches the expected number
                if (tokens.length != COLS) {
                	
                    throw new BadConfigFormatException("Number of columns does not match expected value at row " + row);
                }
               
                //room validation
                for (int col = 0; col < tokens.length; col++) {
                    String cellData = tokens[col].trim();
                    char initial = cellData.charAt(0);
                    
                    if (!roomMap.containsKey(initial)) {
                        throw new BadConfigFormatException("Room initial " + initial + " in layout not found in setup configuration.");
                    }

                    BoardCell cell = new BoardCell(row, col, initial);
                    //function to set up room initial, center, if secret passage, and other thing contained in the csv file
                    setUpRoom(cellData,  initial,  cell);

          
                    grid[row][col] = cell;
                    
                    
                }
                row++;
            }
        } catch (IOException e) {
            System.err.println("Error loading layout configuration: " + e.getMessage());
        }
    }
    // function to help with setUpRoom function, changes the cell instance variable isRoom to true if it is not a X or W
    public void setRoom(String cellData, BoardCell cell) {
    	cell.setRoom(!(cellData.contains("X") || cellData.contains("W")));
    	
    }
    
    public void setUpRoom(String cellData, char initial, BoardCell cell) {
    	// Handle room labels and center cells
        setRoom(cellData, cell);
        if (cellData.contains("*")) {
            cell.setRoomCenter(true);
            Room room = getRoom(initial);
            room.setCenterCell(cell);
            centerMap.put(initial, cell);
        }
        //this sets the room label to true if this is where the room script will be written
        if (cellData.contains("#")) {
            cell.setRoomLabel(true);
            Room room = getRoom(initial);
            room.setLabelCell(cell);
        }
        
        // set setWalk to true if a walkway, this helps us with our ajc list further in the code
        if (cellData.contains("W") && cellData.length() == 1) {
            cell.setWalk(true);
            
            
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
        // Handle door directions and sets the doorway boolean to true for that room cell
        if (cellData.length() > 1 && cellData.contains("W")) {
            char doorChar = cellData.charAt(1);
            doors.add(cell);
            switch (doorChar) {
                case '^':
                    cell.setDoorDirection(DoorDirection.UP);
                    cell.setDoorway(true);
           
                    break;
                case 'v':
                    cell.setDoorDirection(DoorDirection.DOWN);
                    cell.setDoorway(true);

                    break;
                case '<':
                    cell.setDoorDirection(DoorDirection.LEFT);
                    cell.setDoorway(true);

                    break;
                case '>':
                    cell.setDoorDirection(DoorDirection.RIGHT);
                    cell.setDoorway(true);

                    break;
                default:
                    break;
            }
        }
    }
    
    // Load the setup configuration
    public void loadSetupConfig() throws BadConfigFormatException {
    	//starts reading in the file for configuring the rooms and there names
        try (BufferedReader reader = new BufferedReader(new FileReader(setupConFile))) {
            String line;
            //makes sure next line isnt empty
            while ((line = reader.readLine()) != null) {
            	 //ignores lines commented out
            	if (line.startsWith("//") || line.trim().isEmpty()) {
                    continue;  // Skip comments and empty lines
                }
            	// starts a string that gets split at commas
                String[] tokens = line.split(", ");
                // check if the file isnt congigured as we are expecting
                if (tokens.length != 3) {
                    throw new BadConfigFormatException("Bad format in setup configuration: " + line);
                }
                //sets the room name and initial and puts it in our map to be pulled from later
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
		//calculates legal moves from startCell a distance of pathLength away
		// always clears the targets and visited lists so there is no bleed over from previouse calls
		targets.clear();
		visited.clear();
		visited.add(startCell);
		//calls its helper
		calcTargetsHelper(startCell, pathlength);
	}

	public void calcTargetsHelper(BoardCell startCell, int pathlength) {
		//loops throuhg all the cells in the startercells ajc list 
	    for (BoardCell adjCell : startCell.getAdjList()) {
	        
	        // Skip if it's already visited, unless it's an occupied room
	        // Skip if it's occupied and not a room
	        if ((adjCell.isOccupied() && !adjCell.isRoom()) || visited.contains(adjCell)) {
	            continue;
	        }
	        
	        visited.add(adjCell);

	        // Always allow movement into rooms, even if occupied
	        if (adjCell.isRoom()) {
	            targets.add(adjCell);
	        }
	        else if (pathlength == 1) {
	            // Add cell if we're at the end of the path and it's not occupied
	            targets.add(adjCell);
	        }
	        else {
	            // Recursively calculate targets for further steps
	            calcTargetsHelper(adjCell, pathlength - 1);
	        }

	        visited.remove(adjCell);  // Backtrack to explore other paths
	    }
	}
    
    // this function is a helper function for creating our ajc list, it returns the cell that is the room center for a particular room,
	//with only the room initial given
    public BoardCell getRoomCenter(char roomInitial) {
        Room room = roomMap.get(roomInitial);
        return room != null ? room.getCenterCell() : null;
    }

    //this starts initializing the ajc list for every cell on out map, becuase out grid is 2D we have to use a nested for loop
    private void initializeAdjacencyLists() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                setAdjacencyList(grid[row][col]);
            }
        }
    }
    
    // helper function that calls individaul functions depending on what the cell type is
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
    
    // this function gets called if the cell is a walkway
    private void walkAdjacency(int r, int c) {
    	// to check cell above
    	if ((r+1 < ROWS - 1) && (grid[r+1][c].isDoorway() || grid[r+1][c].iswalk())) {
    		grid[r][c].addAdjacency(grid[r+1][c]);
    	}
    	// to check cell below
    	if ((r-1 >= 0) && (grid[r-1][c].isDoorway() || grid[r-1][c].iswalk())) {
    		grid[r][c].addAdjacency(grid[r-1][c]);
    	}
    	// to check cell right
    	if ((c+1 < COLS - 1) && (grid[r][c+1].isDoorway() || grid[r][c+1].iswalk())) {
    		grid[r][c].addAdjacency(grid[r][c+1]);
    	}
    	// to check cell left
    	if ((c-1 >= 0) && (grid[r][c-1].isDoorway() || grid[r][c-1].iswalk())) {
    		grid[r][c].addAdjacency(grid[r][c-1]);
    	}
    }

    // this function creates ajc list if the cell is a doorway
    private void doorwayAdjacency(int r, int c) {
    	// for a door direction going left
    	if (grid[r][c].getDoorDirection() == DoorDirection.LEFT) {
    		Character roomInitial = grid[r][c-1].getInitial();
    		grid[r][c].addAdjacency(centerMap.get(roomInitial));
    		//after adding the center of the room to the ajc list, it then just calls the walkway adj func.
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
   
    // this function creats the adj list if the cell is a center of the room
    private void centerRoomAdjacency(int r, int c) {
    	for (BoardCell door : doors) {
    	    DoorDirection dir = door.getDoorDirection();
    	    int doorRow = door.getRow();
    	    int doorCol = door.getCol();
    	    
    	    // Calculate adjacent cell coordinates based on direction
    	    int adjRow = doorRow; 
    	    int adjCol = doorCol;
    	    switch (dir) {
    	        case RIGHT:
    	            adjCol += 1;
    	            break;
    	        case LEFT:
    	            adjCol -= 1;
    	            break;
    	        case UP:
    	            adjRow -= 1;
    	            break;
    	        case DOWN:
    	            adjRow += 1;
    	            break;
    	        default:
    	        	break;
    	    }
    	    
    	    // Check if the adjacent cell's initial matches the target cell's initial
    	    if (grid[adjRow][adjCol].getInitial() == grid[r][c].getInitial()) {
    	        grid[r][c].addAdjacency(door);
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
    public int getNumRows() {
        return ROWS;
    }

    // Get number of columns
    public int getNumColumns() {
        return COLS;
    }

    // Get a specific cell from the board
    public BoardCell getCell(int row, int col) {
        return grid[row][col];
    }

    // Get the set of calculated targets
    public Set<BoardCell> getTargets() {
        return targets;
    }
    // returns the ajc list for a specific cell in our grid
    public Set<BoardCell> getAdjList(int row, int col){
    	return grid[row][col].getAdjList();
    }
}
