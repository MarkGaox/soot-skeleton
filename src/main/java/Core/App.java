package Core;

import Exception.JDKException;
import fj.test.Bool;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;
import java.util.logging.Logger;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class App {
    private static final String PATH_TO_CONFIG_YAML = "src/main/java/Core/input/config.yaml";
    private static final String PATH_TO_EXAMPLES_YAML = "src/main/java/Core/input/examples.yaml";

    public static void main(String[] args) {
        Logger myLogger = Logger.getLogger("soot-skeleton.Core");

        /*
        JDKVersionTester versionTester = new JDKVersionTester();
        if (!versionTester.isJava7()) {
            myLogger.info("Using incompatible JDK version: " + versionTester.getJavaVersion() + ".");
            throw new JDKException("JDK version is not 1.7.");
        }
         */

        try {
            Map<String, String> all;
            Yaml yaml = new Yaml();
            File fileConfig = new File(PATH_TO_CONFIG_YAML);
            all = yaml.loadAs(new FileInputStream(fileConfig), Map.class);
            boolean analysisWithAPK = Boolean.parseBoolean(all.get("apk"));
            boolean analysisWithJavaClass = Boolean.parseBoolean(all.get("javaClass"));

            if (analysisWithAPK) {

            } else if (analysisWithJavaClass) {

                // TODO: implement examples reading in and use the examples to generate a Soot configuration java file
                //       which has outputs that can cover the given examples.

                myLogger.info("Creating Inferred Soot Configuration for Java Class =========>");
//                ExamplesInterpreting example = new ExamplesInterpreting();
//                example.ExamplesInterpretation(PATH_TO_EXAMPLES_YAML);
                Skeleton.main(all, PATH_TO_EXAMPLES_YAML);

            }




        } catch (FileNotFoundException e) {
            myLogger.info("Can't find config.yaml file within the path: " + PATH_TO_CONFIG_YAML + ".");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }




    }

}
