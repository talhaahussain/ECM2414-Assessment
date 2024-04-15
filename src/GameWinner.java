public class GameWinner {
  // ATTRIBUTES
  // all attributes are volatile to avoid CPU caching and predictive execution
  // all members are static; cannot be instantiated
  static private volatile Boolean playerWon = false;
  static private volatile int playerNumber;

  // METHODS
  public synchronized static void setPlayerVictory(Boolean playerWon, int playerNumber) {
    // called by the winning player and populated with their player number
    GameWinner.playerWon = playerWon;
    GameWinner.playerNumber = playerNumber;
  }
  public synchronized static Boolean getPlayerWon() {
    return playerWon;
  }
  public synchronized static int getPlayerNumber() {
   return playerNumber;
  }
}