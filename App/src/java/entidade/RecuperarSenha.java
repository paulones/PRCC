/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entidade;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Paulo
 */
@Entity
@Table(name = "recuperar_senha")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RecuperarSenha.findAll", query = "SELECT r FROM RecuperarSenha r"),
    @NamedQuery(name = "RecuperarSenha.findByUsuarioFk", query = "SELECT r FROM RecuperarSenha r WHERE r.usuarioFk = :usuarioFk"),
    @NamedQuery(name = "RecuperarSenha.findByCodigo", query = "SELECT r FROM RecuperarSenha r WHERE r.codigo = :codigo")})
public class RecuperarSenha implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "usuario_fk")
    private Long usuarioFk;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "codigo")
    private String codigo;
    @JoinColumn(name = "usuario_fk", referencedColumnName = "cpf", insertable = false, updatable = false)
    @OneToOne(optional = false)
    private Usuario usuario;

    public RecuperarSenha() {
    }

    public RecuperarSenha(Long usuarioFk) {
        this.usuarioFk = usuarioFk;
    }

    public RecuperarSenha(Long usuarioFk, String codigo) {
        this.usuarioFk = usuarioFk;
        this.codigo = codigo;
    }

    public Long getUsuarioFk() {
        return usuarioFk;
    }

    public void setUsuarioFk(Long usuarioFk) {
        this.usuarioFk = usuarioFk;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (usuarioFk != null ? usuarioFk.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RecuperarSenha)) {
            return false;
        }
        RecuperarSenha other = (RecuperarSenha) object;
        if ((this.usuarioFk == null && other.usuarioFk != null) || (this.usuarioFk != null && !this.usuarioFk.equals(other.usuarioFk))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidade.RecuperarSenha[ usuarioFk=" + usuarioFk + " ]";
    }
    
}
