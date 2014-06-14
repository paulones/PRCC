/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Entity;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author ppccardoso
 */
@Entity
@Table(name = "Movements")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Movements.findAll", query = "SELECT m FROM Movements m"),
    @NamedQuery(name = "Movements.findByMovId", query = "SELECT m FROM Movements m WHERE m.movId = :movId"),
    @NamedQuery(name = "Movements.findByMovDesc", query = "SELECT m FROM Movements m WHERE m.movDesc = :movDesc"),
    @NamedQuery(name = "Movements.findByOcult", query = "SELECT m FROM Movements m WHERE m.ocult = :ocult"),
    @NamedQuery(name = "Movements.findByDataIni", query = "SELECT m FROM Movements m WHERE m.dataIni = :dataIni"),
    @NamedQuery(name = "Movements.findGTDate", query = "SELECT m FROM Movements m WHERE m.dataIni <= :date and m.extractId = :id")})
public class Movements implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "mov_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer movId;
    @Basic(optional = false)
    @Column(name = "mov_desc")
    private String movDesc;
    @Basic(optional = false)
    @Column(name = "ocult")
    private boolean ocult;
    @Basic(optional = false)
    @Column(name = "data_ini")
    @Temporal(TemporalType.DATE)
    private Date dataIni;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "movId")
    private Collection<Logs> logsCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "movId")
    private Collection<Parcelas> parcelasCollection;
    @JoinColumn(name = "extract_id", referencedColumnName = "extract_id")
    @ManyToOne(optional = false)
    private Extracts extractId;
    
    private transient Logs atualLog;
    private transient String regStr;
    private transient String pgtoStr;
    private transient String parcelaStr;
    private transient Boolean oculted = false;

    public Movements() {
        atualLog = new Logs();
    }

    public Movements(Integer movId) {
        this.movId = movId;
    }

    public Movements(Integer movId, String movDesc, boolean ocult, Date dataIni) {
        this.movId = movId;
        this.movDesc = movDesc;
        this.ocult = ocult;
        this.dataIni = dataIni;
    }

    public Integer getMovId() {
        return movId;
    }

    public void setMovId(Integer movId) {
        this.movId = movId;
    }

    public String getMovDesc() {
        return movDesc;
    }

    public void setMovDesc(String movDesc) {
        this.movDesc = movDesc;
    }

    public boolean getOcult() {
        return ocult;
    }

    public void setOcult(boolean ocult) {
        this.ocult = ocult;
    }

    public Date getDataIni() {
        return dataIni;
    }

    public void setDataIni(Date dataIni) {
        this.dataIni = dataIni;
    }

    public Extracts getExtractId() {
        return extractId;
    }

    public void setExtractId(Extracts extractId) {
        this.extractId = extractId;
    }

    public Logs getAtualLog() {
        return atualLog;
    }

    public void setAtualLog(Logs atualLog) {
        atualLog.setSaldo(atualLog.getReceita() - atualLog.getDespesa());
        if (atualLog.getSaldo() >= 0){
            atualLog.setPositivo(true);
        } else {
            atualLog.setPositivo(false);
        }
        this.atualLog = atualLog;
    }

    public String getRegStr() {
        return regStr;
    }

    public void setRegStr(String regStr) {
        this.regStr = regStr;
    }

    public String getPgtoStr() {
        return pgtoStr;
    }

    public void setPgtoStr(String pgtoStr) {
        this.pgtoStr = pgtoStr;
    }

    public String getParcelaStr() {
        return parcelaStr;
    }

    public void setParcelaStr(String parcelaStr) {
        this.parcelaStr = parcelaStr;
    }

    public Boolean getOculted() {
        return oculted;
    }

    public void setOculted(Boolean oculted) {
        this.oculted = oculted;
    }
    
    @XmlTransient
    public Collection<Logs> getLogsCollection() {
        return logsCollection;
    }

    public void setLogsCollection(Collection<Logs> logsCollection) {
        this.logsCollection = logsCollection;
    }

    @XmlTransient
    public Collection<Parcelas> getParcelasCollection() {
        return parcelasCollection;
    }

    public void setParcelasCollection(Collection<Parcelas> parcelasCollection) {
        this.parcelasCollection = parcelasCollection;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (movId != null ? movId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Movements)) {
            return false;
        }
        Movements other = (Movements) object;
        if ((this.movId == null && other.movId != null) || (this.movId != null && !this.movId.equals(other.movId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entity.Movements[ movId=" + movId + " ]";
    }
}
