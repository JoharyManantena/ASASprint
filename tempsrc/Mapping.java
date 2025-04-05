package prom16.fonction;

import java.util.*;

public class Mapping {
    String className;
    List<VerbAction> verbeAction = new ArrayList<>();

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
    
    
    public List<VerbAction> getVerbeAction() {
        return verbeAction;
    }

    public void setVerbeAction(List<VerbAction> verbeAction) {
        this.verbeAction = verbeAction;
    }

    public void addVerbAction(VerbAction verbe){
        this.verbeAction.add(verbe);
    }

    public boolean contains(VerbAction verbMethode){
        boolean valiny = false;
        for (VerbAction verbAct : this.verbeAction) {
            if ((verbAct.getMethodName().equals(verbMethode.getMethodName())) && (verbAct.getVerb().equals(verbMethode.getVerb()))) {
                valiny = true;
            }
        }
        return valiny;
    }
}
