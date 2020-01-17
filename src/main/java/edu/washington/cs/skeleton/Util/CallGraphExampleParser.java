package edu.washington.cs.skeleton.Util;

import java.util.Map;
import java.util.Set;

public class CallGraphExampleParser {
    private Map<String , Map<String, Set<String>>> allClasses;

    public Map<String , Map<String, Set<String>>> getAllClasses() {
        return allClasses;
    }

    public void setAllClasses(Map<String , Map<String, Set<String>>> examples) {
        this.allClasses = examples;
    }

    @Override
    public String toString() {
        return "Recipe{" +  '\'' + ", demoClass=" + allClasses + '}';
    }
}
