import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class TestDeck {
  Deck testDeck;
  Card testCard1;
  Card testCard2;
  Card testCard3;
  Card testCard4;
  Scanner scanner;

  @Before
  public void setup() {
    // Initialises a deck "deck 1"
    testDeck = new Deck(1);
    // Initialises four cards
    testCard1 = new Card(1);
    testCard2 = new Card(2);
    testCard3 = new Card(3);
    testCard4 = new Card(4);
    // Assigns one card to deck
    testDeck.assignCard(testCard1, 0);
  }

  @Test
  public void testGetDeckNumber() {
    assertEquals(1, testDeck.getDeckNumber());
  }
  @Test
  public void testGetDeckSize() {
    assertEquals(1, testDeck.getDeckSize());
  }
  @Test
  public void testGetOutputFilename() {
    assertEquals("deck" + testDeck.getDeckNumber() + "_output.txt", testDeck.getOutputFilename());
  }
  @Test
  public void testGiveTopCard() {
    // checks that the deck expels the single card and is empty after doing so
    assertEquals(1, testDeck.getDeckSize());
    assertEquals(testCard1, testDeck.giveTopCard());
    assertEquals(0, testDeck.getDeckSize());
  }
  @Test
  public void testPushBottomCard() {
    // checks that the deck properly adds the new card and adds it to the bottom of the deck
    testDeck.pushBottomCard(testCard2);
    assertEquals(2, testDeck.getDeckSize());
    assertEquals(testCard1, testDeck.giveTopCard());
  }
  @Test
  public void testAssignCard() {
    // populates deck with cards and checks size at various stages
    assertEquals(1, testDeck.getDeckSize());
    testDeck.assignCard(testCard1, 0);
    assertEquals(2, testDeck.getDeckSize());
    testDeck.assignCard(testCard2, 1);
    assertEquals(3, testDeck.getDeckSize());
    testDeck.assignCard(testCard3, 2);
    assertEquals(4, testDeck.getDeckSize());
    testDeck.assignCard(testCard4, 3);
    assertEquals(5, testDeck.getDeckSize());
  }
  @Test
  public void testGetDenominations() {
    testDeck.assignCard(testCard2, 1);
    testDeck.assignCard(testCard3, 2);
    testDeck.assignCard(testCard4, 3);
    assertEquals("1 2 3 4", testDeck.getDenominations());
  }
  @Test
  public void testCreateFile() {
    // creates a file, checks if it exists
    testDeck.createFile();
    File file = new File(testDeck.getOutputFilename());
    assert(file.exists() && !file.isDirectory() && file.isFile());
  }

  @Test
  public void testWriteFile() {
    // writes to the file, checks the contents written are correct
    testDeck.writeFile();
    File file = new File(testDeck.getOutputFilename());
    try {
      scanner = new Scanner(file);
      assertEquals("deck" + testDeck.getDeckNumber() + " contents:" + testDeck.getDenominations(), scanner.nextLine());
    } catch (FileNotFoundException e) {
      assert(false);
    }
  }
}
