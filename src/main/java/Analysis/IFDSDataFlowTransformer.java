package Analysis;

import heros.InterproceduralCFG;
import heros.solver.IFDSSolver;
import heros.IFDSTabulationProblem;

import soot.jimple.toolkits.ide.icfg.JimpleBasedInterproceduralCFG;
import soot.jimple.DefinitionStmt;
import soot.toolkits.scalar.Pair;
import soot.SceneTransformer;
import soot.*;

import Analysis.IFDSReachingDefinitions;


import java.util.*;

// Subclass of SceneTransformer to run Heros IFDS solver in Soot's "wjtp" pack
public class IFDSDataFlowTransformer extends SceneTransformer {

    IFDSSolver<Unit, Pair<Value, Set<DefinitionStmt>>, SootMethod, InterproceduralCFG<Unit, SootMethod>> solver;

    @Override
    protected void internalTransform(String phaseName, Map<String, String> options) {
        JimpleBasedInterproceduralCFG icfg= new JimpleBasedInterproceduralCFG();

        IFDSTabulationProblem<Unit, soot.toolkits.scalar.Pair<Value, Set<DefinitionStmt>>,
                SootMethod, InterproceduralCFG<Unit, SootMethod>> problem = new Analysis.IFDSReachingDefinitions(icfg);

        this.solver =
                new IFDSSolver<Unit, Pair<Value, Set<DefinitionStmt>>, SootMethod,
                        InterproceduralCFG<Unit, SootMethod>>(problem);

        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>> Starting solver");
        solver.solve();
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>> Done");
    }

    public IFDSSolver<Unit, Pair<Value, Set<DefinitionStmt>>, SootMethod, InterproceduralCFG<Unit, SootMethod>> getSolver() {
        return solver;
    }
}