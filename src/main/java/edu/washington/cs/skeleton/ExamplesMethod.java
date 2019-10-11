package edu.washington.cs.skeleton;

public class ExamplesMethod {
    private int modifier;
    private String returnType;
    private String signature;

    public ExamplesMethod(String method, int modifier) {
        this.modifier = modifier;
        parseMethod(method);
    }

    private void parseMethod(String method) {
        method.replaceAll("<>():", "");
        String[] list = method.split(" ");

        this.returnType = list[1];
        this.signature = list[2];
    }

    public String getReturnType() {
        return this.returnType;
    }

    public String getSignature() {
        return this.signature;
    }

    public int getModifier() {
        return this.modifier;
    }
}
