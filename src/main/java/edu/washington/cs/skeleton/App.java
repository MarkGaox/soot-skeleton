package edu.washington.cs.skeleton;

import beaver.Parser;
import org.yaml.snakeyaml.Yaml;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.io.FileNotFoundException;

import Exception.JDKException;

public class App {
    public static void main(String[] args) throws IOException {
        Options options = new Options();
        Option cfg = new Option("cfg", "pathToConfig", true, "path to config.yaml");
        cfg.setArgs(1);
        Option exp = new Option("exp", "pathToExamples", true, "path to examples.yaml");
        exp.setArgs(1);
        Option mode = new Option("r", "runnerMode", true, "This is the runner mode. " +
                "There should be 2 seperate arguments. First one should be the file path to the " +
                "The second argument should be the path of the generated result configuration");
        // one for config.yaml path, one for result.yaml path
        mode.setArgs(2);

        // Options for types of runner mode
        Option cgType = new Option("cg", "runnerModeCG", false, "This is the " +
                "option to choose whether you want to runner to run given input with call graphs or reaching definition");
        Option rfType = new Option("rf", "runnerModeIFDS", false, "This is the " +
                "option to choose whether you want to runner to run given input with call graphs or reaching definition");

        options.addOption(cfg);
        options.addOption(exp);
        options.addOption(mode);
        options.addOption(cgType);
        options.addOption(rfType);

        // Parse the commandline options
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);

            System.exit(-1);
        }

        String pathToConfig = cmd.getOptionValue("pathToConfig");
        String pathToExamples = cmd.getOptionValue("pathToExamples");
        String[] runnerMode = cmd.getOptionValues("runnerMode");

        Option[] cmdOptions = cmd.getOptions();
        boolean runCG = false;
        boolean runIFDS = false;
        for (Option option : cmdOptions) {
            if (option.getLongOpt().equals("runnerModeCG")) {
                runCG = true;
            } else if (option.getLongOpt().equals("runnerModeIFDS")) {
                runIFDS = true;
            }
        }

        // enter runner mode
        if (runnerMode != null && runnerMode.length != 0) {
            // check whether argument is in a correct form
            if (runCG && runIFDS) {
                System.out.println("Can't select call graph runner mode(-cg) and reaching definition runner mode(-rf) at same time");
                throw new IllegalArgumentException();
            }

            if (!runCG && !runIFDS) {
                System.out.println("Need to select at least one of call graph runner mode(-cg) and reaching definition runner mode(-rf)");
                throw new IllegalArgumentException();
            }

            Map<String, String> all;    // uses to parse config.yaml
            Yaml yaml = new Yaml();
            File fileConfig = new File(runnerMode[0]);
            all = yaml.loadAs(new FileInputStream(fileConfig), Map.class);
            Runner run = new Runner();
            run.runGivenConfig(all, runCG, runnerMode);
            return;
        }
        JDKVersionTester versionTester = new JDKVersionTester();
        if (!versionTester.isJava8()) {
            throw new JDKException("Incorrect JDK version: " + versionTester.getJavaVersion() + ". Please check again.");
        }

        Map<String, String> all;  // uses to parse config.yaml
        Yaml yaml = new Yaml();
        File fileConfig = new File(pathToConfig);
        all = yaml.loadAs(new FileInputStream(fileConfig), Map.class);

        // Runner


        // Generator
        boolean analysisWithAPK = Boolean.parseBoolean(all.get("apk"));
        boolean analysisWithJavaClass = Boolean.parseBoolean(all.get("javaClass"));
        if (analysisWithAPK) {
            // TODO: Generalize into APK
        } else if (analysisWithJavaClass) {
            new Skeleton(all, pathToExamples);
        }
    }
}
