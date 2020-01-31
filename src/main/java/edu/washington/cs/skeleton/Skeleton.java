package edu.washington.cs.skeleton;

import Analysis.CoreSootAnalyzer;
import edu.washington.cs.skeleton.util.*;

import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;
import java.util.Map;


public class Skeleton {
    private Map<String , Map<String, Set<String>>> allClasses;
    private String pathToTargetDirectory;
    private String outputPath;
    private SkeletonSootOptions skeletonSootOptions;

    public Skeleton(Map<String, String> userData, String pathToExamples) throws IOException {
        String CallGraphOrReachingDef = userData.get("CallGraphOrReachingDef");
        this.pathToTargetDirectory = userData.get("pathToTargetDirectory");
        this.outputPath = userData.get("outputPath");
        boolean relation = Boolean.parseBoolean(CallGraphOrReachingDef);
        // According to user config data, decide whether to analysis call graph or IFDS
        retrieveResult(userData, pathToExamples, relation);
    }


    public void retrieveResult(Map<String, String> userConfig, String pathToExamples, boolean callGraphOrReachingDef) throws IOException {
        Yaml yaml = new Yaml();
        IFDSExampleParser exp = null;
        CallGraphExampleParser exampleParser = null;
        InputStream inputStream = new FileInputStream(pathToExamples);
        if (callGraphOrReachingDef) {
            exampleParser = yaml.loadAs(inputStream, CallGraphExampleParser.class);
        } else {
            exp = yaml.loadAs(inputStream, edu.washington.cs.skeleton.util.IFDSExampleParser.class);
        }

        String targetClassName = userConfig.get("className");
        this.allClasses = exampleParser.getAllClasses();
        if ((!callGraphOrReachingDef && (exp.getStatement() == null || exp.getStatement().size() == 0)) ||
        (callGraphOrReachingDef && (exampleParser.getAllClasses() == null || exampleParser.getAllClasses().size() == 0))) {
            defaultIFDSConfigTraverse(targetClassName, callGraphOrReachingDef);
        } else {
            algorithmAnalysisFunction(targetClassName, exp, exampleParser, callGraphOrReachingDef);
        }
    }

    /**
     *
     * Handle the case where users have no examples to contribute in the case of Callgraph
     *
     * @param
     * @param targetClassName
     */
    public void defaultIFDSConfigTraverse(String targetClassName, boolean callGraphOrReachingDef) throws IOException {
        CoreSootAnalyzer coreSootAnalyzer = new CoreSootAnalyzer(callGraphOrReachingDef, this.pathToTargetDirectory, targetClassName, this.skeletonSootOptions.WHOLE_PROGRAM.getValue(),
                this.skeletonSootOptions.SET_APP.getValue(), this.skeletonSootOptions.ALLOW_PHANTOM_REF.getValue(), this.skeletonSootOptions.CG_Safe_New_Instance.getValue(),
                this.skeletonSootOptions.CG_Cha_Enabled.getValue(), this.skeletonSootOptions.CG_Spark_Enabled.getValue(), this.skeletonSootOptions.CG_Spark_Verbose.getValue(),
                this.skeletonSootOptions.CG_Spark_OnFlyCg.getValue(), this.skeletonSootOptions.IGNORE_RESOLUTION.getValue(), this.skeletonSootOptions.NOBODY_EXCLUDED.getValue(),
                this.skeletonSootOptions.VERBOSE.getValue());
        generateConfig();
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>> FOUND THE DESIRED OUTPUT");
    }

    /**
     *  Analysis the given user examples(reaching definition)
     *  Enumerate configuration Options of Soot, find the best-fit configuration that fits given target
     * @param targetClassName
     * @param exp
     * @param exampleParser
     * @param callGraphOrReachingDef
     * @throws IOException
     */
    public void algorithmAnalysisFunction( String targetClassName, IFDSExampleParser exp, CallGraphExampleParser exampleParser,
                                               boolean callGraphOrReachingDef) throws IOException {
        List<SkeletonSootOptions> options = new ArrayList<SkeletonSootOptions>();
        for (SkeletonSootOptions skeletonSootOptions : this.skeletonSootOptions.values()) {
            options.add(skeletonSootOptions);
        }
        boolean found = false ;
        if (callGraphOrReachingDef) {
            found = searchForCGValidConfig(options, 0, targetClassName, callGraphOrReachingDef);
        } else {
            found = searchForIFDSValidConfig(options, 0, targetClassName, exp, callGraphOrReachingDef);
        }
        if (!found) {
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>> FAILED TO FIND CONFIGURATION");
        } else {
            generateConfig();
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>> FOUND THE DESIRED OUTPUT");
        }
    }

