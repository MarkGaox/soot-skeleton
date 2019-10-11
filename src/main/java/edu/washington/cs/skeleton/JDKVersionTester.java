package edu.washington.cs.skeleton;

public class JDKVersionTester {
    private static final String javaVersion;
    private static boolean isJava7;

    static {
        javaVersion = System.getProperty("java.version");
        System.out.println("javaVersion=" + javaVersion);
        if (javaVersion.contains("1.7.")) {
            isJava7 = true;
        }
    }

    public static String getJavaVersion() {
        return javaVersion;
    }

    public static boolean isJava7() {
        return isJava7;
    }
}
