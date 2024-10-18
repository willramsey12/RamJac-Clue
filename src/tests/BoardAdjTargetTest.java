package tests;

import static org.junit.Assert.*;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import clueGame.Board;
import clueGame.BoardCell;

public class BoardAdjTargetTest {

    private static Board board;

    @BeforeAll
    public static void setUp() {
        board = Board.getInstance();
        board.setConfigFiles("ClueLayout.csv", "ClueSetup.txt"); // Update with your actual files
        board.initialize();
    }

    // Test walkways with only other walkways adjacent
    @Test
    public void testAdjacencyWalkways() {
    	// Test walkway at (8,13) - should have adjacencies to all 4 directions (walkways only)
		Set<BoardCell> testList = board.getAdjList(8, 13);
		assertEquals(4, testList.size());
		assertTrue(testList.contains(board.getCell(9, 13)));
		assertTrue(testList.contains(board.getCell(8, 14)));

    }

    // Test a cell beside a room but not a doorway
    @Test
    public void testAdjacencyBesideRoomNotDoorway() {
    	// Test walkway at (8,3) - should have only 3 adjacencies in 3 directions because it is connected to a door
		Set<BoardCell> testList = board.getAdjList(8, 3);
		assertEquals(3, testList.size());
		assertTrue(testList.contains(board.getCell(8, 2)));
		assertTrue(testList.contains(board.getCell(8, 4)));
    }

    // Test a doorway cell
    @Test
    public void testAdjacencyDoorway() {
    	// Test door at (16,13) leading to room
    	Set<BoardCell> testList = board.getAdjList(16, 13);
		assertEquals(4, testList.size());
		assertTrue(testList.contains(board.getCell(17, 13)));
		assertTrue(testList.contains(board.getCell(16, 14)));

    }

    // Test secret passage adjacency
    @Test
    public void testAdjacencySecretPassage() {
        // Room H has a secret passage at (3, 22) that connects to room R
    	Set<BoardCell> testList = board.getAdjList(3, 22);
    	assertEquals(2, testList.size());   	
    }

    // Test target calculations for walkways
    @Test
    public void testTargetsAlongWalkways() {
        // Test starting at (5, 5) and roll of 2
        board.calcTargets(board.getCell(5, 5), 2);
        Set<BoardCell> targets = board.getTargets();
        // Expecting 4 possible targets within 2 moves
        assertTrue(targets.contains(board.getCell(3, 5))); // Up 2
        assertTrue(targets.contains(board.getCell(7, 5))); // Down 2
        assertTrue(targets.contains(board.getCell(5, 3))); // Left 2
        assertTrue(targets.contains(board.getCell(5, 7))); // Right 2
    }
    
    @Test
    public void testTargetsEnterRoom() {
        // Start at walkway (5, 5) and roll of 3, should be able to enter the room through a door
        board.calcTargets(board.getCell(5, 5), 3);
        Set<BoardCell> targets = board.getTargets();
        // Assume there's a doorway at (5, 6) that leads to Room R (room center at 6, 6)
        assertTrue(targets.contains(board.getCell(6, 6))); // Room center, entered via door
    }
    
    @Test
    public void testTargetsLeaveRoomNoSecretPassage() {
        // Start in Room R at (2, 2), room center, and roll of 1, leaving via door
        board.calcTargets(board.getCell(2, 2), 1);
        Set<BoardCell> targets = board.getTargets();
        // Room R has a door at (2, 3) leading to a walkway (2, 4)
        assertTrue(targets.contains(board.getCell(2, 3))); // Door leading to walkway
        assertTrue(targets.contains(board.getCell(2, 4))); // Walkway after door
    }

    @Test
    public void testTargetsLeaveRoomWithSecretPassage() {
        // Start in Room R at (2, 2), which has a secret passage to Room H (center at 20, 19)
        board.calcTargets(board.getCell(2, 2), 1); // Rolling 1 allows secret passage usage
        Set<BoardCell> targets = board.getTargets();
        assertTrue(targets.contains(board.getCell(20, 19))); // Moved to Room H center via secret passage
    }

    


}