    /**
     * Does the actual recursive searching
     * @param options
     * @param index
     * @param targetClassName
     * @param exp
     * @param callGraphOrReachingDef
     * @return
     * @throws AssertionError
     * @throws RuntimeException
     */
    private boolean searchForIFDSValidConfig(List<SkeletonSootOptions> options, int index, String targetClassName, IFDSExampleParser exp,
                                             boolean callGraphOrReachingDef) throws AssertionError, RuntimeException {
        if (index == options.size()) {
            // try false first, then true
            try {
                CoreSootAnalyzer coreSootAnalyzer = new CoreSootAnalyzer(callGraphOrReachingDef, this.pathToTargetDirectory, targetClassName, this.skeletonSootOptions.WHOLE_PROGRAM.getValue(),
                        this.skeletonSootOptions.SET_APP.getValue(), this.skeletonSootOptions.ALLOW_PHANTOM_REF.getValue(), this.skeletonSootOptions.CG_Safe_New_Instance.getValue(),
                        this.skeletonSootOptions.CG_Cha_Enabled.getValue(), this.skeletonSootOptions.CG_Spark_Enabled.getValue(), this.skeletonSootOptions.CG_Spark_Verbose.getValue(),
                        this.skeletonSootOptions.CG_Spark_OnFlyCg.getValue(), this.skeletonSootOptions.IGNORE_RESOLUTION.getValue(), this.skeletonSootOptions.NOBODY_EXCLUDED.getValue(),
                        this.skeletonSootOptions.VERBOSE.getValue());

                if (ValidateIFDS(coreSootAnalyzer, exp)) {
                    for (SkeletonSootOptions skeletonSootOptions : this.skeletonSootOptions.values()) {
                        System.out.println(skeletonSootOptions.name() + " : " + skeletonSootOptions.getValue());
                    }
                    return true;
                }
            }  catch (AssertionError e) {
//
//          Generally, this error is invoked  by unsound configuration that has false positive or false negative

            } catch (RuntimeException e) { }
            // right config not found
            return false;
        }
        SkeletonSootOptions current = options.get(index);
        current.valueF();
        boolean search1 = searchForIFDSValidConfig(options, index + 1, targetClassName, exp,  callGraphOrReachingDef);
        // found the corresponding result;
        if (search1) {
            return true;
        }
        current.valueT();
        boolean search2 = searchForIFDSValidConfig(options, index + 1, targetClassName, exp,  callGraphOrReachingDef);
        return search2;
    }


    /**
     * this method will generate the Soot Configuration into a yaml file which is called config.yaml
     */
    public void generateConfig() throws IOException {
        ResultConfig res = new ResultConfig();
        Map<String, Boolean> config = new HashMap<String, Boolean>();
        for (SkeletonSootOptions x : skeletonSootOptions.values()) {
            config.put(x.name(), x.getValue());
        }
        res.setResult(config);
        FileWriter writer = new FileWriter(this.outputPath);
        Yaml yaml = new Yaml();
        yaml.dump(res, writer);
        System.out.println("Output File Path Not Found");
    }

    /**
     * Does the actual recursive searching
     * @param options
     * @param index this field is going to decide to open which soot options
     * @param targetClassName
     * @param callGraphOrReachingDef
     * @return whether the desired result is found or not
     */
    private boolean searchForCGValidConfig(List<SkeletonSootOptions> options, int index,
                                           String targetClassName, boolean callGraphOrReachingDef) {
        if (index == options.size()) {
            // try false first, then true
            try {
                CoreSootAnalyzer coreSootAnalyzer = new CoreSootAnalyzer(callGraphOrReachingDef, this.pathToTargetDirectory, targetClassName, this.skeletonSootOptions.WHOLE_PROGRAM.getValue(),
                        this.skeletonSootOptions.SET_APP.getValue(), this.skeletonSootOptions.ALLOW_PHANTOM_REF.getValue(), this.skeletonSootOptions.CG_Safe_New_Instance.getValue(),
                        this.skeletonSootOptions.CG_Cha_Enabled.getValue(), this.skeletonSootOptions.CG_Spark_Enabled.getValue(), this.skeletonSootOptions.CG_Spark_Verbose.getValue(),
                        this.skeletonSootOptions.CG_Spark_OnFlyCg.getValue(), this.skeletonSootOptions.IGNORE_RESOLUTION.getValue(), this.skeletonSootOptions.NOBODY_EXCLUDED.getValue(),
                        this.skeletonSootOptions.VERBOSE.getValue());
                if (validateCGOutput(coreSootAnalyzer, targetClassName)) {
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
        boolean search1 = searchForCGValidConfig(options, index + 1, targetClassName, callGraphOrReachingDef);
        // found the corresponding result;
        if (search1) {
            return true;
        }
        current.valueT();
        boolean search2 = searchForCGValidConfig(options, index + 1, targetClassName, callGraphOrReachingDef);
        return search2;
    }

    private boolean validateCGOutput(CoreSootAnalyzer defAnalysis, String target) {
        System.out.println("Validating generated output ------------------------------------>");
        Map<String, Set<String>> res = defAnalysis.getCallGraph();
//      that's the case where the input call graph of that Java class
//      is empty. As long as our analyzer successfully load this class,
//      return true;

        if (this.allClasses.get(target) == null) {
            System.out.println("Target ");
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

    private boolean ValidateIFDS(CoreSootAnalyzer defAnalysis, IFDSExampleParser exp) {
        if (exp == null || defAnalysis == null || exp.getStatement() == null || defAnalysis.getReachingResult() == null) {
            return false;
        }
        System.out.println("Validating generated output ------------------------------------>");
        for (String stmt : exp.getStatement()) {
            if (!defAnalysis.getReachingResult().contains(stmt)) {
                return false;
            }
        }
        return true;
    }
}
