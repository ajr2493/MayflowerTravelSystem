/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package flight;

import java.sql.Date;
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
    private Date arriving;
    
    //One-Way, Round-Trip or Multi-Trip
    private String flightDirection;
    public flightSearch() {
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

    public Date getArriving() {
        return arriving;
    }

    public void setArriving(Date arriving) {
        this.arriving = arriving;
    }

    public String getFlightDirection() {
        return flightDirection;
    }

    public void setFlightDirection(String flightDirection) {
        this.flightDirection = flightDirection;
    }
    
}
