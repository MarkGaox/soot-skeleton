package Core;

import com.sun.org.apache.xerces.internal.impl.xpath.XPath;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
    This class will helper to generate the Soot configuration with given information.
 */
public class Skeleton {
    public static void main(Map<String, String> neededInformation, String pathToExamples) {
        String pathToSource = neededInformation.get("pathToSource");
        Yaml yaml = new Yaml();
        try {
            File fileExamples = new File(pathToExamples);
            Map<String, List<String>> userExamples = yaml.loadAs(new FileInputStream(fileExamples), Map.class);
            Set<String> hashSet = userExamples.keySet();
            for (String methods:
                 hashSet) {
                List<String> outEdges = userExamples.get(methods);
                for (String edge:
                     outEdges) {
                    System.out.println(edge);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

}
