import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        TestCard.class,
        TestDeck.class,
        TestGameWinner.class,
        TestPlayer.class,
})

public class TestSuite {
}
