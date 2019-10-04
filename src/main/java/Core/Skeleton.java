package Core;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;

/*
    This class will helper to generate the Soot configuration with given information.
 */
public class Skeleton {
    public static void main(Map<String, String> neededInformation) {
        String pathToSource = neededInformation.get("pathToSource");
        String outputType = neededInformation.get("outputType");
        String outputPath = neededInformation.get("outputPath");
    }

}
