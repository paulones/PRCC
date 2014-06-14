/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Beans;

import BO.ExtractBO;
import BO.UsersBO;
import Entity.Extracts;
import Entity.Logs;
import Entity.Movements;
import Entity.Users;
import JPAControllers.exceptions.PreexistingEntityException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import org.primefaces.event.SelectEvent;
import reports.RelatorioMensal;

/**
 *
 * @author Pedro
 */
@SessionScoped
@ManagedBean(name = "extractBean")
public class ExtractBean implements Serializable {

    //Variaveis de Navegação
    private Calendar calendar;
    private int mes;
    private int ano;
    private String mesStr;
    private String anoStr;
    //Variaveis de Extrato
    private Movements selectedMove;
    private List<Movements> movesList;
    private Movements newMove;
    private double SaldoTotal;
    private String saldoColor;
    private double ReceitaTotal;
    private double DespesaTotal;
    //variaveis de login
    private Users user;
    private Extracts extract;

    public ExtractBean() {
        System.out.println("Construct");
        selectedMove = new Movements();
        //init();
    }

    public void init() {
        UsersBO ubo = new UsersBO();
        LoginBean lb = (LoginBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("loginBean");
        user = lb.getLoggedUser();
        extract = ubo.getExtract(lb.getLoggedUser().getAtualextract().getExtractId());
        if (calendar == null) {
            calendar = new GregorianCalendar();
            atualMonth();
        }
        newMove = new Movements();
        if (movesList.isEmpty()) {
            getAtualMoves();
        }
        System.out.println("selected: " + selectedMove.getMovDesc());
    }

    public String logout() {
        LoginBean lb = (LoginBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("loginBean");
        lb.setLoggedUser(new Users());
        lb.setListaItem(new ArrayList<SelectItem>());
        return "login?faces-redirect=true";
    }

    public void atualMonth() {
        calendar.getTime();
        getMonth();
        getYear();
        getAtualMoves();
    }

    public void back() {
        System.out.println("entrou - back");
        if (mes > 0) {
            calendar.add(GregorianCalendar.MONTH, -1);
        } else {
            calendar.set(GregorianCalendar.MONTH, GregorianCalendar.DECEMBER);
            calendar.add(GregorianCalendar.YEAR, -1);
        }
        getMonth();
        getYear();
        getAtualMoves();
    }

    public void backYear() {
        System.out.println("entrou - backYear");
        calendar.add(GregorianCalendar.YEAR, -1);
        getYear();
        getAtualMoves();
    }

    public void advance() {
        System.out.println("entrou - advance");
        if (mes < 11) {
            calendar.add(GregorianCalendar.MONTH, +1);
        } else {
            calendar.set(GregorianCalendar.MONTH, GregorianCalendar.JANUARY);
            calendar.add(GregorianCalendar.YEAR, +1);
        }
        getMonth();
        getYear();
        getAtualMoves();
    }

    public void advanceYear() {
        System.out.println("entrou - advanceYear");
        calendar.add(GregorianCalendar.YEAR, +1);
        getYear();
        getAtualMoves();
    }

    private void getMonth() {
        mes = calendar.get(GregorianCalendar.MONTH);
        mesStr = mesIntToString(mes);
    }

    private void getYear() {
        ano = calendar.get(GregorianCalendar.YEAR);
        anoStr = String.valueOf(ano);
    }

    private String mesIntToString(int mesInt) {
        String mesString = "";
        switch (mesInt) {
            case 0:
                mesString = "Janeiro";
                break;
            case 1:
                mesString = "Fevereiro";
                break;
            case 2:
                mesString = "Março";
                break;
            case 3:
                mesString = "Abril";
                break;
            case 4:
                mesString = "Maio";
                break;
            case 5:
                mesString = "Junho";
                break;
            case 6:
                mesString = "Julho";
                break;
            case 7:
                mesString = "Agosto";
                break;
            case 8:
                mesString = "Setembro";
                break;
            case 9:
                mesString = "Outubro";
                break;
            case 10:
                mesString = "Novembro";
                break;
            case 11:
                mesString = "Dezembro";
                break;
            default:
        }
        return mesString;
    }

    public void getAtualMoves() {
        List<Movements> consultedList = new ArrayList<Movements>(); //lista que receberá todos os movimentos e logs
        movesList = new ArrayList<Movements>(); //lista global (filtrada) que será exibida
        SaldoTotal = 0; //Saldo geral zerado em cada pesquisa
        ReceitaTotal = 0;
        DespesaTotal = 0;
        DateFormat mesAno = new SimpleDateFormat("dd/MM/yyyy");
        DateFormat dia = new SimpleDateFormat("dd");
        ExtractBO bo = new ExtractBO();
        GregorianCalendar atualDate = new GregorianCalendar(ano, mes, Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH));
        //Consulta os movimentos que foram criados antes ou no mês e ano de pesquisa
        consultedList = bo.getMovements(atualDate.getTime(), extract);
        GregorianCalendar logRegCal;

        int limite;
        if ((user.getPremium()) || (consultedList.size() <= 10)) {
            limite = consultedList.size();
        } else {
            limite = 10;
        }
        if (user.getPremium()) {
            addMessages("Você só pode visualizar 10 movimentos por mês. Adquira uma conta prêmio!");
        }
        for (int i = 0; i < limite; i++) {
            //Pega os logs referentes ao movimento
            List<Logs> logsList = new ArrayList<Logs>(consultedList.get(i).getLogsCollection());
            //Corre os logs do movimento
            for (int j = 0; j < logsList.size(); j++) {
                logRegCal = new GregorianCalendar();
                logRegCal.setTime(logsList.get(j).getDataReg());
                //Verifica se o mês e ano do log têm registro de criação anterior ou igual ao mês e ano de pesquisa
                if (logRegCal.getTime().before(atualDate.getTime())) {
                    //Verifica se o log possui tempo de vida
                    if (logsList.get(j).getParcelas() != 0) {
                        GregorianCalendar limitCal = new GregorianCalendar();
                        limitCal = logRegCal;
                        limitCal.add(GregorianCalendar.MONTH, logsList.get(j).getParcelas());
                        //Verifica se o mês e ano de pesquisa atual já passou do tempo de vida do log
                        if (limitCal.after(atualDate)) {
                            //Verifica se o log atual é mais antigo que o novo

                            if (consultedList.get(i).getAtualLog().getDataReg() != null) {
                                if (consultedList.get(i).getAtualLog().getDataReg().before(logsList.get(j).getDataReg())) {
                                    consultedList.get(i).setAtualLog(logsList.get(j));
                                }
                            } else {
                                consultedList.get(i).setAtualLog(logsList.get(j));
                            }
                        }
                    } else {
                        //Verifica se o log atual é mais antigo que o novo
                        if (consultedList.get(i).getAtualLog().getDataReg() != null) {
                            if (consultedList.get(i).getAtualLog().getDataReg().before(logsList.get(j).getDataReg())) {
                                consultedList.get(i).setAtualLog(logsList.get(j));
                            }
                        } else {
                            consultedList.get(i).setAtualLog(logsList.get(j));
                        }
                    }
                }
            }
            //Retira da lista os movimentos que não devem aparecer
            if (consultedList.get(i).getAtualLog().getDataReg() != null && !consultedList.get(i).getOcult()) {
                //Seta a data de cadastro em um valor String para exibição
                consultedList.get(i).setRegStr("Registrado em: " + mesAno.format(consultedList.get(i).getAtualLog().getDataReg()));
                consultedList.get(i).setPgtoStr(dia.format(consultedList.get(i).getAtualLog().getDataReg()) + "/" + (mes + 1) + "/" + anoStr);
                //Caso seja parcelado, mostra o nº de parcelas
                if (consultedList.get(i).getAtualLog().getParcelas() > 1) {
                    int left = 0;
                    GregorianCalendar calcParcLeft = new GregorianCalendar();
                    calcParcLeft.setTime(consultedList.get(i).getAtualLog().getDataReg());
                    long milis1 = atualDate.getTime().getTime();
                    calcParcLeft.add(GregorianCalendar.MONTH, consultedList.get(i).getAtualLog().getParcelas());
                    long milis2 = calcParcLeft.getTime().getTime();
                    calcParcLeft.setTimeInMillis(milis2 - milis1);
                    left = calcParcLeft.get(GregorianCalendar.MONTH);
                    left = consultedList.get(i).getAtualLog().getParcelas() - left;
                    consultedList.get(i).setParcelaStr(" x" + left + "/" + consultedList.get(i).getAtualLog().getParcelas());
                } else {
                    consultedList.get(i).setParcelaStr("-");
                }
                movesList.add(consultedList.get(i));
                ReceitaTotal += consultedList.get(i).getAtualLog().getReceita();
                DespesaTotal += consultedList.get(i).getAtualLog().getDespesa();
                SaldoTotal += consultedList.get(i).getAtualLog().getSaldo();
            }
        }
        if (SaldoTotal >= 0) {
            saldoColor = "green";
        } else {
            saldoColor = "red";
        }
    }

    public void resetNewMoves() {
        newMove = new Movements();
        newMove.setOcult(false);
        newMove.getAtualLog().setStatus(1);
        newMove.getAtualLog().setPago(true);

        addMessages("Novo Cadastro");
    }

    public void addMovement() throws PreexistingEntityException, Exception {
        System.out.println("entrou - addMovement");
        newMove.setExtractId(extract);
        ExtractBO bo = new ExtractBO();
        newMove.getAtualLog().setDataReg(newMove.getDataIni());
        bo.createMovement(newMove);
        Movements mov = bo.getMovement(newMove.getMovDesc());
        newMove.getAtualLog().setMovId(mov);
        bo.createLog(newMove.getAtualLog());

        addMessages("Movimento Cadastrado com Sucesso");
        getAtualMoves();
        newMove = new Movements();
    }

    public void modifyMovement() throws PreexistingEntityException, Exception {
        System.out.println("entrou - modifyMovement");
        ExtractBO bo = new ExtractBO();
        bo.editMovement(selectedMove);
        bo.editLog(selectedMove.getAtualLog());

        addMessages("Movimento corrigido com Sucesso");
        getAtualMoves();
        selectedMove = new Movements();
    }

    public void updateMovement() throws PreexistingEntityException, Exception {
        System.out.println("entrou - updateMovement");
        ExtractBO bo = new ExtractBO();
        Movements mov = bo.getMovement(selectedMove.getMovDesc());
        selectedMove.getAtualLog().setMovId(mov);
        bo.createLog(selectedMove.getAtualLog());

        addMessages("Movimento atualizado com Sucesso");
        getAtualMoves();
        selectedMove = new Movements();
    }

    public void deleteMovement() throws PreexistingEntityException, Exception {
        System.out.println("entrou - deleteMovement");
        ExtractBO bo = new ExtractBO();
        bo.deleteMovement(selectedMove);

        addMessages("Movimento deletado com Sucesso");
        getAtualMoves();
        selectedMove = new Movements();
    }

    public void onRowSelect(SelectEvent event) {
        System.out.println("selected on row: "+selectedMove.getMovDesc());
    }

    public void addMessages(String msg) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, msg, null);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void createReport() {
        addMessages("Gerando Relatório...");
        System.out.println("entrou - createReport");
        RelatorioMensal relatorio = new RelatorioMensal();
        relatorio.setBean(this);
        try {
            relatorio.Generate();
            addMessages("Relatório Mensal");
        } catch (Exception ex) {
            addMessages("Falha ao gerar relatório");
            System.out.println("e> " + ex);
        }
    }

