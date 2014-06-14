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
import Entity.Users;
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
public class UsersJpaController implements Serializable {

    public UsersJpaController() {
        this.emf = emf;
    }
    private EntityManagerFactory emf = JPAUtil.getEMF();

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Users users) {
        if (users.getExtratsCollection() == null) {
            users.setExtratsCollection(new ArrayList<Extracts>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Extracts> attachedExtratsCollection = new ArrayList<Extracts>();
            for (Extracts extratsCollectionExtratsToAttach : users.getExtratsCollection()) {
                extratsCollectionExtratsToAttach = em.getReference(extratsCollectionExtratsToAttach.getClass(), extratsCollectionExtratsToAttach.getExtractId());
                attachedExtratsCollection.add(extratsCollectionExtratsToAttach);
            }
            users.setExtratsCollection(attachedExtratsCollection);
            em.persist(users);
            for (Extracts extratsCollectionExtrats : users.getExtratsCollection()) {
                Users oldUserIdOfExtratsCollectionExtrats = extratsCollectionExtrats.getUserId();
                extratsCollectionExtrats.setUserId(users);
                extratsCollectionExtrats = em.merge(extratsCollectionExtrats);
                if (oldUserIdOfExtratsCollectionExtrats != null) {
                    oldUserIdOfExtratsCollectionExtrats.getExtratsCollection().remove(extratsCollectionExtrats);
                    oldUserIdOfExtratsCollectionExtrats = em.merge(oldUserIdOfExtratsCollectionExtrats);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Users users) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Users persistentUsers = em.find(Users.class, users.getUserId());
            Collection<Extracts> extratsCollectionOld = persistentUsers.getExtratsCollection();
            Collection<Extracts> extratsCollectionNew = users.getExtratsCollection();
            List<String> illegalOrphanMessages = null;
            for (Extracts extratsCollectionOldExtrats : extratsCollectionOld) {
                if (!extratsCollectionNew.contains(extratsCollectionOldExtrats)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Extrats " + extratsCollectionOldExtrats + " since its userId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Extracts> attachedExtratsCollectionNew = new ArrayList<Extracts>();
            for (Extracts extratsCollectionNewExtratsToAttach : extratsCollectionNew) {
                extratsCollectionNewExtratsToAttach = em.getReference(extratsCollectionNewExtratsToAttach.getClass(), extratsCollectionNewExtratsToAttach.getExtractId());
                attachedExtratsCollectionNew.add(extratsCollectionNewExtratsToAttach);
            }
            extratsCollectionNew = attachedExtratsCollectionNew;
            users.setExtratsCollection(extratsCollectionNew);
            users = em.merge(users);
            for (Extracts extratsCollectionNewExtrats : extratsCollectionNew) {
                if (!extratsCollectionOld.contains(extratsCollectionNewExtrats)) {
                    Users oldUserIdOfExtratsCollectionNewExtrats = extratsCollectionNewExtrats.getUserId();
                    extratsCollectionNewExtrats.setUserId(users);
                    extratsCollectionNewExtrats = em.merge(extratsCollectionNewExtrats);
                    if (oldUserIdOfExtratsCollectionNewExtrats != null && !oldUserIdOfExtratsCollectionNewExtrats.equals(users)) {
                        oldUserIdOfExtratsCollectionNewExtrats.getExtratsCollection().remove(extratsCollectionNewExtrats);
                        oldUserIdOfExtratsCollectionNewExtrats = em.merge(oldUserIdOfExtratsCollectionNewExtrats);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = users.getUserId();
                if (findUsers(id) == null) {
                    throw new NonexistentEntityException("The users with id " + id + " no longer exists.");
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
            Users users;
            try {
                users = em.getReference(Users.class, id);
                users.getUserId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The users with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Extracts> extratsCollectionOrphanCheck = users.getExtratsCollection();
            for (Extracts extratsCollectionOrphanCheckExtrats : extratsCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Users (" + users + ") cannot be destroyed since the Extrats " + extratsCollectionOrphanCheckExtrats + " in its extratsCollection field has a non-nullable userId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(users);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Users> findUsersEntities() {
        return findUsersEntities(true, -1, -1);
    }

    public List<Users> findUsersEntities(int maxResults, int firstResult) {
        return findUsersEntities(false, maxResults, firstResult);
    }

    private List<Users> findUsersEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Users.class));
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

    public Users findUsers(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Users.class, id);
        } finally {
            em.close();
        }
    }

    public int getUsersCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Users> rt = cq.from(Users.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
    public Users findUserByName(String userName) {
        EntityManager em = getEntityManager();
        Users user = new Users();
        System.out.println("username = "+userName);
        try {
            TypedQuery<Users> query = em.createNamedQuery("Users.findByUserName", Users.class);
            user = query.setParameter("userName", userName).getSingleResult();
        } catch (Exception e) {
            System.out.println("findAtualMoves e:> " + e);
        } finally {
            em.close();
            return user;
        }
    }
    
}
