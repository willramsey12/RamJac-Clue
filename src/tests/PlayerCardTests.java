package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import clueGame.BadConfigFormatException;
import clueGame.Board;
import clueGame.Card;
import clueGame.CardType;
import clueGame.Player;
import clueGame.Solution;

public class PlayerCardTests {
	private static Board board;
	
	
	@BeforeAll
	public static void setUp() throws BadConfigFormatException {
        board = Board.getInstance();
        board.setConfigFiles("ClueBoardLayout.csv", "ClueSetup.txt");
        board.initialize();
        board.setupGame();
	}
	
	
	@Test
	public void testPlayers() {
		// test total number of players
		ArrayList<Player> players = board.getPlayers();
		assertEquals(6, players.size());
		
		// test that only 1 player is a human and 5 players are bots
		int humanCounter = 0;
		int computerCounter = 0;
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i).isHuman()) {
				humanCounter++;
			}
			else {
				computerCounter++;
			}
		}
		assertEquals(1, humanCounter);
		assertEquals(5, computerCounter);
	}
	
	@Test
	public void testCards() {
		
		ArrayList<Card> deck = board.getDeck();
		// test that there are a total of 21 cards
		assertEquals(21, deck.size());
		
		int roomCounter = 0;
		int weaponCounter = 0;
		int personCounter = 0;
		for (int i = 0; i < deck.size(); i++) {
			if (deck.get(i).getCardType() == CardType.PERSON) {
				personCounter++;
			}
			else if (deck.get(i).getCardType() == CardType.WEAPON) {
				weaponCounter++;
			}
			else if (deck.get(i).getCardType() == CardType.ROOM) {
				roomCounter++;
			}
		}
		// test that there are 6 person cards
		assertEquals(6, personCounter);
		// test that there are 6 weapon cards
		assertEquals(6, weaponCounter);
		// test that there are 9 room cards
		assertEquals(9, roomCounter);
	}
	
	@Test
	public void testSolution() {
		Solution solution = board.getSolution(); 
		assertEquals(3, solution.getSolutionDeck().size());
		int roomCounter = 0;
		int weaponCounter = 0;
		int personCounter = 0;
		for (int i = 0; i < solution.getSolutionDeck().size(); i++) {
			if (solution.getSolutionDeck().get(i).getCardType() == CardType.PERSON) {
				personCounter++;
			}
			if (solution.getSolutionDeck().get(i).getCardType() == CardType.WEAPON) {
				weaponCounter++;
			}
			if (solution.getSolutionDeck().get(i).getCardType() == CardType.ROOM) {
				roomCounter++;
			}
		}
		
		// test that there are 1 card of each in the solution
		assertEquals(1, personCounter);
		assertEquals(1, weaponCounter);
		assertEquals(1, roomCounter);
		
	}
	
	@Test
	public void testCardDealing() {
        ArrayList<Player> players = board.getPlayers();
        ArrayList<Card> originalDeck = board.getDeck();

        // Check that the correct number of cards were dealt (deck size minus 3 solution cards)
        int totalDealtCards = players.stream().mapToInt(player -> player.getHand().size()).sum();
        assertEquals(originalDeck.size() - 3, totalDealtCards, "Incorrect number of cards dealt.");

        // Check for duplicates by adding each card to a set
        Set<Card> dealtCards = new HashSet<>();
        for (Player player : players) {
            dealtCards.addAll(player.getHand());
        }
        assertEquals(totalDealtCards, dealtCards.size(), "Duplicate cards found among players' hands.");
    }
	
	

}