    public Movements getSelectedMove() {
        return selectedMove;
    }

    public void setSelectedMove(Movements selectedMove) {
        this.selectedMove = selectedMove;
    }

    public List<Movements> getMovesList() {
        return movesList;
    }

    public void setMovesList(List<Movements> movesList) {
        this.movesList = movesList;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public String getMesStr() {
        return mesStr;
    }

    public void setMesStr(String mesStr) {
        this.mesStr = mesStr;
    }

    public String getAnoStr() {
        return anoStr;
    }

    public void setAnoStr(String anoStr) {
        this.anoStr = anoStr;
    }

    public Movements getNewMove() {
        return newMove;
    }

    public void setNewMove(Movements newMove) {
        this.newMove = newMove;
    }

    public double getSaldoTotal() {
        return SaldoTotal;
    }

    public void setSaldoTotal(double SaldoTotal) {
        this.SaldoTotal = SaldoTotal;
    }

    public String getSaldoColor() {
        return saldoColor;
    }

    public void setSaldoColor(String saldoColor) {
        this.saldoColor = saldoColor;
    }

    public double getReceitaTotal() {
        return ReceitaTotal;
    }

    public void setReceitaTotal(double ReceitaTotal) {
        this.ReceitaTotal = ReceitaTotal;
    }

    public double getDespesaTotal() {
        return DespesaTotal;
    }

    public void setDespesaTotal(double DespesaTotal) {
        this.DespesaTotal = DespesaTotal;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public Extracts getExtract() {
        return extract;
    }

    public void setExtract(Extracts extract) {
        this.extract = extract;
    }
}
