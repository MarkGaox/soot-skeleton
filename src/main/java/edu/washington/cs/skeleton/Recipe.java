package edu.washington.cs.skeleton;

import java.util.HashMap;
import java.util.HashSet;

public class Recipe {
    private HashMap<String , HashMap<String, HashSet<String>>>  allClasses;

    public HashMap<String , HashMap<String, HashSet<String>>> getAllClasses() {
        return allClasses;
    }

    public void setAllClasses(HashMap<String , HashMap<String, HashSet<String>>> examples) {
        this.allClasses = examples;
    }

    @Override
    public String toString() {
        return "Recipe{" +  '\'' + ", demoClass=" + allClasses + '}';
    }
}
