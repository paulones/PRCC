/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package JPAControllers;

import Entity.Logs;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Entity.Movements;
import JPAControllers.exceptions.NonexistentEntityException;
import JPAControllers.exceptions.PreexistingEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Pedro
 */
public class LogsJpaController implements Serializable {

    public LogsJpaController() {
        this.emf = emf;
    }
    private EntityManagerFactory emf = JPAUtil.getEMF();

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Logs logs) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Movements movId = logs.getMovId();
            if (movId != null) {
                movId = em.getReference(movId.getClass(), movId.getMovId());
                logs.setMovId(movId);
            }
            em.persist(logs);
            if (movId != null) {
                movId.getLogsCollection().add(logs);
                movId = em.merge(movId);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findLogs(logs.getLogId()) != null) {
                throw new PreexistingEntityException("Logs " + logs + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Logs logs) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Logs persistentLogs = em.find(Logs.class, logs.getLogId());
            Movements movIdOld = persistentLogs.getMovId();
            Movements movIdNew = logs.getMovId();
            if (movIdNew != null) {
                movIdNew = em.getReference(movIdNew.getClass(), movIdNew.getMovId());
                logs.setMovId(movIdNew);
            }
            logs = em.merge(logs);
            if (movIdOld != null && !movIdOld.equals(movIdNew)) {
                movIdOld.getLogsCollection().remove(logs);
                movIdOld = em.merge(movIdOld);
            }
            if (movIdNew != null && !movIdNew.equals(movIdOld)) {
                movIdNew.getLogsCollection().add(logs);
                movIdNew = em.merge(movIdNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = logs.getLogId();
                if (findLogs(id) == null) {
                    throw new NonexistentEntityException("The logs with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Logs logs;
            try {
                logs = em.getReference(Logs.class, id);
                logs.getLogId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The logs with id " + id + " no longer exists.", enfe);
            }
            Movements movId = logs.getMovId();
            if (movId != null) {
                movId.getLogsCollection().remove(logs);
                movId = em.merge(movId);
            }
            em.remove(logs);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Logs> findLogsEntities() {
        return findLogsEntities(true, -1, -1);
    }

    public List<Logs> findLogsEntities(int maxResults, int firstResult) {
        return findLogsEntities(false, maxResults, firstResult);
    }

    private List<Logs> findLogsEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Logs.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Logs findLogs(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Logs.class, id);
        } finally {
            em.close();
        }
    }

    public int getLogsCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Logs> rt = cq.from(Logs.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

    public void deleteMoveLogs(Integer movId) {
        EntityManager em = getEntityManager();
        try {
            Query query = em.createQuery("DELETE FROM Logs l WHERE l.mov_id = "+movId);
                    query.executeUpdate();

        } catch (Exception e) {
            System.out.println("e:> " + e);
        } finally {
            em.close();
        }
    }
}
