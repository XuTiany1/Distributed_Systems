package Server.Interface;

import java.rmi.Remote;
import java.util.*;

/** 
 * Simplified version from CSE 593 Univ. of Washington
 *
 * Distributed  System in Java.
 * 
 * failure reporting is done using two pieces, exceptions and boolean 
 * return values.  Exceptions are used for systemy things. Return
 * values are used for operations that would affect the consistency
 * 
 * If there is a boolean return value and you're not sure how it 
 * would be used in your implementation, ignore it.  I used boolean
 * return values in the interface generously to allow flexibility in 
 * implementation.  But don't forget to return true when the operation
 * has succeeded.
 */




 /**
 * IResourceManager.java
 * 
 * This interface defines the operations available for managing resources such as flights, cars, rooms, 
 * and customers in a distributed system. It is designed for use with Java RMI (Remote Method Invocation),
 * allowing clients to remotely add, delete, query, and reserve these resources. 
 * The interface provides flexibility with boolean return values indicating the success of operations.
 * 
 * Functions:
 * - addFlight(int flightNum, int flightSeats, int flightPrice): Adds a flight or updates seats and price for an existing flight.
 * - addCars(String location, int numCars, int price): Adds cars at a specified location.
 * - addRooms(String location, int numRooms, int price): Adds rooms at a specified location.
 * - newCustomer(): Creates a new customer and returns their unique ID.
 * - newCustomer(int cid): Creates a customer with the specified ID.
 * - deleteFlight(int flightNum): Deletes a flight if there are no reservations.
 * - deleteCars(String location): Deletes all cars at a specified location.
 * - deleteRooms(String location): Deletes all rooms at a specified location.
 * - deleteCustomer(int customerID): Deletes a customer and their reservations.
 * - queryFlight(int flightNumber): Queries the number of available seats for a flight.
 * - queryCars(String location): Queries the number of available cars at a location.
 * - queryRooms(String location): Queries the number of available rooms at a location.
 * - queryCustomerInfo(int customerID): Retrieves and returns a formatted bill for a customer.
 * - queryFlightPrice(int flightNumber): Queries the price of a seat on a flight.
 * - queryCarsPrice(String location): Queries the price of cars at a location.
 * - queryRoomsPrice(String location): Queries the price of rooms at a location.
 * - reserveFlight(int customerID, int flightNumber): Reserves a flight for a customer.
 * - reserveCar(int customerID, String location): Reserves a car for a customer at a location.
 * - reserveRoom(int customerID, String location): Reserves a room for a customer at a location.
 * - bundle(int customerID, Vector<String> flightNumbers, String location, boolean car, boolean room): Reserves a bundle of flights, car, and/or room for a customer.
 * - getName(): Returns the name of the resource manager.
 */

public interface IResourceManager extends Remote 
{
    /**
     * Add seats to a flight.
     *
     * In general this will be used to create a new
     * flight, but it should be possible to add seats to an existing flight.
     * Adding to an existing flight should overwrite the current price of the
     * available seats.
     *
     * @return Success
     */
    public boolean addFlight(int flightNum, int flightSeats, int flightPrice);
    
    /**
     * Add car at a location.
     *
     * This should look a lot like addFlight, only keyed on a string location
     * instead of a flight number.
     *
     * @return Success
     */
    public boolean addCars(String location, int numCars, int price);
   
    /**
     * Add room at a location.
     *
     * This should look a lot like addFlight, only keyed on a string location
     * instead of a flight number.
     *
     * @return Success
     */
    public boolean addRooms(String location, int numRooms, int price);
			    
    /**
     * Add customer.
     *
     * @return Unique customer identifier
     */
    public int newCustomer();
    
    /**
     * Add customer with id.
     *
     * @return Success
     */
    public boolean newCustomer(int cid);

    /**
     * Delete the flight.
     *
     * deleteFlight implies whole deletion of the flight. If there is a
     * reservation on the flight, then the flight cannot be deleted
     *
     * @return Success
     */   
    public boolean deleteFlight(int flightNum); 
    
    /**
     * Delete all cars at a location.
     *
     * It may not succeed if there are reservations for this location
     *
     * @return Success
     */		    
    public boolean deleteCars(String location); 

    /**
     * Delete all rooms at a location.
     *
     * It may not succeed if there are reservations for this location.
     *
     * @return Success
     */
    public boolean deleteRooms(String location); 
    
    /**
     * Delete a customer and associated reservations.
     *
     * @return Success
     */
    public boolean deleteCustomer(int customerID); 

    /**
     * Query the status of a flight.
     *
     * @return Number of empty seats
     */
    public int queryFlight(int flightNumber); 

    /**
     * Query the status of a car location.
     *
     * @return Number of available cars at this location
     */
    public int queryCars(String location); 

    /**
     * Query the status of a room location.
     *
     * @return Number of available rooms at this location
     */
    public int queryRooms(String location); 

    /**
     * Query the customer reservations.
     *
     * @return A formatted bill for the customer
     */
    public String queryCustomerInfo(int customerID); 
    
    /**
     * Query the status of a flight.
     *
     * @return Price of a seat in this flight
     */
    public int queryFlightPrice(int flightNumber); 

    /**
     * Query the status of a car location.
     *
     * @return Price of car
     */
    public int queryCarsPrice(String location); 

    /**
     * Query the status of a room location.
     *
     * @return Price of a room
     */
    public int queryRoomsPrice(String location); 

    /**
     * Reserve a seat on this flight.
     *
     * @return Success
     */
    public boolean reserveFlight(int customerID, int flightNumber); 

    /**
     * Reserve a car at this location.
     *
     * @return Success
     */
    public boolean reserveCar(int customerID, String location); 

    /**
     * Reserve a room at this location.
     *
     * @return Success
     */
    public boolean reserveRoom(int customerID, String location); 

    /**
     * Reserve a bundle for the trip.
     *
     * @return Success
     */
    public boolean bundle(int customerID, Vector<String> flightNumbers, String location, boolean car, boolean room); 

    /**
     * Convenience for probing the resource manager.
     *
     * @return Name
     */
    public String getName();
}
