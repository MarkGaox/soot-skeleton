package edu.washington.cs.skeleton;

import Analysis.Analyzer;
import Analysis.ReachingDefAnalysis;
import edu.washington.cs.skeleton.Util.*;
import org.apache.commons.cli.CommandLine;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

public class Runner {
    private edu.washington.cs.skeleton.Util.CallGraphOptions cgconfig;
    private IFDSOptions ifdsOptions;

    public void runGivenConfig(Map<String, String> config, boolean CallGraphOrReachingDef, String[] runnerMode) {
        Yaml yaml = new Yaml();
        ResultConfig exp = null;
        try {
            String loadPath = runnerMode[1];
            InputStream inputStream = new FileInputStream(loadPath);
            exp = yaml.loadAs(inputStream, ResultConfig.class);

            if (exp == null) {
                throw new FileNotFoundException();
            }
            // Convey current data to analysis.
            System.out.println(exp.getResult().toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        String pathToTargetDirectory = config.get("pathToTargetDirectory");
        String targetClassName = config.get("className");
        if (!CallGraphOrReachingDef) {
            IFDSRun(exp, pathToTargetDirectory, targetClassName);
        } else {
            CGRun(exp, pathToTargetDirectory, targetClassName);
        }
    }

    public void CGRun(ResultConfig exp, String pathToTargetDirectory, String targetClassName) {
        Map<String, Boolean> result = exp.getResult();
        System.out.println("Start Call Graph Runner");
        for (CallGraphOptions options : cgconfig.values()) {
            if (result.containsKey(options.name())) {
                //System.out.println(options.name() + " : " + options.getValue());
                options.setValue(result.get(options.name()));
               // System.out.println(options.name() + " : " + options.getValue());
            }
        }
        Analyzer analyzer = new Analyzer(pathToTargetDirectory, targetClassName, this.cgconfig.WHOLE_PROGRAM.getValue(),
                this.cgconfig.ALLOW_PHANTOM_REF.getValue(), this.cgconfig.VERBOSE.getValue(), this.cgconfig.IGNORE_RESOLUTION.getValue(),
                this.cgconfig.NOBODY_EXCLUDED.getValue());
        System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< Printing Result >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        for (String s : analyzer.getCallGraph().keySet()) {
            System.out.println(s + " : " + analyzer.getCallGraph().get(s));
        }
    }

    public void IFDSRun(ResultConfig exp, String pathToTargetDirectory, String targetClassName) {
        Map<String, Boolean> result = exp.getResult();
        System.out.println("Start IFDS Runner");
        for (IFDSOptions options : ifdsOptions.values()) {
            if (result.containsKey(options.name())) {
                //System.out.println(options.name() + " : " + options.getValue());
                options.setValue(result.get(options.name()));
               // System.out.println(options.name() + " : " + options.getValue());
            }
        }
        for (IFDSOptions options : ifdsOptions.values()) {
            System.out.println(options.name() + " : " + options.getValue());
        }
        System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< Printing Result >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        ReachingDefAnalysis ifdsAnalysis = new ReachingDefAnalysis(pathToTargetDirectory, targetClassName, this.ifdsOptions.WHOLE_PROGRAM.getValue(),
                this.ifdsOptions.SET_APP.getValue(), this.ifdsOptions.ALLOW_PHANTOM_REF.getValue(), this.ifdsOptions.CG_Safe_New_Instance.getValue(),
                this.ifdsOptions.CG_Cha_Enabled.getValue(), this.ifdsOptions.CG_Spark_Enabled.getValue(), this.ifdsOptions.CG_Spark_Verbose.getValue(),
                this.ifdsOptions.CG_Spark_OnFlyCg.getValue());
    }
}
