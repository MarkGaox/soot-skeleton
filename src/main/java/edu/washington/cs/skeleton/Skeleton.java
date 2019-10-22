package edu.washington.cs.skeleton;

import Analysis.AnalysisJava;
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

            // key set will be the names of classes, the corresponding HashMap is the
            // the all reachable methods and its out degrees.
            Map<String , Map<String, Set<String>>> demoClass = exp.getAllClasses();

            // Convey current data to analysis.
            AnalysisJava analysisJava = new AnalysisJava(pathToTargetDirectory, demoClass, true, true, true, true, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(-1);
        }

    }

}
