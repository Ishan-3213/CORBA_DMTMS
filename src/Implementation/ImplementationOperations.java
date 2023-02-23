package Implementation;
import InterfaceOperationApp.InterfaceOperatiosPOA;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.logging.Logger;
import org.omg.CORBA.ORB;

public class ImplementationOperations extends InterfaceOperatiosPOA {
    HashMap<String, HashMap<String, Integer>> datastorage;
    HashMap<String, HashMap<String, Integer>> user_data;
    HashMap<String, Integer> booking_hashmap;
    HashMap<String, Integer> customer_booking_hashmap;
    String server_name;
    Logger LogObj;
    private ORB orb;

    public ImplementationOperations(String server_name, Logger LogObj) {
        super();
        this.server_name = server_name;
        this.LogObj = LogObj;
        datastorage = new HashMap<>();
        user_data = new HashMap<>();
        booking_hashmap = new HashMap<String, Integer>();
        customer_booking_hashmap = new HashMap<String, Integer>();

        datastorage.put("AVATAR", new HashMap<String, Integer>());
        datastorage.put("AVENGER", new HashMap<String, Integer>());
        datastorage.put("TITANIC", new HashMap<String, Integer>());

        if(server_name.equals("ATW")){

            booking_hashmap = datastorage.get("AVENGER");
            booking_hashmap.put("ATWM24022023", 50);
            booking_hashmap.put("ATWE23042023", 50);
            datastorage.put("AVENGER", booking_hashmap);

            booking_hashmap = datastorage.get("TITANIC");
            booking_hashmap.put("ATWA13042023", 50);
            datastorage.put("TITANIC", booking_hashmap);

            user_data.put("ATWC1234", new HashMap<String, Integer>());

            customer_booking_hashmap = user_data.get("ATWC1234");
            customer_booking_hashmap.put("AVENGER-ATWM13042023", 5);
            user_data.put("ATWC1234", customer_booking_hashmap);

        }else if(server_name.equals("VER")){
            booking_hashmap = datastorage.get("AVATAR");
            booking_hashmap.put("VERA23042023", 50);
            datastorage.put("AVATAR", booking_hashmap);

            booking_hashmap = datastorage.get("TITANIC");
            booking_hashmap.put("VERE23042023", 50);
            booking_hashmap.put("VERM20042023", 50);
            datastorage.put("TITANIC", booking_hashmap);

            user_data.put("VERC4321", new HashMap<String, Integer>());

            customer_booking_hashmap = user_data.get("VERC4321");
            customer_booking_hashmap.put("TITANIC-VERM20042023", 10);
            user_data.put("VERC4321", customer_booking_hashmap);

        }else if(server_name.equals("OUT")){

            booking_hashmap = datastorage.get("AVATAR");
            booking_hashmap.put("OUTM16022023", 100);
            booking_hashmap.put("OUTE15022023", 100);
            datastorage.put("AVATAR", booking_hashmap);

            booking_hashmap = datastorage.get("AVENGER");
            booking_hashmap.put("OUTA20042023", 100);
            booking_hashmap.put("OUTM24022023", 40);
            datastorage.put("AVENGER", booking_hashmap);

            user_data.put("OUTC4321", new HashMap<String, Integer>());
            customer_booking_hashmap = user_data.get("OUTC4321");
            customer_booking_hashmap.put("AVENGER-OUTM23042023", 10);
            user_data.put("OUTC4321", customer_booking_hashmap);
        }
    }
    public void setORB(ORB orb_val) {
        orb = orb_val;
    }
    @Override
    public String cancelMovieTickets(String customerID, String movieID, String movieName, int numberOfTickets) {
        if (movieID.substring(0,3).equals(this.server_name)){
            if(user_data.containsKey(customerID)){
                String movie_string = movieName + "-" + movieID;
                if(user_data.get(customerID).containsKey(movie_string)){
                    if(user_data.get(customerID).get(movie_string) > numberOfTickets){
                        user_data.get(customerID).put(movie_string, user_data.get(customerID).get(movie_string) - numberOfTickets);
                        datastorage.get(movieName).put(movieID, datastorage.get(movieName).get(movieID) + numberOfTickets);
                        System.out.println(user_data.get(customerID).get(movie_string) + " IF---Here is the user data " + "\n------>>" + "\n...custome id"+user_data.get(customerID)+"\n ehole user data" + user_data);
                        return numberOfTickets + " Movie tickets for " + movieName + " has been removed";
                    }else if((user_data.get(customerID).get(movie_string).equals(numberOfTickets))){
                        datastorage.get(movieName).put(movieID, datastorage.get(movieName).get(movieID) + numberOfTickets);
                        user_data.get(customerID).remove(movie_string, user_data.get(customerID).get(movie_string));
                        System.out.println(user_data.get(customerID).get(movie_string) + " ELSE Here is the user data " + "\n------>>" + "\n...custome id"+user_data.get(customerID)+"\n ehole user data" + user_data);
                        return numberOfTickets + " Movie tickets for " + movieName + " has been removed";
                    }else{
                        return "Please enter valid ticket number to be removed!!";
                    }
                }else{
                    return "No movie found with the movieID- " + movieID;
                }
            }else{
                return "There is no userdata found with the id " + customerID;
            }
        }else {
            StringBuilder sBuilder = new StringBuilder();
            // method + "<>" + movie_name + "<>" + movie_id + "<>" + customer_id + "<>" + tickets
            String methodsList = "cancelMovieTickets" + "<>" + movieName + "<>" + movieID + "<>" + customerID + "<>" + numberOfTickets;
            if(movieID.substring(0, 3).equals("VER") && !(movieID.substring(0,3).equals(this.server_name))){
                sBuilder.append(sending_message(methodsList, "VER", 8002));
            }else if(movieID.substring(0, 3).equals("OUT") && !(movieID.substring(0,3).equals(this.server_name))){
                sBuilder.append(sending_message(methodsList, "OUT", 8003));
            }else if(movieID.substring(0, 3).equals("ATW") && !(movieID.substring(0,3).equals(this.server_name))){
                sBuilder.append(sending_message(methodsList, "ATW", 8001));
            }
            return sBuilder.toString();
        }    }

