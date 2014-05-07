/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reservations;

import java.sql.Connection;
import java.sql.Date;
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
public class myReservations {

    /**
     * Creates a new instance of myReservations
     */
    private static final ArrayList<TableReservation> reservations = new ArrayList<TableReservation>();
    private static final ArrayList<TableReservation> cur_reservations = new ArrayList<TableReservation>();
    private static TableReservation selectedReservation;

    private static final ArrayList<Itinerary> itinerary = new ArrayList<Itinerary>();

    private int accountNo = (Integer) FacesContext.getCurrentInstance().getExternalContext()
            .getSessionMap().get("accountNo");

    public ArrayList<TableReservation> getReservations() {

        return reservations;
    }

    public ArrayList<TableReservation> getCur_reservations() {
        return cur_reservations;
    }

    public ArrayList<Itinerary> getItinerary() {
        return itinerary;
    }

    public TableReservation getSelectedReservation() {
        return selectedReservation;
    }

    public void setSelectedReservation(TableReservation selectedReservation) {
        myReservations.selectedReservation = selectedReservation;
    }

    public myReservations() {
    }

    public void listItinerary() {
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
                    String sql = "SELECT DISTINCT I.ResrNo, I.AirlineID, I.FlightNo, \n"
                            + "DA.Name AS Departing, AA.Name AS Arriving, \n"
                            + "L.DepTime, L.ArrTime \n"
                            + "FROM Includes I, Leg L, Airport DA, Airport AA \n"
                            + "WHERE I.AirlineID = L.AirlineID AND I.FlightNo = L.FlightNo \n"
                            + "AND L.DepAirportID = DA.Id AND L.ArrAirportID = AA.Id \n"
                            + "AND I.ResrNo = ?";
                    ps = con.prepareStatement(sql);
                    ps.setInt(1, selectedReservation.resrNo);
                    ps.execute();

                    rs = ps.getResultSet();
                    itinerary.removeAll(itinerary);
                    while (rs.next()) {
                        itinerary.add(new Itinerary(rs.getInt("ResrNo"), rs.getString("AirlineID"), rs.getInt("FlightNo"), rs.getString("Departing"), rs.getString("Arriving"),
                                rs.getTimestamp("DepTime"), rs.getTimestamp("ArrTime")));
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

    public void cancelReservation() {
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
                    String sql = "DELETE FROM Includes WHERE ResrNo = ? ";
                    ps = con.prepareStatement(sql);
                    ps.setInt(1, selectedReservation.resrNo);
                    ps.executeUpdate();

                    sql = "DELETE FROM ReservationPassenger WHERE ResrNo = ? ";
                    ps = con.prepareStatement(sql);
                    ps.setInt(1, selectedReservation.resrNo);
                    ps.executeUpdate();

                    sql = "DELETE FROM Reservation WHERE ResrNo  = ? ";
                    ps = con.prepareStatement(sql);
                    ps.setInt(1, selectedReservation.resrNo);
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

    public void myReservations() {
        reservations.removeAll(reservations);
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
                    String sql = "SELECT * FROM Reservation R \n"
                            + "WHERE EXISTS ( \n"
                            + " SELECT * FROM Includes I, Leg L \n"
                            + " WHERE R.ResrNo = I.ResrNo AND I.AirlineID = L.AirlineID \n"
                            + " AND I.FlightNo = L.FlightNo) \n"
                            + "AND R.AccountNo = ?";
                    ps = con.prepareStatement(sql);
                    ps.setInt(1, accountNo);
                    ps.execute();
                    rs = ps.getResultSet();
                    while (rs.next()) {
                        reservations.add(new TableReservation(rs.getInt("ResrNo"), rs.getDate("ResrDate"), rs.getDouble("BookingFee"), rs.getDouble("TotalFare"), rs.getInt("RepSSN")));
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
        //reservations.add(new TableReservation(555, new Date(141224241), 22.4, 12.1,11111));
        //reserveArray = reservations.toArray(reserveArray);
    }

    public void curReservations() {
        cur_reservations.removeAll(cur_reservations);
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
                    String sql = "SELECT * FROM Reservation R \n"
                            + "WHERE EXISTS ( \n"
                            + " SELECT * FROM Includes I, Leg L \n"
                            + " WHERE R.ResrNo = I.ResrNo AND I.AirlineID = L.AirlineID \n"
                            + " AND I.FlightNo = L.FlightNo AND L.DepTime >= NOW()) \n"
                            + "AND R.AccountNo = ?";
                    ps = con.prepareStatement(sql);
                    ps.setInt(1, accountNo);
                    ps.execute();
                    rs = ps.getResultSet();
                    while (rs.next()) {
                        cur_reservations.add(new TableReservation(rs.getInt("ResrNo"), rs.getDate("ResrDate"), rs.getDouble("BookingFee"), rs.getDouble("TotalFare"), rs.getInt("RepSSN")));
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

    public static class Itinerary {

        int resrNo;
        String airlineId;
        int flightNo;
        String Departing;
        String Arriving;
        Timestamp DepTime;
        Timestamp ArrTime;

        public int getResrNo() {
            return resrNo;
        }

        public void setResrNo(int resrNo) {
            this.resrNo = resrNo;
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

        public String getDeparting() {
            return Departing;
        }

        public void setDeparting(String Departing) {
            this.Departing = Departing;
        }

        public String getArriving() {
            return Arriving;
        }

        public void setArriving(String Arriving) {
            this.Arriving = Arriving;
        }

        public Timestamp getDepTime() {
            return DepTime;
        }

        public void setDepTime(Timestamp DepTime) {
            this.DepTime = DepTime;
        }

        public Timestamp getArrTime() {
            return ArrTime;
        }

        public void setArrTime(Timestamp ArrTime) {
            this.ArrTime = ArrTime;
        }

        public Itinerary(int resrNo, String airlineId, int flightNo, String Departing, String Arriving, Timestamp DepTime, Timestamp ArrTime) {
            this.resrNo = resrNo;
            this.airlineId = airlineId;
            this.flightNo = flightNo;
            this.Departing = Departing;
            this.Arriving = Arriving;
            this.DepTime = DepTime;
            this.ArrTime = ArrTime;
        }

        @Override
        public String toString() {
            return "Itinerary{" + "resrNo=" + resrNo + ", airlineId=" + airlineId + ", flightNo=" + flightNo + ", Departing=" + Departing + ", Arriving=" + Arriving + ", DepTime=" + DepTime + ", ArrTime=" + ArrTime + '}';
        }

    }

    public static class TableReservation {

        int resrNo;
        Date resrDate;
        double bookingFee;
        double totalFare;
        int repSSN;

        public TableReservation(int resrNo, Date resrDate, double bookingFee, double totalFare, int repSSN) {
            this.resrNo = resrNo;
            this.resrDate = resrDate;
            this.bookingFee = bookingFee;
            this.totalFare = totalFare;
            this.repSSN = repSSN;
        }

        public int getResrNo() {
            return resrNo;
        }

        public void setResrNo(int resrNo) {
            this.resrNo = resrNo;
        }

        public Date getResrDate() {
            return resrDate;
        }

        public void setResrDate(Date resrDate) {
            this.resrDate = resrDate;
        }

        public double getBookingFee() {
            return bookingFee;
        }

        public void setBookingFee(double bookingFee) {
            this.bookingFee = bookingFee;
        }

        public double getTotalFare() {
            return totalFare;
        }

        public void setTotalFare(double totalFare) {
            this.totalFare = totalFare;
        }

        public int getRepSSN() {
            return repSSN;
        }

        public void setRepSSN(int repSSN) {
            this.repSSN = repSSN;
        }

        @Override
        public String toString() {
            return "TableReservation{" + "resrNo=" + resrNo + ", resrDate=" + resrDate + ", bookingFee=" + bookingFee + ", totalFare=" + totalFare + ", repSSN=" + repSSN + '}';
        }

    }

}
