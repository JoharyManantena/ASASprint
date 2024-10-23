package prom16.fonction;

import java.util.*;

public class Mapping {
    String className;
    List<VERBmethod> verbeAction = new ArrayList<>();

    public Mapping(){

    }

    public Mapping(String className) {
        this.setClassName(className);
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }  
    
    
    public List<VERBmethod> getVerbeAction() {
        return verbeAction;
    }

    public void setVerbeAction(List<VERBmethod> verbeAction) {
        this.verbeAction = verbeAction;
    }

    public void addVERBmethod(VERBmethod verbe){
        this.verbeAction.add(verbe);
    }

    public boolean contains(VERBmethod VERBmethode){
        boolean valiny = false;
        for (VERBmethod verbAct : this.verbeAction) {
            if ((verbAct.getMethodName().equals(VERBmethode.getMethodName())) && (verbAct.getVerb().equals(VERBmethode.getVerb()))) {
                valiny = true;
            }
        }
        return valiny;
    }
}