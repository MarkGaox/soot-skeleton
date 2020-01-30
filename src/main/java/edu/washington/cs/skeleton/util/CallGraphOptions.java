package edu.washington.cs.skeleton.util;

public enum CallGraphOptions implements SootOptions{
    // set default Options to true
    WHOLE_PROGRAM(true),
    ALLOW_PHANTOM_REF(true),
    VERBOSE(true),
    IGNORE_RESOLUTION(true),
    NOBODY_EXCLUDED(true);

    private boolean value;

    CallGraphOptions(boolean set) {
        this.value = set;
    }

    public void valueF() {  this.value = false;}
    public void valueT() {  this.value = true;}
    public void opposite() {    this.value = !this.value;}
    public boolean getValue() {return this.value;}
    public void setValue(boolean config) {   this.value = config; }
}
