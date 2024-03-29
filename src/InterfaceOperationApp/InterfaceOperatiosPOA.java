package InterfaceOperationApp;


/**
* InterfaceOperationApp/InterfaceOperatiosPOA.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from ./InterfaceOperations.idl
* Thursday, February 23, 2023 1:34:51 AM EST
*/

public abstract class InterfaceOperatiosPOA extends org.omg.PortableServer.Servant
 implements InterfaceOperationApp.InterfaceOperatiosOperations, org.omg.CORBA.portable.InvokeHandler
{

  // Constructors

  private static java.util.Hashtable _methods = new java.util.Hashtable ();
  static
  {
    _methods.put ("cancelMovieTickets", new java.lang.Integer (0));
    _methods.put ("addMovieSlots", new java.lang.Integer (1));
    _methods.put ("removeMovieSlots", new java.lang.Integer (2));
    _methods.put ("listMovieShowsAvailability", new java.lang.Integer (3));
    _methods.put ("getBookingSchedule", new java.lang.Integer (4));
    _methods.put ("bookMovieTickets", new java.lang.Integer (5));
  }

  public org.omg.CORBA.portable.OutputStream _invoke (String $method,
                                org.omg.CORBA.portable.InputStream in,
                                org.omg.CORBA.portable.ResponseHandler $rh)
  {
    org.omg.CORBA.portable.OutputStream out = null;
    java.lang.Integer __method = (java.lang.Integer)_methods.get ($method);
    if (__method == null)
      throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);

    switch (__method.intValue ())
    {
       case 0:  // InterfaceOperationApp/InterfaceOperatios/cancelMovieTickets
       {
         String customerID = in.read_string ();
         String movieID = in.read_string ();
         String movieName = in.read_string ();
         int numberOfTickets = in.read_long ();
         String $result = null;
         $result = this.cancelMovieTickets (customerID, movieID, movieName, numberOfTickets);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       case 1:  // InterfaceOperationApp/InterfaceOperatios/addMovieSlots
       {
         String movieId = in.read_string ();
         String movieName = in.read_string ();
         int bookingCapacity = in.read_long ();
         String $result = null;
         $result = this.addMovieSlots (movieId, movieName, bookingCapacity);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       case 2:  // InterfaceOperationApp/InterfaceOperatios/removeMovieSlots
       {
         String movieId = in.read_string ();
         String movieName = in.read_string ();
         String $result = null;
         $result = this.removeMovieSlots (movieId, movieName);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       case 3:  // InterfaceOperationApp/InterfaceOperatios/listMovieShowsAvailability
       {
         String movieName = in.read_string ();
         String $result = null;
         $result = this.listMovieShowsAvailability (movieName);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       case 4:  // InterfaceOperationApp/InterfaceOperatios/getBookingSchedule
       {
         String customerID = in.read_string ();
         String $result = null;
         $result = this.getBookingSchedule (customerID);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       case 5:  // InterfaceOperationApp/InterfaceOperatios/bookMovieTickets
       {
         String customerID = in.read_string ();
         String movieId = in.read_string ();
         String movieName = in.read_string ();
         int numberOfTickets = in.read_long ();
         String $result = null;
         $result = this.bookMovieTickets (customerID, movieId, movieName, numberOfTickets);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       default:
         throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
    }

    return out;
  } // _invoke

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:InterfaceOperationApp/InterfaceOperatios:1.0"};

  public String[] _all_interfaces (org.omg.PortableServer.POA poa, byte[] objectId)
  {
    return (String[])__ids.clone ();
  }

  public InterfaceOperatios _this() 
  {
    return InterfaceOperatiosHelper.narrow(
    super._this_object());
  }

  public InterfaceOperatios _this(org.omg.CORBA.ORB orb) 
  {
    return InterfaceOperatiosHelper.narrow(
    super._this_object(orb));
  }


} // class InterfaceOperatiosPOA
