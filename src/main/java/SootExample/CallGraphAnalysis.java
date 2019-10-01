package SootExample;

import java.util.List;

import fj.data.Option;
import jdk.nashorn.internal.runtime.regexp.joni.constants.OPCode;
import soot.Body;
import soot.BodyTransformer;
import soot.Local;
import soot.PackManager;
import soot.PatchingChain;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Transform;
import soot.Unit;
import soot.jimple.AbstractStmtSwitch;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.StringConstant;
import soot.jimple.infoflow.android.entryPointCreators.AndroidEntryPointCreator;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.options.Options;
import soot.jimple.infoflow.android.SetupApplication;
import soot.jimple.infoflow.entryPointCreators.*;
import soot.util.Chain;
import sun.nio.ch.sctp.SctpNet;

public class CallGraphAnalysis {
    public static void main(String[] args) {
        cha();
        /*
        CallGraph cg = Scene.v().getCallGraph();

        System.out.println(cg.toString());

        List<SootClass> classes = Scene.v().getClasses(3);
        for (SootClass sootClass : classes) {
            System.out.println(sootClass.getName());
        }
        */
    }

    private static void cha() {
        SetupApplication app = new
                SetupApplication("android-platforms/android-28/android.jar","test-resource/HelloWorld.apk");

//        AndroidEntryPointCreator entryPointCreator = new AndroidEntryPointCreator();

        soot.G.reset();
        Options.v().set_android_jars("android-platforms/android-28/android.jar");
        Options.v().set_soot_classpath("test-resource/HelloWorld.apk");
        Options.v().set_src_prec(Options.src_prec_apk);
        Options.v().set_verbose(true);
        Options.v().set_app(true);
        Options.v().set_whole_program(true);

        Options.v().set_allow_phantom_refs(true);
        Options.v().set_ignore_resolution_errors(true);
        Options.v().set_process_multiple_dex(true);
        Options.v().set_no_bodies_for_excluded(true);

        Options.v().setPhaseOption("cg", "on");

        soot.Scene.v().loadNecessaryClasses();
        soot.PackManager.v().runPacks();

 //       SootClass testClass = Scene.v().getSootClass("MainActivity");

        Chain<SootClass> test =  Scene.v().getApplicationClasses();


        CallGraph cg = Scene.v().getCallGraph();
        System.out.println(cg.toString());


//        Iterator<Edge> edges = cg
//                .edgesOutOf(Scene
//                        .v()
//                        .getMethod(
//                                "<android.accounts.IAccountAuthenticatorResponse$Stub$Proxy: void onError(int,java.lang.String)>"));

        List<SootClass> classes = Scene.v().getClasses(3);
        for (SootClass sootClass : classes) {
            System.out.println(sootClass.getName());
            List<SootMethod> allMethods = sootClass.getMethods();
            for (SootMethod sootMethod : allMethods) {
                System.out.println(sootMethod.getName() + " : " + sootMethod.getModifiers());
            }
            System.out.println();
        }
    }

    public static void cgCall() {
        Options.v().set_whole_program(true);
        Options.v().set_allow_phantom_refs(true);
        Options.v().set_ignore_resolution_errors(true);
        Options.v().set_process_multiple_dex(true);
        Options.v().set_no_bodies_for_excluded(true);
        Options.v().set_omit_excepting_unit_edges(true);

        Options.v().setPhaseOption("cg", "on");

        soot.Scene.v().loadNecessaryClasses();
        soot.PackManager.v().runPacks();
    }
}
