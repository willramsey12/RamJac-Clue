package experiment;

import java.util.HashSet;
import java.util.Set;

public class TestBoard {
    private TestBoardCell[][] grid = new TestBoardCell[4][4];
    private Set<TestBoardCell> targets = new HashSet<>();

    public TestBoard() {
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                grid[row][col] = new TestBoardCell(row, col);
            }
        }
        // Initialize adjacency lists for each cell (4x4 grid)
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                TestBoardCell cell = grid[row][col];
                if (row > 0) cell.addAdjacency(grid[row - 1][col]); // Add top cell
                if (row < 3) cell.addAdjacency(grid[row + 1][col]); // Add bottom cell
                if (col > 0) cell.addAdjacency(grid[row][col - 1]); // Add left cell
                if (col < 3) cell.addAdjacency(grid[row][col + 1]); // Add right cell
            }
        }
    }

    public TestBoardCell getCell(int row, int col) {
        return grid[row][col];
    }

    public void calcTargets(TestBoardCell startCell, int pathLength) {
        targets.clear(); // Clear previous targets
        Set<TestBoardCell> visited = new HashSet<>(); // Track visited cells
        visited.add(startCell); // Add the starting cell to visited
        findTargets(startCell, pathLength, visited);
    }

    // helper method for calcTargets
    private void findTargets(TestBoardCell cell, int stepsLeft, Set<TestBoardCell> visited) {
        // If we have no more steps left, add the cell to targets and return
        if (stepsLeft == 0 || cell.isRoom()) {
            targets.add(cell);
            return;
        }

        // Explore each adjacent cell
        for (TestBoardCell adjCell : cell.getAdjList()) {
            // Skip if the cell is occupied or already visited
            if (adjCell.getOccupied() || visited.contains(adjCell)) {
                continue;
            }

            // Mark this cell as visited and explore further
            visited.add(adjCell);
            findTargets(adjCell, stepsLeft - 1, visited);
            visited.remove(adjCell); // Unmark the cell for other paths
        }
    }

    public Set<TestBoardCell> getTargets() {
        return targets;
    }
}
