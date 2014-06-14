/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BO;

import Entity.Extracts;
import Entity.Users;
import JPAControllers.ExtractsJpaController;
import JPAControllers.UsersJpaController;
import JPAControllers.exceptions.PreexistingEntityException;
import java.util.List;

/**
 *
 * @author ppccardoso
 */
public class UsersBO {

    public Users getUser(Integer userId) {
        UsersJpaController jpa = new UsersJpaController();
        return jpa.findUsers(userId);
    }

    public Users getUserByName(String userName) {
        UsersJpaController jpa = new UsersJpaController();
        return jpa.findUserByName(userName);
    }

    public void createUser(Users user) throws PreexistingEntityException, Exception {
        UsersJpaController jpa = new UsersJpaController();
        jpa.create(user);
    }
    
    public void createExtract(Extracts extract) throws PreexistingEntityException, Exception {
        ExtractsJpaController jpa = new ExtractsJpaController();
        jpa.create(extract);
    }

    public Extracts getExtract(Integer extractId) {
        ExtractsJpaController jpa = new ExtractsJpaController();
        return jpa.findExtracts(extractId);
    }

    public List<Extracts> getExtracts(Users user) {
        ExtractsJpaController jpa = new ExtractsJpaController();
        return jpa.findExtractsByUser(user);
    }
}
