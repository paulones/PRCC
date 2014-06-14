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
import Entity.Movements;
import Entity.Parcelas;
import JPAControllers.exceptions.NonexistentEntityException;
import JPAControllers.exceptions.PreexistingEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Pedro
 */
public class ParcelasJpaController implements Serializable {

    public ParcelasJpaController() {
        this.emf = emf;
    }
    private EntityManagerFactory emf = JPAUtil.getEMF();

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Parcelas parcelas) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Movements movId = parcelas.getMovId();
            if (movId != null) {
                movId = em.getReference(movId.getClass(), movId.getMovId());
                parcelas.setMovId(movId);
            }
            em.persist(parcelas);
            if (movId != null) {
                movId.getParcelasCollection().add(parcelas);
                movId = em.merge(movId);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findParcelas(parcelas.getParcId()) != null) {
                throw new PreexistingEntityException("Parcelas " + parcelas + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Parcelas parcelas) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Parcelas persistentParcelas = em.find(Parcelas.class, parcelas.getParcId());
            Movements movIdOld = persistentParcelas.getMovId();
            Movements movIdNew = parcelas.getMovId();
            if (movIdNew != null) {
                movIdNew = em.getReference(movIdNew.getClass(), movIdNew.getMovId());
                parcelas.setMovId(movIdNew);
            }
            parcelas = em.merge(parcelas);
            if (movIdOld != null && !movIdOld.equals(movIdNew)) {
                movIdOld.getParcelasCollection().remove(parcelas);
                movIdOld = em.merge(movIdOld);
            }
            if (movIdNew != null && !movIdNew.equals(movIdOld)) {
                movIdNew.getParcelasCollection().add(parcelas);
                movIdNew = em.merge(movIdNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = parcelas.getParcId();
                if (findParcelas(id) == null) {
                    throw new NonexistentEntityException("The parcelas with id " + id + " no longer exists.");
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
            Parcelas parcelas;
            try {
                parcelas = em.getReference(Parcelas.class, id);
                parcelas.getParcId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The parcelas with id " + id + " no longer exists.", enfe);
            }
            Movements movId = parcelas.getMovId();
            if (movId != null) {
                movId.getParcelasCollection().remove(parcelas);
                movId = em.merge(movId);
            }
            em.remove(parcelas);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Parcelas> findParcelasEntities() {
        return findParcelasEntities(true, -1, -1);
    }

    public List<Parcelas> findParcelasEntities(int maxResults, int firstResult) {
        return findParcelasEntities(false, maxResults, firstResult);
    }

    private List<Parcelas> findParcelasEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Parcelas.class));
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

    public Parcelas findParcelas(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Parcelas.class, id);
        } finally {
            em.close();
        }
    }

    public int getParcelasCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Parcelas> rt = cq.from(Parcelas.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
