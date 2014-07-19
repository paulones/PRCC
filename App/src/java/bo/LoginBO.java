/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bo;

import java.io.Serializable;
import util.Criptografia;

/**
 *
 * @author Pedro
 */
public class LoginBO implements Serializable {

    private String chaveTeste = "JNOrQqmuzs6Fupeoo124QEZLWHKF6zLc";
    private String descriptografia = "";

    public LoginBO (){
        
    }
    
    //Valida uma nova licença fornecida ao sistema
    public Boolean licenciado() {
        Boolean ok = false;
        try {
            //Consulta em uma persistênca a string de licença
            System.out.println("teste: "+chaveTeste);
            descriptografia = DesEncriptonator(chaveTeste);
            System.out.println("key: " + descriptografia);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ok;
    }

    //Verifica se o tempo da última licença já expirou
    public Boolean expirado() {
        Boolean ok = true;
        //Consulta em uma persistênca a string de licença
        System.out.println("teste: "+chaveTeste);
        descriptografia = DesEncriptonator(chaveTeste);
        System.out.println("descriptografia: "+descriptografia);
        return ok;
    }

    public String DesEncriptonator(String chave) {
        Criptografia encrypter = new Criptografia("aabbccaa");
        return encrypter.decrypt(chave);
    }

}
