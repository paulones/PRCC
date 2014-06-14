/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BO;

import Entity.Extracts;
import Entity.Logs;
import Entity.Movements;
import Entity.Users;
import JPAControllers.LogsJpaController;
import JPAControllers.MovementsJpaController;
import JPAControllers.exceptions.PreexistingEntityException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Pedro
 */
public class ExtractBO implements Serializable {

    public List<Movements> getMovements(Date date, Extracts extract) {
        MovementsJpaController jpa = new MovementsJpaController();
        List<Movements> moves = jpa.findAtualMoves(date,extract);
        return moves;
    }

    public Movements getMovement(String desc) {
        MovementsJpaController jpa = new MovementsJpaController();
        return jpa.findMovements(desc);
    }

    public void createMovement(Movements moves) throws PreexistingEntityException, Exception {
        MovementsJpaController jpa = new MovementsJpaController();
        jpa.create(moves);
    }

    public void editMovement(Movements moves) throws PreexistingEntityException, Exception {
        MovementsJpaController jpa = new MovementsJpaController();
        jpa.edit(moves);
    }

    public void createLog(Logs log) throws PreexistingEntityException, Exception {
        LogsJpaController jpa = new LogsJpaController();
        jpa.create(log);
    }

    public void editLog(Logs log) throws PreexistingEntityException, Exception {
        LogsJpaController jpa = new LogsJpaController();
        jpa.edit(log);
    }

    public void deleteMovement(Movements moves) throws PreexistingEntityException, Exception {
        LogsJpaController ljpa = new LogsJpaController();
        //Pega os logs referentes ao movimento
        List<Logs> logsList = new ArrayList<Logs>(moves.getLogsCollection());
        //Corre os logs do movimento
        for (int j = 0; j < logsList.size(); j++) {
            ljpa.destroy(logsList.get(j).getLogId());
        }
        MovementsJpaController jpa = new MovementsJpaController();
        jpa.destroy(moves.getMovId());
    }
}
