package edu.washington.cs.skeleton.util;

import java.util.Set;

public class IFDSExampleParser {
    String method;
    Set<String> statement;

    public Set<String> getStatement() {
        return statement;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setStatement(Set<String> statement) {
        this.statement = statement;
    }

    public String getMethod() {
        return method;
    }

    @Override
    public String toString() {
        return "IFDSExample{" + statement + '}';
    }
}
