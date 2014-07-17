/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;

import bo.UsuarioBO;
import entidade.Usuario;
import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author PRCC
 */
@ManagedBean(name = "loginBean")
@SessionScoped
public class LoginBean implements Serializable {

    private transient HttpServletRequest request;
    
    private Integer cpf;
    private String message;
    private UsuarioBO usuarioBO;

    public void init() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            usuarioBO = new UsuarioBO();
            message = "";
        }
    }
    public void login() {
        request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        
        cpf = Integer.valueOf(request.getParameter("cpf").replace(".", "").replace("-", ""));                         
        Usuario usuario = usuarioBO.findUsuario(cpf);
        if (usuario.getCpf() != null) {
            message = "forgetSuccess";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Login existe..", null));
        } else {
            message = "loginFail";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Usuário não existe.", null));
        }
    }

    public void recoverPassword() {
        message = "forgetSuccess";
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Solicitação enviada. Verifique seu e-mail.", null));
        //FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro ao tentar enviar e-mail. Tente novamente.", null));
    }

    public void register() {
        //message = "loginSuccess";
        message = "registerFail";
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro interno. Tente novamente.", null));
        //FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Usuário registrado com sucesso!", null));
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
