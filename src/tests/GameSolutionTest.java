package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import clueGame.BadConfigFormatException;
import clueGame.Board;
import clueGame.Card;
import clueGame.CardType;
import clueGame.ComputerPlayer;
import clueGame.HumanPlayer;
import clueGame.Player;
import clueGame.Solution;

public class GameSolutionTest {
    private static Board board;
    private static Card personCard, roomCard, weaponCard;
    private static Solution correctSolution;

    @BeforeAll
    public static void setUp() throws BadConfigFormatException {
        board = Board.getInstance();
        board.setConfigFiles("ClueBoardLayout.csv", "ClueSetup.txt");
        board.initialize();
        
        // Initialize cards for solution
        personCard = new Card("Colonel Mustard", CardType.PERSON);
        roomCard = new Card("Library", CardType.ROOM);
        weaponCard = new Card("Revolver", CardType.WEAPON);
        
        // Set the correct solution in the board
        board.getSolution().setRoom(roomCard);
        board.getSolution().setPerson(personCard);
        board.getSolution().setWeapon(weaponCard);
    }

    // Check an accusation tests
    @Test
    public void testCorrectSolution() {
        assertTrue(board.checkAccusation(roomCard, personCard, weaponCard));
    }

    @Test
    public void testSolutionWithWrong() {
        Card wrongPerson = new Card("Professor Plum", CardType.PERSON);
        Card wrongWeapon = new Card("Knife", CardType.WEAPON);
        Card wrongRoom = new Card("Kitchen", CardType.ROOM);


        assertFalse(board.checkAccusation(roomCard, wrongPerson, weaponCard));
        assertFalse(board.checkAccusation(roomCard, personCard, wrongWeapon));
        assertFalse(board.checkAccusation(wrongRoom, personCard, weaponCard));
    }

    @Test
    public void testDisproveSuggestionOneMatchingCard() {
        Player player = new ComputerPlayer();
        player.updateHand(personCard);
        player.updateHand(new Card("Knife", CardType.WEAPON));
        player.updateHand(new Card("Kitchen", CardType.ROOM));

        Card result = player.disproveSuggestion(personCard, roomCard, weaponCard);
        assertEquals(personCard, result);
    }

    @Test
    public void testDisproveSuggestionMultipleMatchingCards() {
        Player player = new ComputerPlayer();
        player.updateHand(personCard);
        player.updateHand(roomCard);
        Set<Card> results = new HashSet<>();

        for (int i = 0; i < 10; i++) {
            Card result = player.disproveSuggestion(personCard, roomCard, weaponCard);
            results.add(result);
        }

        assertTrue(results.contains(personCard));
        assertTrue(results.contains(roomCard));
    }

    @Test
    public void testDisproveSuggestionNoMatchingCards() {
        Player player = new ComputerPlayer();
        player.updateHand(new Card("Kitchen", CardType.ROOM));
        player.updateHand(new Card("Knife", CardType.WEAPON));

        assertNull(player.disproveSuggestion(personCard, roomCard, weaponCard));
    }

    // Handle a suggestion made tests
    @Test
    public void testSuggestionNoOneCanDisprove() {
        Player suggestingPlayer = new ComputerPlayer();
        Player otherPlayer = new ComputerPlayer();
        otherPlayer.updateHand(new Card("Kitchen", CardType.ROOM));
        otherPlayer.updateHand(new Card("Knife", CardType.WEAPON));

        board.getPlayers().clear();
        board.getPlayers().add(suggestingPlayer);
        board.getPlayers().add(otherPlayer);

        assertNull(board.handleSuggestion(suggestingPlayer, personCard, roomCard, weaponCard));
    }

    @Test
    public void testSuggestionOnlySuggestingPlayerCanDisprove() {
        Player suggestingPlayer = new ComputerPlayer();
        suggestingPlayer.updateHand(roomCard);

        board.getPlayers().clear();
        board.getPlayers().add(suggestingPlayer);

        assertNull(board.handleSuggestion(suggestingPlayer, personCard, roomCard, weaponCard));
    }

    @Test
    public void testSuggestionOnlyHumanCanDisprove() {
        Player suggestingPlayer = new ComputerPlayer();
        Player humanPlayer = new HumanPlayer();
        humanPlayer.updateHand(roomCard);

        board.getPlayers().clear();
        board.getPlayers().add(suggestingPlayer);
        board.getPlayers().add(humanPlayer);

        assertEquals(roomCard, board.handleSuggestion(suggestingPlayer, personCard, roomCard, weaponCard));
    }

    @Test
    public void testSuggestionTwoPlayersCanDisprove() {
        Player suggestingPlayer = new ComputerPlayer();
        Player player1 = new ComputerPlayer();
        Player player2 = new ComputerPlayer();

        player1.updateHand(roomCard);
        player2.updateHand(personCard);

        board.getPlayers().clear();
        board.getPlayers().add(suggestingPlayer);
        board.getPlayers().add(player1);
        board.getPlayers().add(player2);

        assertEquals(roomCard, board.handleSuggestion(suggestingPlayer, personCard, roomCard, weaponCard));
    }
}
