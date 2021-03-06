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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

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
    private String flightMiddle;
    private Date departing;
    private Date returning;
    private int employeeSSN;
    private int employeeSSNRoundTrip;
    private int accountNo = (Integer) FacesContext.getCurrentInstance().getExternalContext()
            .getSessionMap().get("accountNo");

    private int personID = (Integer) FacesContext.getCurrentInstance().getExternalContext()
            .getSessionMap().get("personID");

    private int seatNo;
    private String seatPreference;
    private String mealPreference;

    //One-Way, Round-Trip or Multi-Trip
    private static String flightDirection = "Round-Trip";

    private static final ArrayList<Flight> flightResults = new ArrayList<Flight>();
    private static final ArrayList<Flight> flightReturnResults = new ArrayList<Flight>();
    private static final ArrayList<Flight> bestSellingFlights = new ArrayList<Flight>();
    private static final ArrayList<Flight> recommendFlights = new ArrayList<Flight>();

    private static Flight selectedFlight;
    private static Flight selectedReturnFlight;

    public Flight getSelectedReturnFlight() {
        return selectedReturnFlight;
    }

    public String getFlightMiddle() {
        return flightMiddle;
    }

    public void setFlightMiddle(String flightMiddle) {
        this.flightMiddle = flightMiddle;
    }

    
    public void setSelectedReturnFlight(Flight selectedReturnFlight) {
        flightSearch.selectedReturnFlight = selectedReturnFlight;
    }

    public int getEmployeeSSN() {
        return employeeSSN;
    }

    public void setEmployeeSSN(int employeeSSN) {
        this.employeeSSN = employeeSSN;
    }

    public int getSeatNo() {
        return seatNo;
    }

    public void setSeatNo(int seatNo) {
        this.seatNo = seatNo;
    }

    public String getSeatPreference() {
        return seatPreference;
    }

    public void setSeatPreference(String seatPreference) {
        this.seatPreference = seatPreference;
    }

    public String getMealPreference() {
        return mealPreference;
    }

    public void setMealPreference(String mealPreference) {
        this.mealPreference = mealPreference;
    }

    public Flight getSelectedFlight() {
        return selectedFlight;
    }

    public void setSelectedFlight(Flight selectedFlight) {
        flightSearch.selectedFlight = selectedFlight;
    }

    public ArrayList<Flight> getFlightResults() {
        return flightResults;
    }

    public ArrayList<Flight> getBestSellingFlights() {
        return bestSellingFlights;
    }

    public ArrayList<Flight> getRecommendFlights() {
        return recommendFlights;
    }

    public ArrayList<Flight> getFlightReturnResults() {
        return flightReturnResults;
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

    public String getFlightDirection() {
        return flightDirection;
    }

    public void setFlightDirection(String flightDirection) {
        this.flightDirection = flightDirection;

    }

    //Seat Preferences
    public List<String> seatPreferences() {
        List<String> preference = new ArrayList<String>();
        preference.add("Aisle Seat");
        preference.add("Window Seat");
        return preference;
    }

    public List<String> mealPreferences() {
        List<String> preference = new ArrayList<String>();
        preference.add("Chicken");
        preference.add("Beef");
        preference.add("Seafood");
        preference.add("Vegetarian");
        preference.add("Vegan");
        preference.add("No Food");
        return preference;
    }

    /*Get a list of available seats*/
    public List<Integer> seatChoices() {
        List<Integer> availableSeats = new ArrayList<Integer>();
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Where is your MySQL JDBC Driver?");
            e.printStackTrace();

        }
        PreparedStatement ps = null;
        Connection con = null;
        ResultSet rs;
        List<Integer> takenSeats = new ArrayList<Integer>();
        try {

            con = DriverManager.getConnection("jdbc:mysql://mysql2.cs.stonybrook.edu:3306/mlavina", "mlavina", "108262940");
            if (con != null) {
                con.setAutoCommit(false);

                String sql = "SELECT SeatNo \n"
                        + "FROM includes I, reservationpassenger R\n"
                        + "WHERE R.ResrNo = I.ResrNo AND I.FlightNo = ? ";

                ps = con.prepareStatement(sql);
                ps.setInt(1, selectedFlight.flightNo);

                try {
                    ps.execute();

                    rs = ps.getResultSet();
                    while (rs.next()) {
                        takenSeats.add(rs.getInt("SeatNo"));
                    }
                    rs.close();

                    sql = "SELECT NoOfSeats \n"
                            + "FROM flight f, airline A\n"
                            + "WHERE f.FlightNo = ? AND f.AirlineID= A.Id AND A.name = ?";

                    ps = con.prepareStatement(sql);
                    ps.setInt(1, selectedFlight.flightNo);
                    ps.setString(2, selectedFlight.airline);
                    ps.execute();
                    rs = ps.getResultSet();
                    if (rs.next()) {
                        for (int i = rs.getInt("NoOfSeats"); i > 0; i--) {
                            if (takenSeats.contains(i)) {
                                continue;
                            } else {
                                availableSeats.add(i);
                            }
                        }
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

        return availableSeats;
    }

    public flightSearch() {
    }

    /**
     * Reserve Tickets
     */
    public void reserveOneWayTicket() {
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
                String sql = "SELECT max(ResrNo) FROM reservation;";
                ps = con.prepareStatement(sql);

                try {
                    ps.execute();
                    rs = ps.getResultSet();
                    int resrNo = -1;

                    if (rs.next()) {
                        resrNo = rs.getInt(1) + 1;
                    }

                    sql = "Insert into passenger (Id, AccountNo) values (?,?) ON DUPLICATE KEY UPDATE Id=Id;";

                    ps = con.prepareStatement(sql);
                    ps.setInt(1, personID);
                    ps.setInt(2, accountNo);

                    ps.execute();

                    sql = "INSERT INTO Reservation VALUES (?, ?, ?, ?, ?,?); ";

                    ps = con.prepareStatement(sql);
                    ps.setInt(1, resrNo);
                    ps.setTimestamp(2, new java.sql.Timestamp(new Date().getTime()));
                    ps.setDouble(3, (selectedFlight.fare * .10));
                    ps.setDouble(4, selectedFlight.fare);
                    ps.setInt(5, employeeSSN);
                    ps.setInt(6, accountNo);

                    ps.execute();

                    sql = "INSERT INTO Includes VALUES (?, ?, ?, ?, ?);";

                    ps = con.prepareStatement(sql);

                    ps.setInt(1, resrNo);
                    ps.setString(2, selectedFlight.airlineID);
                    ps.setInt(3, selectedFlight.flightNo);
                    ps.setInt(4, selectedFlight.legNo);
                    ps.setDate(5, new java.sql.Date(selectedFlight.deptTime.getTime()));

                    ps.execute();

                    sql = "INSERT INTO ReservationPassenger VALUES(?, ?, ?, ?, ?, ?);";

                    ps = con.prepareStatement(sql);
                    ps.setInt(1, resrNo);
                    ps.setInt(2, personID);
                    ps.setInt(3, accountNo);
                    ps.setString(4, ("" + seatNo));
                    ps.setString(5, selectedFlight.seatClass);
                    ps.setString(6, mealPreference);

                    ps.execute();
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

    public void reserveRoundTripTicket() {
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
                String sql = "SELECT max(ResrNo) FROM reservation;";
                ps = con.prepareStatement(sql);
                try {
                    ps.execute();
                    rs = ps.getResultSet();
                    int resrNo = -1;

                    if (rs.next()) {
                        resrNo = rs.getInt(1) + 1;
                    }

                    sql = "Insert into passenger (Id, AccountNo) values (?,?) ON DUPLICATE KEY UPDATE Id=Id;";

                    ps = con.prepareStatement(sql);
                    ps.setInt(1, personID);
                    ps.setInt(2, accountNo);

                    ps.execute();

                    sql = "INSERT INTO Reservation VALUES (?, ?, ?, ?, ?,?); ";

                    double fare = selectedFlight.fare + selectedReturnFlight.fare;

                    ps = con.prepareStatement(sql);
                    ps.setInt(1, resrNo);
                    ps.setTimestamp(2, new java.sql.Timestamp(new Date().getTime()));
                    ps.setDouble(3, (fare * .10));
                    ps.setDouble(4, fare);
                    ps.setInt(5, employeeSSN);
                    ps.setInt(6, accountNo);

                    ps.execute();

                    sql = "INSERT INTO Includes VALUES (?, ?, ?, ?, ?);";

                    ps = con.prepareStatement(sql);

                    ps.setInt(1, resrNo);
                    ps.setString(2, selectedFlight.airlineID);
                    ps.setInt(3, selectedFlight.flightNo);
                    ps.setInt(4, selectedFlight.legNo);
                    ps.setDate(5, new java.sql.Date(selectedReturnFlight.deptTime.getTime()));

                    ps.execute();

                    sql = "INSERT INTO Includes VALUES (?, ?, ?, ?, ?);";

                    ps = con.prepareStatement(sql);

                    ps.setInt(1, resrNo);
                    ps.setString(2, selectedReturnFlight.airlineID);
                    ps.setInt(3, selectedReturnFlight.flightNo);
                    ps.setInt(4, selectedReturnFlight.legNo);
                    ps.setDate(5, new java.sql.Date(selectedReturnFlight.deptTime.getTime()));

                    ps.execute();

                    sql = "INSERT INTO ReservationPassenger VALUES(?, ?, ?, ?, ?, ?);";

                    ps = con.prepareStatement(sql);
                    ps.setInt(1, resrNo);
                    ps.setInt(2, personID);
                    ps.setInt(3, accountNo);
                    ps.setString(4, ("" + seatNo));
                    ps.setString(5, selectedFlight.seatClass);
                    ps.setString(6, mealPreference);

                    ps.execute();
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
        try {

            con = DriverManager.getConnection("jdbc:mysql://mysql2.cs.stonybrook.edu:3306/mlavina", "mlavina", "108262940");
            if (con != null) {
                con.setAutoCommit(false);
                String sql = "Create OR Replace view SearchResults(Name, FlightNo, DepTime, ArrTime, DepAirportID, ArrAirportID, LegNo, Class, fare, AirlineID)\n"
                        + "as\n"
                        + "SELECT DISTINCT R.Name, L.FlightNo, L.DepTime, L.ArrTime, L.DepAirportId, L.ArrAirportId, L.LegNo, M.Class, M.Fare, L.AirlineID\n"
                        + "FROM Flight F, Leg L, Airport A , Fare M, Airline R\n"
                        + "WHERE F.AirlineID = L.AirlineID AND F.FlightNo = L.FlightNo \n"
                        + " AND (L.DepAirportId = ? OR L.ArrAirportId = ?) AND M.AirlineID = L.AirlineID \n"
                        + "AND M.FlightNo = L.FlightNo AND R.Id = L.AirlineID AND M.FareType = 'Regular' \n"
                        + "AND L.DepTime > ? AND L.DepTime < ? ";
                ps = con.prepareStatement(sql);
                ps.setString(1, flightFrom);
                ps.setString(2, flightTo);
                ps.setDate(3, new java.sql.Date(departing.getTime()));
                ps.setDate(4, new java.sql.Date(departing.getTime() + +(1000 * 60 * 60 * 24)));
                System.out.println(ps);
                try {
                    ps.execute();

                    sql = "select Distinct A.Name, A.FlightNo, A.DepTime, B.ArrTime ,A.class, A.fare, A.DepAirportID, A.LegNo, A.AirlineID, B.ArrAirportID\n"
                            + "From searchresults A, searchresults B\n"
                            + "Where ((A.ArrAirportID = B.DepAirportID) OR (A.DepAirportID = ? AND B.ArrAirportID = ?))";

                    ps = con.prepareStatement(sql);
                    ps.setString(1, flightFrom);
                    ps.setString(2, flightTo);
                    ps.execute();
                    rs = ps.getResultSet();
                    flightResults.removeAll(flightResults);
                    int i = 0;
                    while (rs.next()) {
                        //flightResults.add()
                        flightResults.add(new Flight(rs.getString("AirlineID"), rs.getString("Name"), rs.getInt("FlightNo"), rs.getTimestamp("DepTime"), rs.getTimestamp("ArrTime"),
                                rs.getString("Class"), rs.getDouble("fare"), rs.getString("DepAirportID"), rs.getString("ArrAirportID"), i, rs.getInt("LegNo")));
                        i++;
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

    public void searchRoundTripFlights() {

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
                String sql = "Create OR Replace view SearchResults(Name, FlightNo, DepTime, ArrTime, DepAirportID, ArrAirportID, LegNo, Class, fare, AirlineID)\n"
                        + "as\n"
                        + "SELECT DISTINCT R.Name, L.FlightNo, L.DepTime, L.ArrTime, L.DepAirportId, L.ArrAirportId, L.LegNo, M.Class, M.Fare, L.AirlineID\n"
                        + "FROM Flight F, Leg L, Airport A , Fare M, Airline R\n"
                        + "WHERE F.AirlineID = L.AirlineID AND F.FlightNo = L.FlightNo \n"
                        + " AND (L.DepAirportId = ? OR L.ArrAirportId = ?) AND M.AirlineID = L.AirlineID \n"
                        + "AND M.FlightNo = L.FlightNo AND R.Id = L.AirlineID AND M.FareType = 'Regular' \n"
                        + "AND L.DepTime > ? AND L.DepTime < ? ";
                ps = con.prepareStatement(sql);
                ps.setString(1, flightTo);
                ps.setString(2, flightFrom);
                ps.setDate(3, new java.sql.Date(returning.getTime()));
                ps.setDate(4, new java.sql.Date(returning.getTime() + +(1000 * 60 * 60 * 24)));
                try {
                    ps.execute();

                    sql = "select Distinct A.Name, A.FlightNo, A.DepTime, B.ArrTime ,A.class, A.fare, A.DepAirportID, A.LegNo, A.AirlineID, B.ArrAirportID\n"
                            + "From searchresults A, searchresults B\n"
                            + "Where ((A.ArrAirportID = B.DepAirportID) OR (A.DepAirportID = ? AND B.ArrAirportID = ?))";

                    ps = con.prepareStatement(sql);
                    ps.setString(1, flightTo);
                    ps.setString(2, flightFrom);
                    ps.execute();
                    rs = ps.getResultSet();
                    flightReturnResults.removeAll(flightReturnResults);
                    int i = 0;
                    while (rs.next()) {
                        //flightResults.add()
                        flightReturnResults.add(new Flight(rs.getString("AirlineID"), rs.getString("Name"), rs.getInt("FlightNo"), rs.getTimestamp("DepTime"), rs.getTimestamp("ArrTime"),
                                rs.getString("Class"), rs.getDouble("fare"), rs.getString("DepAirportID"), rs.getString("ArrAirportID"), i, rs.getInt("LegNo")));
                        i++;
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

    public void searchMultiTripFlightsPartOne() {

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
                String sql = "Create OR Replace view SearchResults(Name, FlightNo, DepTime, ArrTime, DepAirportID, ArrAirportID, LegNo, Class, fare, AirlineID)\n"
                        + "as\n"
                        + "SELECT DISTINCT R.Name, L.FlightNo, L.DepTime, L.ArrTime, L.DepAirportId, L.ArrAirportId, L.LegNo, M.Class, M.Fare, L.AirlineID\n"
                        + "FROM Flight F, Leg L, Airport A , Fare M, Airline R\n"
                        + "WHERE F.AirlineID = L.AirlineID AND F.FlightNo = L.FlightNo \n"
                        + " AND (L.DepAirportId = ? OR L.ArrAirportId = ?) AND M.AirlineID = L.AirlineID \n"
                        + "AND M.FlightNo = L.FlightNo AND R.Id = L.AirlineID AND M.FareType = 'Regular' \n"
                        + "AND L.DepTime > ? AND L.DepTime < ? ";
                ps = con.prepareStatement(sql);
                ps.setString(1, flightFrom);
                ps.setString(2, flightMiddle);
                ps.setDate(3, new java.sql.Date(departing.getTime()));
                ps.setDate(4, new java.sql.Date(departing.getTime() + +(1000 * 60 * 60 * 24)));
                try {
                    ps.execute();

                    sql = "select Distinct A.Name, A.FlightNo, A.DepTime, B.ArrTime ,A.class, A.fare, A.DepAirportID, A.LegNo, A.AirlineID, B.ArrAirportID\n"
                            + "From searchresults A, searchresults B\n"
                            + "Where ((A.ArrAirportID = B.DepAirportID) OR (A.DepAirportID = ? AND B.ArrAirportID = ?))";

                    ps = con.prepareStatement(sql);
                    ps.setString(1, flightFrom);
                    ps.setString(2, flightMiddle);
                    ps.execute();
                    rs = ps.getResultSet();
                    flightResults.removeAll(flightResults);
                    int i = 0;
                    while (rs.next()) {
                        //flightResults.add()
                        flightResults.add(new Flight(rs.getString("AirlineID"), rs.getString("Name"), rs.getInt("FlightNo"), rs.getTimestamp("DepTime"), rs.getTimestamp("ArrTime"),
                                rs.getString("Class"), rs.getDouble("fare"), rs.getString("DepAirportID"), rs.getString("ArrAirportID"), i, rs.getInt("LegNo")));
                        i++;
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
    
    public void searchMultiTripFlightsPartTwo() {

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
                String sql = "Create OR Replace view SearchResults(Name, FlightNo, DepTime, ArrTime, DepAirportID, ArrAirportID, LegNo, Class, fare, AirlineID)\n"
                        + "as\n"
                        + "SELECT DISTINCT R.Name, L.FlightNo, L.DepTime, L.ArrTime, L.DepAirportId, L.ArrAirportId, L.LegNo, M.Class, M.Fare, L.AirlineID\n"
                        + "FROM Flight F, Leg L, Airport A , Fare M, Airline R\n"
                        + "WHERE F.AirlineID = L.AirlineID AND F.FlightNo = L.FlightNo \n"
                        + " AND (L.DepAirportId = ? OR L.ArrAirportId = ?) AND M.AirlineID = L.AirlineID \n"
                        + "AND M.FlightNo = L.FlightNo AND R.Id = L.AirlineID AND M.FareType = 'Regular' \n"
                        + "AND L.DepTime > ? AND L.DepTime < ? ";
                ps = con.prepareStatement(sql);
                ps.setString(1, flightMiddle);
                ps.setString(2, flightTo);
                ps.setDate(3, new java.sql.Date(returning.getTime()));
                ps.setDate(4, new java.sql.Date(returning.getTime() + +(1000 * 60 * 60 * 24)));
                try {
                    ps.execute();

                    sql = "select Distinct A.Name, A.FlightNo, A.DepTime, B.ArrTime ,A.class, A.fare, A.DepAirportID, A.LegNo, A.AirlineID, B.ArrAirportID\n"
                            + "From searchresults A, searchresults B\n"
                            + "Where ((A.ArrAirportID = B.DepAirportID) OR (A.DepAirportID = ? AND B.ArrAirportID = ?))";

                    ps = con.prepareStatement(sql);
                    ps.setString(1, flightMiddle);
                    ps.setString(2, flightTo);
                    ps.execute();
                    rs = ps.getResultSet();
                    flightReturnResults.removeAll(flightReturnResults);
                    int i = 0;
                    while (rs.next()) {
                        //flightResults.add()
                        flightReturnResults.add(new Flight(rs.getString("AirlineID"), rs.getString("Name"), rs.getInt("FlightNo"), rs.getTimestamp("DepTime"), rs.getTimestamp("ArrTime"),
                                rs.getString("Class"), rs.getDouble("fare"), rs.getString("DepAirportID"), rs.getString("ArrAirportID"), i, rs.getInt("LegNo")));
                        i++;
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
    
    public void searchBestSellingFlights() {
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
                String sql = "CREATE OR Replace VIEW FlightReservation(AirlineID, FlightNo, ResrCount) \n"
                        + "AS \n"
                        + "SELECT I.AirlineID, I.FlightNo, COUNT(DISTINCT I.ResrNo) \n"
                        + "FROM Includes I \n"
                        + "GROUP BY I.AirlineID, I.FlightNo";

                ps = con.prepareStatement(sql);
                try {
                    ps.execute();

                    sql = "SELECT * FROM FlightReservation ORDER BY ResrCount DESC";
                    ps = con.prepareStatement(sql);

                    ps.execute();

                    rs = ps.getResultSet();
                    bestSellingFlights.removeAll(bestSellingFlights);
                    while (rs.next()) {
                        //flightResults.add()
                        bestSellingFlights.add(new Flight(rs.getString("AirlineID"), rs.getInt("FlightNo")));
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

    public void searchRecommendedFlights() {
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
                String sql = "CREATE OR Replace VIEW FlightReservation(AirlineID, FlightNo, ResrCount) \n"
                        + "AS \n"
                        + "SELECT I.AirlineID, I.FlightNo, COUNT(DISTINCT I.ResrNo) \n"
                        + "FROM Includes I \n"
                        + "GROUP BY I.AirlineID, I.FlightNo";

                ps = con.prepareStatement(sql);

                ps.execute();
                try {
                    sql = "SELECT * FROM FlightReservation FR \n"
                            + "WHERE NOT EXISTS ( \n"
                            + " SELECT * FROM Reservation R, Includes I \n"
                            + " WHERE R.ResrNo = I.ResrNo AND FR.AirlineID = I.AirlineID \n"
                            + " AND FR.FlightNo = I.FlightNo AND R.AccountNo = ?) \n"
                            + "ORDER BY FR.ResrCount DESC";
                    ps = con.prepareStatement(sql);
                    ps.setInt(1, accountNo);
                    ps.execute();

                    rs = ps.getResultSet();
                    recommendFlights.removeAll(recommendFlights);
                    while (rs.next()) {
                        //flightResults.add()
                        recommendFlights.add(new Flight(rs.getString("AirlineID"), rs.getInt("FlightNo")));
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
                con.setAutoCommit(false);
                String sql = "SELECT id From airport";
                ps = con.prepareStatement(sql);
                try {
                    ps.execute();
                    rs = ps.getResultSet();

                    while (rs.next()) {
                        results.add(rs.getString("id"));
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
        return results;
    }

    public static class Flight {

        private String airline;
        private String airlineID;
        private int flightNo;
        private Timestamp deptTime;
        private Timestamp arrTime;
        private String seatClass;
        private double fare;
        private String deptAirport;
        private String arrAirport;
        private int UID;
        private int legNo;

        public String getAirline() {
            return airline;
        }

        public int getFlightNo() {
            return flightNo;
        }

        public String getAirlineID() {
            return airlineID;
        }

        public Timestamp getDeptTime() {
            return deptTime;
        }

        public Timestamp getArrTime() {
            return arrTime;
        }

        public String getSeatClass() {
            return seatClass;
        }

        public double getFare() {
            return fare;
        }

        public void setFare(double fare) {
            this.fare = fare;
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

        public int getLegNo() {
            return legNo;
        }

        public Flight(String airlineID, int flightNo) {
            this.airlineID = airlineID;
            this.flightNo = flightNo;
        }

        public Flight(String airlineID, String airline, int flightNo, Timestamp deptTime, Timestamp arrTime, String seatClass, double fare, String deptAirport, String arrAirport, int UID, int legNo) {
            this.airline = airline;
            this.airlineID = airlineID;
            this.flightNo = flightNo;
            this.deptTime = deptTime;
            this.arrTime = arrTime;
            this.seatClass = seatClass;
            this.fare = fare;
            this.deptAirport = deptAirport;
            this.arrAirport = arrAirport;
            this.UID = UID;
            this.legNo = legNo;
        }

        @Override
        public String toString() {
            return "Flight{" + "airline=" + airline + ", airlineID=" + airlineID + ", flightNo=" + flightNo + ", deptTime=" + deptTime + ", arrTime=" + arrTime + ", seatClass=" + seatClass + ", fare=" + fare + ", deptAirport=" + deptAirport + ", arrAirport=" + arrAirport + ", UID=" + UID + ", legNo=" + legNo + '}';
        }

    }

    /**
     * *************************************REVERSE
     * AUCTION*********************************************
     */
    private double buyerPrice;
    private static double buyerReturningPrice;
    private static double hiddenPrice;
    private double hiddenReturningPrice;

    public double getBuyerReturningPrice() {
        return buyerReturningPrice;
    }

    public void setBuyerReturningPrice(double buyerReturningPrice) {
        this.buyerReturningPrice = buyerReturningPrice;
    }

    public double getBuyerPrice() {
        return buyerPrice;
    }

    public void setBuyerPrice(double buyerPrice) {
        this.buyerPrice = buyerPrice;
    }

    public double getHiddenPrice() {
        return hiddenPrice;
    }

    public void findHiddenPrice() {
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
                String sql = "SELECT Fare FROM mlavina.fare where FareType = 'Hidden' AND AirlineID = ? AND FlightNo = ? and Class =  ? ;";

                ps = con.prepareStatement(sql);
                ps.setString(1, selectedFlight.airlineID);
                ps.setInt(2, selectedFlight.flightNo);
                ps.setString(3, selectedFlight.seatClass);

                try {
                    ps.execute();

                    rs = ps.getResultSet();

                    if (rs.next()) {
                        this.hiddenPrice = rs.getDouble(1);
                    }
                    System.out.println("Hidden Price" + hiddenPrice);
                    con.commit();
                } catch (Exception e) {
                    con.rollback();
                }

                if (flightDirection.equals("Round-Trip")) {
                    sql = "SELECT Fare FROM mlavina.fare where FareType = 'Hidden' AND AirlineID = ? AND FlightNo = ? and Class =  ? ;";

                    ps = con.prepareStatement(sql);
                    ps.setString(1, selectedReturnFlight.airlineID);
                    ps.setInt(2, selectedReturnFlight.flightNo);
                    ps.setString(3, selectedReturnFlight.seatClass);

                    try {
                        ps.execute();

                        rs = ps.getResultSet();

                        if (rs.next()) {
                            this.hiddenReturningPrice = rs.getDouble(1);
                        }
                        con.commit();
                    } catch (Exception e) {
                        con.rollback();
                    }
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

    public String determineAuctionResults() {
        if (flightDirection.equals("One-Way")) {
            if (buyerPrice > hiddenPrice) {
                selectedFlight.fare = buyerPrice;
                addReservation(accountNo, selectedFlight.airlineID, selectedFlight.flightNo, selectedFlight.seatClass, new Timestamp(new Date().getTime()), buyerPrice, 1);
                return "reserveFlight";

            } else {
                addReservation(accountNo, selectedFlight.airlineID, selectedFlight.flightNo, selectedFlight.seatClass, new Timestamp(new Date().getTime()), buyerPrice, 0);
                return "auctionError";
            }
        } else {
            if (buyerReturningPrice > hiddenReturningPrice && buyerPrice > hiddenPrice) {
                selectedFlight.fare = buyerPrice;
                selectedReturnFlight.fare = buyerReturningPrice;
                addReservation(accountNo, selectedFlight.airlineID, selectedFlight.flightNo, selectedFlight.seatClass, new Timestamp(new Date().getTime()), buyerPrice, 1);
                addReservation(accountNo, selectedReturnFlight.airlineID, selectedReturnFlight.flightNo, selectedReturnFlight.seatClass, new Timestamp(new Date().getTime()), buyerReturningPrice, 1);
                return "reserveFlightRoundTrip";

            } else if (buyerReturningPrice > hiddenReturningPrice) {
                addReservation(accountNo, selectedFlight.airlineID, selectedFlight.flightNo, selectedFlight.seatClass, new Timestamp(new Date().getTime()), buyerPrice, 0);
                addReservation(accountNo, selectedReturnFlight.airlineID, selectedReturnFlight.flightNo, selectedReturnFlight.seatClass, new Timestamp(new Date().getTime()), buyerReturningPrice, 1);
                return "auctionError";
            } else if (buyerPrice > hiddenPrice) {
                addReservation(accountNo, selectedFlight.airlineID, selectedFlight.flightNo, selectedFlight.seatClass, new Timestamp(new Date().getTime()), buyerPrice, 1);
                addReservation(accountNo, selectedReturnFlight.airlineID, selectedReturnFlight.flightNo, selectedReturnFlight.seatClass, new Timestamp(new Date().getTime()), buyerReturningPrice, 0);
                return "auctionError";
            } else {
                addReservation(accountNo, selectedFlight.airlineID, selectedFlight.flightNo, selectedFlight.seatClass, new Timestamp(new Date().getTime()), buyerPrice, 0);
                addReservation(accountNo, selectedReturnFlight.airlineID, selectedReturnFlight.flightNo, selectedReturnFlight.seatClass, new Timestamp(new Date().getTime()), buyerReturningPrice, 0);
                return "auctionError";
            }

        }
    }

    public void addReservation(int accNo, String aID, int fNo, String cs, Timestamp date, double NYOP, int accepted) {
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
                String sql = "Insert into auctions values (?, ?, ?, ?, ?, ?, ?);";

                ps = con.prepareStatement(sql);
                ps.setInt(1, accNo);
                ps.setString(2, aID);
                ps.setInt(3, fNo);
                ps.setString(4, cs);
                ps.setTimestamp(5, date);
                ps.setDouble(6, NYOP);
                ps.setInt(7, accepted);
                System.out.println(ps);
                try {
                    ps.execute();
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
}
