package edu.washington.cs.skeleton;

import Analysis.Analyzer;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;
import java.util.Map;

enum Configurations{
    // set default Options to true
    WHOLE_PROGRAM(true),
    ALLOW_PHANTOM_REF(true),
    VERBOSE(true),
    IGNORE_RESOLUTION(true),
    NOBODY_EXCLUDED(true);

    private boolean value;

    Configurations(boolean set) {
        this.value = set;
    }

    public void valueF() {  this.value = false;}
    public void valueT() {  this.value = true;}
    public void opposite() {    this.value = !this.value;}
    public boolean getValue() {return this.value;}
}



public class Skeleton {
    private Map<String , Map<String, Set<String>>> allClasses;
    private Configurations config;

    public Skeleton(Map<String, String> userData, String pathToExamples) {
        String pathToTargetDirectory = userData.get("pathToTargetDirectory");
        Yaml yaml = new Yaml();
        Recipe exp = null;
        try {
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
        this.allClasses = exp.getAllClasses();

        // TODO: design a better files processing procedure.
        for (String className: allClasses.keySet()) {
            /**
             *  empty call-graph
             */
            if (allClasses.get(className) == null) {
                defaultParser(pathToTargetDirectory, className);
            } else {
                mostNarrowParser(pathToTargetDirectory, className);
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

    public void mostNarrowParser(String pathToTargetDirectory, String target) {
        /**
         * Starts with all false
         */
        Map<Configurations, Boolean> visited = new HashMap<Configurations, Boolean>();
        for (Configurations option : Configurations.values()) {
            option.valueF();
            visited.put(option, false);
        }
        boolean notFound = true;
        Iterator<Map.Entry<Configurations, Boolean>> itr = visited.entrySet().iterator();
        while (itr.hasNext() && notFound) {
            try {
                Analyzer analyzer = new Analyzer(pathToTargetDirectory, target, this.config.WHOLE_PROGRAM.getValue(),
                        this.config.ALLOW_PHANTOM_REF.getValue(), this.config.VERBOSE.getValue(), this.config.IGNORE_RESOLUTION.getValue(),
                        this.config.NOBODY_EXCLUDED.getValue());
                notFound = !validateOutput(analyzer, target);
            } catch (AssertionError e) {

                /**
                 * Generally, this error is invoked  by unsound configuration that has false positive or false negative
                 *
                 * More importantly, after encounter error, update the configuration space.
                 */
                updateConfig(itr);
            } catch (RuntimeException e) {

                updateConfig(itr);
            }
        }
    }

    private void updateConfig(Iterator<Map.Entry<Configurations, Boolean>> itr ) {
        /**
         * Set those necessary options to true(set_whole_program, etc...)
         */
        Map.Entry<Configurations, Boolean> cur = itr.next();
        cur.getKey().valueT();
    }

    private boolean validateOutput(Analyzer analyzer, String target) {
        System.out.println("Validating generated output------------------------------------>");
        Map<String, Set<String>> res = analyzer.getCallGraph();

        /**
         * that's the case where the input call graph of that Java class
         * is empty. As long as our analyzer successfully load this class,
         * return true;
         */
        if (this.allClasses.get(target) == null) {
            return !res.isEmpty();
        }
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
