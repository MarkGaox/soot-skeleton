package edu.washington.cs.skeleton;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import edu.washington.cs.skeleton.analysis.*;
import edu.washington.cs.skeleton.util.SkeletonSootOptions;

import soot.*;
import soot.options.Options;

public class AppTest {
    private static final String CLASS_PATH =  "test-resource";

    @Test
    public void checkMethodNumbers() {
        soot.G.reset();

        Options.v().set_process_dir(Collections.singletonList(CLASS_PATH));
        Options.v().set_src_prec(Options.src_prec_class);
        Options.v().set_soot_classpath(CLASS_PATH + ":lib/rt.jar");

        Options.v().set_whole_program(true);
        Options.v().set_allow_phantom_refs(true);
        Options.v().set_no_bodies_for_excluded(true);
        Options.v().set_ignore_resolution_errors(true);
        Scene.v().loadNecessaryClasses();
        final SootClass testClass = Scene.v().getSootClass("DemoClass");
        assertEquals(testClass.getMethods().toString(), 17, testClass.getMethods().size());
        assertEquals(testClass.getFields().toString(), 1, testClass.getFields().size());
        PackManager.v().getPack("wjtp").add(new Transform("wjtp.test", new SceneTransformer() {
            @Override
            protected void internalTransform(String s, Map<String, String> map) {
                List<SootMethod> methods = testClass.getMethods();
                assertEquals(methods.size(), 17);
            }
        }));
    }

    @Test
    public void checkIFDSStmt() {
        soot.G.reset();
        SkeletonSootOptions.CG_Spark_Verbose.setValue(true);
        SkeletonSootOptions.CG_Spark_Enabled.setValue(false);
        SkeletonSootOptions.NOBODY_EXCLUDED.setValue(true);
        SkeletonSootOptions.CG_Spark_OnFlyCg.setValue(true);
        SkeletonSootOptions.CG_Cha_Enabled.setValue(true);
        SkeletonSootOptions.WHOLE_PROGRAM.setValue(true);
        SkeletonSootOptions.IGNORE_RESOLUTION.setValue(true);
        SkeletonSootOptions.VERBOSE.setValue(true);
        SkeletonSootOptions.SET_APP.setValue(true);
        SkeletonSootOptions.ALLOW_PHANTOM_REF.setValue(true);
        CoreSootAnalyzer analyzer = new CoreSootAnalyzer(false, CLASS_PATH, "DemoClass");
        assertEquals(analyzer.count, 1891);
    }
}
