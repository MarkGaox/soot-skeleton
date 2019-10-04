package SootExample;

import fj.data.Option;
import java.util.*;

import soot.BodyTransformer;
import soot.Local;
import soot.PackManager;
import soot.PatchingChain;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.toolkits.callgraph.*;
import soot.options.Options;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.StringConstant;
import soot.MethodOrMethodContext;
import soot.PackManager;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootClass;
import soot.SootMethod;
import soot.Transform;
import soot.Body;
import soot.Unit;


public class Analysis {
    //private static String jarPath = "android-platforms/android-28/android.jar";


    public static void main(String[] args) {
        soot.G.reset();
        // getPartialFlowTest();
       // analysisDemo();
        testConfigSpace();
    }

    public static void analysisDemo() {
        soot.G.reset();
        Options.v().set_process_dir(Collections.singletonList("test-resource"));
        Options.v().set_src_prec(Options.src_prec_java);
        Options.v().set_soot_classpath("test-resource");
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
        SootClass testClass = Scene.v().getSootClass("DemoClass");


        Options.v().setPhaseOption("cg.spark", "on");
        List<SootClass> classes = Scene.v().getClasses(3);



        for (SootClass sootClass : classes) {
            System.out.println(sootClass.getName());
        }

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
                    System.out.println(md2.getSignature() + "has in degree: ");
                    Iterator<Edge> indg = cg.edgesInto(md2);
                    while(indg.hasNext()) {
                        System.out.println(md2.getSignature() + "has in degree: " + indg.next());
                    }

                    Iterator<Edge> outdg = cg.edgesOutOf(md2);
                    while (outdg.hasNext()) {
                        System.out.println(md2.getSignature() + "has out degree: " + outdg.next());
                    }
                }

                for (Unit u : b.getUnits()) {
                    Stmt s = (Stmt) u;

                    // Call Graph Test


                    Iterator<Edge> outEdges = cg.edgesOutOf(u);
                    System.out.println("Trying to print the out edges");
                    while (outEdges.hasNext()) {
                        System.out.println(outEdges.next());
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
//        Options.v().set_no_bodies_for_excluded(false);
        Options.v().set_verbose(true);
//        Scene.v().addBasicClass("DemoClass", SootClass.SIGNATURES);
 //       Scene.v().loadClassAndSupport("DemoClass");
        Scene.v().loadNecessaryClasses();
        SootClass testClass = Scene.v().getSootClass("DemoClass");


        List<SootMethod> allMethods = testClass.getMethods();
        for (SootMethod md : allMethods) {
            System.out.println(md);
//            System.out.println(md.retrieveActiveBody());
        }
    }
}
