/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;

import bo.LoginBO;
import bo.UsuarioBO;
import entidade.RecuperarSenha;
import entidade.Usuario;
import java.io.IOException;
import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import util.EnviarEmail;
import util.GeradorMD5;
import util.ValidaCPF;

/**
 *
 * @author PRCC
 */
@ManagedBean(name = "loginBean")
@SessionScoped
public class LoginBean implements Serializable {

    private String nome;
    private String cpf;
    private String senha;
    private String email;
    private String mensagem;
    private Usuario usuario;
    private UsuarioBO usuarioBO;
    private LoginBO loginBO;
    private String licenca;

    public void init() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            usuarioBO = new UsuarioBO();
            loginBO = new LoginBO();
            usuario = new Usuario();
            mensagem = "";
            nome = "";
            email = "";
            cpf = "";
            licenca = "";
        }
    }

    public void login() throws IOException {
        if (!loginBO.expirado()){
        usuario = usuarioBO.findUsuario(Long.valueOf(cpf.replace(".", "").replace("-", "")));
        senha = GeradorMD5.generate(senha);
        if (usuario != null) {
            if (senha.equals(usuario.getSenha())) {
                FacesContext.getCurrentInstance().getExternalContext().redirect("/index.xhtml");
            } else {
                mensagem = "loginFail";
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Senha incorreta.", null));
            }
        } else {
            mensagem = "loginFail";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Usuário não existe.", null));
        }
        } else {
            mensagem = "loginFail";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Tempo da conta expirou, adquira outra licença!", null));
        }
    }

    public void recuperarSenha() {
        try {
            //FAZER QUANDO COMPRAR O DEDICADO
            RecuperarSenha rp = new RecuperarSenha();
            String accountActivation = "App - Ativar Conta";
            String mailtext = "Olá!\n\nObrigado pelo seu interesse em se registrar no App.\n\nPara concluir o processo, será preciso que você clique no link abaixo para ativar sua conta.\n\n";
            mailtext += "http://procuradoriapp.prcc.com.br/login.xhtml?code=" + rp.getCodigo() + "&cpf=" + usuario.getCpf();
            mailtext += "\n\nAtenciosamente,\n\nPRCC - Gestão em TI e negócios.";
            EnviarEmail.enviar(mailtext, accountActivation, email);
            mensagem = "forgetSuccess";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Solicitação enviada. Verifique seu e-mail.", null));
            //-------
        } catch (Exception e) {
            e.printStackTrace();
            mensagem = "forgetFail";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Ocorreu um erro ao tentar enviar e-mail de recuperação de senha. Tente novamente.", null));
        }
    }

    public void registrar() {
        cpf = this.cpf.replace(".", "").replace("-", "");
        if (ValidaCPF.isCPF(cpf)) {
            Long cpf = Long.valueOf(this.cpf);
            usuario.setCpf(cpf);
            usuario.setNome(nome);
            usuario.setEmail(email);
            usuario.setSenha(GeradorMD5.generate(senha));
            if (usuarioBO.findUsuario(cpf) == null) {
                usuarioBO.create(usuario);
                mensagem = "registerSuccess";
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Usuário registrado com sucesso!", null));
                nome = "";
                email = "";
                this.cpf = "";
            } else {
                mensagem = "registerFail";
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Já existe um usuário com o CPF " + this.cpf + " cadastrado.", null));
                this.cpf = "";
            }
        } else {
            mensagem = "registerFail";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Por favor, digite um CPF válido.", null));
            this.cpf = "";
        }
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

}
