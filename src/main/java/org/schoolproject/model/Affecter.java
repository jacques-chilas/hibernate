package org.schoolproject.model;

// Affecter.java
import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "affecter")
public class Affecter {

    @EmbeddedId
    private AffecterPK id;

    @ManyToOne
    @MapsId("codeEmp")
    @JoinColumn(name = "codeemp")
    private Employe employe;

    @ManyToOne
    @MapsId("codeLieu")
    @JoinColumn(name = "codelieu")
    private Lieu lieu;

    @Temporal(TemporalType.DATE)
    @Column(name = "date", nullable = false)
    private Date date;

    // Constructors
    public Affecter() {
    }

    public Affecter(Employe employe, Lieu lieu, Date date) {
        this.employe = employe;
        this.lieu = lieu;
        this.date = date;
        this.id = new AffecterPK(employe.getCodeEmp(), lieu.getCodeLieu());
    }

    // Getters and Setters
    public AffecterPK getId() {
        return id;
    }

    public void setId(AffecterPK id) {
        this.id = id;
    }

    public Employe getEmploye() {
        return employe;
    }

    public void setEmploye(Employe employe) {
        this.employe = employe;
    }

    public Lieu getLieu() {
        return lieu;
    }

    public void setLieu(Lieu lieu) {
        this.lieu = lieu;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Affecter{" +
                "id=" + id +
                ", date=" + date +
                '}';
    }
}
