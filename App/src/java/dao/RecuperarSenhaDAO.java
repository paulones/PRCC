/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dao;

import dao.exceptions.IllegalOrphanException;
import dao.exceptions.NonexistentEntityException;
import dao.exceptions.PreexistingEntityException;
import dao.exceptions.RollbackFailureException;
import entidade.RecuperarSenha;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entidade.Usuario;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author PRCC
 */
public class RecuperarSenhaDAO implements Serializable {

    public RecuperarSenhaDAO() {
    }
    
    private transient EntityManagerFactory emf = JPAUtil.getEMF();

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(RecuperarSenha recuperarSenha) throws IllegalOrphanException, PreexistingEntityException, RollbackFailureException, Exception {
        List<String> illegalOrphanMessages = null;
        Usuario usuarioOrphanCheck = recuperarSenha.getUsuario();
        if (usuarioOrphanCheck != null) {
            RecuperarSenha oldRecuperarSenhaOfUsuario = usuarioOrphanCheck.getRecuperarSenha();
            if (oldRecuperarSenhaOfUsuario != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("The Usuario " + usuarioOrphanCheck + " already has an item of type RecuperarSenha whose usuario column cannot be null. Please make another selection for the usuario field.");
            }
        }
        if (illegalOrphanMessages != null) {
            throw new IllegalOrphanException(illegalOrphanMessages);
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuario usuario = recuperarSenha.getUsuario();
            if (usuario != null) {
                usuario = em.getReference(usuario.getClass(), usuario.getCpf());
                recuperarSenha.setUsuario(usuario);
            }
            em.persist(recuperarSenha);
            if (usuario != null) {
                usuario.setRecuperarSenha(recuperarSenha);
                usuario = em.merge(usuario);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            try {
                em.getTransaction().rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findRecuperarSenha(recuperarSenha.getUsuarioFk()) != null) {
                throw new PreexistingEntityException("RecuperarSenha " + recuperarSenha + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(RecuperarSenha recuperarSenha) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RecuperarSenha persistentRecuperarSenha = em.find(RecuperarSenha.class, recuperarSenha.getUsuarioFk());
            Usuario usuarioOld = persistentRecuperarSenha.getUsuario();
            Usuario usuarioNew = recuperarSenha.getUsuario();
            List<String> illegalOrphanMessages = null;
            if (usuarioNew != null && !usuarioNew.equals(usuarioOld)) {
                RecuperarSenha oldRecuperarSenhaOfUsuario = usuarioNew.getRecuperarSenha();
                if (oldRecuperarSenhaOfUsuario != null) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("The Usuario " + usuarioNew + " already has an item of type RecuperarSenha whose usuario column cannot be null. Please make another selection for the usuario field.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (usuarioNew != null) {
                usuarioNew = em.getReference(usuarioNew.getClass(), usuarioNew.getCpf());
                recuperarSenha.setUsuario(usuarioNew);
            }
            recuperarSenha = em.merge(recuperarSenha);
            if (usuarioOld != null && !usuarioOld.equals(usuarioNew)) {
                usuarioOld.setRecuperarSenha(null);
                usuarioOld = em.merge(usuarioOld);
            }
            if (usuarioNew != null && !usuarioNew.equals(usuarioOld)) {
                usuarioNew.setRecuperarSenha(recuperarSenha);
                usuarioNew = em.merge(usuarioNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            try {
                em.getTransaction().rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = recuperarSenha.getUsuarioFk();
                if (findRecuperarSenha(id) == null) {
                    throw new NonexistentEntityException("The recuperarSenha with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Long id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RecuperarSenha recuperarSenha;
            try {
                recuperarSenha = em.getReference(RecuperarSenha.class, id);
                recuperarSenha.getUsuarioFk();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The recuperarSenha with id " + id + " no longer exists.", enfe);
            }
            Usuario usuario = recuperarSenha.getUsuario();
            if (usuario != null) {
                usuario.setRecuperarSenha(null);
                usuario = em.merge(usuario);
            }
            em.remove(recuperarSenha);
            em.getTransaction().commit();
        } catch (Exception ex) {
            try {
                em.getTransaction().rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<RecuperarSenha> findRecuperarSenhaEntities() {
        return findRecuperarSenhaEntities(true, -1, -1);
    }

    public List<RecuperarSenha> findRecuperarSenhaEntities(int maxResults, int firstResult) {
        return findRecuperarSenhaEntities(false, maxResults, firstResult);
    }

    private List<RecuperarSenha> findRecuperarSenhaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(RecuperarSenha.class));
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

    public RecuperarSenha findRecuperarSenha(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(RecuperarSenha.class, id);
        } finally {
            em.close();
        }
    }

    public int getRecuperarSenhaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<RecuperarSenha> rt = cq.from(RecuperarSenha.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
