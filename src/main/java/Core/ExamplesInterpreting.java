package Core;

import org.yaml.snakeyaml.*;
import sun.net.www.content.text.Generic;
import Exception.InputException;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;
import java.io.FileNotFoundException;

import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.options.Options;

import org.yaml.snakeyaml.Yaml;

/*
    This class is used to find all methods of given target. And it should have a method that can identify what's
    the pattern of given examples.
 */
public class ExamplesInterpreting {

    /**
     * Read in given examples and infer what's the common factors for the examples file
     * @param pathToExamples
     */
    public void ExamplesInterpretation(String pathToExamples) {
            Logger myLogger = Logger.getLogger("soot-skeleton.Core");
            Yaml yaml = new Yaml();
            LinkedHashMap<String, List<String>> output = null;
            List<SootClass> examplesClasses = new ArrayList<SootClass>();
            List<ExamplesMethod> examplesMethods = new ArrayList<ExamplesMethod>();
            try {
                File file = new File(pathToExamples);
                output = yaml.loadAs(new FileInputStream(file), LinkedHashMap.class);
                Set<String> examplesClass = output.keySet();

                for (String className : examplesClass) {
                    SootClass sootClass = loadClasses(className);
                    examplesClasses.add(sootClass);
                    for (String method : output.get(className)) {
                        method.replaceAll("<>():", "");
                        String[] list = method.split(" ");
                        examplesMethods.add(new ExamplesMethod(method,
                                sootClass.getMethodByName(list[list.length - 1]).getModifiers()));
                    }
                }
            } catch (FileNotFoundException e) {
                myLogger.info("Can't find examples.yaml file within the path: " + pathToExamples + ".");
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        if (output == null || output.size() == 0) {
            throw new InputException("examples.yaml file is empty");
        }

        // TODO: TO analysis the given output and find the pattern(For the demo purpose, we only check whether they are
        //  static methods and whether they have same return type).
        methodCheck(examplesMethods, examplesClasses);
    }

    /**
     * Load given class
     * @param className
     * @return given class as a SootClass
     */
    private SootClass loadClasses(String className) {
        Options.v().set_process_dir(Collections.singletonList("test-resource"));
        Options.v().set_soot_classpath("test-resource");
        Options.v().set_whole_program(true);
        Options.v().set_allow_phantom_refs(true);
        Scene.v().addBasicClass(className, SootClass.BODIES);
        Scene.v().loadClassAndSupport(className);
        SootClass testClass = Scene.v().getSootClass(className);
        return testClass;
    }

    static void methodCheck(List<ExamplesMethod> examplesMethods, List<SootClass> examplesClasses) {
        List<ExamplesMethod> allMethods = new ArrayList<ExamplesMethod>();

        for (SootClass sootClass : examplesClasses) {
            for (SootMethod sootMethod : sootClass.getMethods()) {
                allMethods.add(new ExamplesMethod(sootMethod.toString(), sootMethod.getModifiers()));
            }
        }
    }

    /*
        This method will check whether all elements in given examples list have the same modifier(public, private)
        TODO: Implement this part after finishing implementing the Core.
     */
    private boolean checkModifier(List<ExamplesMethod> examplesMethods) {
        return true;
    }

    private class ResultType {
        private boolean modifierPattern;
        private boolean returnPattern;


    }
}
