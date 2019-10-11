package unit;

import org.junit.Test;
import edu.washington.cs.skeleton.App;

public class AppTest {

    @Test
    public void CommandLineTest() {
        App.main(new String[]{"-cfg", "src/main/java/Core/input/config.yaml", "-exp", "src/main/java/Core/input/examples.yaml"});
    }
}
