/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Pedro
 */
@Entity
@Table(name = "Logs")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Logs.findAll", query = "SELECT l FROM Logs l"),
    @NamedQuery(name = "Logs.findByLogId", query = "SELECT l FROM Logs l WHERE l.logId = :logId"),
    @NamedQuery(name = "Logs.findByDataReg", query = "SELECT l FROM Logs l WHERE l.dataReg = :dataReg"),
    @NamedQuery(name = "Logs.findByReceita", query = "SELECT l FROM Logs l WHERE l.receita = :receita"),
    @NamedQuery(name = "Logs.findByDespesa", query = "SELECT l FROM Logs l WHERE l.despesa = :despesa"),
    @NamedQuery(name = "Logs.findByParcelas", query = "SELECT l FROM Logs l WHERE l.parcelas = :parcelas"),
    @NamedQuery(name = "Logs.findByAtivo", query = "SELECT l FROM Logs l WHERE l.ativo = :ativo"),
    @NamedQuery(name = "Logs.findByStatus", query = "SELECT l FROM Logs l WHERE l.status = :status"),
    @NamedQuery(name = "Logs.findByPago", query = "SELECT l FROM Logs l WHERE l.pago = :pago")})
public class Logs implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "log_id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer logId;
    @Basic(optional = false)
    @Column(name = "data_reg")
    @Temporal(TemporalType.DATE)
    private Date dataReg;
    @Basic(optional = false)
    @Column(name = "receita")
    private double receita;
    @Basic(optional = false)
    @Column(name = "despesa")
    private double despesa;
    @Basic(optional = false)
    @Column(name = "parcelas")
    private int parcelas;
    @Basic(optional = false)
    @Column(name = "ativo")
    private boolean ativo;
    @Basic(optional = false)
    @Column(name = "status")
    private int status;
    @Basic(optional = false)
    @Column(name = "pago")
    private boolean pago;
    @JoinColumn(name = "mov_id", referencedColumnName = "mov_id")
    @ManyToOne(optional = false)
    private Movements movId;
    private transient double saldo;
    private transient Boolean positivo = false;

    public Logs() {
    }

    public Logs(Integer logId) {
        this.logId = logId;
    }

    public Logs(Integer logId, Date dataReg, double receita, double despesa, int parcelas, boolean ativo, int status, boolean pago) {
        this.logId = logId;
        this.dataReg = dataReg;
        this.receita = receita;
        this.despesa = despesa;
        this.parcelas = parcelas;
        this.ativo = ativo;
        this.status = status;
        this.pago = pago;


    }

    public Integer getLogId() {
        return logId;
    }

    public void setLogId(Integer logId) {
        this.logId = logId;
    }

    public Date getDataReg() {
        return dataReg;
    }

    public void setDataReg(Date dataReg) {
        this.dataReg = dataReg;
    }

    public double getReceita() {
        return receita;
    }

    public void setReceita(double receita) {
        this.receita = receita;
    }

    public double getDespesa() {
        return despesa;
    }

    public void setDespesa(double despesa) {
        this.despesa = despesa;
    }

    public int getParcelas() {
        return parcelas;
    }

    public void setParcelas(int parcelas) {
        this.parcelas = parcelas;
    }

    public boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean getPago() {
        return pago;
    }

    public void setPago(boolean pago) {
        this.pago = pago;
    }

    public Movements getMovId() {
        return movId;
    }

    public void setMovId(Movements movId) {
        this.movId = movId;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public Boolean getPositivo() {
        return positivo;
    }

    public void setPositivo(Boolean positivo) {
        this.positivo = positivo;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (logId != null ? logId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Logs)) {
            return false;
        }
        Logs other = (Logs) object;
        if ((this.logId == null && other.logId != null) || (this.logId != null && !this.logId.equals(other.logId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entity.Logs[ logId=" + logId + " ]";
    }
}
