package edu.washington.cs.skeleton.util;

public enum IFDSOptions implements SootOptions{
    WHOLE_PROGRAM(true),
    ALLOW_PHANTOM_REF(true),
    SET_APP(true),

    // Call Graph Options
    CG_Safe_New_Instance(true),
    CG_Cha_Enabled(false),
    CG_Spark_Enabled(true),
    CG_Spark_Verbose(true),
    CG_Spark_OnFlyCg(true),
    IGNORE_RESOLUTION(true),
    VERBOSE(true),
    NOBODY_EXCLUDED(true);

    private boolean value;

    IFDSOptions(boolean set) { this.value = set; }

    public void valueF() {  this.value = false;}
    public void valueT() {  this.value = true;}
    public void opposite() {    this.value = !this.value;}
    public boolean getValue() {return this.value;}
    public void setValue(boolean set) {this.value = set;}
}
