package edu.washington.cs.skeleton;

import Analysis.Analyzer;
import Analysis.ReachingDefAnalysis;
import edu.washington.cs.skeleton.util.*;

import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;
import java.util.Map;


public class Skeleton {
    private Map<String , Map<String, Set<String>>> allClasses;
    private String pathToTargetDirectory;
    private String outputPath;
    private edu.washington.cs.skeleton.util.CallGraphOptions config;
    private IFDSOptions ifdsOptions;

    public Skeleton(Map<String, String> userData, String pathToExamples) throws IOException {
        String CallGraphOrReachingDef = userData.get("CallGraphOrReachingDef");
        this.pathToTargetDirectory = userData.get("pathToTargetDirectory");
        this.outputPath = userData.get("outputPath");
        boolean relation = Boolean.parseBoolean(CallGraphOrReachingDef);
        // According to user config data, decide whether to analysis call graph or IFDS
        targetIFDS(userData, pathToExamples, relation);
    }

    /**
     * Load as CallGraph input

    public void TargetCallGraph(String pathToExamples) throws IOException {
        Yaml yaml = new Yaml();
        CallGraphExampleParser exp = null;
        try {
            InputStream inputStream = new FileInputStream(pathToExamples);
            exp = yaml.loadAs(inputStream, CallGraphExampleParser.class);

            if (exp == null) {
                throw new FileNotFoundException();
            }
            // Convey current data to analysis.

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        /*
         *  key set will be the names of classes, the corresponding HashMap is the
         *  the all reachable methods and its out degrees.

        this.allClasses = exp.getAllClasses();

        for (String className: allClasses.keySet()) {
            /*
             *  empty call-graph

            if (allClasses.get(className) == null) {
                // FIXME: confusing name
                defaultParser(className);
            } else {
                cgAlgorithmAnalysisFunction(className);
            }
        }
    }
    */

    // FIXME: name convention
    public void targetIFDS(Map<String, String> userConfig, String pathToExamples, boolean callGraphOrReachingDef) throws IOException {
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
        if ((!callGraphOrReachingDef && (exp.getStatement() == null || exp.getStatement().size() == 0)) ||
        (callGraphOrReachingDef && (exampleParser.getAllClasses() == null || exampleParser.getAllClasses().size() == 0))) {
            defaultIFDSConfigTraverse(targetClassName, callGraphOrReachingDef);
        } else {
            ifdsAlgorithmAnalysisFunction(targetClassName, exp, exampleParser, callGraphOrReachingDef);
        }
    }

    /**
     *
     * Handle the case where users have no examples to contribute in the case of Callgraph
     *
     * @param
     * @param targetClassName
     */
    public void defaultIFDSConfigTraverse(String targetClassName, boolean callGraphOrReachingDef) {
        ReachingDefAnalysis ifdsAnalysis = new ReachingDefAnalysis(callGraphOrReachingDef, this.pathToTargetDirectory, targetClassName, this.ifdsOptions.WHOLE_PROGRAM.getValue(),
                this.ifdsOptions.SET_APP.getValue(), this.ifdsOptions.ALLOW_PHANTOM_REF.getValue(), this.ifdsOptions.CG_Safe_New_Instance.getValue(),
                this.ifdsOptions.CG_Cha_Enabled.getValue(), this.ifdsOptions.CG_Spark_Enabled.getValue(), this.ifdsOptions.CG_Spark_Verbose.getValue(),
                this.ifdsOptions.CG_Spark_OnFlyCg.getValue(), this.ifdsOptions.IGNORE_RESOLUTION.getValue(), this.ifdsOptions.NOBODY_EXCLUDED.getValue(),
                this.ifdsOptions.VERBOSE.getValue());

        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>> FOUND THE DESIRED OUTPUT");
    }

