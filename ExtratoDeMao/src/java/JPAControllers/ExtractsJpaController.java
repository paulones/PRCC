/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package JPAControllers;

import Entity.Extracts;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Entity.Users;
import Entity.Movements;
import JPAControllers.exceptions.IllegalOrphanException;
import JPAControllers.exceptions.NonexistentEntityException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

/**
 *
 * @author ppccardoso
 */
public class ExtractsJpaController implements Serializable {

    public ExtractsJpaController() {
        this.emf = emf;
    }
    private EntityManagerFactory emf = JPAUtil.getEMF();

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Extracts extracts) {
        if (extracts.getMovementsCollection() == null) {
            extracts.setMovementsCollection(new ArrayList<Movements>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Users userId = extracts.getUserId();
            if (userId != null) {
                userId = em.getReference(userId.getClass(), userId.getUserId());
                extracts.setUserId(userId);
            }
            Collection<Movements> attachedMovementsCollection = new ArrayList<Movements>();
            for (Movements movementsCollectionMovementsToAttach : extracts.getMovementsCollection()) {
                movementsCollectionMovementsToAttach = em.getReference(movementsCollectionMovementsToAttach.getClass(), movementsCollectionMovementsToAttach.getMovId());
                attachedMovementsCollection.add(movementsCollectionMovementsToAttach);
            }
            extracts.setMovementsCollection(attachedMovementsCollection);
            em.persist(extracts);
            if (userId != null) {
                userId.getExtratsCollection().add(extracts);
                userId = em.merge(userId);
            }
            for (Movements movementsCollectionMovements : extracts.getMovementsCollection()) {
                Extracts oldExtractIdOfMovementsCollectionMovements = movementsCollectionMovements.getExtractId();
                movementsCollectionMovements.setExtractId(extracts);
                movementsCollectionMovements = em.merge(movementsCollectionMovements);
                if (oldExtractIdOfMovementsCollectionMovements != null) {
                    oldExtractIdOfMovementsCollectionMovements.getMovementsCollection().remove(movementsCollectionMovements);
                    oldExtractIdOfMovementsCollectionMovements = em.merge(oldExtractIdOfMovementsCollectionMovements);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Extracts extracts) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Extracts persistentExtracts = em.find(Extracts.class, extracts.getExtractId());
            Users userIdOld = persistentExtracts.getUserId();
            Users userIdNew = extracts.getUserId();
            Collection<Movements> movementsCollectionOld = persistentExtracts.getMovementsCollection();
            Collection<Movements> movementsCollectionNew = extracts.getMovementsCollection();
            List<String> illegalOrphanMessages = null;
            for (Movements movementsCollectionOldMovements : movementsCollectionOld) {
                if (!movementsCollectionNew.contains(movementsCollectionOldMovements)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Movements " + movementsCollectionOldMovements + " since its extractId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (userIdNew != null) {
                userIdNew = em.getReference(userIdNew.getClass(), userIdNew.getUserId());
                extracts.setUserId(userIdNew);
            }
            Collection<Movements> attachedMovementsCollectionNew = new ArrayList<Movements>();
            for (Movements movementsCollectionNewMovementsToAttach : movementsCollectionNew) {
                movementsCollectionNewMovementsToAttach = em.getReference(movementsCollectionNewMovementsToAttach.getClass(), movementsCollectionNewMovementsToAttach.getMovId());
                attachedMovementsCollectionNew.add(movementsCollectionNewMovementsToAttach);
            }
            movementsCollectionNew = attachedMovementsCollectionNew;
            extracts.setMovementsCollection(movementsCollectionNew);
            extracts = em.merge(extracts);
            if (userIdOld != null && !userIdOld.equals(userIdNew)) {
                userIdOld.getExtratsCollection().remove(extracts);
                userIdOld = em.merge(userIdOld);
            }
            if (userIdNew != null && !userIdNew.equals(userIdOld)) {
                userIdNew.getExtratsCollection().add(extracts);
                userIdNew = em.merge(userIdNew);
            }
            for (Movements movementsCollectionNewMovements : movementsCollectionNew) {
                if (!movementsCollectionOld.contains(movementsCollectionNewMovements)) {
                    Extracts oldExtractIdOfMovementsCollectionNewMovements = movementsCollectionNewMovements.getExtractId();
                    movementsCollectionNewMovements.setExtractId(extracts);
                    movementsCollectionNewMovements = em.merge(movementsCollectionNewMovements);
                    if (oldExtractIdOfMovementsCollectionNewMovements != null && !oldExtractIdOfMovementsCollectionNewMovements.equals(extracts)) {
                        oldExtractIdOfMovementsCollectionNewMovements.getMovementsCollection().remove(movementsCollectionNewMovements);
                        oldExtractIdOfMovementsCollectionNewMovements = em.merge(oldExtractIdOfMovementsCollectionNewMovements);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = extracts.getExtractId();
                if (findExtracts(id) == null) {
                    throw new NonexistentEntityException("The extracts with id " + id + " no longer exists.");
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
            Extracts extracts;
            try {
                extracts = em.getReference(Extracts.class, id);
                extracts.getExtractId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The extracts with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Movements> movementsCollectionOrphanCheck = extracts.getMovementsCollection();
            for (Movements movementsCollectionOrphanCheckMovements : movementsCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Extracts (" + extracts + ") cannot be destroyed since the Movements " + movementsCollectionOrphanCheckMovements + " in its movementsCollection field has a non-nullable extractId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Users userId = extracts.getUserId();
            if (userId != null) {
                userId.getExtratsCollection().remove(extracts);
                userId = em.merge(userId);
            }
            em.remove(extracts);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Extracts> findExtractsEntities() {
        return findExtractsEntities(true, -1, -1);
    }

    public List<Extracts> findExtractsEntities(int maxResults, int firstResult) {
        return findExtractsEntities(false, maxResults, firstResult);
    }

    private List<Extracts> findExtractsEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Extracts.class));
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

    public Extracts findExtracts(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Extracts.class, id);
        } finally {
            em.close();
        }
    }

    public int getExtractsCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Extracts> rt = cq.from(Extracts.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
    public List<Extracts> findExtractsByUser(Users user) {
        EntityManager em = getEntityManager();
        List<Extracts> list = new ArrayList<Extracts>();
        System.out.println("user id: "+user.getUserId());
        try {
            TypedQuery<Extracts> query = em.createNamedQuery("Extracts.findByUser", Extracts.class);
            list = query.setParameter("id", user).getResultList();
        } catch (Exception e) {
            System.out.println("findAtualMoves e:> " + e);
        } finally {
            em.close();
            return list;
        }
    }
    
}
