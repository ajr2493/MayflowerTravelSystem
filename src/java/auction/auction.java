/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package auction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author Michael
 */
@ManagedBean
@RequestScoped
public class auction {

    private int accountNo = (Integer) FacesContext.getCurrentInstance().getExternalContext()
            .getSessionMap().get("accountNo");

    private int personID = (Integer) FacesContext.getCurrentInstance().getExternalContext()
            .getSessionMap().get("personID");

    private static final ArrayList<Auction> auctions = new ArrayList<Auction>();
    private static Auction selectedAuction;

    public  Auction getSelectedAuction() {
        return selectedAuction;
    }

    public  void setSelectedAuction(Auction selectedAuction) {
        auction.selectedAuction = selectedAuction;
    }

    public ArrayList<Auction> getAuctions() {
        return auctions;
    }
    
    
    
    public void allBids(){
        auctions.removeAll(auctions);
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
                    String sql = "SELECT * FROM Auctions WHERE AccountNo = ? AND AirlineID = ? AND FlightNo = ? AND Class = ? ORDER BY `Date` DESC";
                    ps = con.prepareStatement(sql);
                    ps.setInt(1, accountNo);
                    ps.setString(2, selectedAuction.airlineId);
                    ps.setInt(3, selectedAuction.flightNo);
                    ps.setString(4, selectedAuction.seatClass);
                    ps.execute();
                    rs = ps.getResultSet();
                    while (rs.next()) {
                        auctions.add(new Auction(rs.getString("AirlineID"), rs.getInt("FlightNo"), rs.getString("Class"), rs.getTimestamp("Date"), rs.getDouble("NYOP"), rs.getInt("Accepted")));
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
    
    public void currentBids(){
        auctions.removeAll(auctions);
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
                    String sql = "SELECT * FROM Auctions WHERE AccountNo = ? AND AirlineID = ? AND FlightNo = ? AND Class = ? ORDER BY `Date` DESC LIMIT 0,1;";
                    ps = con.prepareStatement(sql);
                    ps.setInt(1, accountNo);
                    ps.setString(2, selectedAuction.airlineId);
                    ps.setInt(3, selectedAuction.flightNo);
                    ps.setString(4, selectedAuction.seatClass);
                    ps.execute();
                    rs = ps.getResultSet();
                    while (rs.next()) {
                        auctions.add(new Auction(rs.getString("AirlineID"), rs.getInt("FlightNo"), rs.getString("Class"), rs.getTimestamp("Date"), rs.getDouble("NYOP"), rs.getInt("Accepted")));
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
    
    public void auctionList() {
        
        auctions.removeAll(auctions);
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
                    String sql = "SELECT distinct AirlineID, FlightNo,Class from mlavina.auctions where AccountNo = ?;";
                    ps = con.prepareStatement(sql);
                    ps.setInt(1, accountNo);
                    ps.execute();
                    rs = ps.getResultSet();
                    while (rs.next()) {
                        auctions.add(new Auction(rs.getString("AirlineID"), rs.getInt("FlightNo"), rs.getString("Class")));
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

    public class Auction{
        private String airlineId;
        private int flightNo;
        private String seatClass;
        private Timestamp date;
        private double bid;
        private int accepted;

        public int getAccepted() {
            return accepted;
        }

        public void setAccepted(int accepted) {
            this.accepted = accepted;
        }

        
        public Timestamp getDate() {
            return date;
        }

        public void setDate(Timestamp date) {
            this.date = date;
        }

        public double getBid() {
            return bid;
        }

        public void setBid(double bid) {
            this.bid = bid;
        }

        public String getAirlineId() {
            return airlineId;
        }

        public void setAirlineId(String airlineId) {
            this.airlineId = airlineId;
        }

        public int getFlightNo() {
            return flightNo;
        }

        public void setFlightNo(int flightNo) {
            this.flightNo = flightNo;
        }

        public String getSeatClass() {
            return seatClass;
        }

        public void setSeatClass(String seatClass) {
            this.seatClass = seatClass;
        }

        public Auction(String airlineId, int flightNo, String seatClass) {
            this.airlineId = airlineId;
            this.flightNo = flightNo;
            this.seatClass = seatClass;
        }

        public Auction(String airlineId, int flightNo, String seatClass, Timestamp date, double bid, int accepted) {
            this.airlineId = airlineId;
            this.flightNo = flightNo;
            this.seatClass = seatClass;
            this.date = date;
            this.bid = bid;
            this.accepted = accepted;
        }

        
        
    }
}
