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

    private boolean loginFail;

    public void init() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            loginFail = false;
        }
    }

    public void submit() {
        loginFail = true;
    }

    public boolean isLoginFail() {
        return loginFail;
    }

    public void setLoginFail(boolean loginFail) {
        this.loginFail = loginFail;
    }

}
