/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import java.io.Serializable;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;



/**
 *
 * @author Pedro
 */
public class JPAUtil implements Serializable{
    
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("HandExtractPU2");
    
    public static EntityManagerFactory getEMF() {
        return emf;
    }
}
