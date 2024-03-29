import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestCard {
  Card testCard;

  @Before
  public void setup() {
    testCard = new Card(50);
  }

  @Test
  public void testGetDenomination() {
    assertEquals(50, testCard.getDenomination());
  }
}
