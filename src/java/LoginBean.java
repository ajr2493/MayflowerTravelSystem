/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author Aaron
 */
@ManagedBean
@SessionScoped
public class LoginBean {
  private String username;
  private String password;

  public String getUsername() {
    return this.username;
  }

  public void setUserName(String username) {
    this.username = username;
  }

  public String getPassword() {
    return this.password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
  
  public String login() {
      Statement stmt;
      try {
		Class.forName("com.mysql.jdbc.Driver");
	} catch (ClassNotFoundException e) {
		System.out.println("Where is your MySQL JDBC Driver?");
		e.printStackTrace();
		return "invalid";
	}
 
	System.out.println("MySQL JDBC Driver Registered!");
	Connection connection = null;
 
	try {
		connection = DriverManager
		.getConnection("jdbc:mysql://mysql2.cs.stonybrook.edu:3306/mlavina","mlavina", "108262940");
 
	} catch (SQLException e) {
		System.out.println("Connection Failed! Check output console");
		e.printStackTrace();
		return "invalid";
	}
 
	if (connection != null) {
		System.out.println("You made it, take control your database now!");
                System.out.println("Creating statement...");
            try {
                stmt = connection.createStatement();
                String sql;
                sql = "SELECT * FROM Users";
                ResultSet rs = stmt.executeQuery(sql);
                while(rs.next()){
                //Retrieve by column name
                if(rs.getString("username").equals(username) && rs.getString("password").equals(password)){
                    return "valid";
                }
                return "invalid";
                }
            } catch (SQLException ex) {
                Logger.getLogger(test.class.getName()).log(Level.SEVERE, null, ex);
            }
	} else {
		return "invalid";
	}
      return "invalid";
  }
}
