package edu.washington.cs.skeleton.util;

public enum SkeletonSootOptions implements SootOptions{
    // Call Graph Options
    CG_Safe_New_Instance(true),

    CG_Spark_Verbose(true),
    CG_Spark_OnFlyCg(true),
    IGNORE_RESOLUTION(true),
    VERBOSE(true),
    NOBODY_EXCLUDED(true),
    SET_APP(true),

    CG_Cha_Enabled(false),
    WHOLE_PROGRAM(true),
    ALLOW_PHANTOM_REF(true),
    CG_Spark_Enabled(true);

    private boolean value;

    SkeletonSootOptions(boolean set) { this.value = set; }

    public void valueF() {  this.value = false;}
    public void valueT() {  this.value = true;}
    public void opposite() {    this.value = !this.value;}
    public boolean getValue() {return this.value;}
    public void setValue(boolean set) {this.value = set;}
}
