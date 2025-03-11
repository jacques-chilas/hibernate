package org.schoolproject.model;

// Employe.java

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "employe")
public class Employe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codeemp")
    private Long codeEmp;

    @Column(name = "nom", nullable = false)
    private String nom;

    @Column(name = "prenom", nullable = false)
    private String prenom;

    @Column(name = "poste", nullable = false)
    private String poste;

    @OneToMany(mappedBy = "employe", cascade = CascadeType.ALL)
    private Set<Affecter> affectations = new HashSet<>();

    // Constructors
    public Employe() {
    }

    public Employe(String nom, String prenom, String poste) {
        this.nom = nom;
        this.prenom = prenom;
        this.poste = poste;
    }

    // Getters and Setters
    public Long getCodeEmp() {
        return codeEmp;
    }

    public void setCodeEmp(Long codeEmp) {
        this.codeEmp = codeEmp;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getPoste() {
        return poste;
    }

    public void setPoste(String poste) {
        this.poste = poste;
    }

    public Set<Affecter> getAffectations() {
        return affectations;
    }

    public void setAffectations(Set<Affecter> affectations) {
        this.affectations = affectations;
    }

    @Override
    public String toString() {
        return "Employe{" +
                "codeEmp=" + codeEmp +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", poste='" + poste + '\'' +
                '}';
    }
}