    /**
     * Analysis the given user examples(reaching definition)
     * Enumerate configuration Options of Soot, find the best-fit configuration that fits given target
     * @param targetClassName
     */
    public void ifdsAlgorithmAnalysisFunction( String targetClassName, IFDSExampleParser exp, CallGraphExampleParser exampleParser,
                                               boolean callGraphOrReachingDef) throws IOException {
        List<IFDSOptions> options = new ArrayList<IFDSOptions>();
        for (IFDSOptions ifdsOptions : this.ifdsOptions.values()) {
            options.add(ifdsOptions);
        }
        boolean found = false ;
        if (callGraphOrReachingDef) {
            found = searchForCGValidConfig(options, 0, targetClassName, exampleParser, callGraphOrReachingDef);
        } else {
            found = searchForIFDSValidConfig(options, 0, targetClassName, exp, callGraphOrReachingDef);
        }
        if (!found) {
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>> FAILED TO FIND CONFIGURATION");
        } else {
            generatConfig("ifds");
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>> FOUND THE DESIRED OUTPUT");
        }
    }

    private boolean searchForIFDSValidConfig(List<IFDSOptions> options, int index, String targetClassName, IFDSExampleParser exp,
                                             boolean callGraphOrReachingDef) throws AssertionError, RuntimeException {

        if (index == options.size()) {
            // try false first, then true
            try {
                ReachingDefAnalysis ifdsAnalysis = new ReachingDefAnalysis(callGraphOrReachingDef, this.pathToTargetDirectory, targetClassName, this.ifdsOptions.WHOLE_PROGRAM.getValue(),
                        this.ifdsOptions.SET_APP.getValue(), this.ifdsOptions.ALLOW_PHANTOM_REF.getValue(), this.ifdsOptions.CG_Safe_New_Instance.getValue(),
                        this.ifdsOptions.CG_Cha_Enabled.getValue(), this.ifdsOptions.CG_Spark_Enabled.getValue(), this.ifdsOptions.CG_Spark_Verbose.getValue(),
                        this.ifdsOptions.CG_Spark_OnFlyCg.getValue(), this.ifdsOptions.IGNORE_RESOLUTION.getValue(), this.ifdsOptions.NOBODY_EXCLUDED.getValue(),
                        this.ifdsOptions.VERBOSE.getValue());

                if (ValidateIFDS(ifdsAnalysis, exp)) {
                    for (IFDSOptions ifdsOptions : this.ifdsOptions.values()) {
                        System.out.println(ifdsOptions.name() + " : " + ifdsOptions.getValue());
                    }
                    return true;
                }
            }  catch (AssertionError e) {
//
//                 * Generally, this error is invoked  by unsound configuration that has false positive or false negative
//                 *
//                 * More importantly, after encounter error, update the configuration space.

            } catch (RuntimeException e) { }
            // right config not found
            return false;
        }
        IFDSOptions current = options.get(index);
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
     * @param mode
     */
    // FIXME: remove try-catch by using throws IOEXCEPTION
    public void generatConfig(String mode) throws IOException {

            ResultConfig res = new ResultConfig();
            Map<String, Boolean> config = new HashMap<String, Boolean>();
            for (IFDSOptions x : ifdsOptions.values()) {
                config.put(x.name(), x.getValue());
            }
            res.setResult(config);

            FileWriter writer = new FileWriter(this.outputPath);
            Yaml yaml = new Yaml();
            yaml.dump(res, writer);
            System.out.println("Output File Path Not Found");
    }


/*



    /**
     *
     * Handle the case where users have no examples to contribute in the case of Callgraph
     *
     * @param
     * @param target

    public void defaultParser(String target) {
        /*
         * default parser

        for (edu.washington.cs.skeleton.util.CallGraphOptions option : CallGraphOptions.values()) {
            option.valueT();
        }
        Analyzer analyzer = new Analyzer(this.pathToTargetDirectory, target, this.config.WHOLE_PROGRAM.getValue(),
                this.config.ALLOW_PHANTOM_REF.getValue(), this.config.VERBOSE.getValue(), this.config.IGNORE_RESOLUTION.getValue(),
                this.config.NOBODY_EXCLUDED.getValue());
        boolean result = validateCGOutput(analyzer, target);
        if (!result) {
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>> FAILED TO FIND CONFIGURATION");
        } else {
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>> FOUND THE DESIRED OUTPUT");
        }
    }

    /**
     * Analysis the given user examples(Call Graph)
     * Enumerate configuration Options of Soot, find the best-fit configuration that fits given target
     * @param target

    public void cgAlgorithmAnalysisFunction(String target) throws IOException {
        List<CallGraphOptions> options = new ArrayList<CallGraphOptions>();
        for (CallGraphOptions callGraphOptions : this.config.values()) {
            options.add(callGraphOptions);
        }
        boolean found = searchForCGValidConfig(options, 0, target);
        if (!found) {
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>> FAILED TO FIND CONFIGURATION");
        } else {
            // FIXME: combine default parser <-> generateConfig
            generatConfig("callgraph");
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>> FOUND THE DESIRED OUTPUT");
        }
    }

    /**
     *
     * @param options
     * @param index TODO
     * @param target
     * @return
*/
    private boolean searchForCGValidConfig(List<IFDSOptions> options, int index, String targetClassName,
                                           CallGraphExampleParser exampleParser, boolean callGraphOrReachingDef) {

        if (index == options.size()) {
            // try false first, then true
            try {
                ReachingDefAnalysis ifdsAnalysis = new ReachingDefAnalysis(callGraphOrReachingDef, this.pathToTargetDirectory, targetClassName, this.ifdsOptions.WHOLE_PROGRAM.getValue(),
                        this.ifdsOptions.SET_APP.getValue(), this.ifdsOptions.ALLOW_PHANTOM_REF.getValue(), this.ifdsOptions.CG_Safe_New_Instance.getValue(),
                        this.ifdsOptions.CG_Cha_Enabled.getValue(), this.ifdsOptions.CG_Spark_Enabled.getValue(), this.ifdsOptions.CG_Spark_Verbose.getValue(),
                        this.ifdsOptions.CG_Spark_OnFlyCg.getValue(), this.ifdsOptions.IGNORE_RESOLUTION.getValue(), this.ifdsOptions.NOBODY_EXCLUDED.getValue(),
                        this.ifdsOptions.VERBOSE.getValue());
                if (validateCGOutput(ifdsAnalysis, targetClassName)) {
                    for (IFDSOptions options1 : options) {
                        System.out.println(options1.name() + " : " + options1.getValue());
                    }
                    return true;
                }
            } catch (AssertionError e) {
//
//                 * Generally, this error is invoked  by unsound configuration that has false positive or false negative
//                 *
//                 * More importantly, after encounter error, update the configuration space.

            } catch (RuntimeException e) { }
            // right config not found
            return false;
        }
        IFDSOptions current = options.get(index);
        current.valueF();
        boolean search1 = searchForCGValidConfig(options, index + 1, targetClassName, exampleParser, callGraphOrReachingDef);
        // found the corresponding result;
        if (search1) {
            return true;
        }
        current.valueT();
        boolean search2 = searchForCGValidConfig(options, index + 1, targetClassName, exampleParser, callGraphOrReachingDef);
        return search2;
    }
/*
    /**
     * dead code
     * @param itr

    private void updateConfig(Iterator<Map.Entry<CallGraphOptions, Boolean>> itr ) {
        /*
         * Set those necessary options to true(set_whole_program, etc...)

        Map.Entry<edu.washington.cs.skeleton.util.CallGraphOptions, Boolean> cur = itr.next();
        cur.getKey().valueT();
    }

    /*
     * Check the generated call graph output
     * @param analyzer: generated output
     * @param target: examples that output needs to fit
     * @return
*/
    private boolean validateCGOutput(ReachingDefAnalysis defAnalysis, String target) {
        System.out.println("Validating generated output ------------------------------------>");
        Map<String, Set<String>> res = defAnalysis.getCallGraph();


//         * that's the case where the input call graph of that Java class
//         * is empty. As long as our analyzer successfully load this class,
//         * return true;

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
/*
    /**
     * Validate the stmt picked in the ReachingDefAnalysis
     * @param exp: expectation for the example
     * @param defAnalysis: generated output
     * @return

*/
    private boolean ValidateIFDS(ReachingDefAnalysis defAnalysis, IFDSExampleParser exp) {
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
