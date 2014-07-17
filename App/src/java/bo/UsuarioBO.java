/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bo;

import dao.UsuarioDAO;
import entidade.Usuario;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author PRCC
 */
public class UsuarioBO implements Serializable {
    
    private UsuarioDAO usuarioDAO;
    
    public UsuarioBO(){
        usuarioDAO = new UsuarioDAO();
    }

    public void create(Usuario usuario) {
        try {
            usuarioDAO.create(usuario);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Usuario findUsuario(Long cpf) {
        try { 
            return usuarioDAO.findUsuario(cpf);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Usuario();
    }

    public void edit(Usuario usuario) {
        try {
            usuarioDAO.edit(usuario);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
