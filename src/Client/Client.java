package Client;
import java.time.LocalDate;
import java.util.*;

import InterfaceOperationApp.InterfaceOperatios;
import InterfaceOperationApp.InterfaceOperatiosHelper;
import Logs.Log;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import java.util.logging.Logger;
public class Client {
    static String user_id;
    static Integer RMIPortNumber;
    public static void main(String[] args) {
        Log logger;
        try (Scanner read = new Scanner(System.in)){
            System.out.println("\nPlease enter your UserID: ");
            user_id = (read.nextLine()).toUpperCase();
            while(user_id.length()!=8 ){
                System.out.println("\nPlease enter valid UserId !!");
                user_id = (read.nextLine()).toUpperCase();
            }
            while(true){
                if (user_id.startsWith("ATW")){
                    RMIPortNumber = 8001;
                    break;
                }else if (user_id.startsWith("VER")){
                    RMIPortNumber = 8002;
                    break;
                } else if (user_id.startsWith("OUT")) {
                    RMIPortNumber = 8003;
                    break;
                }else {
                    System.out.println("\nPlease enter valid UserId !!");
                    user_id = (read.nextLine()).toUpperCase();
                }
            }
            Logger LogObj = Logger.getLogger(user_id.substring(0,3));
            logger = new Log(user_id, true, false);
            LogObj = logger.attachFileHandlerToLogger(LogObj);

            ORB orb = ORB.init(args, null);
            org.omg.CORBA.Object objRef =orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
            InterfaceOperatios intOpr = InterfaceOperatiosHelper.narrow(ncRef.resolve_str(user_id.substring(0,3)));

            AvailableOptions(intOpr, user_id, LogObj);
        }catch (Exception e) {
            System.err.println("Server exception: " + e);
            e.printStackTrace();
        }
    }
    public static void AvailableOptions(InterfaceOperatios intOpr, String user_id, Logger LogObj) {
        int user_choice;
        boolean is_admin;
        String movieName;
        String movieID;
        int capacity;
        boolean login = true;
        boolean choice;
        try (Scanner read = new Scanner(System.in)){
            while(login){

                is_admin = user_id.substring(0, 4).endsWith("A") && (!user_id.substring(0, 4).endsWith("C"));
                choice = true;
                // Customer Options CLI
                if(!is_admin){
                    LogObj.info("Client has been logged with ID: " + user_id);
                    System.out.println();
                    System.out.println("\t Hey there Customer - " + user_id);
                    while (choice) {
                        System.out.println("Select the choice given below: ");
                        System.out.println();
                        System.out.println("1. Book movie tickets.");
                        System.out.println("2. List your booked movie tickets.");
                        System.out.println("3. Cancel movie tickets. ");
                        System.out.println("4. Exit.");
                        user_choice = Integer.parseInt(read.nextLine()) ;
                        switch (user_choice) {
                            case 1:
                                LogObj.info(user_id + " wants to book movie.");
                                System.out.println("Enter movie name you want to add from the option.");
                                System.out.println("AVATAR \t AVENGER \t TITANIC");
                                movieName = (read.nextLine()).toUpperCase();
                                if (movieName.isEmpty()){
                                    System.out.println("Please enter valid movie-Name\n");
                                    LogObj.info("Please enter valid movie-Name\n");
                                    break;
                                }
                                String movie_shows =intOpr.listMovieShowsAvailability(movieName);
                                if(movie_shows.isEmpty()){
                                    System.out.println("Sorry there is no show available for " + movieName);
                                    LogObj.info("Sorry there is no show available for " + movieName);
                                    break;
                                }
                                else{
                                    System.out.println();
                                    System.out.println("Here is the movie shows available for the "+movieName);
                                    System.out.println(movie_shows.replace("<>", "-"));
                                }
                                System.out.println();
                                System.out.println("Enter the movieId you want to book.");
                                movieID = (read.nextLine()).toUpperCase();
                                if (movieID.isEmpty()){
                                    System.out.println("Please enter valid movie-ID\n");
                                    LogObj.info(user_id + " User has entered invalid movie-ID\n");
                                    break;
                                }
                                long days = DaysLeft(movieID);
                                if (days>7 | days<0) {
                                    System.out.println("Can not book tickets later than 1 week/before today.\n");
                                    LogObj.info("Can not book tickets later than 1 week/before today.\n");
                                    break;
                                }
                                System.out.println("Please enter number of tickets for the movie " + movieName + "-" +movieID);
                                try {
                                    capacity = Integer.parseInt(read.nextLine());
                                    String data = intOpr.bookMovieTickets(user_id, movieID, movieName, capacity);
                                    System.out.println(data);
                                    LogObj.info(data);
                                    break;
                                }catch (NumberFormatException e){System.out.println("Please enter valid number!");}
                                break;
                            case 2:
                                System.out.println();
                                String booking_schedule = intOpr.getBookingSchedule(user_id);
                                if (booking_schedule.isEmpty()){
                                    System.out.println("There is no booked movie tickets found with the ID - " + user_id);
                                }else{
                                    System.out.println("Here is your booking schedule..!!\n");
                                    System.out.println(booking_schedule);
                                    LogObj.info(user_id + "'s booking schedule: \n" + booking_schedule);
                                }
                                break;
                            case 3:
                                System.out.println();

                                System.out.println("Please Enter UserId again: ");
                                String userId_cancel = (read.nextLine()).toUpperCase();

                                while(userId_cancel.length() > 8){
                                    System.out.println("Please enter valid UserId !!");
                                    userId_cancel = (read.nextLine()).toUpperCase();
                                }
                                while(!user_id.equals(userId_cancel)){
                                    System.out.println("Unauthorized..!! You are not logged in with the given userID!!");
                                    // userId_cancel = (read.nextLine()).toUpperCase();
                                    LogObj.info(user_id + " has no booked movie tickets.");
                                    break;
                                }
                                String booked_movie =intOpr.getBookingSchedule(user_id);
                                if(booked_movie.isEmpty()){
                                    System.out.println("There is no booked movie tickets found with the ID -" + user_id);
                                    LogObj.info(user_id + " has no booked movie tickets.");
                                    break;
                                }
                                else{
                                    System.out.println();
                                    System.out.println("Here is the booked shows with the userID - "+user_id);
                                    System.out.println(booked_movie + "\n");
                                }
                                System.out.println("Enter movie name you want to cancel from the option.");
                                System.out.println("AVATAR \t AVENGER \t TITANIC");
                                movieName = (read.nextLine()).toUpperCase();
                                if (!booked_movie.contains(movieName)){
                                    System.out.println("You have no show booked for the movie "+ movieName );
                                    LogObj.info(user_id + " has no booked for the movie "+ movieName);
                                    break;
                                }
                                System.out.println();
                                System.out.println("Enter the movieId you want to cancel.");
                                movieID = (read.nextLine()).toUpperCase();
                                if (!booked_movie.contains(movieID)){
                                    System.out.println("You have no show booked for the movieID "+ movieID );
                                    LogObj.info(user_id + " has no booked for the movieID "+ movieID);
                                    break;
                                }
                                System.out.println();
                                System.out.println("\nPlease enter number of tickets for the movie " + movieName + "-" +movieID);
                                capacity = Integer.parseInt(read.nextLine());
                                String reply = intOpr.cancelMovieTickets(user_id, movieID, movieName, capacity);
                                System.out.println(reply);
                                LogObj.info(reply);
                                break;
                            case 4:
                                choice = false;
                                login = false;
                                break;
                            default:
                                System.out.println("Invalid Choice..!!");
                                System.out.println();
                                break;
                        }
                    }
                }

                // Admin Options CLI
                else if(is_admin){
                    LogObj.info("Admin has logged in with Id: " + user_id);
                    System.out.println();
                    System.out.println("---------------\tHey there Admin("+user_id+") ---------------");
                    while (choice) {
                        System.out.println("*******\tSelect the choice given below\t*******");
                        System.out.println();
                        // String region = user_id.substring(0, 3);
                        System.out.println("1. Add movie slots.");
                        System.out.println("2. Remove movie slots.");
                        System.out.println("3. List out movie shows available.");
                        System.out.println("4. Book movie tickets.");
                        System.out.println("5. List your booked movie tickets.");
                        System.out.println("6. Cancel movie tickets / Exit. ");
//                    System.out.println("7. ");
                        user_choice = Integer.parseInt(read.nextLine());
                        switch (user_choice) {
                            case 1:
                                LogObj.info("Admin wants to add movie slots.");
                                System.out.println("Enter movie name you want to add from the option.");
                                System.out.println("AVATAR \t AVENGER \t TITANIC");
                                movieName = (read.nextLine()).toUpperCase();
                                if (movieName.isEmpty()){
                                    System.out.println("Please enter valid movie-Name\n");
                                    break;
                                }
                                System.out.println();
                                System.out.println("Enter movieId for the movie - " + movieName);
                                movieID = (read.nextLine()).toUpperCase();
                                long days = DaysLeft(movieID);
                                if (days>30 | days<0) {
                                    System.out.println("Can not add slots later than 1 months/before today.");
                                    break;
                                }
                                if(movieID.substring(0,3).equals(user_id.substring(0,3))){
                                    while(movieID.length()<11){
                                        System.out.println("\nPlease enter valid movie details..!!");
                                        System.out.println();
                                        System.out.println("Enter movie name you want to add from the option.");
                                        System.out.println("AVATAR\nAVENGER\nTITANIC");
                                        movieName = (read.nextLine()).toUpperCase();
                                        System.out.println();
                                        System.out.println("Enter movieId for the movie - " + movieName + " ");
                                        movieID = (read.nextLine()).toUpperCase();
                                    }
                                    System.out.println();
                                    System.out.println("Enter capacity for the Movie: " + movieName + " with the MovieId: "+ movieID);
                                    capacity = Integer.parseInt(read.nextLine());
                                    String response = intOpr.addMovieSlots(movieID, movieName, capacity);
                                    System.out.println(response);
                                    LogObj.info(response);
                                    break;
                                }else{
                                    System.out.println("You have no permission to add movies in region! " + movieID.substring(0,3));
                                    LogObj.info("You have no permission to add movies in region! " + movieID.substring(0,3));
                                    break;
                                }
                            case 2:
                                System.out.println("Enter movie name you want to remove from the option.");
                                System.out.println("AVATAR \t AVENGER \t TITANIC");
                                movieName = (read.nextLine()).toUpperCase();
                                System.out.println();
                                System.out.println("Enter movieId for the movie - " + movieName);
                                movieID = (read.nextLine()).toUpperCase();
                                long remove_day_check = DaysLeft(movieID);
                                if (remove_day_check>30 | remove_day_check<0) {
                                    System.out.println("Can not remove slots later than 1 months/before today.");
                                    break;
                                }
                                while(movieID.length() < 11 | movieName.isEmpty()){
                                    System.out.println("Please enter valid movie details..!!");
                                    System.out.println();
                                    System.out.println("Enter movie name you want to add from the option.");
                                    System.out.println("AVATAR \t AVENGER \t TITANIC");
                                    movieName = (read.nextLine()).toUpperCase();
                                    System.out.println();
                                    System.out.println("Enter movieId for the movie - " + movieName);
                                    movieID = (read.nextLine()).toUpperCase();
                                }
                                String data = intOpr.removeMovieSlots(movieID, movieName);
                                System.out.println(data);
                                LogObj.info(data);
                                break;
                            case 3:
                                LogObj.info("Admin wants to list movie shows.");
                                System.out.println();
                                System.out.println("Enter movie name you want to add from the option.");
                                System.out.println("\nAVATAR\nAVENGER\nTITANIC");
                                movieName = (read.nextLine()).toUpperCase();
                                while(movieName.isEmpty()){
                                    System.out.println();
                                    System.out.println("Enter movie name you want to add from the option.");
                                    System.out.println("AVATAR \t AVENGER \t TITANIC");
                                    movieName = (read.nextLine()).toUpperCase();
                                }
                                String movie_shows = intOpr.listMovieShowsAvailability(movieName);
                                if(movie_shows.isEmpty()){
                                    System.out.println();
                                    System.out.println("Sorry there is no show available for-> " + movieName + "\n");
                                    LogObj.info("Sorry there is no show available for-> " + movieName);
                                }
                                else{
                                    System.out.println();
                                    System.out.println("Here is the movie shows available for the "+movieName);
                                    System.out.println(movie_shows.replace("<>", "-"));
                                    LogObj.info(movie_shows.replace("<>", "-"));
                                }
                                break;
                            case 4:
                                System.out.println();
                                LogObj.info(user_id + " wants to book movie.");
                                System.out.println("Enter movie name you want to add from the option.");
                                System.out.println("AVATAR \t AVENGER \t TITANIC");
                                movieName = (read.nextLine()).toUpperCase();
                                String available_movie_shows =intOpr.listMovieShowsAvailability(movieName);
                                if(available_movie_shows.isEmpty()){
                                    System.out.println("Sorry there is no show available for " + movieName);
                                    break;
                                }
                                else{
                                    System.out.println();
                                    System.out.println("Here is the movie shows available for the "+movieName);
                                    System.out.println(available_movie_shows.replace("<>", "-"));
                                }
                                System.out.println();
                                System.out.println("Enter the movieId you want to book.");
                                movieID = (read.nextLine()).toUpperCase();
                                long days_check = DaysLeft(movieID);
                                if (days_check>7 | days_check<0) {
                                    System.out.println("Can not book tickets later than 1 week.");
                                    break;
                                }
                                System.out.println();
                                System.out.println("Please enter number of tickets for the movie " + movieName + "-" +movieID);
                                capacity = Integer.parseInt(read.nextLine());
                                String received_data = intOpr.bookMovieTickets(user_id, movieID, movieName, capacity);
                                System.out.println(received_data);
                                LogObj.info(received_data);
                                break;
                            case 5:
                                System.out.println();
                                System.out.println("Enter UserId: ");
                                String userId_booking = (read.nextLine()).toUpperCase();
                                while(userId_booking.length()>8){
                                    System.out.println("Please enter valid UserId !!");
                                    userId_booking = (read.nextLine()).toUpperCase();
                                }
                                String booking_schedule = intOpr.getBookingSchedule(userId_booking);
                                if (booking_schedule.isEmpty()){
                                    System.out.println("There is no booked movie tickets found with the ID - " + userId_booking);
                                }else{
                                    System.out.println("Here is your booking schedule..!!");
                                    System.out.println(booking_schedule);
                                }
                                break;
                            case 6:
//                        case 7:
                                System.out.println("Logging out from the - " + user_id);
                                choice = false;
                                login = false;
                                break;
                            default:
                                System.out.println();
                                System.out.println("!!..Invalid Choice..!!");
                                break;
                        }
                    }
                }
            }
        }
    }

    private static long DaysLeft(String MovieID){
        String movieID = MovieID.substring(4,12);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
        LocalDate movie_date = LocalDate.parse(movieID, formatter);
        LocalDate currentDate = LocalDate.now();
        long days = ChronoUnit.DAYS.between(currentDate, movie_date);
        return days;
    }
}
