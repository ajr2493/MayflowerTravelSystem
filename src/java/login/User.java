package login;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

@ManagedBean(name = "user")
@SessionScoped
public class User {

    private static String name;
    private String password;
    private String dbPassword;
    private String dbName;
    private String loggedIn;
    private String cardNum;
    private String fname, lname, address, city, state;
    private int zip;

    boolean isLoginPage = (FacesContext.getCurrentInstance().getViewRoot()
            .getViewId().lastIndexOf("login.xhtml") > -1);

    public User() {
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
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

    public String getDbPassword() {
        return dbPassword;
    }

    public String getCardNum() {
        return cardNum;
    }

    public void setCardNum(String cardNum) {
        this.cardNum = cardNum;
    }

    public String getLoggedIn() {
        return (String) FacesContext.getCurrentInstance().getExternalContext()
                .getSessionMap().get("loggedIn");
    }

    public String getDbName() {
        return dbName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String add() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Where is your MySQL JDBC Driver?");
            e.printStackTrace();
            return "";
        }
        int i = 0;
        int j = 0;
        if (name != null) {
            PreparedStatement ps = null;
            //prepared statement for max account no
            PreparedStatement ps2 = null;
            //prepared statement for person id
            PreparedStatement ps3 = null;
            //prepared statement for creating person
            PreparedStatement ps4 = null;
            ResultSet rs = null;
            Connection con = null;
            try {

                con = DriverManager.getConnection("jdbc:mysql://mysql2.cs.stonybrook.edu:3306/mlavina", "mlavina", "108262940");
                if (con != null) {
                    con.setAutoCommit(false);
                    try {
                        String sql = "INSERT INTO customer(email, password, creditcardno,creationdate,rating, accountno, id) VALUES(?,?,?,?,?,?,?)";
                        ps = con.prepareStatement(sql);
                        ps.setString(1, name);
                        ps.setString(2, password);
                        ps.setString(3, cardNum);
                        java.util.Date utilDate = new java.util.Date();
                        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
                        ps.setDate(4, sqlDate);
                        ps.setInt(5, 0);

                        String sql2 = "SELECT MAX(accountno) FROM customer";
                        ps2 = con.prepareStatement(sql2);
                        rs = ps2.executeQuery();
                        rs.next();
                        int accNo = rs.getInt(1) + 1;
                        ps.setInt(6, accNo);

                        String sql3 = "SELECT MAX(id) FROM person";
                        ps3 = con.prepareStatement(sql3);
                        rs = ps3.executeQuery();
                        rs.next();
                        int id = rs.getInt(1) + 1;
                        ps.setInt(7, id);

                        //todo create new person row in person table with id
                        String sql4 = "INSERT INTO person(id,firstname,lastname,address,city,state,zipcode) VALUES(?,?,?,?,?,?,?)";
                        ps4 = con.prepareStatement(sql4);
                        ps4.setInt(1, id);
                        ps4.setString(2, fname);
                        ps4.setString(3, lname);
                        ps4.setString(4, address);
                        ps4.setString(5, city);
                        ps4.setString(6, state);
                        ps4.setInt(7, zip);
                        j = ps4.executeUpdate();
                        i = ps.executeUpdate();
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
                    ps2.close();
                    ps3.close();
                    ps4.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (i > 0 && j > 0) {
            return "success";
        } else {
            System.out.println("Unsuccess");
            return "unsuccess";
        }
    }

    public void dbData(String uName) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Where is your MySQL JDBC Driver?");
            e.printStackTrace();

        }
        if (uName != null) {
            PreparedStatement ps = null;
            Connection con = null;
            ResultSet rs = null;

            try {
                con = DriverManager.getConnection("jdbc:mysql://mysql2.cs.stonybrook.edu:3306/mlavina", "mlavina", "108262940");
                if (con != null) {
                    con.setAutoCommit(false);
                    try {
                        String sql = "select email,password from customer where email = '"
                                + uName + "'";
                        ps = con.prepareStatement(sql);
                        rs = ps.executeQuery();
                        rs.next();
                        dbName = rs.getString("Email");
                        dbPassword = rs.getString("password");
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
        if ((name.toUpperCase().equals(dbName.toUpperCase()) && password.equals(dbPassword))) {
            FacesContext.getCurrentInstance().getExternalContext()
                    .getSessionMap().put("email", name);
            FacesContext.getCurrentInstance().getExternalContext()
                    .getSessionMap().put("loggedIn", "valid");

            PreparedStatement ps = null;
            Connection con = null;
            ResultSet rs = null;
            int personID = 0;
            int accountNo = 0;
            //get account number and personid from database
            try {
                con = DriverManager.getConnection("jdbc:mysql://mysql2.cs.stonybrook.edu:3306/mlavina", "mlavina", "108262940");
                if (con != null) {
                    con.setAutoCommit(false);
                    try {
                        String sql = "select id,accountNo from customer where email = '"
                                + name + "'";
                        ps = con.prepareStatement(sql);
                        rs = ps.executeQuery();
                        rs.next();
                        personID = rs.getInt(1);
                        accountNo = rs.getInt(2);
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

            //Save account Number in session scope
            FacesContext.getCurrentInstance().getExternalContext()
                    .getSessionMap().put("accountNo", accountNo);
            //save personid in session scope
            FacesContext.getCurrentInstance().getExternalContext()
                    .getSessionMap().put("personID", personID);
            loggedIn = "valid";
            return "valid";
        } else {
            return "invalid";
        }
    }

    public void logout() {

//        FacesContext
//                .getCurrentInstance()
//                .getApplication()
//                .getNavigationHandler()
//                .handleNavigation(FacesContext.getCurrentInstance(), null,
//                        "/login.xhtml");
        loggedIn = "";
        FacesContext.getCurrentInstance().getExternalContext()
                .getSessionMap().put("loggedIn", "");
    }
}
