package prom16.fonction;

public class VerbAction {
    String methodName;
    String verb;//Soit 'GET' ou 'POST' en majuscule

    public VerbAction() {
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getVerb() {
        return verb;
    }
    
    public void setVerb(String verb) {
        this.verb = verb;
    }

    public VerbAction(String methodName, String verb) {
        this.setMethodName(methodName);
        this.setVerb(verb);
    }

}
