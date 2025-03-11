package org.schoolproject.model;


// AffecterPK.java (Composite key for Affecter entity)

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class AffecterPK implements Serializable {

    @Column(name = "codeemp")
    private Long codeEmp;

    @Column(name = "codelieu")
    private Long codeLieu;

    // Constructors
    public AffecterPK() {
    }

    public AffecterPK(Long codeEmp, Long codeLieu) {
        this.codeEmp = codeEmp;
        this.codeLieu = codeLieu;
    }

    // Getters and Setters
    public Long getCodeEmp() {
        return codeEmp;
    }

    public void setCodeEmp(Long codeEmp) {
        this.codeEmp = codeEmp;
    }

    public Long getCodeLieu() {
        return codeLieu;
    }

    public void setCodeLieu(Long codeLieu) {
        this.codeLieu = codeLieu;
    }

    // equals and hashCode methods (required for composite keys)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AffecterPK that = (AffecterPK) o;
        return Objects.equals(codeEmp, that.codeEmp) &&
                Objects.equals(codeLieu, that.codeLieu);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codeEmp, codeLieu);
    }
}