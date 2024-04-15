public class Card {
// ATTRIBUTES
  private final int DENOMINATION;

// GETTERS AND SETTERS
  public synchronized int getDenomination() {
    return DENOMINATION;
  }

// CONSTRUCTOR
  public Card (int denomination) {
    this.DENOMINATION = denomination;
  }
}
