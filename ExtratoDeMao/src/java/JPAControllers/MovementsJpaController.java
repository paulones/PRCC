/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package JPAControllers;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Entity.Extracts;
import Entity.Logs;
import Entity.Movements;
import java.util.ArrayList;
import java.util.Collection;
import Entity.Parcelas;
import JPAControllers.exceptions.IllegalOrphanException;
import JPAControllers.exceptions.NonexistentEntityException;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

/**
 *
 * @author ppccardoso
 */
public class MovementsJpaController implements Serializable {

    public MovementsJpaController() {
        this.emf = emf;
    }
    private EntityManagerFactory emf = JPAUtil.getEMF();

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Movements movements) {
        if (movements.getLogsCollection() == null) {
            movements.setLogsCollection(new ArrayList<Logs>());
        }
        if (movements.getParcelasCollection() == null) {
            movements.setParcelasCollection(new ArrayList<Parcelas>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Extracts extractId = movements.getExtractId();
            if (extractId != null) {
                extractId = em.getReference(extractId.getClass(), extractId.getExtractId());
                movements.setExtractId(extractId);
            }
            Collection<Logs> attachedLogsCollection = new ArrayList<Logs>();
            for (Logs logsCollectionLogsToAttach : movements.getLogsCollection()) {
                logsCollectionLogsToAttach = em.getReference(logsCollectionLogsToAttach.getClass(), logsCollectionLogsToAttach.getLogId());
                attachedLogsCollection.add(logsCollectionLogsToAttach);
            }
            movements.setLogsCollection(attachedLogsCollection);
            Collection<Parcelas> attachedParcelasCollection = new ArrayList<Parcelas>();
            for (Parcelas parcelasCollectionParcelasToAttach : movements.getParcelasCollection()) {
                parcelasCollectionParcelasToAttach = em.getReference(parcelasCollectionParcelasToAttach.getClass(), parcelasCollectionParcelasToAttach.getParcId());
                attachedParcelasCollection.add(parcelasCollectionParcelasToAttach);
            }
            movements.setParcelasCollection(attachedParcelasCollection);
            em.persist(movements);
            if (extractId != null) {
                extractId.getMovementsCollection().add(movements);
                extractId = em.merge(extractId);
            }
            for (Logs logsCollectionLogs : movements.getLogsCollection()) {
                Movements oldMovIdOfLogsCollectionLogs = logsCollectionLogs.getMovId();
                logsCollectionLogs.setMovId(movements);
                logsCollectionLogs = em.merge(logsCollectionLogs);
                if (oldMovIdOfLogsCollectionLogs != null) {
                    oldMovIdOfLogsCollectionLogs.getLogsCollection().remove(logsCollectionLogs);
                    oldMovIdOfLogsCollectionLogs = em.merge(oldMovIdOfLogsCollectionLogs);
                }
            }
            for (Parcelas parcelasCollectionParcelas : movements.getParcelasCollection()) {
                Movements oldMovIdOfParcelasCollectionParcelas = parcelasCollectionParcelas.getMovId();
                parcelasCollectionParcelas.setMovId(movements);
                parcelasCollectionParcelas = em.merge(parcelasCollectionParcelas);
                if (oldMovIdOfParcelasCollectionParcelas != null) {
                    oldMovIdOfParcelasCollectionParcelas.getParcelasCollection().remove(parcelasCollectionParcelas);
                    oldMovIdOfParcelasCollectionParcelas = em.merge(oldMovIdOfParcelasCollectionParcelas);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Movements movements) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Movements persistentMovements = em.find(Movements.class, movements.getMovId());
            Extracts extractIdOld = persistentMovements.getExtractId();
            Extracts extractIdNew = movements.getExtractId();
            Collection<Logs> logsCollectionOld = persistentMovements.getLogsCollection();
            Collection<Logs> logsCollectionNew = movements.getLogsCollection();
            Collection<Parcelas> parcelasCollectionOld = persistentMovements.getParcelasCollection();
            Collection<Parcelas> parcelasCollectionNew = movements.getParcelasCollection();
            List<String> illegalOrphanMessages = null;
            for (Logs logsCollectionOldLogs : logsCollectionOld) {
                if (!logsCollectionNew.contains(logsCollectionOldLogs)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Logs " + logsCollectionOldLogs + " since its movId field is not nullable.");
                }
            }
            for (Parcelas parcelasCollectionOldParcelas : parcelasCollectionOld) {
                if (!parcelasCollectionNew.contains(parcelasCollectionOldParcelas)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Parcelas " + parcelasCollectionOldParcelas + " since its movId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (extractIdNew != null) {
                extractIdNew = em.getReference(extractIdNew.getClass(), extractIdNew.getExtractId());
                movements.setExtractId(extractIdNew);
            }
            Collection<Logs> attachedLogsCollectionNew = new ArrayList<Logs>();
            for (Logs logsCollectionNewLogsToAttach : logsCollectionNew) {
                logsCollectionNewLogsToAttach = em.getReference(logsCollectionNewLogsToAttach.getClass(), logsCollectionNewLogsToAttach.getLogId());
                attachedLogsCollectionNew.add(logsCollectionNewLogsToAttach);
            }
            logsCollectionNew = attachedLogsCollectionNew;
            movements.setLogsCollection(logsCollectionNew);
            Collection<Parcelas> attachedParcelasCollectionNew = new ArrayList<Parcelas>();
            for (Parcelas parcelasCollectionNewParcelasToAttach : parcelasCollectionNew) {
                parcelasCollectionNewParcelasToAttach = em.getReference(parcelasCollectionNewParcelasToAttach.getClass(), parcelasCollectionNewParcelasToAttach.getParcId());
                attachedParcelasCollectionNew.add(parcelasCollectionNewParcelasToAttach);
            }
            parcelasCollectionNew = attachedParcelasCollectionNew;
            movements.setParcelasCollection(parcelasCollectionNew);
            movements = em.merge(movements);
            if (extractIdOld != null && !extractIdOld.equals(extractIdNew)) {
                extractIdOld.getMovementsCollection().remove(movements);
                extractIdOld = em.merge(extractIdOld);
            }
            if (extractIdNew != null && !extractIdNew.equals(extractIdOld)) {
                extractIdNew.getMovementsCollection().add(movements);
                extractIdNew = em.merge(extractIdNew);
            }
            for (Logs logsCollectionNewLogs : logsCollectionNew) {
                if (!logsCollectionOld.contains(logsCollectionNewLogs)) {
                    Movements oldMovIdOfLogsCollectionNewLogs = logsCollectionNewLogs.getMovId();
                    logsCollectionNewLogs.setMovId(movements);
                    logsCollectionNewLogs = em.merge(logsCollectionNewLogs);
                    if (oldMovIdOfLogsCollectionNewLogs != null && !oldMovIdOfLogsCollectionNewLogs.equals(movements)) {
                        oldMovIdOfLogsCollectionNewLogs.getLogsCollection().remove(logsCollectionNewLogs);
                        oldMovIdOfLogsCollectionNewLogs = em.merge(oldMovIdOfLogsCollectionNewLogs);
                    }
                }
            }
            for (Parcelas parcelasCollectionNewParcelas : parcelasCollectionNew) {
                if (!parcelasCollectionOld.contains(parcelasCollectionNewParcelas)) {
                    Movements oldMovIdOfParcelasCollectionNewParcelas = parcelasCollectionNewParcelas.getMovId();
                    parcelasCollectionNewParcelas.setMovId(movements);
                    parcelasCollectionNewParcelas = em.merge(parcelasCollectionNewParcelas);
                    if (oldMovIdOfParcelasCollectionNewParcelas != null && !oldMovIdOfParcelasCollectionNewParcelas.equals(movements)) {
                        oldMovIdOfParcelasCollectionNewParcelas.getParcelasCollection().remove(parcelasCollectionNewParcelas);
                        oldMovIdOfParcelasCollectionNewParcelas = em.merge(oldMovIdOfParcelasCollectionNewParcelas);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = movements.getMovId();
                if (findMovements(id) == null) {
                    throw new NonexistentEntityException("The movements with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Movements movements;
            try {
                movements = em.getReference(Movements.class, id);
                movements.getMovId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The movements with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Logs> logsCollectionOrphanCheck = movements.getLogsCollection();
            for (Logs logsCollectionOrphanCheckLogs : logsCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Movements (" + movements + ") cannot be destroyed since the Logs " + logsCollectionOrphanCheckLogs + " in its logsCollection field has a non-nullable movId field.");
            }
            Collection<Parcelas> parcelasCollectionOrphanCheck = movements.getParcelasCollection();
            for (Parcelas parcelasCollectionOrphanCheckParcelas : parcelasCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Movements (" + movements + ") cannot be destroyed since the Parcelas " + parcelasCollectionOrphanCheckParcelas + " in its parcelasCollection field has a non-nullable movId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Extracts extractId = movements.getExtractId();
            if (extractId != null) {
                extractId.getMovementsCollection().remove(movements);
                extractId = em.merge(extractId);
            }
            em.remove(movements);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Movements> findMovementsEntities() {
        return findMovementsEntities(true, -1, -1);
    }

    public List<Movements> findMovementsEntities(int maxResults, int firstResult) {
        return findMovementsEntities(false, maxResults, firstResult);
    }

    private List<Movements> findMovementsEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Movements.class));
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

    public Movements findMovements(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Movements.class, id);
        } finally {
            em.close();
        }
    }

    public int getMovementsCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Movements> rt = cq.from(Movements.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
    public Movements findMovements(String desc) {
        EntityManager em = getEntityManager();
        Movements mov = new Movements();
        try {
            TypedQuery<Movements> query = em.createNamedQuery("Movements.findByMovDesc", Movements.class);
            mov = query.setParameter("movDesc", desc).getSingleResult();

        } catch (Exception e) {
            System.out.println("findMovements e:> " + e);
        } finally {
            em.close();
            return mov;
        }
    }
    
    public List<Movements> findAtualMoves(Date data, Extracts extract) {
        EntityManager em = getEntityManager();
        List<Movements> mov = new ArrayList<Movements>();
        try {
            TypedQuery<Movements> query = em.createNamedQuery("Movements.findGTDate", Movements.class);
            mov = query.setParameter("date", data).setParameter("id", extract).getResultList();
        } catch (Exception e) {
            System.out.println("findAtualMoves e:> " + e);
        } finally {
            em.close();
            return mov;
        }
    }
    
}
