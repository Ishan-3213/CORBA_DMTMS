package InterfaceOperationApp;


/**
* InterfaceOperationApp/InterfaceOperatiosOperations.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from ./InterfaceOperations.idl
* Friday, February 24, 2023 11:20:09 AM EST
*/

public interface InterfaceOperatiosOperations 
{
  String cancelMovieTickets (String customerID, String movieID, String movieName, int numberOfTickets);
  String addMovieSlots (String movieId, String movieName, int bookingCapacity);
  String removeMovieSlots (String movieId, String movieName);
  String listMovieShowsAvailability (String movieName);
  String getBookingSchedule (String customerID);
  String bookMovieTickets (String customerID, String movieId, String movieName, int numberOfTickets);
  String exchangeMovieTickets (String customerID, String movieId, String movieName, String new_movieId, String new_movieName, int numberOfTickets);
} // interface InterfaceOperatiosOperations
