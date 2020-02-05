package edu.washington.cs.skeleton.analysis;

import edu.washington.cs.skeleton.util.SkeletonSootOptions;
import soot.Scene;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.options.Options;
import soot.SootClass;
import soot.*;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.Stmt;

import java.util.*;

public class CoreSootAnalyzer {
    private Set<String> reachingResult;
    private Map<String, Set<String>> callGraph;

    public CoreSootAnalyzer(boolean callGraphOrReachingDef, String classpath, String mainClass) {
        reachingResult = new HashSet<String>();
        reachingDefinitionAnalysis(callGraphOrReachingDef, classpath, mainClass, SkeletonSootOptions.WHOLE_PROGRAM.getValue(),
                SkeletonSootOptions.SET_APP.getValue(), SkeletonSootOptions.ALLOW_PHANTOM_REF.getValue(), SkeletonSootOptions.CG_Safe_New_Instance.getValue(),
                SkeletonSootOptions.CG_Cha_Enabled.getValue(), SkeletonSootOptions.CG_Spark_Enabled.getValue(), SkeletonSootOptions.CG_Spark_Verbose.getValue(),
                SkeletonSootOptions.CG_Spark_OnFlyCg.getValue(), SkeletonSootOptions.IGNORE_RESOLUTION.getValue(), SkeletonSootOptions.NOBODY_EXCLUDED.getValue(),
                SkeletonSootOptions.VERBOSE.getValue());
    }

    public void reachingDefinitionAnalysis(boolean callGraphOrReachingDef, String classpath, String mainClass, boolean wholeProgram, boolean setApp,
                                           boolean allowPhantomRef, boolean CGSafeNewInstance, boolean CGChaEnabled,
                                           boolean CGSparkEnabled, boolean CGSparkVerbose, boolean CGSparkOnFlyCg,
                                           boolean ignoreResolutionError, boolean noBodyExcluded, boolean verbose) {
        soot.G.reset();
        // Set Soot's internal classpath
        Options.v().set_process_dir(Collections.singletonList(classpath));
        Options.v().set_src_prec(Options.src_prec_class);
        Options.v().set_soot_classpath(classpath + ":lib/rt.jar");

        // Enable whole-program mode
        Options.v().set_whole_program(wholeProgram);
        Options.v().set_app(setApp);

        // Call-graph options
        Options.v().setPhaseOption("cg", "safe-newinstance:" + CGSafeNewInstance);
        Options.v().setPhaseOption("cg.cha","enabled:" + CGChaEnabled);

        // Enable SPARK call-graph construction
        Options.v().setPhaseOption("cg.spark","enabled:" + CGSparkEnabled);
        Options.v().setPhaseOption("cg.spark","verbose:" + CGSparkVerbose);
        Options.v().setPhaseOption("cg.spark","on-fly-cg:" + CGSparkOnFlyCg);

        // Other Essential Options that users frequently use
        Options.v().set_allow_phantom_refs(allowPhantomRef);
        Options.v().set_ignore_resolution_errors(ignoreResolutionError);
        Options.v().set_no_bodies_for_excluded(noBodyExcluded);
        Options.v().set_verbose(verbose);

        // Set the main class of the application to be analysed
        Options.v().set_main_class(mainClass);

        if (callGraphOrReachingDef) {
            runCGPack(mainClass);
            return;
        }
        runIFDSPack(mainClass);
    }

    public void runIFDSPack(String mainClass) {
        // Load the main class
        SootClass c = Scene.v().loadClass(mainClass, SootClass.BODIES);
        c.setApplicationClass();
        Scene.v().loadNecessaryClasses();

        // Load the "main" method of the main class and set it as a Soot entry point
        // TODO: need to implement entry point inference.
        SootMethod entryPoint = c.getMethodByName("main");
        List<SootMethod> entryPoints = new ArrayList<SootMethod>();
        entryPoints.add(entryPoint);
        Scene.v().setEntryPoints(entryPoints);

        PackManager.v().getPack("wjtp").add(new Transform("wjtp.herosifds", new IFDSDataFlowTransformer()));
        PackManager.v().getPack("jtp").add(new Transform("jtp.myTransform", new BodyTransformer() {
            @Override
            protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
                String className = b.getMethod().getDeclaringClass().getName();
                for (Unit u : b.getUnits()) {
                    Stmt s = (Stmt) u;
                    if (s.containsInvokeExpr() && s.getInvokeExpr() instanceof InstanceInvokeExpr) {
                        InstanceInvokeExpr e = (InstanceInvokeExpr) s.getInvokeExpr();

                        // find all invokeExpress in given configuration
                        System.out.println(s.getInvokeExprBox().getValue().toString());
                        reachingResult.add(s.getInvokeExprBox().getValue().toString());

                        /*
                        Only for test purpose

                        if (e.getMethod().getName().equals("println") && className.equals("DemoClass")) {
                            boolean equals = false;
                            String constants = "[[other]]";
                            for (Pair result : transformer.getSolver().ifdsResultsAt(u)) {
                                if (result.getO1().equals(e.getArg(0))) {
                                    System.out.println(result.getO2().toString());
                                    String str = result.getO2().toString();
                                    if (str.equals(constants)) {
                                        equals = true;
                                    }
                                }
                            }
                        }
                         */
                    }
                }
            }
        }));
        PackManager.v().runPacks();
    }

    public void runCGPack(String mainClass) {
        Scene.v().addBasicClass(mainClass, SootClass.SIGNATURES);
        Scene.v().loadClassAndSupport(mainClass);
        Scene.v().loadNecessaryClasses();
        PackManager.v().runPacks();

        SootClass testClass = Scene.v().getSootClass(mainClass);
        CallGraph cg = Scene.v().getCallGraph();
        parseOutput(testClass, cg);
    }

    private void parseOutput(SootClass testClass, CallGraph cg) {
        this.callGraph = new HashMap<String, Set<String>>();

        List<SootMethod> allMethods = testClass.getMethods();
        for (SootMethod md : allMethods) {
            String sig = md.getSignature();
            System.out.println("Parsing: " + sig);
            if (callGraph.get(sig) == null) {
                this.callGraph.put(sig, new HashSet<String>());
            }
            Iterator<Edge> outEdges = cg.edgesOutOf(md);
            Set<String> outDegrees = this.callGraph.get(sig);
            while (outEdges.hasNext()) {
                outDegrees.add(outEdges.next().getTgt().toString());
            }
        }
    }

    public Set<String> getReachingResult() {
        return reachingResult;
    }

    public Map<String, Set<String>> getCallGraph() {
        return callGraph;
    }
}
