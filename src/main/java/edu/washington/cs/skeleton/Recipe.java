package edu.washington.cs.skeleton;

import java.util.HashMap;
import java.util.HashSet;

public class Recipe {
    private HashMap<String, HashSet<String>> demoClass;

    public HashMap<String, HashSet<String>> getDemoClass() {
        return demoClass;
    }

    public void setDemoClass(HashMap<String, HashSet<String>> examples) {
        this.demoClass = examples;
    }

    @Override
    public String toString() {
        return "Recipe{" +  '\'' + ", demoClass=" + demoClass + '}';
    }
}
