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

    }
    
    @Test
    public void testTargetsEnterRoom() {

    }
    
    @Test
    public void testTargetsLeaveRoomNoSecretPassage() {

    }

    @Test
    public void testTargetsLeaveRoomWithSecretPassage() {

    }

    


}