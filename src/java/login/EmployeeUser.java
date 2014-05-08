/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package login;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.faces.bean.ManagedBean;
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
    private static final ArrayList<String> emails = new ArrayList<String>();
    private static final ArrayList<TableUser> users = new ArrayList<TableUser>();
    private static TableUser selectedUser;

    /**
     * Creates a new instance of EmployeeUser
     */
    public EmployeeUser() {
    }

    public TableUser getSelectedUser() {
        return selectedUser;
    }

    public void setSelectedUser(TableUser selectedUser) {
        EmployeeUser.selectedUser = selectedUser;
    }

    public ArrayList<TableUser> getUsers() {
        return users;
    }

    public ArrayList<String> getEmails() {
        return emails;
    }

    public int getDbName() {
        return dbName;
    }
    
    public void produceUsers(){
        users.removeAll(users);
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Where is your MySQL JDBC Driver?");
            e.printStackTrace();

        }
        PreparedStatement ps = null;
        Connection con = null;
        ResultSet rs;
        try {

            con = DriverManager.getConnection("jdbc:mysql://mysql2.cs.stonybrook.edu:3306/mlavina", "mlavina", "108262940");
            if (con != null) {
                con.setAutoCommit(false);
                try {
                    String sql = "SELECT * FROM customer C, Person P Where C.id = P.Id;";
                    ps = con.prepareStatement(sql);
                    ps.execute();
                    rs = ps.getResultSet();
                    while (rs.next()) {
                        
                        users.add(new TableUser(rs.getInt("id"),rs.getInt("accountNo"), rs.getString("creditcardno"),rs.getString("email"),
                                 rs.getDate("creationDate"), rs.getString("FirstName"), rs.getString("LastName"), rs.getString("Address"),
                                 rs.getString("City"),rs.getString("State"), rs.getInt("ZipCode")));
                    
                    }
                    con.commit();
                } catch (Exception e) {
                    System.out.println("SQL Rollback");
                    System.out.println(e);
                    con.rollback();
                }
            }

        } catch (Exception e) {
            System.out.println(e);
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
    
    public void updateUser(){
        users.removeAll(users);
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Where is your MySQL JDBC Driver?");
            e.printStackTrace();

        }
        PreparedStatement ps = null;
        Connection con = null;
        ResultSet rs;
        try {

            con = DriverManager.getConnection("jdbc:mysql://mysql2.cs.stonybrook.edu:3306/mlavina", "mlavina", "108262940");
            if (con != null) {
                con.setAutoCommit(false);
                try {
                    String sql = "UPDATE Person SET FirstName = ?, LastName = ?, Address = ?, City = ?, State = ?, ZipCode= ? WHERE Id = ?; ";
                    ps = con.prepareStatement(sql);
                    ps.setString(1, selectedUser.firstName);
                    ps.setString(2, selectedUser.lastName);
                    ps.setString(3, selectedUser.address);
                    ps.setString(4, selectedUser.city);
                    ps.setString(5, selectedUser.state);
                    ps.setInt(6, selectedUser.zip);
                    ps.setInt(7, selectedUser.id);
                    ps.execute();

                    con.commit();
                } catch (Exception e) {
                    System.out.println("SQL Rollback");
                    System.out.println(e);
                    con.rollback();
                }
                
                try {
                    String sql = "UPDATE Customer SET CreditCardNo = ?, email = ? WHERE AccountNo = ?; ";
                    ps = con.prepareStatement(sql);
                    ps.setString(1, selectedUser.creditNo);
                    ps.setString(2, selectedUser.email);
                    ps.setInt(3, selectedUser.accountNo);
                    ps.execute();

                    con.commit();
                } catch (Exception e) {
                    System.out.println("SQL Rollback");
                    System.out.println(e);
                    con.rollback();
                }
            }

        } catch (Exception e) {
            System.out.println(e);
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
    
    public void produceEmails(){
        emails.removeAll(emails);
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Where is your MySQL JDBC Driver?");
            e.printStackTrace();

        }
        PreparedStatement ps = null;
        Connection con = null;
        ResultSet rs;
        try {

            con = DriverManager.getConnection("jdbc:mysql://mysql2.cs.stonybrook.edu:3306/mlavina", "mlavina", "108262940");
            if (con != null) {
                con.setAutoCommit(false);
                try {
                    String sql = "SELECT Email from Customer";
                    ps = con.prepareStatement(sql);
                    ps.execute();
                    rs = ps.getResultSet();
                    while (rs.next()) {
                        emails.add(rs.getString("email"));
                    }
                    con.commit();
                } catch (Exception e) {
                    con.rollback();
                }
            }

        } catch (Exception e) {
            System.out.println(e);
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
    public void deleteUser(){
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Where is your MySQL JDBC Driver?");
            e.printStackTrace();

        }
        PreparedStatement ps = null;
        Connection con = null;
        ResultSet rs;
        try {

            con = DriverManager.getConnection("jdbc:mysql://mysql2.cs.stonybrook.edu:3306/mlavina", "mlavina", "108262940");
            if (con != null) {
                con.setAutoCommit(false);
                try {
                    
                    String sql = "DELETE FROM Person WHERE id = ? ";
                    ps = con.prepareStatement(sql);
                    ps.setInt(1, selectedUser.id);
                    ps.executeUpdate();

                    sql = "DELETE FROM Passenger WHERE id = ? ";
                    ps = con.prepareStatement(sql);
                    ps.setInt(1, selectedUser.id);
                    ps.executeUpdate();

                    con.commit();
                } catch (Exception e) {
                    con.rollback();
                    System.out.println(e);
                }
            }
                
        } catch (Exception e) {
            System.out.println(e);
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
    
    public static class TableUser {
        int id;
        int accountNo;

        String email;
        Date creationDate;
        String firstName;
        String lastName;
        String address;
        String city;
        String state;
        int zip;
        String creditNo;
        

        public TableUser(int id, int accountNo,String creditNo, String email, Date creationDate, String fName, String lName, String address, String city, String state, int zip) {
            this.id = id;
            this.accountNo = accountNo;
            this.email = email;
            this.creationDate = creationDate;
            this.firstName = fName;
            this.lastName = lName;
            this.address = address;
            this.city = city;
            this.state = state;
            this.zip = zip;
            this.creditNo = creditNo;
        }

        
        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public int getZip() {
            return zip;
        }

        public void setZip(int zip) {
            this.zip = zip;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        

        
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getAccountNo() {
            return accountNo;
        }

        public void setAccountNo(int accountNo) {
            this.accountNo = accountNo;
        }

        public String getCreditNo() {
            return creditNo;
        }

        public void setCreditNo(String creditNo) {
            this.creditNo = creditNo;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public Date getCreationDate() {
            return creationDate;
        }

        public void setCreationDate(Date creationDate) {
            this.creationDate = creationDate;
        }

        @Override
        public String toString() {
            return "TableUser{" + "id=" + id + ", accountNo=" + accountNo + ", creditNo=" + creditNo + ", email=" + email + ", creationDate=" + creationDate + '}';
        }
        
        
    }

}
