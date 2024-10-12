package tests;

import experiment.TestBoard;
import experiment.TestBoardCell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class BoardTestsExp {
    private TestBoard board;

    @BeforeEach
    public void setUp() {
        board = new TestBoard();
    }

    @Test
    public void testAdjacencyTopLeftCorner() {
        TestBoardCell cell = board.getCell(0, 0);
        Set<TestBoardCell> adjList = cell.getAdjList();
        assertEquals(2, adjList.size());
        // should be (0,1) and (1,0)
        assertTrue(adjList.contains(board.getCell(0, 1))); // Right
        assertTrue(adjList.contains(board.getCell(1, 0))); // Bottom
    }

    @Test
    public void testAdjacencyBottomRightCorner() {
        TestBoardCell cell = board.getCell(3, 3);
        Set<TestBoardCell> adjList = cell.getAdjList();
        assertEquals(2, adjList.size());
        // should be 2,3 and 3,2
        assertTrue(adjList.contains(board.getCell(2, 3))); // Top
        assertTrue(adjList.contains(board.getCell(3, 2))); // Left
    }

    @Test
    public void testTargetsOnEmptyBoard() {
        TestBoardCell cell = board.getCell(0, 0);
        board.calcTargets(cell, 3); // Roll of 3
        Set<TestBoardCell> targets = board.getTargets();
        assertEquals(6, targets.size());
        assertTrue(targets.contains(board.getCell(3, 0)));
        assertTrue(targets.contains(board.getCell(2, 1)));
        assertTrue(targets.contains(board.getCell(1, 2)));
    }

    @Test
    public void testTargetsWithOccupiedAndRooms() {
        board.getCell(0, 2).setOccupied(true);
        board.getCell(1, 2).setRoom(true);
        TestBoardCell cell = board.getCell(0, 3);
        board.calcTargets(cell, 3);
        Set<TestBoardCell> targets = board.getTargets();
        assertEquals(3, targets.size());
        assertTrue(targets.contains(board.getCell(1, 2))); // Room
        assertTrue(targets.contains(board.getCell(2, 2))); // Not occupied
    }

    @Test
    public void testMaxDieRoll() {
        TestBoardCell cell = board.getCell(2, 2);
        board.calcTargets(cell, 6); // Maximum roll
        Set<TestBoardCell> targets = board.getTargets();
        assertTrue(targets.size() > 0); // Ensure targets are generated
    }
}
