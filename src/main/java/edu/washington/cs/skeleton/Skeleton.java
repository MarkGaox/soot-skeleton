package edu.washington.cs.skeleton;

import edu.washington.cs.skeleton.analysis.CoreSootAnalyzer;
import edu.washington.cs.skeleton.util.*;

import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;
import java.util.Map;
import java.util.regex.*;

public class Skeleton {
    private Map<String , Map<String, Set<String>>> allClasses;
    private String pathToTargetDirectory;
    private String outputPath;
    private boolean callGraphOrReachingDef;

    public Skeleton(Map<String, String> userData, String pathToExamples) throws IOException {
        this.pathToTargetDirectory = userData.get("pathToTargetDirectory");
        this.outputPath = userData.get("outputPath");
        // According to user config data, decide whether to analysis call graph or IFDS
        retrieveResult(userData, pathToExamples);
    }


    /**
     * This is a dummy implementation of inference of input
     * TODO: Need to have a better inference
     * @return
     */
    private double inferTheInput() {
        double cg = 1;
        double ifds = 1;
        for (String classes : allClasses.keySet()) {
            for (String methods : allClasses.get(classes).keySet()) {
                Set<String> statements = new HashSet<String>( allClasses.get(classes).get(methods));
                for (String stmt : statements) {
                    String[] example = stmt.split(" ");
                    // count the IFDS with key word
                    if (example.length != 0 && (example[0].equals("virtualinvoke") || example[0].equals("specialinvoke"))) {
                        ifds++;
                    }

                    // count by format
                    String methodDef = example[example.length - 1];
                    Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(methodDef);
                    while(m.find()) {
                        String param = m.group(1);
                        if (testJavaType(param.toLowerCase())) {
                            cg++;
                        } else {
                            ifds++;
                        }
                    }
                }
            }
        }
        // NOT GOING TO GET HERE
        return cg / (cg + ifds);
    }

    private boolean testJavaType(String param) {
        return param.equals("byte") || param.equals("short") || param.equals("long") ||
            param.equals("int") || param.equals("double") || param.equals("float") || param.equals("");
    }

    public void retrieveResult(Map<String, String> userConfig, String pathToExamples) throws IOException {
        Yaml yaml = new Yaml();

        InputStream inputStream = new FileInputStream(pathToExamples);
        CallGraphExampleParser exampleParser = yaml.loadAs(inputStream, CallGraphExampleParser.class);
        // loading the users' examples

        // Set up the allClasses
        String targetClassName = userConfig.get("className");
        if (exampleParser != null) {
            this.allClasses = exampleParser.getAllClasses();
        }

        // Infer the input type
        double rateToBeCG = inferTheInput();

        if ((exampleParser.getAllClasses() == null || exampleParser.getAllClasses().size() == 0)) {
            defaultIFDSConfigTraverse(targetClassName);
        } else {
            algorithmAnalysisFunction(targetClassName, rateToBeCG);
        }
    }

    /**
     * Handle the case where users have no examples to contribute in the case of Callgraph
     * @param targetClassName
     * @throws IOException
     */
    private void defaultIFDSConfigTraverse(String targetClassName) throws IOException {
        CoreSootAnalyzer coreSootAnalyzer = new CoreSootAnalyzer(this.callGraphOrReachingDef, this.pathToTargetDirectory, targetClassName);
        generateConfig();
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>> FOUND THE DESIRED OUTPUT");
    }

    /**
     *  edu.washington.cs.skeleton.Analysis the given user examples(reaching definition)
     *  Enumerate configuration Options of Soot, find the best-fit configuration that fits given target
     * @param targetClassName
     * @throws IOException
     */
    private void algorithmAnalysisFunction(String targetClassName, double rateToBeCG) throws IOException {
        List<SkeletonSootOptions> options = new ArrayList<SkeletonSootOptions>();
        for (SkeletonSootOptions skeletonSootOptions : SkeletonSootOptions.values()) {
            options.add(skeletonSootOptions);
        }
        // infer the rate to be CG input
        if (rateToBeCG >= 0.5) {
            this.callGraphOrReachingDef = true;
        } else {
            this.callGraphOrReachingDef = false;
        }

        boolean found = searchForConfig(options, 0, targetClassName);
        if (!found) {
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>> FAILED TO FIND CONFIGURATION");
        } else {
            generateConfig();
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>> FOUND THE DESIRED OUTPUT");
            return;
        }

        this.callGraphOrReachingDef = !this.callGraphOrReachingDef;
        boolean secondTurn = searchForConfig(options, 0, targetClassName);
        if (!secondTurn) {
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>> FAILED TO FIND CONFIGURATION");
        } else {
            generateConfig();
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>> FOUND THE DESIRED OUTPUT");
        }
    }

    private boolean searchForConfig(List<SkeletonSootOptions> options, int index, String targetClassName)
            throws AssertionError, RuntimeException {
        if (index == options.size()) {
            // try false first, then true
            total += 1;
            System.out.println("Tested " + total + " : " + Math.pow(2.0, 11.0));
            try {
                CoreSootAnalyzer coreSootAnalyzer = new CoreSootAnalyzer(this.callGraphOrReachingDef, this.pathToTargetDirectory, targetClassName);
                if (validateOutput(coreSootAnalyzer, targetClassName)) {
                    for (SkeletonSootOptions options1 : options) {
                        System.out.println(options1.name() + " : " + options1.getValue());
                    }
                    return true;
                }
            } catch (AssertionError e) {
            } catch (RuntimeException e) { }
            // right config not found
            return false;
        }
        SkeletonSootOptions current = options.get(index);
        current.valueF();
        boolean search1 = searchForConfig(options, index + 1, targetClassName);
        // found the corresponding result;
        if (search1) {
            return true;
        }
        current.valueT();
        boolean search2 = searchForConfig(options, index + 1, targetClassName);
        return search2;
    }


    int total = 0;

    /**
     * this method will generate the Soot Configuration into a yaml file which is called config.yaml
     */
    private void generateConfig() throws IOException {
        ResultConfig res = new ResultConfig();
        Map<String, Boolean> config = new HashMap<String, Boolean>();
        for (SkeletonSootOptions x : SkeletonSootOptions.values()) {
            config.put(x.name(), x.getValue());
        }
        config.put("CG_OR_RF", this.callGraphOrReachingDef);
        res.setResult(config);
        FileWriter writer = new FileWriter(this.outputPath);
        Yaml yaml = new Yaml();
        yaml.dump(res, writer);
        System.out.println("Output File Path Not Found");
    }


    private boolean validateOutput(CoreSootAnalyzer coreSootAnalyzer, String target) {
        System.out.println("Validating generated output ------------------------------------>");

        Map<String, Set<String>> res;
        if (this.callGraphOrReachingDef) {
            res = coreSootAnalyzer.getCallGraph();
        } else {
            res = coreSootAnalyzer.getReachingResult();
        }

        //      that's the case where the input call graph of that Java class
        //      is empty. As long as our analyzer successfully load this class,
        //      return true;
        if (this.allClasses.get(target) == null) {
            System.out.println("Target example class is not found");
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
