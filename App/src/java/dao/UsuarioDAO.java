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
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entidade.RecuperarSenha;
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
public class UsuarioDAO implements Serializable {

    public UsuarioDAO() {
    }
    
    private transient EntityManagerFactory emf = JPAUtil.getEMF();

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Usuario usuario) throws PreexistingEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RecuperarSenha recuperarSenha = usuario.getRecuperarSenha();
            if (recuperarSenha != null) {
                recuperarSenha = em.getReference(recuperarSenha.getClass(), recuperarSenha.getUsuarioFk());
                usuario.setRecuperarSenha(recuperarSenha);
            }
            em.persist(usuario);
            if (recuperarSenha != null) {
                Usuario oldUsuarioOfRecuperarSenha = recuperarSenha.getUsuario();
                if (oldUsuarioOfRecuperarSenha != null) {
                    oldUsuarioOfRecuperarSenha.setRecuperarSenha(null);
                    oldUsuarioOfRecuperarSenha = em.merge(oldUsuarioOfRecuperarSenha);
                }
                recuperarSenha.setUsuario(usuario);
                recuperarSenha = em.merge(recuperarSenha);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            try {
                em.getTransaction().rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findUsuario(usuario.getCpf()) != null) {
                throw new PreexistingEntityException("Usuario " + usuario + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Usuario usuario) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuario persistentUsuario = em.find(Usuario.class, usuario.getCpf());
            RecuperarSenha recuperarSenhaOld = persistentUsuario.getRecuperarSenha();
            RecuperarSenha recuperarSenhaNew = usuario.getRecuperarSenha();
            List<String> illegalOrphanMessages = null;
            if (recuperarSenhaOld != null && !recuperarSenhaOld.equals(recuperarSenhaNew)) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("You must retain RecuperarSenha " + recuperarSenhaOld + " since its usuario field is not nullable.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (recuperarSenhaNew != null) {
                recuperarSenhaNew = em.getReference(recuperarSenhaNew.getClass(), recuperarSenhaNew.getUsuarioFk());
                usuario.setRecuperarSenha(recuperarSenhaNew);
            }
            usuario = em.merge(usuario);
            if (recuperarSenhaNew != null && !recuperarSenhaNew.equals(recuperarSenhaOld)) {
                Usuario oldUsuarioOfRecuperarSenha = recuperarSenhaNew.getUsuario();
                if (oldUsuarioOfRecuperarSenha != null) {
                    oldUsuarioOfRecuperarSenha.setRecuperarSenha(null);
                    oldUsuarioOfRecuperarSenha = em.merge(oldUsuarioOfRecuperarSenha);
                }
                recuperarSenhaNew.setUsuario(usuario);
                recuperarSenhaNew = em.merge(recuperarSenhaNew);
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
                Long id = usuario.getCpf();
                if (findUsuario(id) == null) {
                    throw new NonexistentEntityException("The usuario with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Long id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuario usuario;
            try {
                usuario = em.getReference(Usuario.class, id);
                usuario.getCpf();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The usuario with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            RecuperarSenha recuperarSenhaOrphanCheck = usuario.getRecuperarSenha();
            if (recuperarSenhaOrphanCheck != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Usuario (" + usuario + ") cannot be destroyed since the RecuperarSenha " + recuperarSenhaOrphanCheck + " in its recuperarSenha field has a non-nullable usuario field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(usuario);
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

    public List<Usuario> findUsuarioEntities() {
        return findUsuarioEntities(true, -1, -1);
    }

    public List<Usuario> findUsuarioEntities(int maxResults, int firstResult) {
        return findUsuarioEntities(false, maxResults, firstResult);
    }

    private List<Usuario> findUsuarioEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Usuario.class));
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

    public Usuario findUsuario(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Usuario.class, id);
        } finally {
            em.close();
        }
    }

    public int getUsuarioCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Usuario> rt = cq.from(Usuario.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
}
