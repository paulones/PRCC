/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demonstration;

import java.io.Serializable;
import java.util.Date;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolationException;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

/**
 *
 * @author ramki
 */

@SessionScoped
@ManagedBean(name = "demoBean")
public class DemoBean implements Serializable{
    //@Inject Test test;
    
    
    @NotEmpty(message = "User Name Should not be Empty")
    private String name;
    
    @NotEmpty(message = "Password Should not be Empty")
    @Size(min = 5,message = "Give atleast 5 char")
    private String password;
    
 //   @NotNull (message = "DOB Should not be Empty")
  //  @Past
    private Date dob;
    
    @NotEmpty (message = "Email Should not be Empty")
    @Email
    private String email;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    
    
    public String submit(){
        try{
        
       // test.businesslogic(name,password,dob,email);
        }catch(ConstraintViolationException ex){
           //
            
            return null;
        }
        return "result?faces-redirect=true";
    }

   
    
    
}