    @Override
    public String addMovieSlots(String movieId, String movieName, int bookingCapacity) {
        try {
            if (datastorage.containsKey(movieName)){
                if (datastorage.get(movieName).containsKey(movieId)) {
                    datastorage.get(movieName).put(movieId, bookingCapacity + datastorage.get(movieName).get(movieId));
                    System.out.println();
                    System.out.println("Movie's slot with the ID " + movieId + " has been updated!");
                    System.out.println(datastorage);
                    LogObj.info("Movie slot updated.");
                    return "Movie slot updated.";
                }else{
                    datastorage.get(movieName).put(movieId, bookingCapacity);
                    System.out.println("Data has been added" + datastorage);
                    LogObj.info("Movie slot updated.");
                    return "Movie slot added.";
                }
            }else{
                System.out.println();
                System.out.println("Movie is not there..!!");
                booking_hashmap.put(movieId, bookingCapacity);
                datastorage.put(movieName, booking_hashmap);
                System.out.println("Movie slot has been added..!!" + datastorage);
                LogObj.info("New Movie slot added.");
                return "New movie slot has been added..!!";
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }    }

    @Override
    public String bookMovieTickets(String customerID, String movieId, String movieName, int numberOfTickets)  {
        String methodsList;
        StringBuilder sBuilder = new StringBuilder();

        if(customerID.substring(0,3).equals(server_name)){}else
        {
            if(numberOfTickets >3){
                System.out.println("You cannot book more than 3 tickets on other theater");
                return "You cannot book more than 3 tickets on other theater";
            }
        }
//        String [] check_data = getBookingSchedule(customerID).split("\n");
        for (String check_data : getBookingSchedule(customerID).split("\n")){
            if (check_data.contains(movieName) && check_data.contains(movieId.substring(4,12))) {
                System.out.println("Already Booked ticket in another theater!");
                return "Tickets are already booked in another theater.";
            }
        }

        if(movieId.substring(0,3).equals(this.server_name))  {
            if(datastorage.containsKey(movieName)){
                if(datastorage.get(movieName).containsKey(movieId)){
                    if(datastorage.get(movieName).get(movieId) > numberOfTickets){
                        System.out.println("Tickets in the server----->>" + server_name +  "\nStrg-->" + datastorage.get(movieName).get(movieId));
                        datastorage.get(movieName).put(movieId, datastorage.get(movieName).get(movieId) - numberOfTickets);
                        System.out.println("\n\n" + " tickets removal done-->> " + datastorage.get(movieName).get(movieId));
                        // Tickets are available 0
                        String movie_string = movieName + "-" + movieId;
                        if (user_data.containsKey(customerID)){
                            System.out.println("\n\nUSer data in server " + server_name + " data-->" + user_data);
                            if (user_data.get(customerID).containsKey(movie_string) ){
                                user_data.get(customerID).put(movie_string, user_data.get(customerID).get(movie_string) + numberOfTickets);
                                System.out.println(user_data.get(customerID) + " --Already booked tickets for the same movie id-- " + customer_booking_hashmap);
                                LogObj.info(numberOfTickets + " tickets booked for the movie " + movieName + "-" + movieId);

                                return numberOfTickets + " tickets booked for the movie " + movieName + "-" + movieId;
                            }else{
                                // add data to the existing cutomer id in hashmap
                                user_data.get(customerID).put(movie_string, numberOfTickets);
                                System.out.println("Customer-ID --->>"+ customerID + " ---" + user_data.get(customerID) + " --Tickets added -- " + customer_booking_hashmap);
                                LogObj.info(numberOfTickets + " tickets booked for the movie " + movieName + "-" + movieId);
                                return numberOfTickets + " tickets booked for the movie " + movieName + "-" + movieId;
                            }
                        }else{
                            // create new customer id in hasmap
                            user_data.putIfAbsent(customerID, new HashMap<String, Integer>());
                            customer_booking_hashmap = user_data.get(customerID);
                            customer_booking_hashmap.put(movie_string, numberOfTickets);
                            user_data.put(customerID, customer_booking_hashmap);
                            System.out.println(user_data.get(customerID) + " --New User-- " + user_data);
                            LogObj.info(numberOfTickets + " tickets booked for the movie " + movieName + "-" + movieId);
                            return numberOfTickets + " tickets booked for the movie " + movieName + "-" + movieId;
                        }
                    }else{
                        LogObj.info(numberOfTickets + " Seats are not available for the " + movieName + " - " + movieId);
                        return numberOfTickets + " Seats are not available for the " + movieName + " - " + movieId;
                    }
                }else{
                    LogObj.info("No movie found with the ID" + movieId);
                    return "No movie found with the ID" + movieId;
                }
            }
            LogObj.info("No movie found with the namw" + movieName);
            return "No movie found with the name " + movieName;
        }else{
            // method + "<>" + movie_name + "<>" + movie_id + "<>" + customer_id + "<>" + tickets
            methodsList = "bookMovieTickets" + "<>" + movieName + "<>" + movieId + "<>" + customerID + "<>" + numberOfTickets;
            if(movieId.substring(0, 3).equals("VER") && !(movieId.substring(0,3).equals(this.server_name))){
                sBuilder.append(sending_message(methodsList, "VER", 8002));
            }else if(movieId.substring(0, 3).equals("OUT") && !(movieId.substring(0,3).equals(this.server_name))){
                sBuilder.append(sending_message(methodsList, "OUT", 8003));
            }else if(movieId.substring(0, 3).equals("ATW") && !(movieId.substring(0,3).equals(this.server_name))){
                sBuilder.append(sending_message(methodsList, "ATW", 8001));
            }
            return sBuilder.toString();
//        if(count<=3){
//        }else{
//            return "Customer with the id " + customerID + " has exceeded the limit of booking in other area.";
//        }

        }
    }
    @Override
    public String removeMovieSlots(String movieId, String movieName) {
        if(datastorage.containsKey(movieName)){
            if (datastorage.get(movieName).containsKey(movieId)){
                datastorage.get(movieName).remove(movieId, datastorage.get(movieName).get(movieId));
                System.out.println();
                System.out.println(datastorage.get(movieName) +" after removal..!! ");
                LogObj.info("Movie slot for " + movieName + " has been removed");
                return "Movie slot for " + movieName + " has been removed";
            }else {
                System.out.println("there is no movie slot for this movie..!!!");
                LogObj.info("No movie slot found for the movie " + movieName + " at " + movieId.substring(0, 3) + " region");
                return "No movie slot found for the movie " + movieName + " at " + movieId.substring(0, 3) + " region";
            }
        }else{
            LogObj.info("No movie slot found for the movie " + movieName + " at " + movieId.substring(0, 3) + " region");
            return "No movie slot found for the movie " + movieName + " at " + movieId.substring(0, 3) + " region";
        }    }

    @Override
    public String listMovieShowsAvailability(String movieName) {
        StringBuilder sBuilder = new StringBuilder();
        if (datastorage.containsKey(movieName)){
            // method + "<>" + movie_name + "<>" + movie_id + "<>" + customer_id + "<>" + tickets
            String data = this.UDPcall("list_movie" + "<>" + movieName + "<>" + null + "<>" + null + "<>" + 0);
            data = data.replace(movieName+"<>", "");
            sBuilder.append(data);
            System.out.println("----------------------------------------------------------\n" +sBuilder.toString() + "\n----------------------------------------\n" );
            for (String OuterKey : this.datastorage.keySet()) {
                System.out.println(OuterKey);
                if(OuterKey.equals(movieName)){
                    for (String InnerKey : this.datastorage.get(OuterKey).keySet()){
                        sBuilder.append(InnerKey).append("<>").append(this.datastorage.get(OuterKey).get(InnerKey)).append("\n");
                    }
                }
            }
            LogObj.info("List of movie shows has been shown.");
            return sBuilder.toString().replace(movieName + "<>", "");
        }else{
            return "No movie slot found for the movie " + movieName;
        }
    }

    @Override
    public String getBookingSchedule(String customerID) {
        StringBuilder sBuilder = new StringBuilder();
        if (this.user_data.containsKey(customerID)){
            for (String x: this.user_data.keySet()){
                if (x.equals(customerID)){
                    for (String InnerKey: this.user_data.get(x).keySet()){
                        sBuilder.append(InnerKey).append(":").append(this.user_data.get(x).get(InnerKey)).append("\n");
                    }
                }
            }
        }
        // method + "<>" + movie_name + "<>" + movie_id + "<>" + customer_id + "<>" + tickets
        String data = this.UDPcall("booking_schedule" + "<>" + null + "<>" + null + "<>" + customerID + "<>" + 0);
        return sBuilder.toString() + data;    }
    public String booking_schedule(String customerID){

        StringBuilder sb = new StringBuilder();
        for (String OuterKey : this.user_data.keySet()) {
            System.out.println(customerID + " OuterKey---->> " + OuterKey);
            if(OuterKey.equals(customerID)){
                for (String InnerKey : this.user_data.get(OuterKey).keySet()){
                    sb.append(InnerKey).append(":").append(this.user_data.get(OuterKey).get(InnerKey)).append("\n");
                }
            }
        }
        return  sb.toString();
    }

    public String UDPcall(String methodsList) {

        StringBuilder sb = new StringBuilder();
        if(this.server_name.equals("ATW")) {
            sb.append(sending_message(methodsList, "OUT", 8003));
            sb.append(sending_message(methodsList, "VER", 8002));
        }else if(this.server_name.equals("VER")){
            sb.append(sending_message(methodsList, "OUT", 8003));
            sb.append(sending_message(methodsList, "ATW", 8001));

        }else if(this.server_name.equals("OUT")){
            sb.append(sending_message(methodsList, "ATW", 8001));
            sb.append(sending_message(methodsList, "VER", 8002));
        }
        return sb.toString();
    }

    public String list_movie(String movie_name){
        StringBuilder sb = new StringBuilder();
        for (String OuterKey : this.datastorage.keySet()) {
            System.out.println(OuterKey);
            if(OuterKey.equals(movie_name)){
                for (String InnerKey : this.datastorage.get(OuterKey).keySet()){
                    sb.append(OuterKey).append("<>").append(InnerKey).append("<>").append(this.datastorage.get(OuterKey).get(InnerKey)).append("\n");
                }
            }
        }
        return  sb.toString();
    }

    public String sending_message(String method_name , String server_name, Integer PortNumber) {
        // args give message contents and destination hostname


        DatagramSocket datasocket = null;
        try{
            System.out.println("server is--->>" + server_name + " \nport-number" + PortNumber);
            datasocket = new DatagramSocket();
            byte[] arguments = method_name.getBytes();
            InetAddress host_name = InetAddress.getLocalHost();

            DatagramPacket request = new DatagramPacket(arguments, method_name.length(), host_name, PortNumber);
            datasocket.send(request);

            byte[] response = new byte[1024];
            DatagramPacket reply = new DatagramPacket(response, response.length);
            datasocket.receive(reply);
            String data = new String(response, 0, reply.getLength());
            // String [] splitted = data.split("<>");

            System.out.println("Here is the data you recieved...!! " + (data).toString());
            return data;
        }catch(SocketException e){ System.out.println("Something went wrong with SKT: " + e.getMessage());
        }catch(IOException e){System.out.println("Something went wrong in IO: " + e.getMessage());
        }finally{if(datasocket != null){datasocket.close();}
        }
        return "Udp connection not worked..!!";
    }
}
