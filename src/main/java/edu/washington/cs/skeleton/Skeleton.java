package edu.washington.cs.skeleton;

import Analysis.Analyzer;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;

enum Configurations{
    // set default Options to true
    WHOLE_PROGRAM(true),
    ALLOW_PHANTOM_REF(true),
    VERBOSE(true),
    IGNORE_RESOLUTION(true),
    NOBODY_EXCLUDED(true);

    private boolean value;

    private Configurations(boolean set) {
        this.value = set;
    }

    public void valueF() { this.value = false;}
    public void valueT() { this.value = true;}
    public boolean getValue() {return this.value;}
}



public class Skeleton {
    private static Map<String , Map<String, Set<String>>> allClasses;
    private static Configurations config;

    public Skeleton(Map<String, String> config, String pathToExamples) {
        String pathToTargetDirectory = config.get("pathToTargetDirectory");
        Yaml yaml = new Yaml();
        Recipe exp = null;
        try {
            File fileExamples = new File(pathToExamples);
            InputStream inputStream = new FileInputStream(pathToExamples);
            exp = yaml.loadAs(inputStream, Recipe.class);

            if (exp == null) {
                throw new FileNotFoundException();
            }
            // Convey current data to analysis.

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        /**
         *  key set will be the names of classes, the corresponding HashMap is the
         *  the all reachable methods and its out degrees.
         */
        allClasses = exp.getAllClasses();

        // TODO: design a better files processing procedure.
        for (String className: allClasses.keySet()) {
            /**
             *  empty call-graph
             */
            if (allClasses.get(className) == null) {
                defaultParser(pathToTargetDirectory, className);
            } else {
                defaultParser(pathToTargetDirectory, className);
            }
        }

    }

    public void defaultParser(String pathToTargetDirectory, String target) {
        /**
         * default parser
         */
        for (Configurations option : Configurations.values()) {
            option.valueT();
        }
        Analyzer analyzer = new Analyzer(pathToTargetDirectory, target, this.config.WHOLE_PROGRAM.getValue(),
                this.config.ALLOW_PHANTOM_REF.getValue(), this.config.VERBOSE.getValue(), this.config.IGNORE_RESOLUTION.getValue(),
                this.config.NOBODY_EXCLUDED.getValue());
        boolean result = validateOutput(analyzer, target);
        if (!result) {
            System.out.println("FAILED ON GENERATE CORRECT OUTPUT");
        } else {
            System.out.println("FOUND THE DESIRED OUTPUT");
        }
    }

    private boolean validateOutput(Analyzer analyzer, String target) {
        System.out.println("Validating generated output------------------------>");
        Map<String, Set<String>> res = analyzer.getCallGraph();
        for (String method : this.allClasses.get(target).keySet()) {
            if (!res.containsKey(method)) {
                return false;
            }
            List<String> outEdges = (List<String>) this.allClasses.get(target).get(method);
            for (String outCall : outEdges) {
                if (!res.get(method).contains(outCall)) {
                    return false;
                }
            }
        }
        return true;
    }
}
