package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import clueGame.BadConfigFormatException;
import clueGame.Board;
import clueGame.BoardCell;

public class BoardAdjTargetTest {

    private static Board board;

    @BeforeAll
    public static void setUp() throws BadConfigFormatException {
        board = Board.getInstance();
        board.setConfigFiles("ClueBoardLayout.csv", "ClueSetup.txt");
        board.initialize();
    }

    // Test walkways with only other walkways adjacent
    @Test
    public void testAdjacencyWalkways() {
    	// Test walkway with all sides walkway adjacencies
		Set<BoardCell> testList = board.getAdjList(10, 6);
		assertEquals(4, testList.size());
		assertTrue(testList.contains(board.getCell(9, 6)));
		assertTrue(testList.contains(board.getCell(11, 6)));
		assertTrue(testList.contains(board.getCell(10, 5)));
		assertTrue(testList.contains(board.getCell(10, 7)));
		
		// Test by a room but not a door
		testList = board.getAdjList(18, 1);
		assertEquals(2, testList.size());
		assertTrue(testList.contains(board.getCell(18, 0)));
		assertTrue(testList.contains(board.getCell(17, 1)));
		
		//Test by a room and edge of board
		testList = board.getAdjList(24, 19);
		assertEquals(1, testList.size());
		assertTrue(testList.contains(board.getCell(24, 18)));
    }

    // Test a doorway cell
    @Test
    public void testAdjacencyDoorway() {
    	// Test door with 4 adjacencies (3 Walk 1 Center)
    	Set<BoardCell> testList = board.getAdjList(12, 4);
		assertEquals(4, testList.size());
		assertTrue(testList.contains(board.getCell(12, 5)));
		assertTrue(testList.contains(board.getCell(11, 4)));
		assertTrue(testList.contains(board.getCell(13, 4)));
		assertTrue(testList.contains(board.getCell(11, 1)));
    	
		// Test door between 2 different rooms
		testList = board.getAdjList(17, 3);
		assertEquals(3, testList.size());
		assertTrue(testList.contains(board.getCell(17, 2)));
		assertTrue(testList.contains(board.getCell(17, 4)));
		assertTrue(testList.contains(board.getCell(11, 1)));
		
		// Test door on the corner of a room
		testList = board.getAdjList(18, 5);
		assertEquals(4, testList.size());
		assertTrue(testList.contains(board.getCell(17, 5)));
		assertTrue(testList.contains(board.getCell(19, 5)));
		assertTrue(testList.contains(board.getCell(18, 6)));
		assertTrue(testList.contains(board.getCell(21, 2)));
    }
    
    // Test center room cells
    @Test
    public void testAdjacencyCenter() {
    	// Test center with a secret passage
		Set<BoardCell> testList = board.getAdjList(3, 11);
		assertEquals(3, testList.size());
		assertTrue(testList.contains(board.getCell(6, 9)));
		assertTrue(testList.contains(board.getCell(3, 15)));
		assertTrue(testList.contains(board.getCell(20, 11)));
    	
    	
    	// Test center without a secret passage
		testList = board.getAdjList(21, 2);
		assertEquals(1, testList.size());
		assertTrue(testList.contains(board.getCell(18, 5)));
    	
    	
    	// Test center with mulktiple doorways into it
		testList = board.getAdjList(20,11);
		assertEquals(4, testList.size());
		assertTrue(testList.contains(board.getCell(18, 7)));
		assertTrue(testList.contains(board.getCell(18, 15)));
		assertTrue(testList.contains(board.getCell(15, 11)));
		assertTrue(testList.contains(board.getCell(3, 11)));

    }



