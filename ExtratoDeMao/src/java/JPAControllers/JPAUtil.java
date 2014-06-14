/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package JPAControllers;

import java.io.Serializable;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;



/**
 *
 * @author Pedro
 */
public class JPAUtil implements Serializable{
    
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("ExtratoDeMaoPU");
    
    public static EntityManagerFactory getEMF() {
        return emf;
    }
}
