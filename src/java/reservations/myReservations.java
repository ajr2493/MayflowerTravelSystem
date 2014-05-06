/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reservations;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
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

    
    private int accountNo = (Integer) FacesContext.getCurrentInstance().getExternalContext()
                .getSessionMap().get("accountNo");;


    public ArrayList<TableReservation> getReservations() {

        return reservations;
    }



    public myReservations() {
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

            }

        } catch (Exception e) {
            System.out.println(e);
        } finally {
            try {
                con.close();
                ps.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //reservations.add(new TableReservation(555, new Date(141224241), 22.4, 12.1,11111));
        //reserveArray = reservations.toArray(reserveArray);
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