    // Test target calculations for walkways
    @Test
    public void testTargetsAlongWalkways() {  	   	
    	
    	// only going to other walkways
    	// roll of 1
    	board.calcTargets(board.getCell(5, 0), 1);
		Set<BoardCell> targets= board.getTargets();
		assertEquals(1, targets.size());
		assertTrue(targets.contains(board.getCell(5, 1)));
		// roll of 2
		board.calcTargets(board.getCell(5, 0), 2);
		targets = board.getTargets();
		assertEquals(3, targets.size());
		assertTrue(targets.contains(board.getCell(4, 1)));
		assertTrue(targets.contains(board.getCell(6, 1)));
		assertTrue(targets.contains(board.getCell(5, 2)));
		assertFalse(targets.contains(board.getCell(5, 1)));
		// roll of 3
		// going from a walkway to a doorway
		board.calcTargets(board.getCell(5, 0), 3);
		targets = board.getTargets();
		assertEquals(3, targets.size());
		assertTrue(targets.contains(board.getCell(4, 2)));
		assertTrue(targets.contains(board.getCell(6, 2)));
		assertTrue(targets.contains(board.getCell(5, 3)));
		assertFalse(targets.contains(board.getCell(5, 2)));
    	
    	
    	// going to other walkways with occupied spaces
		board.getCell(5, 1).setOccupied(true);
		board.calcTargets(board.getCell(5, 0), 3);
		board.getCell(5, 1).setOccupied(false);
		targets = board.getTargets();
		assertEquals(0, targets.size());
		assertFalse(targets.contains(board.getCell(5, 3)));
    }
    
    @Test
    public void testTargetsEnterRoom() {
    	
    	// entering horizon suite
    	// from 2,16 with a roll of 2
    	board.calcTargets(board.getCell(2, 16), 2);
		Set<BoardCell> targets= board.getTargets();
		assertEquals(6, targets.size());
    	assertTrue(targets.contains(board.getCell(2, 20)));
    	assertTrue(targets.contains(board.getCell(1, 15)));
    	assertTrue(targets.contains(board.getCell(3, 15)));
    	assertTrue(targets.contains(board.getCell(1, 17)));
    	assertTrue(targets.contains(board.getCell(3, 17)));
    	assertTrue(targets.contains(board.getCell(4, 16)));
    	// entering hollowed reliquary
    	// from 4,2 with a roll of 1
    	board.calcTargets(board.getCell(4, 2), 1);
		targets= board.getTargets();
		assertEquals(4, targets.size());
    	assertTrue(targets.contains(board.getCell(2, 3)));
    	assertTrue(targets.contains(board.getCell(4, 1)));
    	assertTrue(targets.contains(board.getCell(4, 3)));
    	assertTrue(targets.contains(board.getCell(5, 2)));
    	// entering hollowed reliquary or solitude chamber
    	// from (5,2) with a roll of 2
    	board.calcTargets(board.getCell(5, 2), 2);
		targets= board.getTargets();
		assertEquals(8, targets.size());
    	assertTrue(targets.contains(board.getCell(5, 0)));
    	assertTrue(targets.contains(board.getCell(4, 1)));
    	assertTrue(targets.contains(board.getCell(6, 1)));
    	assertTrue(targets.contains(board.getCell(4, 3)));
    	assertTrue(targets.contains(board.getCell(2, 3)));
    	assertTrue(targets.contains(board.getCell(6, 3)));
    	assertTrue(targets.contains(board.getCell(11, 1)));
    	assertTrue(targets.contains(board.getCell(5, 4)));
    	// entering hollowed reliquary when someone is already in there
    	board.getCell(2, 3).setOccupied(true);
    	board.calcTargets(board.getCell(4, 2), 1);
    	board.getCell(2, 3).setOccupied(false);
		targets= board.getTargets();
		assertEquals(4, targets.size());
    	assertTrue(targets.contains(board.getCell(2, 3)));
    	assertTrue(targets.contains(board.getCell(4, 1)));
    	assertTrue(targets.contains(board.getCell(4, 3)));
    	assertTrue(targets.contains(board.getCell(5, 2)));
    	// entering a hollowed reliquary or solitude chamber that has a doorway blocked in front of hollowed reliquary
    	// from (5,2) with a roll of 2
    	board.getCell(4, 2).setOccupied(true);
    	board.calcTargets(board.getCell(5, 2), 2);
    	board.getCell(4, 2).setOccupied(false);
		targets= board.getTargets();
		assertEquals(7, targets.size());
    	assertTrue(targets.contains(board.getCell(5, 0)));
    	assertTrue(targets.contains(board.getCell(4, 1)));
    	assertTrue(targets.contains(board.getCell(6, 1)));
    	assertTrue(targets.contains(board.getCell(4, 3)));
    	assertFalse(targets.contains(board.getCell(2, 3)));
    	assertTrue(targets.contains(board.getCell(6, 3)));
    	assertTrue(targets.contains(board.getCell(11, 1)));
    	assertTrue(targets.contains(board.getCell(5, 4)));

    }
    
