package edu.washington.cs.skeleton;

import edu.washington.cs.skeleton.analysis.CoreSootAnalyzer;
import edu.washington.cs.skeleton.util.*;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class Runner {
    public void runGivenConfig(Map<String, String> config, boolean callGraphOrReachingDef, String[] runnerMode) throws IOException {
        Yaml yaml = new Yaml();
        ResultConfig exp;
        String loadPath = runnerMode[1];
        InputStream inputStream = new FileInputStream(loadPath);
        exp = yaml.loadAs(inputStream, ResultConfig.class);

        if (exp == null) {
            throw new FileNotFoundException();
        }
        // Convey current data to analysis.
        System.out.println(exp.getResult().toString());

        String pathToTargetDirectory = config.get("pathToTargetDirectory");
        String targetClassName = config.get("className");
        run(exp, pathToTargetDirectory, targetClassName, callGraphOrReachingDef);
    }

    public void run(ResultConfig exp, String pathToTargetDirectory, String targetClassName, boolean callGraphOrReachingDef) {
        Map<String, Boolean> result = exp.getResult();
        System.out.println("Start Runner");
        for (SkeletonSootOptions options : SkeletonSootOptions.values()) {
            if (result.containsKey(options.name())) {
                options.setValue(result.get(options.name()));
            }
        }
        for (SkeletonSootOptions options : SkeletonSootOptions.values()) {
            System.out.println(options.name() + " : " + options.getValue());
        }
        System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< Printing Result >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        CoreSootAnalyzer coreSootAnalyzer = new CoreSootAnalyzer(callGraphOrReachingDef, pathToTargetDirectory, targetClassName, SkeletonSootOptions.WHOLE_PROGRAM.getValue(),
                SkeletonSootOptions.SET_APP.getValue(), SkeletonSootOptions.ALLOW_PHANTOM_REF.getValue(), SkeletonSootOptions.CG_Safe_New_Instance.getValue(),
                SkeletonSootOptions.CG_Cha_Enabled.getValue(), SkeletonSootOptions.CG_Spark_Enabled.getValue(), SkeletonSootOptions.CG_Spark_Verbose.getValue(),
                SkeletonSootOptions.CG_Spark_OnFlyCg.getValue(), SkeletonSootOptions.IGNORE_RESOLUTION.getValue(), SkeletonSootOptions.NOBODY_EXCLUDED.getValue(),
                SkeletonSootOptions.VERBOSE.getValue());
    }
}
