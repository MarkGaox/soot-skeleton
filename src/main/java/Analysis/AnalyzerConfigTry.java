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


public class AnalyzerConfigTry {
    private Map<String, Set<String>> callGraph;

    public AnalyzerConfigTry(String pathToTargetDirectory, String target, boolean wholeProgram, boolean allowPhantom,
                             boolean verbose, boolean ignoreResolutionError, boolean noBodyExcluded) {
        soot.G.reset();
        // analysisDemo(pathToTargetDirectory, target);
        testConfigSpace(pathToTargetDirectory, target, wholeProgram, allowPhantom, verbose, ignoreResolutionError, noBodyExcluded);
    }

    public void analysisDemo(String targetDir, String target) {
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


    public void testConfigSpace(String pathToTargetDirectory, String target, boolean wholeProgram,
                                boolean allowPhantom, boolean verbose, boolean ignoreResolutionError,
                                boolean noBodyExcluded) {
        soot.G.reset();
        Options.v().set_process_dir(Collections.singletonList(pathToTargetDirectory));
        Options.v().set_src_prec(Options.src_prec_class);
        Options.v().set_soot_classpath(pathToTargetDirectory + ":lib/rt.jar");
        Options.v().set_whole_program(wholeProgram);
        Options.v().set_allow_phantom_refs(allowPhantom);

        Options.v().set_ignore_resolution_errors(ignoreResolutionError);
        Options.v().set_no_bodies_for_excluded(noBodyExcluded);
        Options.v().set_verbose(verbose);


        Scene.v().addBasicClass(target, SootClass.SIGNATURES);
        Scene.v().loadClassAndSupport(target);

        Scene.v().loadNecessaryClasses();
        Options.v().setPhaseOption("cg.spark", "on");
        //Pack pk = PackManager.v().getPack("jtp");
        PackManager.v().runPacks();

        SootClass testClass = Scene.v().getSootClass(target);
        CallGraph cg = Scene.v().getCallGraph();
        parseOutput(testClass, cg);
    }

    public Map<String, Set<String>> getCallGraph(){
        return this.callGraph;
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

    private void printOutEdgeOfTarget(SootClass testClass, CallGraph cg) {
        this.callGraph = new HashMap<String, Set<String>>();

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
