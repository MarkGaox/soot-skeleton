package unit;

import org.junit.Test;
import edu.washington.cs.skeleton.App;

import java.io.FileNotFoundException;

public class AppTest {
    @Test
    public void CGTest() throws FileNotFoundException {
        App.main(new String[]{"-cfg", "src/main/java/Core/input/config.yaml", "-exp", "src/main/java/Core/input/examples.yaml"});
    }

    @Test
    public void IFDSTest() throws FileNotFoundException {
        App.main(new String[]{"-cfg", "src/main/java/Core/input/configIFDS.yaml", "-exp", "src/main/java/Core/input/reaching.yaml"});
    }

    @Test
    public void IFDSRunnerTest() throws FileNotFoundException {
        App.main(new String[]{"-r", "src/main/java/Core/input/loadConfigIFDS.yaml", "result.yaml"});
    }

    @Test
    public void CGRunnerTest() throws FileNotFoundException {
        App.main(new String[]{"-r", "src/main/java/Core/input/loadConfig.yaml", "result2.yaml"});
    }
}
