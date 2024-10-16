package prom16.controller;

public class Mapping {
    String className;
    String methodName;
    String verb;

    public Mapping(){}

    public Mapping(String className, String methodName, String verb) {
        this.setClassName(className);
        this.setMethodName(methodName);
        this.setVerb(verb);
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }    

    public String getVerb() {
        return verb;
    }

    public void setVerb(String verb) {
        this.verb = verb;
    }
}
