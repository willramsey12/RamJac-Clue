package tests;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import clueGame.*;

public class ComputerAITest {
    private static Board board;
    private static ArrayList<Card> deck;
    private static ComputerPlayer comp1, comp2, comp3, comp4, comp5;

    @BeforeAll
    public static void setUp() throws BadConfigFormatException  {
        // Initialize board and configuration files
        board = Board.getInstance();
        board.setConfigFiles("ClueBoardLayout.csv", "ClueSetup.txt");
        board.initialize();
        deck = board.getDeck();

        
        comp1 = new ComputerPlayer("Computer1", "BLUE", 1, 1);
        comp2 = new ComputerPlayer("Computer2", "GREEN", 1, 1);
        comp3 = new ComputerPlayer("Computer3", "RED", 1, 1);
        comp4 = new ComputerPlayer("Computer4", "YELLOW", 1, 1);
        comp5 = new ComputerPlayer("Computer5", "PURPLE", 1, 1);

        
        comp1.updateHand(deck.get(0));  
        comp1.updateHand(deck.get(9));  
        comp1.updateHand(deck.get(15));

        comp2.updateHand(deck.get(1));  
        comp2.updateHand(deck.get(10)); 
        comp2.updateHand(deck.get(16));

        comp3.updateHand(deck.get(2));  
        comp3.updateHand(deck.get(11)); 
        comp3.updateHand(deck.get(17)); 

        comp4.updateHand(deck.get(3));  
        comp4.updateHand(deck.get(12)); 
        comp4.updateHand(deck.get(18)); 

        comp5.updateHand(deck.get(4));  
        comp5.updateHand(deck.get(13)); 
        comp5.updateHand(deck.get(19));
    }

    @Test
    public void testComputerSuggestion() {
        ArrayList<Card> hand = comp1.getHand();
        ArrayList<Card> seenCards = comp1.getSeenCards();
        Card roomCard = deck.get(0); 
        BoardCell cell = board.getCell(2,2);
        
        System.out.println(hand);
        System.out.println(comp1.getSeenCards());
        System.out.println(comp1.createSuggestion(board.getRoomMap().get(cell.getInitial())).getPerson());
        
        // Check if the created suggestion uses the current room, unseen person, and weapon
        assertTrue(comp1.createSuggestion(board.getRoomMap().get(cell.getInitial())).getRoom().equals(roomCard));
        assertFalse(hand.contains(comp1.createSuggestion(board.getRoomMap().get(cell.getInitial())).getPerson()));
        assertFalse(seenCards.contains(comp1.createSuggestion(board.getRoomMap().get(cell.getInitial())).getPerson()));
        assertFalse(hand.contains(comp1.createSuggestion(board.getRoomMap().get(cell.getInitial())).getWeapon()));
        assertFalse(seenCards.contains(comp1.createSuggestion(board.getRoomMap().get(cell.getInitial())).getWeapon()));
    }

    @Test
    public void testNarrowDownPersonCard() {
        comp2.addSeenCard(deck.get(6));  // Seen card
        comp2.addSeenCard(deck.get(11));
        comp2.addSeenCard(deck.get(16));
        comp2.addSeenCard(deck.get(13));
        ArrayList<Card> hand = comp2.getHand();
       // System.out.println(hand);
        //System.out.println(comp2.getSeenCards());
        
        ArrayList<Card> seenCards = comp2.getSeenCards();

        Card roomCard = deck.get(1);
        BoardCell cell = board.getCell(7,1);
       // System.out.println(comp2.createSuggestion(board.getRoomMap().get(cell.getInitial())).getPerson());
        
        assertTrue(comp2.createSuggestion(board.getRoomMap().get(cell.getInitial())).getRoom().equals(roomCard));
        assertFalse(hand.contains(comp2.createSuggestion(board.getRoomMap().get(cell.getInitial())).getPerson()));
        
        assertFalse(seenCards.contains(comp2.createSuggestion(board.getRoomMap().get(cell.getInitial())).getPerson()));
    }

    @Test
    public void testSelectTargetNoRooms() {
        Set<BoardCell> targets = Set.of(
            new BoardCell(1, 1, 'W'), new BoardCell(1, 2, 'W'), new BoardCell(2, 1, 'W')
        );
        Set<BoardCell> selectedTargets = new HashSet<>();

        for (int i = 0; i < 10; i++) {
            selectedTargets.add(comp1.selectTarget(targets));
        }

        assertEquals(3, selectedTargets.size());
        assertTrue(selectedTargets.containsAll(targets));
    }

    @Test
    public void testSelectTargetRoomNotSeen() {
        BoardCell roomCell = board.getCell(3, 3);
        

        Set<BoardCell> targets = Set.of(
            roomCell, board.getCell(5, 0), board.getCell(5, 1)
        );
        BoardCell selected = comp1.selectTarget(targets);

        assertEquals(roomCell, selected, "Computer should select the unseen room.");
    }

    @Test
    public void testSelectTargetRoomSeen() {
    	BoardCell roomCell = board.getCell(3, 3);
        
        comp1.addSeenCard(deck.get(0));

        Set<BoardCell> targets = Set.of(
        		roomCell, board.getCell(5, 0), board.getCell(5, 1)
        );
        Set<BoardCell> selectedTargets = new HashSet<>();

        for (int i = 0; i < 10; i++) {
            selectedTargets.add(comp1.selectTarget(targets));
        }

        assertEquals(3, selectedTargets.size());
        assertTrue(selectedTargets.containsAll(targets));
    }
}