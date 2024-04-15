import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class TestPlayer {

  Player testPlayer;
  Deck previousDeck;
  Deck nextDeck;
  Scanner scanner;

  @Before
  public void setup() {
    // Initialises a player with two decks adjacent
    previousDeck = new Deck(1);
    nextDeck = new Deck(2);
    testPlayer = new Player(1, previousDeck, nextDeck);
  }
  @Test
  public void testGetPlayerNumber() {
    assertEquals(testPlayer.getPlayerNumber(), 1);
  }
  @Test
  public void testAssignCard() {
    // assigns four of the same card to hand and checks they all match denominations
    Card myCard = new Card(1);
    testPlayer.assignCard(myCard, 0);
    testPlayer.assignCard(myCard, 1);
    testPlayer.assignCard(myCard, 2);
    testPlayer.assignCard(myCard, 3);
    assertEquals("1 1 1 1", testPlayer.getDenominations() );
  }
  @Test
  public void testGetDenominations() {
    // assigns four unique cards to hand and checks their denominations
    testPlayer.assignCard(new Card(1), 0);
    testPlayer.assignCard(new Card(3), 1);
    testPlayer.assignCard(new Card(5), 2);
    testPlayer.assignCard(new Card(7), 3);
    assertEquals("1 3 5 7", testPlayer.getDenominations());
  }
  @Test
  public void testGetOutputFilename() {
    assertEquals("player" + testPlayer.getPlayerNumber() + "_output.txt", testPlayer.getOutputFilename());
  }
  @Test
  public void testCheckMyWin() {
    // assigns four cards to hand, with 1 unique
    testPlayer.assignCard(new Card(1), 0);
    testPlayer.assignCard(new Card(1), 1);
    testPlayer.assignCard(new Card(1), 2);
    testPlayer.assignCard(new Card(2), 3);
    // checks if the player has a winning hand (should be false)
    assertEquals(false, testPlayer.checkMyWin());
    // replaces card in hand so all denominations match
    testPlayer.assignCard(new Card(1), 3);
    // checks if the player has a winning hand (should now be true)
    assertEquals(true, testPlayer.checkMyWin());
  }
  @Test
  public void testCreateFile() {
    // creates a file, checks if it exists
    testPlayer.createFile();
    File file = new File(testPlayer.getOutputFilename());
    assert(file.exists() && !file.isDirectory() && file.isFile());
  }
  @Test
  public void testWriteFileInitial() {
    // writes initial string to file, checks if string matches expected
    testPlayer.assignCard(new Card(2), 0);
    testPlayer.assignCard(new Card(4), 1);
    testPlayer.assignCard(new Card(6), 2);
    testPlayer.assignCard(new Card(8), 3);
    testPlayer.writeFileInitial();
    File file = new File(testPlayer.getOutputFilename());
    try {
      scanner = new Scanner(file);
      assertEquals("player " + testPlayer.getPlayerNumber() + " initial hand " + testPlayer.getDenominations(), scanner.nextLine());
      scanner.close();
    } catch (FileNotFoundException e) {
      assert(false);
    }
  }
  @Test
  public void testWriteFilePlay() {
    // populates previous deck, next deck and player hand with cards
    previousDeck.assignCard(new Card(1), 0);
    previousDeck.assignCard(new Card(2), 1);
    previousDeck.assignCard(new Card(3), 2);
    previousDeck.assignCard(new Card(4), 3);
    nextDeck.assignCard(new Card(5), 0);
    nextDeck.assignCard(new Card(6), 1);
    nextDeck.assignCard(new Card(7), 2);
    testPlayer.assignCard(new Card(1), 0);
    testPlayer.assignCard(new Card(1), 1);
    testPlayer.assignCard(new Card(8), 2);
    testPlayer.assignCard(new Card(1), 3);
    // writes initial string
    testPlayer.writeFileInitial();
    // plays a move, writes to file
    int[] playedCards = testPlayer.drawAndDiscard();
    testPlayer.writeFilePlay(playedCards[1], previousDeck.getDeckNumber(), playedCards[0], nextDeck.getDeckNumber());
    File file = new File(testPlayer.getOutputFilename());
    try {
      scanner = new Scanner(file);
      scanner.nextLine(); // skips a line
      scanner.nextLine(); // skips a line
      // checks that written lines are formatted correctly
      assertEquals("player " + testPlayer.getPlayerNumber() + " draws a " + playedCards[1] + " from deck " + previousDeck.getDeckNumber(), scanner.nextLine());
      assertEquals("player " + testPlayer.getPlayerNumber() + " discards a " + playedCards[0] + " to deck " + nextDeck.getDeckNumber(), scanner.nextLine());
      assertEquals("player " + testPlayer.getPlayerNumber() + " current hand is " + testPlayer.getDenominations(), scanner.nextLine());
      scanner.close();
    } catch (FileNotFoundException e) {
      assert(false);
    }

  }
  @Test
  public void testWriteFileEnd() {
    // assigns a victory hand to player and assigns flag
    testPlayer.assignCard(new Card(1), 0);
    testPlayer.assignCard(new Card(1), 1);
    testPlayer.assignCard(new Card(1), 2);
    testPlayer.assignCard(new Card(1), 3);
    GameWinner.setPlayerVictory(true, 1);
    // writes initial string and final string to file
    testPlayer.writeFileInitial();
    testPlayer.writeFileEnd();
    File file = new File(testPlayer.getOutputFilename());
    try {
      scanner = new Scanner(file);
      scanner.nextLine(); // skips line
      scanner.nextLine(); // skips line
      // checks that written lines are formatted correctly, in the instance that this player wins
      assertEquals("player " + testPlayer.getPlayerNumber() + " wins", scanner.nextLine());
      assertEquals("player " + testPlayer.getPlayerNumber() + " exits", scanner.nextLine());
      assertEquals("player " + testPlayer.getPlayerNumber() + " final hand: " + testPlayer.getDenominations(), scanner.nextLine());
      scanner.close();
    } catch (FileNotFoundException e) {
      assert(false);
    }
    // redoes the same test, this time when another player has won, verifying that the string is different in this instance
    GameWinner.setPlayerVictory(true, 4);
    testPlayer.writeFileInitial();
    testPlayer.writeFileEnd();
    try {
      scanner = new Scanner(file); // opens new Scanner to read file from top
      scanner.nextLine(); // skips line
      scanner.nextLine(); // skips line
      // checks the strings are different to the instance where player has won
      assertEquals("player " + GameWinner.getPlayerNumber() + " has informed player " + testPlayer.getPlayerNumber() + " that player " + GameWinner.getPlayerNumber() + " has won", scanner.nextLine());
      assertEquals("player " + testPlayer.getPlayerNumber() + " exits", scanner.nextLine());
      assertEquals("player " + testPlayer.getPlayerNumber() + "  hand: " + testPlayer.getDenominations(), scanner.nextLine());
    } catch (FileNotFoundException e) {
      assert(false);
    }
  }
  @Test
  public void testDrawAndDiscard() {
    // populates previous deck, next deck and player hand with cards
    previousDeck.assignCard(new Card(1), 0);
    previousDeck.assignCard(new Card(1), 1);
    previousDeck.assignCard(new Card(1), 2);
    previousDeck.assignCard(new Card(1), 3);
    nextDeck.assignCard(new Card(2), 0);
    nextDeck.assignCard(new Card(2), 1);
    nextDeck.assignCard(new Card(2), 2);
    testPlayer.assignCard(new Card(1), 0);
    testPlayer.assignCard(new Card(1), 1);
    testPlayer.assignCard(new Card(2), 2);
    testPlayer.assignCard(new Card(1), 3);
    // checks that the cards have assigned correctly to the decks and player
    assertEquals("1 1 1 1", previousDeck.getDenominations());
    assertEquals("2 2 2", nextDeck.getDenominations());
    assertEquals("1 1 2 1", testPlayer.getDenominations());
    // ensures that the player does NOT currently have a winning hand
    assertEquals(false, testPlayer.checkMyWin());
    // player makes single draw-discard move, now giving them a winning hand
    int[] playedCards = testPlayer.drawAndDiscard();
    // checks that the state of the decks have changed, and that the player's hand is now a winning hand
    assertEquals("1 1 1", previousDeck.getDenominations());
    assertEquals("2 2 2 2", nextDeck.getDenominations());
    assertEquals("1 1 1 1", testPlayer.getDenominations());
    // check the correct cards were drawn/discarded
    assertEquals(2, playedCards[0]);
    assertEquals(1, playedCards[1]);
    // checks that the player is now a winner
    assertEquals(true, testPlayer.checkMyWin());
  }
}
