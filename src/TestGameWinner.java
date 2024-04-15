import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestGameWinner {
  @Before
  public void setup() {
    GameWinner.setPlayerVictory(true, 4);
  }
  @Test
  public void testSetPlayerVictory() {
    GameWinner.setPlayerVictory(false, 0);
    assertEquals(false, GameWinner.getPlayerWon());
  }
  @Test
  public void testGetPlayerWon() {
    assertEquals(true, GameWinner.getPlayerWon());
  }
  @Test
  public void testGetPlayerNumber() {
    assertEquals(4, GameWinner.getPlayerNumber());
  }
}
