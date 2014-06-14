/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 *
 * @author ppccardoso
 */
public class SQLiteCreator implements Serializable {

    public static Connection c = null;

    public SQLiteCreator() {

        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:HandExtract.db");

            createDB();

            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Opened database successfully");
    }

    private void createDB() {
        createUsers();
        createExtracts();
        createMovements();
        createLogs();
        createParcelas();
    }

    private void createUsers() {
        Statement stmt = null;
        try {
            stmt = c.createStatement();
            String sql = "CREATE TABLE Users ( "
                    + "	user_id int IDENTITY(1,1) PRIMARY KEY NOT NULL, "
                    + "	user_name varchar(50) NOT NULL, "
                    + "	user_pass varchar(50) NULL, "
                    + "	user_exp_time date NULL )";
            stmt.executeUpdate(sql);
            stmt.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("datatable Users created successfully");
    }

    private void createExtracts() {
        Statement stmt = null;
        try {
            stmt = c.createStatement();
            String sql = "CREATE TABLE Extracts ( "
                    + "	extract_id int IDENTITY(1,1) PRIMARY KEY NOT NULL, "
                    + "	extract_name varchar(50) NOT NULL, "
                    + "	user_id int NOT NULL, "
                    + " FOREIGN KEY(user_id) REFERENCES Users(user_id))";
            stmt.executeUpdate(sql);
            stmt.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("datatable Extracts created successfully");
    }

    private void createMovements() {
        Statement stmt = null;
        try {
            stmt = c.createStatement();
            String sql = "CREATE TABLE Movements ( "
                    + "	mov_id int IDENTITY(1,1) PRIMARY KEY NOT NULL, "
                    + "	mov_desc varchar(50) NOT NULL, "
                    + "	ocult bit NOT NULL, "
                    + "	data_ini date NOT NULL, "
                    + "	extract_id int NOT NULL, "
                    + " FOREIGN KEY(extract_id) REFERENCES Extracts(extract_id))";
            stmt.executeUpdate(sql);
            stmt.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("datatable Movements created successfully");
    }

    private void createLogs() {

        Statement stmt = null;
        try {
            stmt = c.createStatement();
            String sql = "CREATE TABLE Logs ( "
                    + "	log_id int IDENTITY(1,1) PRIMARY KEY NOT NULL, "
                    + "	data_reg date NOT NULL, "
                    + "	mov_id int NOT NULL, "
                    + "	receita float NOT NULL, "
                    + "	despesa float NOT NULL, "
                    + "	parcelas int NOT NULL, "
                    + "	ativo bit NOT NULL, "
                    + "	status int NOT NULL, "
                    + "	pago bit NOT NULL, "
                    + " FOREIGN KEY(mov_id) REFERENCES Movements(mov_id))";
            stmt.executeUpdate(sql);
            stmt.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("datatable Logs created successfully");
    }

    private void createParcelas() {
        Statement stmt = null;
        try {
            stmt = c.createStatement();
            String sql = "CREATE TABLE Parcelas ( "
                    + "	parc_id int IDENTITY(1,1) PRIMARY KEY NOT NULL, "
                    + "	mov_id int NOT NULL, "
                    + "	num_parc int NOT NULL, "
                    + "	valor float NOT NULL, "
                    + "	pago bit NOT NULL, "
                    + "	data date NOT NULL, "
                    + " FOREIGN KEY(mov_id) REFERENCES Movements(mov_id))";
            stmt.executeUpdate(sql);
            stmt.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("datatable parcelas created successfully");
    }

    public static void main(String[] args) {
        SQLiteCreator go = new SQLiteCreator();
    }
}
