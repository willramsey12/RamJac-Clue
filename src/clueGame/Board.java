package clueGame;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Collections;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;

public class Board extends JPanel {
    private BoardCell[][] grid;
    private Set<BoardCell> targets = new HashSet<>();
    private Set<BoardCell> visited = new HashSet<>();
    private Map<Character, Room> roomMap = new HashMap<>();
    private Map<Character, BoardCell> centerMap = new HashMap<>();
    private ArrayList<BoardCell> doors = new ArrayList<>();
    private Map<Character, Character> secretPassages = new HashMap<>();
    private ArrayList<Card> deck = new ArrayList<>();
    private Solution trueSolution = new Solution();
    private ArrayList<Player> players = new ArrayList<>();


    private int numCols;
    private int numRows;

    private String layoutConfig;
    private String setupConFile;

    private static Board theInstance = new Board();
    private static final String CONFIG_DIRECTORY = "data/";

    // Private constructor to enforce singleton pattern
    private Board() {
        // No initialization in constructor
    }
    
    public void setConfigFiles(String layoutFileName, String setupFileName) {
        // Combine the relative directory and file name to create the path
        this.layoutConfig = CONFIG_DIRECTORY + layoutFileName;
        this.setupConFile = CONFIG_DIRECTORY + setupFileName;
    }

    // Singleton pattern return the only instance
    public static Board getInstance() {
        return theInstance;
    }

    // Initialize the board after setting config files
    public void initialize() throws BadConfigFormatException {
        if (layoutConfig == null || setupConFile == null) {
            throw new IllegalStateException("Configuration files are not set!");
        }
        
        loadSetupConfig();  // Load the setup configuration first
        loadLayoutConfig();
        initializeAdjacencyLists();
        setupGame();
    }

    // Load the layout configuration (e.g., ClueLayout.csv)
    public void loadLayoutConfig() throws BadConfigFormatException {
        try (BufferedReader reader = new BufferedReader(new FileReader(layoutConfig))) {
            String line;
            
            // Read the first line to determine the number of columns
            line = reader.readLine();
            if (line == null) {
                throw new BadConfigFormatException("Layout configuration file is empty.");
            }
            
            String[] tokens = line.split(",");
            numCols = tokens.length;// Set the number of columns based on the first line
            System.out.println(numCols);
            ArrayList<String[]> layoutLines = new ArrayList<>();
            layoutLines.add(tokens);
            
            while ((line = reader.readLine()) != null) {
                tokens = line.split(",");
                if (tokens.length != numCols) {
                    throw new BadConfigFormatException("Inconsistent number of columns at line: " + layoutLines.size());
                }
                layoutLines.add(tokens);
            }
            numRows = layoutLines.size();
            System.out.println(numRows);// Set the number of rows based on the total lines read

            // Initialize the grid with determined size
            grid = new BoardCell[numRows][numCols];

            for (int row = 0; row < numRows; row++) {
                tokens = layoutLines.get(row);
                for (int col = 0; col < numCols; col++) {
                    String cellData = tokens[col].trim();
                    char initial = cellData.charAt(0);

                    if (!roomMap.containsKey(initial)) {
                        throw new BadConfigFormatException("Room initial " + initial + " in layout not found in setup configuration.");
                    }

                    BoardCell cell = new BoardCell(row, col, initial);
                    setUpRoom(cellData, initial, cell);
                    grid[row][col] = cell;
                }
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
                if (tokens.length != 3 && tokens.length != 2 && tokens.length != 6) {
                    throw new BadConfigFormatException("Bad format in setup configuration: " + line);
                }
                //sets the room name and initial and puts it in our map to be pulled from later
                if (tokens[0].equals("Room")) {
                	// initialize room
                    char roomInitial = tokens[2].charAt(0);  
                    String roomName = tokens[1];             
                    Room room = new Room();
                    room.setName(roomName);
                    roomMap.put(roomInitial, room);
                    
                    // initialize card
                	Card card = new Card(tokens[1], CardType.ROOM);
                	deck.add(card);
                }
                else if (tokens[0].equals("Space")) {
                	// initialize room
                    char roomInitial = tokens[2].charAt(0);  
                    String roomName = tokens[1];             
                    Room room = new Room();
                    room.setName(roomName);
                    roomMap.put(roomInitial, room);
                }
                
                else if (tokens[0].equals("Person")){
                	// initialize card
                	String Name = tokens[1];
                	Card card = new Card(tokens[1], CardType.PERSON);
                	deck.add(card);
                	
                	// initialize player
                	String color = tokens[2];
                	int row = Integer.parseInt(tokens[3]);
                	int col = Integer.parseInt(tokens[4]);
                	if (tokens[5].equals("COMPUTER")) {
                		// initialize the non-human player
                		Player player = new ComputerPlayer();
                		player.setName(Name);
                		player.setColor(color);
                		player.setLocation(row, col);
                		players.add(player);
                	}
                	else if (tokens[5].equals("HUMAN")){
                		// initialize human player
                		Player player = new HumanPlayer();
                		player.setName(Name);
                		player.setColor(color);
                		player.setLocation(row, col);
                		players.add(player);
                	}
              
                }
                else if (tokens[0].equals("Weapon")) {
                	// initialize person card
                	Card card = new Card(tokens[1], CardType.WEAPON);
                	deck.add(card);
                }
                
                else {
                	throw new BadConfigFormatException("Unexpected room type in setup configuration: " + tokens[0]);
                }
                
                //System.out.println("end of deck");
                
            }
            for (Card card : deck) {
            	//System.out.println(card);
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
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                setAjcListInitializer(grid[row][col]);
            }
        }
    }
    
