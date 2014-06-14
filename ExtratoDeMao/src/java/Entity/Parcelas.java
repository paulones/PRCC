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
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Pedro
 */
@Entity
@Table(name = "Parcelas")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Parcelas.findAll", query = "SELECT p FROM Parcelas p"),
    @NamedQuery(name = "Parcelas.findByParcId", query = "SELECT p FROM Parcelas p WHERE p.parcId = :parcId"),
    @NamedQuery(name = "Parcelas.findByNumParc", query = "SELECT p FROM Parcelas p WHERE p.numParc = :numParc"),
    @NamedQuery(name = "Parcelas.findByValor", query = "SELECT p FROM Parcelas p WHERE p.valor = :valor"),
    @NamedQuery(name = "Parcelas.findByPago", query = "SELECT p FROM Parcelas p WHERE p.pago = :pago"),
    @NamedQuery(name = "Parcelas.findByData", query = "SELECT p FROM Parcelas p WHERE p.data = :data")})
public class Parcelas implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "parc_id")
    private Integer parcId;
    @Basic(optional = false)
    @Column(name = "num_parc")
    private int numParc;
    @Basic(optional = false)
    @Column(name = "valor")
    private double valor;
    @Basic(optional = false)
    @Column(name = "pago")
    private boolean pago;
    @Basic(optional = false)
    @Column(name = "data")
    @Temporal(TemporalType.DATE)
    private Date data;
    @JoinColumn(name = "mov_id", referencedColumnName = "mov_id")
    @ManyToOne(optional = false)
    private Movements movId;

    public Parcelas() {
    }

    public Parcelas(Integer parcId) {
        this.parcId = parcId;
    }

    public Parcelas(Integer parcId, int numParc, double valor, boolean pago, Date data) {
        this.parcId = parcId;
        this.numParc = numParc;
        this.valor = valor;
        this.pago = pago;
        this.data = data;
    }

    public Integer getParcId() {
        return parcId;
    }

    public void setParcId(Integer parcId) {
        this.parcId = parcId;
    }

    public int getNumParc() {
        return numParc;
    }

    public void setNumParc(int numParc) {
        this.numParc = numParc;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public boolean getPago() {
        return pago;
    }

    public void setPago(boolean pago) {
        this.pago = pago;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public Movements getMovId() {
        return movId;
    }

    public void setMovId(Movements movId) {
        this.movId = movId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (parcId != null ? parcId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Parcelas)) {
            return false;
        }
        Parcelas other = (Parcelas) object;
        if ((this.parcId == null && other.parcId != null) || (this.parcId != null && !this.parcId.equals(other.parcId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entity.Parcelas[ parcId=" + parcId + " ]";
    }
    
}
