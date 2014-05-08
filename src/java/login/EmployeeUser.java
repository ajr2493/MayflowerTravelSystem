/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package login;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author Aaron
 */
@ManagedBean(name = "emp")
@SessionScoped
public class EmployeeUser {

    private int dbName;
    private int dbPassword;
    private static int name;
    private int password;
    private String loggedIn;

    /**
     * Creates a new instance of EmployeeUser
     */
    public EmployeeUser() {
    }

    public int getDbName() {
        return dbName;
    }

    public void setDbName(int dbName) {
        this.dbName = dbName;
    }

    public int getDbPassword() {
        return dbPassword;
    }

    public void setDbPassword(int dbPassword) {
        this.dbPassword = dbPassword;
    }

    public int getName() {
        return name;
    }

    public void setName(int name) {
        EmployeeUser.name = name;
    }

    public int getPassword() {
        return password;
    }

    public void setPassword(int password) {
        this.password = password;
    }

    public String getLoggedIn() {
        return (String) FacesContext.getCurrentInstance().getExternalContext()
                .getSessionMap().get("loggedIn");
    }

    public void dbData(int uName) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Where is your MySQL JDBC Driver?");
            e.printStackTrace();

        }
        if (uName >= 0) {
            PreparedStatement ps = null;
            Connection con = null;
            ResultSet rs = null;

            try {
                con = DriverManager.getConnection("jdbc:mysql://mysql2.cs.stonybrook.edu:3306/mlavina", "mlavina", "108262940");
                if (con != null) {
                    con.setAutoCommit(false);
                    try {
                        String sql = "select id,ssn from employee where id = '"
                                + uName + "'";
                        ps = con.prepareStatement(sql);
                        rs = ps.executeQuery();
                        rs.next();
                        dbName = rs.getInt("id");
                        dbPassword = rs.getInt("ssn");
                        con.commit();
                    } catch (Exception e) {
                        con.rollback();
                    }
                }
            } catch (SQLException sqle) {
                sqle.printStackTrace();
            } finally {
                try {
                    con.setAutoCommit(true);
                    con.close();
                    ps.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public String login() {
        dbData(name);
        if (name == dbName && password == dbPassword) {
            FacesContext.getCurrentInstance().getExternalContext()
                    .getSessionMap().put("loggedIn", "EmpValid");
            loggedIn = "EmpValid";
            return "EmpValid";
        } else {
            return "invalid";
        }
    }
}
