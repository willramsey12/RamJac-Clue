package clueGame;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Board {
    private BoardCell[][] grid = new BoardCell[ROWS][COLS];
    private Set<BoardCell> targets = new HashSet<>();
    private Set<BoardCell> visited = new HashSet<>();
    private Map<Character, Room> roomMap = new HashMap<>();
    private Set<BoardCell> adjList = new HashSet<>();

    private static final int COLS = 25;
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
        loadLayoutConfig(); // Then load the layout configuration

        // Initialize adjacency lists for the grid
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
                        //System.out.println(cell);
                    }
                    if (cellData.contains("#")) {
                        cell.setRoomLabel(true);
                        Room room = getRoom(initial);
                        room.setLabelCell(cell);
                        //System.out.println(cell);
                    }

                    // Handle secret passages
                    if (cellData.length() == 2) {
                        char secretPassage = cellData.charAt(1);
                        cell.setSecretPassage(secretPassage);
                    }

                    // Handle door directions
                    if (cellData.length() > 1) {
                        char doorChar = cellData.charAt(1);
                        switch (doorChar) {
                            case '^':
                                cell.setDoorDirection(DoorDirection.UP);
                                cell.setDoorway(true);
                                System.out.println("Door at " + row + ", " + col + ": UP");
                                break;
                            case 'v':
                                cell.setDoorDirection(DoorDirection.DOWN);
                                cell.setDoorway(true);
                                System.out.println("Door at " + row + ", " + col + ": DOWN");
                                break;
                            case '<':
                                cell.setDoorDirection(DoorDirection.LEFT);
                                cell.setDoorway(true);
                                System.out.println("Door at " + row + ", " + col + ": LEFT");
                                break;
                            case '>':
                                cell.setDoorDirection(DoorDirection.RIGHT);
                                cell.setDoorway(true);
                                System.out.println("Door at " + row + ", " + col + ": RIGHT");
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

    // Initialize the adjacency lists for all cells
    private void initializeAdjacencyLists() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                BoardCell cell = grid[i][j];
                // Add adjacent cells for each grid cell
                if (i > 0) cell.addAdjacency(grid[i - 1][j]);  // Above
                if (i < ROWS - 1) cell.addAdjacency(grid[i + 1][j]);  // Below
                if (j > 0) cell.addAdjacency(grid[i][j - 1]);  // Left
                if (j < COLS - 1) cell.addAdjacency(grid[i][j + 1]);  // Right
            }
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
    
    public Set<BoardCell> getAdjList(int row, int col){
    	return adjList;
    }
}
