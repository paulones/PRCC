/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;

import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author PRCC
 */
@ManagedBean(name = "loginBean")
@SessionScoped
public class LoginBean implements Serializable {

    private boolean success;
    private boolean fail;
    
    public void init() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            fail = false;
            success = false;
        } 
    }

    public void login() {
        fail = true;
        success = false; 
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Usuário não existe.", null));
    }
    
    public void recoverPassword(){
        success = true;
        fail = false;
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Solicitação enviada. Verifique seu e-mail.", null));
        //FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro ao tentar enviar e-mail. Tente novamente.", null));
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isFail() {
        return fail;
    }

    public void setFail(boolean fail) {
        this.fail = fail;
    }

}
