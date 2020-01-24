package edu.washington.cs.skeleton;

import Analysis.Analyzer;
import Analysis.ReachingDefAnalysis;
import edu.washington.cs.skeleton.Util.*;

import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;
import java.util.Map;
import java.lang.System.*;


public class Skeleton {
    private Map<String , Map<String, Set<String>>> allClasses;
    private String pathToTargetDirectory;
    private String outputPath;
    private edu.washington.cs.skeleton.Util.CallGraphOptions config;
    private IFDSOptions ifdsOptions;

    public Skeleton(Map<String, String> userData, String pathToExamples) {
        String CallGraphOrReachingDef = userData.get("CallGraphOrReachingDef");
        this.pathToTargetDirectory = userData.get("pathToTargetDirectory");
        this.outputPath = userData.get("outputPath");



        boolean relation = Boolean.parseBoolean(CallGraphOrReachingDef);
        // According to user config data, decide whether to analysis call graph or IFDS
        if (relation) {
            TargetCallGraph(userData, pathToExamples);
        } else {
            TargetIFDS(userData, pathToExamples);
        }
    }

    /**
     * Load as CallGraph input
     */
    public void TargetCallGraph(Map<String, String> userConfig, String pathToExamples) {
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
         */
        this.allClasses = exp.getAllClasses();

        for (String className: allClasses.keySet()) {
            /*
             *  empty call-graph
             */
            if (allClasses.get(className) == null) {
                defaultParser(className);
            } else {
                cgAlgorithmAnalysisFunction(className);
            }
        }
    }

