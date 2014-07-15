/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bean;

import java.io.Serializable;
import javax.faces.bean.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author PRCC
 */
@Named
@SessionScoped
public class LoginBean implements Serializable{
    
    
    public void submit(){
        System.out.println("teste"); 
    }
}
