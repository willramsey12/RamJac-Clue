package clueGame;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

public class ComputerPlayer extends Player {
	private ArrayList<Card> seenCards;
	public ComputerPlayer() {
		super();
		isHuman = false;
		seenCards = new ArrayList<>();
	}
	public Solution createSuggestion(Room currentRoom) {
		Random random = new Random();
		
		ArrayList<Card> unseenPersons = new ArrayList<>();
        ArrayList<Card> unseenWeapons = new ArrayList<>();
        
        for (Card card : this.getHand()) {
            // Exclude cards already seen
            if (!seenCards.contains(card)) {
            	
                if (card.getCardType() == CardType.PERSON) {
                    unseenPersons.add(card);
                    } 
                
                else if (card.getCardType() == CardType.WEAPON) {
                    unseenWeapons.add(card);
                }
            }
        }
            Card suggestedPerson = unseenPersons.get(random.nextInt(unseenPersons.size()));
            Card suggestedWeapon = unseenWeapons.get(random.nextInt(unseenWeapons.size()));

            // Use the current room as the suggested room
            Card suggestedRoom = new Card(currentRoom.getName(), CardType.ROOM);

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

    // Helper method to check if a room has been seen by the player
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
}
