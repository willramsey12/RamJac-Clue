package clueGame;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Board {
    private BoardCell[][] grid;
    private Set<BoardCell> targets = new HashSet<>();
    private Set<BoardCell> visited = new HashSet<>();
    private Map<Character, Room> roomMap = new HashMap<>();

    private static final int COLS = 24;
    private static final int ROWS = 25;

    private String layConFile;
    private String setupConFile;

    private static Board theInstance = new Board();

    // Private constructor to enforce singleton pattern
    private Board() {
        // No initialization in constructor
    }

    // Singleton pattern - return the only instance
    public static Board getInstance() {
        return theInstance;
    }

    // Initialize the board after setting config files
    public void initialize() {
        if (layConFile == null || setupConFile == null) {
            throw new IllegalStateException("Configuration files are not set!");
        }

        loadSetupConfig();  // Load the setup configuration first
        loadLayoutConfig(); // Then load the layout configuration

        // Initialize adjacency lists for the grid
        initializeAdjacencyLists();
    }

    // Set the configuration files for layout and setup
    public void setConfigFiles(String layout, String setUp) {
        this.layConFile = layout;
        this.setupConFile = setUp;
    }

    // Load the layout configuration (e.g., ClueLayout.csv)
    public void loadLayoutConfig() {
        try (BufferedReader reader = new BufferedReader(new FileReader(layConFile))) {
            String line;
            int row = 0;

            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");
                for (int col = 0; col < tokens.length; col++) {
                    String cellData = tokens[col].trim();
                    char initial = cellData.charAt(0);

                    BoardCell cell = new BoardCell(row, col);

                    // Handle room labels and center cells
                    if (cellData.contains("*")) {
                        cell.setRoomCenter(true);
                        Room room = getRoom(initial);
                        room.setCenterCell(cell);
                    }
                    if (cellData.contains("#")) {
                        cell.setRoomLabel(true);
                        Room room = getRoom(initial);
                        room.setLabelCell(cell);
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
                                break;
                            case 'v':
                                cell.setDoorDirection(DoorDirection.DOWN);
                                break;
                            case '<':
                                cell.setDoorDirection(DoorDirection.LEFT);
                                break;
                            case '>':
                                cell.setDoorDirection(DoorDirection.RIGHT);
                                break;
                        }
                    }

                    grid[row][col] = cell;
                }
                row++;
            }
        } catch (IOException e) {
            System.err.println("Error loading layout configuration: " + e.getMessage());
        }
    }

    // Load the setup configuration (e.g., ClueSetup.txt)
    public void loadSetupConfig() {
        try (BufferedReader reader = new BufferedReader(new FileReader(setupConFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("\\") || line.trim().isEmpty()) {
                    continue;  // Skip comments and empty lines
                }

                String[] tokens = line.split(", ");
                if (tokens[0].equals("Room") || tokens[0].equals("Space")) {
                    char roomInitial = tokens[2].charAt(0);  // E.g., 'R'
                    String roomName = tokens[1];             // E.g., "Hollowed Reliquary"
                    Room room = new Room();
                    room.setName(roomName);
                    roomMap.put(roomInitial, room);
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

    // Calculate targets (legal moves) from a starting cell and a given path length
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
}
