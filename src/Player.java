import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Random;

public class Player extends Thread {

// ATTRIBUTES
  private final int PLAYER_NUMBER;
  private Card[] hand = new Card[4];
  private Deck previousDeck;
  private Deck nextDeck;

// GETTERS AND SETTERS
  public int getPlayerNumber() {
    return PLAYER_NUMBER;
  }

// GENERAL METHODS
  public void assignCard(Card card, int pos) {
    this.hand[pos] = card;
  }

  public String getDenominations() {
    String denominations = "";
    for (Card card : hand) {
      denominations = denominations.concat(card.getDenomination() + " ");
    }
    denominations = denominations.substring(0, denominations.length()-1); // removes whitespace at end of string
    return denominations;
  }

  public String getOutputFilename() {
    // filename for player
    return "player" + this.getPlayerNumber() + "_output.txt";
  }

  public Boolean checkMyWin() {
    // checks if all cards in hand have matching denominations
    return hand[0].getDenomination() == hand[1].getDenomination() && hand[1].getDenomination() == hand[2].getDenomination() && hand[2].getDenomination() == hand[3].getDenomination();
  }

// FILE MANIPULATION
  public void createFile() {
    try {
      File playerFile = new File(getOutputFilename());
      playerFile.createNewFile();
    } catch (IOException e) {}
  }

  public synchronized void writeFileInitial() {
    try {
      FileWriter playerWriter = new FileWriter(getOutputFilename()); // get file to write to
      // FileWriter will overwrite contents of file
      playerWriter.write("player " + getPlayerNumber() + " initial hand " + getDenominations() + "\n");
      playerWriter.write("\n"); // formatting choice, makes file easier to read
      playerWriter.close();
    } catch (IOException e) {}
  }

  public synchronized void writeFilePlay(int drawn, int drawnDeck, int discarded, int discardedDeck) {
    try {
      Path path = Paths.get(getOutputFilename()); // get file to write to
      String drawnText = "player " + getPlayerNumber() + " draws a " + drawn + " from deck " + drawnDeck + "\n";
      String discardedText = "player " + getPlayerNumber() + " discards a " + discarded + " to deck " + discardedDeck + "\n";
      String currentHandText = "player " + getPlayerNumber() + " current hand is " + getDenominations() + "\n" + "\n";
      Files.write(path, drawnText.getBytes(), StandardOpenOption.APPEND); // appending to file instead of overwriting
      Files.write(path, discardedText.getBytes(), StandardOpenOption.APPEND);
      Files.write(path, currentHandText.getBytes(), StandardOpenOption.APPEND);
    } catch (IOException e) {}
  }

  public synchronized void writeFileEnd() {
    try {
      Path path = Paths.get(getOutputFilename()); // get file to write to
      String winnerText;
      String exitText = "player " + getPlayerNumber() + " exits\n";
      String finalHandText;
      if (GameWinner.getPlayerNumber() == getPlayerNumber()) { // different outputs in file depending on who one
        winnerText = "player " + getPlayerNumber() + " wins\n";
        finalHandText = "player " + getPlayerNumber() + " final hand: " + getDenominations() + "\n";
      } else {
        winnerText = "player " + GameWinner.getPlayerNumber() + " has informed player " + getPlayerNumber() + " that player " + GameWinner.getPlayerNumber() + " has won\n";
        finalHandText = "player " + getPlayerNumber() + "  hand: " + getDenominations() + "\n";
      }
      Files.write(path, winnerText.getBytes(), StandardOpenOption.APPEND); // appending to file instead of overwriting
      Files.write(path, exitText.getBytes(), StandardOpenOption.APPEND);
      Files.write(path, finalHandText.getBytes(), StandardOpenOption.APPEND);
    } catch (IOException e) {}
  }


  public synchronized int[] drawAndDiscard() {
    // synchronized to ensure atomicity
    Card pushedCard;
    do {
      int rnd = new Random().nextInt(hand.length); // chooses random card to discard
      pushedCard = hand[rnd];
    } while (pushedCard.getDenomination() == this.PLAYER_NUMBER); // retries if card selected matches player number
    nextDeck.pushBottomCard(pushedCard); // discards card to bottom of next deck
    Card pulledCard = null;
    for (int i = 0; i < hand.length; i++) {
      if (hand[i] == pushedCard) { // overwrite discarded card in hand with drawn card
        pulledCard = previousDeck.giveTopCard(); // draws card from previous deck
        hand[i] = pulledCard; // overwrites
      }
    }
    int [] denominations = {pushedCard.getDenomination(), pulledCard.getDenomination()};
    return denominations; // returns played cards in array with format [discarded denomination, drawn denomination]
  }


// RUN METHOD (Thread)
  public void run() {
    while (!GameWinner.getPlayerWon()) { // Checks if anyone has won
      if (checkMyWin()) { // Checks if this player has won
        GameWinner.setPlayerVictory(true, this.PLAYER_NUMBER); // Broadcasts win to rest of players
      } else {
        if (previousDeck.getDeckSize() > 3 && nextDeck.getDeckSize() < 5) {
          // Player will only play if previous deck isn't starved and next deck isn't overpopulated
          int[] playedCards = drawAndDiscard(); // plays, returns [pushed, pulled] cards
          writeFilePlay(playedCards[1], previousDeck.getDeckNumber(), playedCards[0], nextDeck.getDeckNumber()); // write to file
        }
      }
    }
    writeFileEnd(); // writes the final result to file
    nextDeck.writeFile(); // tells next deck to write to file
  }

// CONSTRUCTOR
  public Player(int playerNumber, Deck previousDeck, Deck nextDeck) {
    this.PLAYER_NUMBER = playerNumber;
    this.previousDeck = previousDeck;
    this.nextDeck = nextDeck;
  }
}