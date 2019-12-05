package edu.washington.cs.skeleton;

import org.yaml.snakeyaml.Yaml;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;
import java.io.FileNotFoundException;

import Exception.JDKException;

public class App {
    public static void main(String[] args) {
        Options options = new Options();
        Option cfg = new Option("cfg", "pathToConfig", true, "path to config.yaml");
        cfg.setRequired(true);
        Option exp = new Option("exp", "pathToExamples", true, "path to examples.yaml");
        exp.setRequired(true);
        options.addOption(cfg);
        options.addOption(exp);
        if (args.length != 4) {
            throw new IllegalArgumentException();
        }

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

        JDKVersionTester versionTester = new JDKVersionTester();
        if (!versionTester.isJava8()) {
            throw new JDKException("Incorrect JDK version: " + versionTester.getJavaVersion() + ". Please check again.");
        }


        try
        {
            Map<String, String> all;  // uses to parse config.yaml
            Yaml yaml = new Yaml();
            File fileConfig = new File(pathToConfig);
            all = yaml.loadAs(new FileInputStream(fileConfig), Map.class);
            boolean analysisWithAPK = Boolean.parseBoolean(all.get("apk"));
            boolean analysisWithJavaClass = Boolean.parseBoolean(all.get("javaClass"));
            if (analysisWithAPK) {
                // TODO: Generalize into APK
            } else if (analysisWithJavaClass) {
                new Skeleton(all, pathToExamples);
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            System.exit(-1);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(-1);
        }
    }

}
