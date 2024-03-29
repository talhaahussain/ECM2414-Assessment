import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Deck {

  // ATTRIBUTES
  private final int DECK_NUMBER;
  private ArrayList<Card> deckCards = new ArrayList<>(); // Uses dynamic ArrayList instead of array

  // GETTERS AND SETTERS
  public int getDeckNumber() {
    return DECK_NUMBER;
  }

  public synchronized int getDeckSize() {
    return deckCards.size();
  }

  public String getOutputFilename() {
    return "deck" + this.getDeckNumber() + "_output.txt";
  }

  public synchronized Card giveTopCard() {
    // gives top card to player and removes it from deck
    Card card = deckCards.get(0);
    deckCards.remove(0);
    return card;
  }

  public synchronized void pushBottomCard(Card card) {
    // adds a card to bottom of deck
    deckCards.add(card);
  }

// GENERAL METHODS
  public void assignCard(Card card, int pos) {
    this.deckCards.add(pos, card);
  }

  public synchronized String getDenominations() {
    String denominations = "";
    for (Card card : deckCards) {
      denominations = denominations.concat(card.getDenomination() + " ");
    }
    denominations = denominations.substring(0, denominations.length()-1); // removes whitespace at end of string
    return denominations;
  }

// FILE MANIPULATION
  public void createFile() {
    try {
      File deckFile = new File(getOutputFilename());
      deckFile.createNewFile();
    } catch (IOException e) {}
  }

  public void writeFile() {
    try {
      FileWriter deckWriter = new FileWriter(getOutputFilename()); // FileWriter will automatically truncate file
      deckWriter.write("deck" + getDeckNumber() + " contents:" + getDenominations());
      deckWriter.close();
    } catch (IOException e) {}
  }

// CONSTRUCTOR
  public Deck(int deckNumber) {
    this.DECK_NUMBER = deckNumber;
  }
}