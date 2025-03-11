package org.schoolproject.model;// Lieu.java


import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "lieu")
public class Lieu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codelieu")
    private Long codeLieu;

    @Column(name = "designation", nullable = false)
    private String designation;

    @Column(name = "province", nullable = false)
    private String province;

    @OneToMany(mappedBy = "lieu", cascade = CascadeType.ALL)
    private Set<Affecter> affectations = new HashSet<>();

    // Constructors
    public Lieu() {
    }

    public Lieu(String designation, String province) {
        this.designation = designation;
        this.province = province;
    }

    // Getters and Setters
    public Long getCodeLieu() {
        return codeLieu;
    }

    public void setCodeLieu(Long codeLieu) {
        this.codeLieu = codeLieu;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public Set<Affecter> getAffectations() {
        return affectations;
    }

    public void setAffectations(Set<Affecter> affectations) {
        this.affectations = affectations;
    }

    @Override
    public String toString() {
        return "Lieu{" +
                "codeLieu=" + codeLieu +
                ", designation='" + designation + '\'' +
                ", province='" + province + '\'' +
                '}';
    }
}