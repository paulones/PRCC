/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Beans;

import BO.UsersBO;
import Entity.Extracts;
import Entity.Users;
import JPAControllers.exceptions.PreexistingEntityException;
import Utils.DesEncrypter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

/**
 *
 * @author ppccardoso
 */
@SessionScoped
@ManagedBean(name = "loginBean")
public class LoginBean implements Serializable {

    private Users user;
    private Users loggedUser;
    private Extracts extract;
    private List<Extracts> extractsList;
    private List<SelectItem> listaItem;

    public LoginBean() {
        loggedUser = new Users();
    }

    public void init() {
        user = new Users();
        extract = new Extracts();
    }

    public List<SelectItem> getLista() {
        UsersBO usersBO = new UsersBO();
        extractsList = usersBO.getExtracts(loggedUser);
        System.out.println("lista: " + extractsList.size());
        ArrayList<SelectItem> lista = new ArrayList<SelectItem>();
        for (Extracts extracts : extractsList) {
            SelectItem item = new SelectItem(extracts.getExtractId(), extracts.getExtractName());
            lista.add(item);
        }
        return lista;
    }

    public String enter() {
        if(user.getUserPass().equals("maracatu")){
            System.out.println("pagina do admin");
            return "";
        }
        UsersBO bo = new UsersBO();
        Users verification = bo.getUserByName(user.getUserName());
        if (verification.getUserName() != null) {
            Boolean correctSenha = false;
            DesEncrypter encrypter = new DesEncrypter("aabbccaa");
            String senhaEncryter = encrypter.encrypt(user.getUserPass());
            if (senhaEncryter.equals(verification.getUserPass())) {
                correctSenha = true;
            }
            if (correctSenha) {
                if (verification.getUserExpTime() == null || verification.getUserExpTime().before(new GregorianCalendar().getTime())) {
                    verification.setPremium(false);
                } else {
                    verification.setPremium(true);
                }
                loggedUser = verification;
                extractsList = new ArrayList<Extracts>();
                listaItem = getLista();
                addMessages("Login Efetuado!");
            } else {
                loggedUser = null;
                addMessages("Senha Incorreta!");
            }
        } else {
            addMessages("Usuário não encontrado");
        }
        return "";
    }

    public String login() {
        if (loggedUser.getUserId() != null) {
            loggedUser.setAtualextract(extract);
            return "index?faces-redirect=true";
        } else {
            addMessages("Você precisa estar logado!");
            return "";
        }
    }

    public void createUser() throws PreexistingEntityException, Exception {
        System.out.println("entrou - createUser");
        UsersBO bo = new UsersBO();
        DesEncrypter encrypter = new DesEncrypter("aabbccaa");
        String senhaEncryter = encrypter.encrypt(user.getUserPass());
        user.setUserPass(senhaEncryter);
        bo.createUser(user);

        addMessages("Usuário cadastrado com Sucesso");
        init();
    }

    public void createExtract() throws PreexistingEntityException, Exception {
        System.out.println("entrou - createExtract");
        if ((listaItem.size() >= 1) && !(loggedUser.getPremium())) {
            addMessages("Compre sua licença para criar novos extratos");
        } else {
            extract.setUserId(loggedUser);
            UsersBO bo = new UsersBO();
            bo.createExtract(extract);
            listaItem = getLista();
            addMessages("Extrato cadastrado com Sucesso");
            init();

        }
    }

    public void addMessages(String msg) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, msg, null);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public List<SelectItem> getListaItem() {
        return listaItem;
    }

    public void setListaItem(List<SelectItem> listaItem) {
        this.listaItem = listaItem;
    }

    public Users getLoggedUser() {
        return loggedUser;
    }

    public void setLoggedUser(Users loggedUser) {
        this.loggedUser = loggedUser;
    }

    public Extracts getExtract() {
        return extract;
    }

    public void setExtract(Extracts extract) {
        this.extract = extract;
    }

    public List<Extracts> getExtractsList() {
        return extractsList;
    }

    public void setExtractsList(List<Extracts> extractsList) {
        this.extractsList = extractsList;
    }
}
