package prom16.Entite;

import prom16.annotation.AnnoterAttribut;
import prom16.annotation.AnnoterObject;

@AnnoterObject
public class Employer {
    private String nom;
    private String email;

    @AnnoterAttribut("argent")
    private double vola;

    private int age;

    public Employer(String nom, String email, int age,double argent) {
        this.setNom(nom);
        this.setEmail(email);
        this.setAge(age);
        this.setVola(argent);
    }

    public Employer() {
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public double getVola() {
        return vola;
    }

    public void setVola(double vola) {
        this.vola = vola;
    }
}