    public void TargetIFDS(Map<String, String> userConfig, String pathToExamples) {
        Yaml yaml = new Yaml();
        IFDSExampleParser exp = null;
        try
        {
            InputStream inputStream = new FileInputStream(pathToExamples);
            exp = yaml.loadAs(inputStream, edu.washington.cs.skeleton.Util.IFDSExampleParser.class);

            if (exp == null) {
                throw new FileNotFoundException();
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            System.exit(-1);
        }

        String targetClassName = userConfig.get("className");
        if (exp.getStatement() == null || exp.getStatement().size() == 0) {
            defaultIFDSConfigTraverse(targetClassName);
        } else {
            ifdsAlgorithmAnalysisFunction(targetClassName, exp);
        }
    }

    /**
     * Liner enumeration(reaching def)
     * @param targetClassName
     * @param exp
     */
    public void IFDSEnumerationTraverse(String targetClassName, IFDSExampleParser exp) {
        ReachingDefAnalysis ifdsAnalysis = new ReachingDefAnalysis(this.pathToTargetDirectory, targetClassName, this.ifdsOptions.WHOLE_PROGRAM.getValue(),
                this.ifdsOptions.SET_APP.getValue(), this.ifdsOptions.ALLOW_PHANTOM_REF.getValue(), this.ifdsOptions.CG_Safe_New_Instance.getValue(),
                this.ifdsOptions.CG_Cha_Enabled.getValue(), this.ifdsOptions.CG_Spark_Enabled.getValue(), this.ifdsOptions.CG_Spark_Verbose.getValue(),
                this.ifdsOptions.CG_Spark_OnFlyCg.getValue());

        boolean result = ValidateIFDS(ifdsAnalysis, exp);
        if (!result) {
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>> FAILED ON GENERATE CORRECT STATEMENT");
        } else {
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>> FOUND THE DESIRED STATEMENT");
        }
        System.out.println();
    }


    /**
     *
     * Handle the case where users have no examples to contribute in the case of Callgraph
     *
     * @param
     * @param targetClassName
     */
    public void defaultIFDSConfigTraverse(String targetClassName) {
        ReachingDefAnalysis ifdsAnalysis = new ReachingDefAnalysis(this.pathToTargetDirectory, targetClassName, this.ifdsOptions.WHOLE_PROGRAM.getValue(),
                this.ifdsOptions.SET_APP.getValue(), this.ifdsOptions.ALLOW_PHANTOM_REF.getValue(), this.ifdsOptions.CG_Safe_New_Instance.getValue(),
                this.ifdsOptions.CG_Cha_Enabled.getValue(), this.ifdsOptions.CG_Spark_Enabled.getValue(), this.ifdsOptions.CG_Spark_Verbose.getValue(),
                this.ifdsOptions.CG_Spark_OnFlyCg.getValue());

        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>> FOUND THE DESIRED OUTPUT");
    }

    /**
     * Analysis the given user examples(reaching definition)
     * Enumerate configuration Options of Soot, find the best-fit configuration that fits given target
     * @param targetClassName
     */
    public void ifdsAlgorithmAnalysisFunction(String targetClassName, IFDSExampleParser exp) {
        List<IFDSOptions> options = new ArrayList<IFDSOptions>();
        for (IFDSOptions ifdsOptions : this.ifdsOptions.values()) {
            options.add(ifdsOptions);
        }
        boolean found = searchForIFDSValidConfig(options, 0, targetClassName, exp);
        if (!found) {
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>> FAILED TO FIND CONFIGURATION");
        } else {
            generatConfig("ifds");
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>> FOUND THE DESIRED OUTPUT");
        }
    }

    private boolean searchForIFDSValidConfig(List<IFDSOptions> options, int index, String targetClassName, IFDSExampleParser exp) {

        if (index == options.size()) {
            // try false first, then true
            try {
                ReachingDefAnalysis ifdsAnalysis = new ReachingDefAnalysis(this.pathToTargetDirectory, targetClassName, this.ifdsOptions.WHOLE_PROGRAM.getValue(),
                        this.ifdsOptions.SET_APP.getValue(), this.ifdsOptions.ALLOW_PHANTOM_REF.getValue(), this.ifdsOptions.CG_Safe_New_Instance.getValue(),
                        this.ifdsOptions.CG_Cha_Enabled.getValue(), this.ifdsOptions.CG_Spark_Enabled.getValue(), this.ifdsOptions.CG_Spark_Verbose.getValue(),
                        this.ifdsOptions.CG_Spark_OnFlyCg.getValue());

                if (ValidateIFDS(ifdsAnalysis, exp)) {
                    for (IFDSOptions ifdsOptions : this.ifdsOptions.values()) {
                        System.out.println(ifdsOptions.name() + " : " + ifdsOptions.getValue());
                    }
                    return true;
                }
            } catch (AssertionError e) {
                /*
                 * Generally, this error is invoked  by unsound configuration that has false positive or false negative
                 *
                 * More importantly, after encounter error, update the configuration space.
                 */
            } catch (RuntimeException e) {

            }
            // right config not found
            return false;
        }
        IFDSOptions current = options.get(index);
        current.valueF();
        boolean search1 = searchForIFDSValidConfig(options, index + 1, targetClassName, exp);
        // found the corresponding result;
        if (search1) {
            return true;
        }
        current.valueT();
        boolean search2 = searchForIFDSValidConfig(options, index + 1, targetClassName, exp);
        return search2;
    }


    /**
     * this method will generate the Soot Configuration into a yaml file which is called config.yaml
     * @param mode
     */
    public void generatConfig(String mode) {
        if (mode.equals("ifds")) {
            ResultConfig res = new ResultConfig();
            Map<String, Boolean> config = new HashMap<String, Boolean>();
            for (IFDSOptions x : ifdsOptions.values()) {
                config.put(x.name(), x.getValue());
            }
            res.setResult(config);

            try {
                FileWriter writer = new FileWriter(this.outputPath);
                Yaml yaml = new Yaml();
                yaml.dump(res, writer);
            } catch (IOException e) {
                System.out.println("Output File Path Not Found");
                e.printStackTrace();
                System.exit(-1);
            }
        } else {
            ResultConfig res = new ResultConfig();
            Map<String, Boolean> config = new HashMap<String, Boolean>();
            for (CallGraphOptions x : this.config.values()) {
                config.put(x.name(), x.getValue());
            }
            res.setResult(config);
            try {
                FileWriter writer = new FileWriter(this.outputPath);
                Yaml yaml = new Yaml();
                yaml.dump(res, writer);
            } catch (IOException e) {
                System.out.println("Output File Path Not Found");
                e.printStackTrace();
                System.exit(-1);
            }
        }
    }

    public void WriteResult(String content)
            throws IOException {
        File file = new File(this.outputPath);
        if (!file.exists()) {
            file.createNewFile();
        }
        PrintWriter writer = new PrintWriter(content);
        writer.print(content);
        writer.close();
    }






    /**
     *
     * Handle the case where users have no examples to contribute in the case of Callgraph
     *
     * @param
     * @param target
     */
    public void defaultParser(String target) {
        /*
         * default parser
         */
        for (edu.washington.cs.skeleton.Util.CallGraphOptions option : CallGraphOptions.values()) {
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
     * direct traverse
     * @param target
     */
    public void mostNarrowParser(String target) {
        /*
         * Starts with all false
         */
        Map<CallGraphOptions, Boolean> visited = new HashMap<CallGraphOptions, Boolean>();
        for (CallGraphOptions option : CallGraphOptions.values()) {
            option.valueF();
            visited.put(option, false);
        }
        boolean notFound = true;
        Iterator<Map.Entry<edu.washington.cs.skeleton.Util.CallGraphOptions, Boolean>> itr = visited.entrySet().iterator();
        while (itr.hasNext() && notFound) {
            try {
                Analyzer analyzer = new Analyzer(this.pathToTargetDirectory, target, this.config.WHOLE_PROGRAM.getValue(),
                        this.config.ALLOW_PHANTOM_REF.getValue(), this.config.VERBOSE.getValue(), this.config.IGNORE_RESOLUTION.getValue(),
                        this.config.NOBODY_EXCLUDED.getValue());
                notFound = !validateCGOutput(analyzer, target);
            } catch (AssertionError e) {

                /*
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

    /**
     * Analysis the given user examples(Call Graph)
     * Enumerate configuration Options of Soot, find the best-fit configuration that fits given target
     * @param target
     */
    public void cgAlgorithmAnalysisFunction(String target) {
        List<CallGraphOptions> options = new ArrayList<CallGraphOptions>();
        for (CallGraphOptions callGraphOptions : this.config.values()) {
            options.add(callGraphOptions);
        }
        boolean found = searchForCGValidConfig(options, 0, target);
        if (!found) {
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>> FAILED TO FIND CONFIGURATION");
        } else {
            generatConfig("callgraph");
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>> FOUND THE DESIRED OUTPUT");
        }
    }

    private boolean searchForCGValidConfig(List<CallGraphOptions> options, int index, String target) {

        if (index == options.size()) {
            // try false first, then true
            try {
                Analyzer analyzer = new Analyzer(this.pathToTargetDirectory, target, this.config.WHOLE_PROGRAM.getValue(),
                        this.config.ALLOW_PHANTOM_REF.getValue(), this.config.VERBOSE.getValue(), this.config.IGNORE_RESOLUTION.getValue(),
                        this.config.NOBODY_EXCLUDED.getValue());
                if (validateCGOutput(analyzer, target)) {
                    for (CallGraphOptions options1 : options) {
                        System.out.println(options1.name() + " : " + options1.getValue());
                    }
                    return true;
                }
            } catch (AssertionError e) {
                /*
                 * Generally, this error is invoked  by unsound configuration that has false positive or false negative
                 *
                 * More importantly, after encounter error, update the configuration space.
                 */
            } catch (RuntimeException e) { }
            // right config not found
            return false;
        }
        CallGraphOptions current = options.get(index);
        current.valueF();
        boolean search1 = searchForCGValidConfig(options, index + 1, target);
        // found the corresponding result;
        if (search1) {
            return true;
        }
        current.valueT();
        boolean search2 = searchForCGValidConfig(options, index + 1, target);
        return search2;
    }

    /**
     * dead code
     * @param itr
     */
    private void updateConfig(Iterator<Map.Entry<CallGraphOptions, Boolean>> itr ) {
        /*
         * Set those necessary options to true(set_whole_program, etc...)
         */
        Map.Entry<edu.washington.cs.skeleton.Util.CallGraphOptions, Boolean> cur = itr.next();
        cur.getKey().valueT();
    }

    /**
     * Check the generated call graph output
     * @param analyzer: generated output
     * @param target: examples that output needs to fit
     * @return
     */
    private boolean validateCGOutput(Analyzer analyzer, String target) {
        System.out.println("Validating generated output ------------------------------------>");
        Map<String, Set<String>> res = analyzer.getCallGraph();

        /*
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
