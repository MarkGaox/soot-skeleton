package edu.washington.cs.skeleton.Util;

public enum IFDSOptions implements SootOptions{
    WHOLE_PROGRAM(true),
    ALLOW_PHANTOM_REF(true),
    SET_APP(true),

    // Call Graph Options
    CG_Safe_New_Instance(true),
    CG_Cha_Enabled(false),
    CG_Spark_Enabled(true),
    CG_Spark_Verbose(true),
    CG_Spark_OnFlyCg(true);

    private boolean value;

    IFDSOptions(boolean set) { this.value = set; }

    public void valueF() {  this.value = false;}
    public void valueT() {  this.value = true;}
    public void opposite() {    this.value = !this.value;}
    public boolean getValue() {return this.value;}
    public void setValue(boolean set) {this.value = set;}
}
