package edu.washington.cs.skeleton;

import org.junit.Test;
import edu.washington.cs.skeleton.App;

import java.io.IOException;

public class AppTest {
    @Test
    public void CGTest() throws IOException {
        System.out.println("TESTING CALL GRAPH");
        App.main(new String[]{"-cfg", "src/test/resources/config/config.yaml", "-exp", "src/test/resources/config/examples.yaml"});
        System.out.println();
        System.out.println();
    }

    @Test
    public void IFDSTest() throws IOException {
        System.out.println("TESTING REACHING DEFINITION");
        App.main(new String[]{"-cfg", "src/test/resources/config/configIFDS.yaml", "-exp", "src/test/resources/config/reaching.yaml"});
        System.out.println();
        System.out.println();
    }

    @Test
    public void IFDSRunnerTest() throws IOException {
        System.out.println("TESTING REACHING DEFINITION RUNNER");
        App.main(new String[]{"-r", "src/test/resources/config/loadConfigIFDS.yaml", "result.yaml"});
        System.out.println();
        System.out.println();
    }

    @Test
    public void CGRunnerTest() throws IOException {
        System.out.println("TESTING CALL GRAPH RUNNER");
        App.main(new String[]{"-r", "src/test/resources/config/loadConfig.yaml", "result2.yaml"});
        System.out.println();
        System.out.println();
    }
}
