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
import javax.faces.bean.RequestScoped;

/**
 *
 * @author Aaron
 */
@ManagedBean
@RequestScoped
public class test {

    /**
     * Creates a new instance of test
     */
    public test() {
    }
    
    public String allAirlines(){
        String output= "";
        Statement stmt;
        System.out.println("-------- MySQL JDBC Connection Testing ------------");
 
	try {
		Class.forName("com.mysql.jdbc.Driver");
	} catch (ClassNotFoundException e) {
		System.out.println("Where is your MySQL JDBC Driver?");
		e.printStackTrace();
		return output;
	}
 
	System.out.println("MySQL JDBC Driver Registered!");
	Connection connection = null;
 
	try {
		connection = DriverManager
		.getConnection("jdbc:mysql://mysql2.cs.stonybrook.edu:3306/mlavina","mlavina", "108262940");
 
	} catch (SQLException e) {
		System.out.println("Connection Failed! Check output console");
		e.printStackTrace();
		return output;
	}
 
	if (connection != null) {
		System.out.println("You made it, take control your database now!");
                System.out.println("Creating statement...");
            try {
                stmt = connection.createStatement();
                String sql;
                sql = "SELECT * FROM Airline";
                ResultSet rs = stmt.executeQuery(sql);
                while(rs.next()){
                //Retrieve by column name
                output+= rs.getString("id");
                output+= "\t";
                output+= rs.getString("name");
                output+= "\n";
                }
            } catch (SQLException ex) {
                Logger.getLogger(test.class.getName()).log(Level.SEVERE, null, ex);
            }
	} else {
		output+="Failed to make connection!";
	}
        return output;
    }
    
}
