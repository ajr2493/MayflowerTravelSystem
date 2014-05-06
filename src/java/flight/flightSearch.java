/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


package flight;




import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/**
 *
 * @author Michael
 */
@ManagedBean
@RequestScoped
public class flightSearch {

    /**
     * Creates a new instance of flightSearch
     */

    private String flightFrom;
    private String flightTo;
    private Date departing;
    private Date returning;
    
    //One-Way, Round-Trip or Multi-Trip
    private String flightDirection;
    public flightSearch() {
    }



    private static final ArrayList<Flight> flightResults = new ArrayList<Flight>();

  
    
    private static Flight selectedFlight;




    public Flight getSelectedFlight() {
        return selectedFlight;
    }

    public void setSelectedFlight(Flight selectedFlight) {
        flightSearch.selectedFlight = selectedFlight;
    }
    
    public ArrayList<Flight> getFlightResults() {
        return flightResults;
    }

    public String getFlightFrom() {
        return flightFrom;
    }

    public void setFlightFrom(String flightFrom) {
        this.flightFrom = flightFrom;
    }

    public String getFlightTo() {
        return flightTo;
    }

    public void setFlightTo(String flightTo) {
        this.flightTo = flightTo;
    }

    public Date getDeparting() {
        return departing;
    }

    public void setDeparting(Date departing) {
        this.departing = departing;
    }

    public Date getReturning() {
        return returning;
    }

    public void setReturning(Date returning) {
        this.returning = returning;
    }

    public void searchOneWayFlights() {

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Where is your MySQL JDBC Driver?");
            e.printStackTrace();

        }
        PreparedStatement ps = null;
        Connection con = null;
        ResultSet rs;
        List<String> results = new ArrayList<String>();
        try {

            con = DriverManager.getConnection("jdbc:mysql://mysql2.cs.stonybrook.edu:3306/mlavina", "mlavina", "108262940");
            if (con != null) {
                String sql = "Create OR Replace view SearchResults(Name, FlightNo, DepTime, ArrTime, DepAirportID, ArrAirportID, LegNo, Class, fare)\n"
                        + "as\n"
                        + "SELECT DISTINCT R.Name, L.FlightNo, L.DepTime, L.ArrTime, L.DepAirportId, L.ArrAirportId, L.LegNo, M.Class, M.Fare\n"
                        + "FROM Flight F, Leg L, Airport A , Fare M, Airline R\n"
                        + "WHERE F.AirlineID = L.AirlineID AND F.FlightNo = L.FlightNo \n"
                        + " AND (L.DepAirportId = ? OR L.ArrAirportId = ?) AND M.AirlineID = L.AirlineID \n"
                        + "AND M.FlightNo = L.FlightNo AND R.Id = L.AirlineID AND M.FareType = 'Regular' \n"
                        + "AND L.DepTime > ? AND L.DepTime < ? ";
                ps = con.prepareStatement(sql);
                ps.setString(1, flightFrom);
                ps.setString(2, flightTo);
                ps.setDate(3, new java.sql.Date(departing.getTime()));
                ps.setDate(4, new java.sql.Date(departing.getTime() + + (1000 * 60 * 60 * 24)));
                ps.execute();

                sql = "select Distinct A.Name, A.FlightNo, A.DepTime, B.ArrTime ,A.class, A.fare, A.DepAirportID, B.ArrAirportID\n" +
                      "From searchresults A, searchresults B\n" +
                      "Where ((A.ArrAirportID = B.DepAirportID) OR (A.DepAirportID = ? AND B.ArrAirportID = ?))";
                
                ps = con.prepareStatement(sql);
                ps.setString(1, flightFrom);
                ps.setString(2, flightTo);
                ps.execute();
                rs = ps.getResultSet();
                flightResults.removeAll(flightResults);
                int i = 0;
                while(rs.next()){
                    //flightResults.add()
                    flightResults.add(new Flight(rs.getString("Name"), rs.getString("FlightNo"), rs.getDate("DepTime"), rs.getDate("ArrTime"), 
                             rs.getString("Class"), rs.getDouble("fare"),rs.getString("DepAirportID"), rs.getString("ArrAirportID"), i));
                    i++;
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

    }

    public List<String> completeCities() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Where is your MySQL JDBC Driver?");
            e.printStackTrace();

        }
        PreparedStatement ps = null;
        Connection con = null;
        ResultSet rs;
        List<String> results = new ArrayList<String>();
        try {

            con = DriverManager.getConnection("jdbc:mysql://mysql2.cs.stonybrook.edu:3306/mlavina", "mlavina", "108262940");
            if (con != null) {
                String sql = "SELECT id From airport";
                ps = con.prepareStatement(sql);
                ps.execute();
                rs = ps.getResultSet();

                while (rs.next()) {
                    results.add(rs.getString("id"));
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
        return results;
    }

    public String getFlightDirection() {
        return flightDirection;
    }

    public void setFlightDirection(String flightDirection) {
        this.flightDirection = flightDirection;
    }


    public static class Flight {
        private String airline;
        private String flightNo;
        private Date deptTime;
        private Date arrTime;
        private String seatClass;
        private double fare;
        private String deptAirport;
        private String arrAirport;
        private int UID;
        
        public String getAirline() {
            return airline;
        }

        public String getFlightNo() {
            return flightNo;
        }

        public Date getDeptTime() {
            return deptTime;
        }

        public Date getArrTime() {
            return arrTime;
        }

        public String getSeatClass() {
            return seatClass;
        }

        public double getFare() {
            return fare;
        }

        public String getDeptAirport() {
            return deptAirport;
        }

        public String getArrAirport() {
            return arrAirport;
        }

        public int getUID() {
            return UID;
        }

        public void setUID(int UID) {
            this.UID = UID;
        }

        

        public Flight(String airline, String flightNo, java.sql.Date deptTime, java.sql.Date arrTime, String seatClass, double fare, String deptAirport, String arrAirport, int UID) {
            this.airline = airline;
            this.flightNo = flightNo;
            this.deptTime = new Date(deptTime.getTime());
            this.arrTime = new Date(arrTime.getTime());
            this.seatClass = seatClass;
            this.fare = fare;
            this.deptAirport = deptAirport;
            this.arrAirport = arrAirport;
            this.UID = UID;
        }
        
        
    }

}
