package Analysis;

import java.util.*;
import java.util.HashMap;
import java.util.HashSet;

import soot.*;
import soot.jimple.toolkits.callgraph.*;
import soot.options.Options;
import soot.PackManager;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;


public class AnalysisJava {
    public AnalysisJava(String targetDir,  HashMap<String, HashMap<String, HashSet<String>>> allClasses) {
        soot.G.reset();
        // getPartialFlowTest();
        analysisDemo(targetDir, allClasses);
        //testConfigSpace();
    }

    public static void analysisDemo(String targetDir, HashMap<String, HashMap<String, HashSet<String>>> allClasses) {
        soot.G.reset();
        Options.v().set_process_dir(Collections.singletonList(targetDir));
        Options.v().set_src_prec(Options.src_prec_java);
        Options.v().set_soot_classpath(targetDir);
        Options.v().set_whole_program(true);
        Options.v().set_allow_phantom_refs(true);
        Options.v().set_verbose(true);
        Options.v().set_ignore_resolution_errors(true);
        Options.v().set_no_bodies_for_excluded(false);


  //      Scene.v().loadNecessaryClasses();

        Scene.v().addBasicClass("DemoClass", SootClass.SIGNATURES);

        // TODO: Analysing Java Code directly is not recommended by Soot's developer, we may need to stop use
        //      java code front and analysis APK.
        Scene.v().loadClassAndSupport("DemoClass");
        Scene.v().loadNecessaryClasses();

        Options.v().setPhaseOption("cg.spark", "on");
        PackManager.v().getPack("jtp").add(new Transform("jtp.myTransform", new BodyTransformer() {
            @Override
            protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
                SootClass testClass = Scene.v().getSootClass("DemoClass");
                SootMethod method = testClass.getMethodByName("main");
                System.out.println(method.getModifiers());
                System.out.println(method.retrieveActiveBody());
                List<SootMethod> methodList = testClass.getMethods();
                System.out.println("Printing out all methods inside DemoClass =======================> \n");

                for (SootMethod md : methodList) {
                    System.out.println(md);
                }

                System.out.println("\n\n");

                System.out.println("Analysing indegrees and outdegrees for each methods in DemoClass");
                CallGraph cg = Scene.v().getCallGraph();
                for (SootMethod md2 : methodList) {
                    System.out.println(md2.getSignature() + " has in degree: ");
                    Iterator<Edge> inDegree = cg.edgesInto(md2);
                    while(inDegree.hasNext()) {
                        System.out.print("    ");
                        System.out.println(md2.getSignature() + " has in degree: " + inDegree.next());
                    }

                    Iterator<Edge> outDegree = cg.edgesOutOf(md2);
                    while (outDegree.hasNext()) {
                        System.out.print("    ");
                        System.out.println(md2.getSignature() + "has out degree: " + outDegree.next());
                    }
                }
            }
        }));
        PackManager.v().runPacks();
    }


    public static void testConfigSpace() {
        soot.G.reset();
        Options.v().set_process_dir(Collections.singletonList("test-resource"));
        Options.v().set_src_prec(Options.src_prec_java);
        Options.v().set_soot_classpath("test-resource");
        Options.v().set_whole_program(true);
        Options.v().set_allow_phantom_refs(true);


//        Options.v().set_ignore_resolution_errors(true);
        Options.v().set_no_bodies_for_excluded(true);
        Options.v().set_verbose(true);
//        Scene.v().addBasicClass("DemoClass", SootClass.SIGNATURES);
        Scene.v().loadClassAndSupport("DemoClass");
        Scene.v().loadNecessaryClasses();
        SootClass testClass = Scene.v().getSootClass("DemoClass");

//        Options.v().setPhaseOption("cg.spark", "on");
 //       Pack pk = PackManager.v().getPack("jtp");
        PackManager.v().runPacks();
        CallGraph cg = Scene.v().getCallGraph();
        List<SootMethod> allMethods = testClass.getMethods();
        for (SootMethod md : allMethods) {
            System.out.println(md);
//            System.out.println(md.retrieveActiveBody());
            Iterator<Edge> outEdges = cg.edgesOutOf(md);

            while (outEdges.hasNext()) {
                System.out.println(md.getSignature() + " Calls: " + outEdges.next().getTgt());
            }
            System.out.println();
            System.out.println();
        }
    }
}
