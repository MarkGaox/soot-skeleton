package edu.washington.cs.skeleton;

import Analysis.Analyzer;
import Analysis.ReachingDefAnalysis;
import edu.washington.cs.skeleton.Util.IFDSExampleParser;
import edu.washington.cs.skeleton.Util.IFDSOptions;
import edu.washington.cs.skeleton.Util.Recipe;
import org.yaml.snakeyaml.Yaml;
import edu.washington.cs.skeleton.Util.CallGraphOptions;

import java.io.*;
import java.util.*;
import java.util.Map;


public class Skeleton {
    private Map<String , Map<String, Set<String>>> allClasses;
    private edu.washington.cs.skeleton.Util.CallGraphOptions config;
    private IFDSOptions ifdsOptions;

    public Skeleton(Map<String, String> userData, String pathToExamples) {
        String CallGraphOrReachingDef = userData.get("CallGraphOrReachingDef");
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
    public void TargetCallGraph(Map<String, String> userData, String pathToExamples) {
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
                defaultParser(pathToTargetDirectory, className);
            } else {
                mostNarrowParser(pathToTargetDirectory, className);
            }
        }
    }

    public void TargetIFDS(Map<String, String> userConfig, String pathToExamples) {
        String pathToTargetDirectory = userConfig.get("pathToTargetDirectory");

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
            defaultIFDSConfigTraverse(pathToTargetDirectory, targetClassName);
        } else {
            IFDSEnumerationTraverse(pathToTargetDirectory, targetClassName, exp);
        }
    }

    public void IFDSEnumerationTraverse(String pathToTargetDirectory, String targetClassName, IFDSExampleParser exp) {
        ReachingDefAnalysis ifdsAnalysis = new ReachingDefAnalysis(pathToTargetDirectory, targetClassName, this.ifdsOptions.WHOLE_PROGRAM.getValue(),
                this.ifdsOptions.SET_APP.getValue(), this.ifdsOptions.ALLOW_PHANTOM_REF.getValue(), this.ifdsOptions.CG_Safe_New_Instance.getValue(),
                this.ifdsOptions.CG_Cha_Enabled.getValue(), this.ifdsOptions.CG_Spark_Enabled.getValue(), this.ifdsOptions.CG_Spark_Verbose.getValue(),
                this.ifdsOptions.CG_Spark_OnFlyCg.getValue());

        boolean result = ValidateIFDS(exp, ifdsAnalysis);
        if (!result) {
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>> FAILED ON GENERATE CORRECT STATEMENT");
        } else {
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>> FOUND THE DESIRED STATEMENT");
        }
    }

    /**
     * Validate the stmt picked in the ReachingDefAnalysis
     * @param exp
     * @param defAnalysis
     * @return
     */
    private boolean ValidateIFDS(IFDSExampleParser exp, ReachingDefAnalysis defAnalysis) {
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


    /**
     *
     * Handle the case where users have no examples to contribute in the case of Callgraph
     *
     * @param pathToTargetDirectory
     * @param targetClassName
     */
    public void defaultIFDSConfigTraverse(String pathToTargetDirectory, String targetClassName) {
        ReachingDefAnalysis ifdsAnalysis = new ReachingDefAnalysis(pathToTargetDirectory, targetClassName, this.ifdsOptions.WHOLE_PROGRAM.getValue(),
                this.ifdsOptions.SET_APP.getValue(), this.ifdsOptions.ALLOW_PHANTOM_REF.getValue(), this.ifdsOptions.CG_Safe_New_Instance.getValue(),
                this.ifdsOptions.CG_Cha_Enabled.getValue(), this.ifdsOptions.CG_Spark_Enabled.getValue(), this.ifdsOptions.CG_Spark_Verbose.getValue(),
                this.ifdsOptions.CG_Spark_OnFlyCg.getValue());

        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>> FOUND THE DESIRED OUTPUT");
    }









    /**
     *
     * Handle the case where users have no examples to contribute in the case of Callgraph
     *
     * @param pathToTargetDirectory
     * @param target
     */
    public void defaultParser(String pathToTargetDirectory, String target) {
        /*
         * default parser
         */
        for (edu.washington.cs.skeleton.Util.CallGraphOptions option : CallGraphOptions.values()) {
            option.valueT();
        }
        Analyzer analyzer = new Analyzer(pathToTargetDirectory, target, this.config.WHOLE_PROGRAM.getValue(),
                this.config.ALLOW_PHANTOM_REF.getValue(), this.config.VERBOSE.getValue(), this.config.IGNORE_RESOLUTION.getValue(),
                this.config.NOBODY_EXCLUDED.getValue());
        boolean result = validateOutput(analyzer, target);
        if (!result) {
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>> FAILED ON GENERATE CORRECT OUTPUT");
        } else {
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>> FOUND THE DESIRED OUTPUT");
        }
    }

    public void mostNarrowParser(String pathToTargetDirectory, String target) {
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
                Analyzer analyzer = new Analyzer(pathToTargetDirectory, target, this.config.WHOLE_PROGRAM.getValue(),
                        this.config.ALLOW_PHANTOM_REF.getValue(), this.config.VERBOSE.getValue(), this.config.IGNORE_RESOLUTION.getValue(),
                        this.config.NOBODY_EXCLUDED.getValue());
                notFound = !validateOutput(analyzer, target);
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

    private void updateConfig(Iterator<Map.Entry<CallGraphOptions, Boolean>> itr ) {
        /*
         * Set those necessary options to true(set_whole_program, etc...)
         */
        Map.Entry<edu.washington.cs.skeleton.Util.CallGraphOptions, Boolean> cur = itr.next();
        cur.getKey().valueT();
    }

    private boolean validateOutput(Analyzer analyzer, String target) {
        System.out.println("Validating generated output------------------------------------>");
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
}
