package edu.washington.cs.skeleton;

import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;

/*
    This class will helper to generate the Soot configuration with given information.
 */
public class Skeleton {
    public static void main(Map<String, String> config, String pathToExamples) {
        String pathToTargetDirectory = config.get("pathToTargetDirectory");
        Yaml yaml = new Yaml();

        try {
            File fileExamples = new File(pathToExamples);

            InputStream inputStream = new FileInputStream(pathToExamples);

            Recipe exp = yaml.loadAs(inputStream, Recipe.class);

            HashMap<String, HashSet<String>> demoClass = exp.getDemoClass();
            Set<String> hashSet = demoClass.keySet();
            for (String methods : hashSet) {
                HashSet outEdges = demoClass.get(methods);
                for (Object edge : outEdges) {
                    System.out.println(edge);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(-1);
        }

    }

}
