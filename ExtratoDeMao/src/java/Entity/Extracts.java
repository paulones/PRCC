/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Entity;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author ppccardoso
 */
@Entity
@Table(name = "Extracts")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Extracts.findAll", query = "SELECT e FROM Extracts e"),
    @NamedQuery(name = "Extracts.findByExtractId", query = "SELECT e FROM Extracts e WHERE e.extractId = :extractId"),
    @NamedQuery(name = "Extracts.findByExtractName", query = "SELECT e FROM Extracts e WHERE e.extractName = :extractName"),
    @NamedQuery(name = "Extracts.findByUser", query = "SELECT m FROM Extracts m WHERE m.userId = :id")})
public class Extracts implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "extract_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer extractId;
    @Basic(optional = false)
    @Column(name = "extract_name")
    private String extractName;
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    @ManyToOne(optional = false)
    private Users userId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "extractId")
    private Collection<Movements> movementsCollection;

    public Extracts() {
    }

    public Extracts(Integer extratId) {
        this.extractId = extratId;
    }

    public Extracts(Integer extratId, String extratName) {
        this.extractId = extratId;
        this.extractName = extratName;
    }

    public Integer getExtractId() {
        return extractId;
    }

    public void setExtractId(Integer extractId) {
        this.extractId = extractId;
    }

    public String getExtractName() {
        return extractName;
    }

    public void setExtractName(String extractName) {
        this.extractName = extractName;
    }

    public Users getUserId() {
        return userId;
    }

    public void setUserId(Users userId) {
        this.userId = userId;
    }

    @XmlTransient
    public Collection<Movements> getMovementsCollection() {
        return movementsCollection;
    }

    public void setMovementsCollection(Collection<Movements> movementsCollection) {
        this.movementsCollection = movementsCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (extractId != null ? extractId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Extracts)) {
            return false;
        }
        Extracts other = (Extracts) object;
        if ((this.extractId == null && other.extractId != null) || (this.extractId != null && !this.extractId.equals(other.extractId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entity.Extracts[ extractId=" + extractId + " ]";
    }
}