    @Test
    public void testTargetsLeaveRoomNoSecretPassage() {
    	
    	// leaving a room that has 0/2 doorways blocked
    	// from Gravewatch Bastion with a roll of 2
    	board.calcTargets(board.getCell(16, 21), 2);
		Set<BoardCell> targets= board.getTargets();
		assertEquals(6, targets.size());
    	assertTrue(targets.contains(board.getCell(15, 18)));
    	assertTrue(targets.contains(board.getCell(17, 18)));
    	assertTrue(targets.contains(board.getCell(16, 17)));
    	assertTrue(targets.contains(board.getCell(15, 17)));
    	assertTrue(targets.contains(board.getCell(14, 18)));
    	assertTrue(targets.contains(board.getCell(16, 18)));
    	
    	// leaving a room that has 1/2 doorways blocked
    	board.getCell(16, 18).setOccupied(true);
    	board.calcTargets(board.getCell(16, 21), 2);
    	board.getCell(16, 18).setOccupied(false);
		targets= board.getTargets();
		assertEquals(2, targets.size());
    	assertFalse(targets.contains(board.getCell(15, 18)));
    	assertFalse(targets.contains(board.getCell(17, 18)));
    	assertFalse(targets.contains(board.getCell(16, 17)));
    	assertTrue(targets.contains(board.getCell(15, 17)));
    	assertTrue(targets.contains(board.getCell(14, 18)));
    	assertFalse(targets.contains(board.getCell(16, 18)));
    	
    	// leaving a room that has 2/2 doorways blocked
    	board.getCell(16, 18).setOccupied(true);
    	board.getCell(15, 18).setOccupied(true);
    	board.calcTargets(board.getCell(16, 21), 2);
    	board.getCell(16, 18).setOccupied(false);
    	board.getCell(15, 18).setOccupied(false);
		targets= board.getTargets();
		assertEquals(0, targets.size());
    	assertFalse(targets.contains(board.getCell(15, 18)));
    	assertFalse(targets.contains(board.getCell(17, 18)));
    	assertFalse(targets.contains(board.getCell(16, 17)));
    	assertFalse(targets.contains(board.getCell(15, 17)));
    	assertFalse(targets.contains(board.getCell(14, 18)));
    	assertFalse(targets.contains(board.getCell(16, 18)));
    }

    @Test
    public void testTargetsLeaveRoomWithSecretPassage() {
    	
    	// leaving Blighted Sepulcher which has a secret passage
    	// roll of 1 since we already checked doorway and walkway functionality
    	board.calcTargets(board.getCell(20, 11), 1);
		Set<BoardCell> targets= board.getTargets();
		assertEquals(4, targets.size());
    	assertTrue(targets.contains(board.getCell(3, 11)));
    	assertTrue(targets.contains(board.getCell(18, 7)));
    	assertTrue(targets.contains(board.getCell(18, 15)));
    	assertTrue(targets.contains(board.getCell(15, 11)));
    	// leaving the room but doors are blocked
    	board.getCell(18, 7).setOccupied(true);
    	board.getCell(18, 15).setOccupied(true);
    	board.getCell(15, 11).setOccupied(true);
    	board.calcTargets(board.getCell(20, 11), 1);
    	board.getCell(18, 7).setOccupied(false);
    	board.getCell(18, 15).setOccupied(false);
    	board.getCell(15, 11).setOccupied(false);
        targets= board.getTargets();
		assertEquals(1, targets.size());
    	assertTrue(targets.contains(board.getCell(3, 11)));
    	assertFalse(targets.contains(board.getCell(18, 7)));
    	assertFalse(targets.contains(board.getCell(18, 15)));
    	assertFalse(targets.contains(board.getCell(15, 11)));

    }

    


}