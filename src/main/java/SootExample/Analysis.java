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
        analysisDemo();
    }

    public static void getPartialFlowTest() {
        Options.v().set_src_prec(Options.src_prec_class);
        Options.v().set_process_dir(Collections.singletonList("test-resource/Test.class"));
        Options.v().set_soot_classpath("test-resource/Test.class");
        Options.v().set_whole_program(true);
        Options.v().set_allow_phantom_refs(true);
        Scene.v().addBasicClass("Test", SootClass.BODIES);
        Scene.v().loadClassAndSupport("Test");
        SootClass testClass = Scene.v().getSootClass("Test");
        SootMethod method = testClass.getMethodByName("tester");

        System.out.println(method.getModifiers());
        System.out.println(method.retrieveActiveBody());
        List<SootMethod> methodList = testClass.getMethods();
        for (SootMethod md : methodList) {
            if (md.isStatic()) {
                System.out.println(md);
            }
        }
    }

    public static void analysisDemo() {
        soot.G.reset();
        Options.v().set_process_dir(Collections.singletonList("test-resource"));
        Options.v().set_src_prec(Options.src_prec_class);
        Options.v().set_soot_classpath("test-resource/DemoClass.java");
        Options.v().set_whole_program(true);
        Options.v().set_allow_phantom_refs(true);
        Options.v().set_verbose(true);
        Options.v().set_ignore_resolution_errors(true);
        Options.v().set_process_multiple_dex(true);
        Options.v().set_no_bodies_for_excluded(true);

        Scene.v().loadNecessaryClasses();

        Scene.v().addBasicClass("DemoClass", SootClass.BODIES);
    //    Scene.v().loadClassAndSupport("DemoClass");
        SootClass testClass = Scene.v().getSootClass("java.lang.Long");

        Options.v().setPhaseOption("cg", "on");
        List<SootClass> classes = Scene.v().getClasses(1);

        PackManager.v().runPacks();

        for (SootClass sootClass : classes) {
            System.out.println(sootClass.getName());
        }


//        SootMethod method = testClass.getMethodByName("main");
//        System.out.println(method.getModifiers());
//        System.out.println(method.retrieveActiveBody());
        List<SootMethod> methodList = testClass.getMethods();
        for (SootMethod md : methodList) {
            if (md.isStatic()) {
                System.out.println(md);
            }
        }
    }
}