    // helper function that calls individaul functions depending on what the cell type is
    private void setAjcListInitializer(BoardCell cell) {
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
    	if ((r+1 < numRows - 1) && (grid[r+1][c].isDoorway() || grid[r+1][c].iswalk())) {
    		grid[r][c].addAdjacency(grid[r+1][c]);
    	}
    	// to check cell below
    	if ((r-1 >= 0) && (grid[r-1][c].isDoorway() || grid[r-1][c].iswalk())) {
    		grid[r][c].addAdjacency(grid[r-1][c]);
    	}
    	// to check cell right
    	if ((c+1 < numCols - 1) && (grid[r][c+1].isDoorway() || grid[r][c+1].iswalk())) {
    		grid[r][c].addAdjacency(grid[r][c+1]);
    	}
    	// to check cell left
    	if ((c-1 >= 0) && (grid[r][c-1].isDoorway() || grid[r][c-1].iswalk())) {
    		grid[r][c].addAdjacency(grid[r][c-1]);
    	}
    }

    // this function creates ajc list if the cell is a doorway
    private void doorwayAdjacency(int r, int c) {
        int roomRow = r; 
        int roomCol = c;

        // Adjust room coordinates based on door direction
        switch (grid[r][c].getDoorDirection()) {
            case LEFT:
                roomCol = c - 1;
                break;
            case RIGHT:
                roomCol = c + 1;
                break;
            case UP:
                roomRow = r - 1;
                break;
            case DOWN:
                roomRow = r + 1;
                break;
            default:
        	   break;
        }

        // Add room center to adjacency list and handle walkways
        Character roomInitial = grid[roomRow][roomCol].getInitial();
        grid[r][c].addAdjacency(centerMap.get(roomInitial));
        walkAdjacency(r, c);
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
    
    public void setupGame(){
    	ArrayList<Card> tempDeck = new ArrayList<>(deck);
    	// shuffle the deck
    	Collections.shuffle(tempDeck);
    	
    	// get cards for solution
    	trueSolution.setPerson(cardSolution(tempDeck, CardType.PERSON));
    	trueSolution.setRoom(cardSolution(tempDeck, CardType.ROOM));
    	trueSolution.setWeapon(cardSolution(tempDeck, CardType.WEAPON));
    	
    	// deal the remaining cards to the players
    	dealCards(tempDeck);
    }
    
    // helper function to set aside one card of each type for solution
    private Card cardSolution(ArrayList<Card> d, CardType C) {
    	for (Card card : deck) {
    		if (card.getCardType() == C) {
    			d.remove(card);
    			return card;
    		}
    	}
		return null;
    }
    
    private void dealCards(ArrayList<Card> d) {
        int playerIndex = 0;
        while (!d.isEmpty()) {
            // Get the current player
            Player currentPlayer = players.get(playerIndex);

            // Deal one card to the current player
            Card cardToDeal = d.remove(0); // Remove the top card from the deck
            currentPlayer.updateHand(cardToDeal);

            // Move to the next player in a round-robin fashion
            playerIndex = (playerIndex + 1) % players.size();
        }
    	
    }
    
    public boolean checkAccusation(Card room, Card person, Card weapon) {
        return room.equals(trueSolution.getRoom()) &&
               person.equals(trueSolution.getPerson()) &&
               weapon.equals(trueSolution.getWeapon());
    }
    
    public Card handleSuggestion(Player suggestingPlayer, Card person, Card room, Card weapon) {
        int startIndex = players.indexOf(suggestingPlayer);

        for (int i = 1; i < players.size(); i++) {
            Player player = players.get((startIndex + i) % players.size());

            Card disproveCard = player.disproveSuggestion(person, room, weapon);

            // If a card is found that can disprove the suggestion, return it
            if (disproveCard != null) {
                return disproveCard;
            }
        }

        // If no players can disprove the suggestion, return null
        return null;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Determine cell size based on panel size and number of rows/columns
        int rectLength = getHeight() / numRows;
        int rectWidth = getWidth() / numCols;
        int size = Math.min(rectLength, rectWidth); // Set cell size to smallest dimension

        // Draw each cell in the grid based on its type
        for (int drawRowNum = 0; drawRowNum < numRows; drawRowNum++) {
            for (int drawColNum = 0; drawColNum < numCols; drawColNum++) {
                BoardCell currCell = grid[drawRowNum][drawColNum];

                // Draw cell background based on cell type
                if (currCell.getInitial() == 'W') {
                    currCell.draw(g, size, Color.YELLOW);  // Walkway cells
                } else if (currCell.getInitial() == 'X') {
                    currCell.draw(g, size, Color.BLACK);   // Unreachable areas
                } else {
                    currCell.draw(g, size, Color.LIGHT_GRAY);  // Rooms
                }
            }
        }

        // Highlight target cells
//        for (BoardCell target : targets) {
//            target.draw(g, size, Color.CYAN);  // Highlighted color for targets
//            if (target.isRoomCenter()) {
//                // Draw room cells as highlighted if the target cell is a room center
//                Room room = roomMap.get(target.getInitial());
//                for (BoardCell roomCell : room.getRoomCells()) {
//                    roomCell.draw(g, size, Color.CYAN);
//                }
//            }
//        }

        // Draw overlays for doorways and room names
//        for (int drawRowNum = 0; drawRowNum < numRows; drawRowNum++) {
//            for (int drawColNum = 0; drawColNum < numCols; drawColNum++) {
//                grid[drawRowNum][drawColNum].drawOverlay(g, size);
//            }
//        }

        // Draw players on the board
//        ArrayList<BoardCell> playerLocation = new ArrayList<>();
//        for (int playerIndex = 0; playerIndex < playerList.size(); playerIndex++) {
//            Player player = playerList.get(playerIndex);
//            BoardCell playerCell = grid[player.getRow()][player.getCol()];
//            playerLocation.add(playerCell);
//
//            // Offset if multiple players are in the same cell
//            int offset = 0;
//            for (int i = 0; i < playerIndex; i++) {
//                if (playerLocation.get(i).equals(playerLocation.get(playerIndex))) {
//                    offset++;
//                }
//            }
//            player.draw(g, size, offset);
//        }
    }
    
    public void drawRoomNames(Graphics g, int cellWidth, int cellHeight) {
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.PLAIN, cellHeight/2)); 

        for (Room room : roomMap.values()) {
            BoardCell labelCell = room.getLabelCell();
            if (labelCell != null) {
                int x = labelCell.getCol() * cellWidth;
                int y = labelCell.getRow() * cellHeight;

                // Split the room name into words
                String[] words = room.getName().split(" ");

                // Draw each word, adjusting the y-coordinate for subsequent words
                for (int i = 0; i < words.length; i++) {
                    g.drawString(words[i], x, y + (i * g.getFontMetrics().getHeight()));
                }
            }
        }
    }
    public Room getRoom(BoardCell cell) {
        return roomMap.get(cell.getInitial());
    }

    // Retrieve the room based on a character initial
    public Room getRoom(char c) {
        return roomMap.get(c);
    }

    // Get number of rows
    public int getNumRows() {
        return numRows;
    }

    // Get number of columns
    public int getNumColumns() {
        return numCols;
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
    
    public ArrayList<Player> getPlayers(){
    	return players;
    }
    
    public ArrayList<Card> getDeck() {
		return deck;
	}

	public Solution getSolution() {
		return trueSolution;
	}
	 
	public Map<Character, Room> getRoomMap(){
		return roomMap;
	}
}

