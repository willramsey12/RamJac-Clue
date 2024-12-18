package clueGame;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

public class ComputerPlayer extends Player {
	private ArrayList<Card> seenCards;
	Board board = Board.getInstance();
public ComputerPlayer(String name, String color, int row, int  col) {
		super(name, color, row, col, false);
		seenCards = new ArrayList<>();
		
	}
	
public ComputerPlayer() {
		super();
		seenCards = new ArrayList<>();
		
	}

public Solution createSuggestion(Room currentRoom) {
	    Random random = new Random();
	    Card suggestedRoom = null;
	    Player player = null;
	    ArrayList<Card> unseenPersons = new ArrayList<>();
	    ArrayList<Card> unseenWeapons = new ArrayList<>();
	    
	    // Collect unseen persons and weapons
	    for (Card card : board.getDeck()) {
	        if (!seenCards.contains(card)) {
	            if (card.getCardType() == CardType.PERSON) {
	                unseenPersons.add(card);
	            } else if (card.getCardType() == CardType.WEAPON) {
	                unseenWeapons.add(card);
	            }
	        }
	    }
	    for (Card card : this.getHand()) {
	        if (!seenCards.contains(card)) {
	            seenCards.add(card);
	        }
	    }
	    
	    // Choose random unseen person and weapon, handling cases where list might be empty
	    Card suggestedPerson = !unseenPersons.isEmpty() ? unseenPersons.get(random.nextInt(unseenPersons.size())) : null;
	    Card suggestedWeapon = !unseenWeapons.isEmpty() ? unseenWeapons.get(random.nextInt(unseenWeapons.size())) : null;
	    
	    for (Card card : board.getDeck()) {
	    	if (currentRoom.getName() == card.getCardName()) {
	    		suggestedRoom = card;
	    	}
	    }
	    
	    for (Player p : board.getPlayers()) {
	    	if (p.getName() == suggestedPerson.getCardName()) {
	    		player = p;
	    	}
	    }
	    
	    player.setLocation(board.getCurrentPlayer().getRow(), board.getCurrentPlayer().getCol());
	    
	    // Return the suggestion as a Solution object
	    return new Solution(suggestedRoom, suggestedPerson, suggestedWeapon);
	}

        
        

		
        

	public BoardCell selectTarget(Set<BoardCell> targets) {
        ArrayList<BoardCell> unseenRooms = new ArrayList<>();
        Random random = new Random();

        // Check each target: if it's an unseen room, add to unseenRooms list
        for (BoardCell target : targets) {
            if (target.isRoom() && !hasSeenRoom(target)) {
                unseenRooms.add(target);
            }
        }

        // If there's at least one unseen room, randomly select from unseenRooms
        if (!unseenRooms.isEmpty()) {
            return unseenRooms.get(random.nextInt(unseenRooms.size()));
        }

        // Otherwise, randomly select any target from the targets set
        ArrayList<BoardCell> targetList = new ArrayList<>(targets);
        return targetList.get(random.nextInt(targetList.size()));
    }

    private boolean hasSeenRoom(BoardCell roomCell) {
    	Board board = Board.getInstance();
        for (Card card : seenCards) {
            if (card.getCardType() == CardType.ROOM && card.getCardName().equals(board.getRoomMap().get(roomCell.getInitial()).getName())) {
                return true;
            }
        }
        return false;
    }
	

    public void addSeenCard(Card card) {
        if (!seenCards.contains(card)) {
            seenCards.add(card);
        }
    }
	
    
    public void clearSeenCards() {
	    seenCards.clear();
	}
	public ArrayList<Card> getSeenCards() {
		return seenCards;
	}
	
	public void updatePosition() {
		// Get the current player's position
	    BoardCell currentCell = board.getCell(getRow(), getCol());

	    // Calculate the available targets for the player
	    board.calcTargets(currentCell, board.getRoll());
	    Set<BoardCell> targets = board.getTargets();

	    // Select a target
	    BoardCell selectedTarget = selectTarget(targets);

	    // Update the computer player's position to the selected target
	    setLocation(selectedTarget.getRow(), selectedTarget.getCol());
	}
	
}
