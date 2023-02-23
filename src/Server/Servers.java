package Server;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.logging.Logger;

import Implementation.ImplementationOperations;
import InterfaceOperationApp.InterfaceOperatios;
import InterfaceOperationApp.InterfaceOperatiosHelper;
import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

public class Servers implements Runnable {
    int RMIPortNum;
    String server_name;
    Logger LogObj;
    Servers(int RMIPortNum, String server_name, Logger LogObj, String[] args){
        super();
            this.server_name = server_name;
            this.RMIPortNum = RMIPortNum;
            this.LogObj = LogObj;
    }

    public void serve_listener(ImplementationOperations impobj, Logger LogObj){
        DatagramSocket datasocket = null;
        String customer_id;
        String movie_id;
        String movie_name;
        String method;
        try {
            datasocket = new DatagramSocket(this.RMIPortNum);
            byte [] buffer = new byte[1024];
            System.out.println("Server ->" + this.server_name +"\n" + "Port ->" + this.RMIPortNum);

            while(true){
                DatagramPacket received = new DatagramPacket(buffer, buffer.length);
                datasocket.receive(received);

                String data = new String(buffer, 0, received.getLength());
                String [] splitted = data.split("<>");

                Integer tickets = Integer.parseInt(splitted[4]);
                customer_id = splitted[3];
                movie_id = splitted[2];
                movie_name = splitted[1];
                method = splitted[0];
                System.out.println(data+ "\n" +  method + "\n" + movie_name);
                switch(method){
                    case "list_movie":
                        String received_data = impobj.list_movie(movie_name);
                        System.out.println("----------" + received_data + "----------");
                        byte [] byte_data = received_data.getBytes();
                        DatagramPacket reply = new DatagramPacket(byte_data, received_data.length() ,received.getAddress(), received.getPort());
                        System.out.println("Message from the server " + server_name + " at the port " +RMIPortNum);
                        LogObj.info("Message from the server " + server_name + " in list_movie at the port " +RMIPortNum);
                        datasocket.send(reply);
                        break;
                    case "bookMovieTickets":
                        String data_received = impobj.bookMovieTickets(customer_id, movie_id, movie_name, tickets);
                        System.out.println("----------" + data_received + "----------");
                        byte [] data_byte = data_received.getBytes();
                        DatagramPacket response = new DatagramPacket(data_byte, data_received.length() ,received.getAddress(), received.getPort());
                        System.out.println("Message from the server " + server_name + " at the port " +RMIPortNum);
                        LogObj.info("Message from the server " + server_name + " in bookMovieTickets at the port " +RMIPortNum);
                        datasocket.send(response);
                        break;
                    case "booking_schedule":
                        String received_str = impobj.booking_schedule(customer_id);
                        System.out.println("----------" + received_str + "----------");
                        byte [] data_byt = received_str.getBytes();
                        DatagramPacket answer = new DatagramPacket(data_byt, received_str.length() ,received.getAddress(), received.getPort());
                        System.out.println("Message from the server " + server_name + " at the port " +RMIPortNum);
                        LogObj.info("Message from the server " + server_name + " in booking_schedule at the port " +RMIPortNum);
                        datasocket.send(answer);
                        break;
                    case "cancelMovieTickets":
                        String str_received = impobj.cancelMovieTickets(customer_id, movie_id, movie_name, tickets);
                        System.out.println("----------" + str_received + "----------");
                        byte [] byt_data = str_received.getBytes();
                        DatagramPacket acknowledgment = new DatagramPacket(byt_data, str_received.length() ,received.getAddress(), received.getPort());
                        System.out.println("Message from the server " + server_name + " at the port " +RMIPortNum);
                        LogObj.info("Message from the server " + server_name + " in cancelMovieTickets at the port " +RMIPortNum);
                        datasocket.send(acknowledgment);
                        break;
                    default:
                        System.out.println("Not working...!");
                }
            }
        }catch (SocketException e) {System.out.println("Something wrong with the SKT-ServerSide: " + e.getMessage());
        }catch(IOException e){System.out.println("Somthing went wrong in IO: " + e.getMessage());
        }finally{if(datasocket != null) datasocket.close();}
    }

    @Override
    public void run() {
        String[] args = new String[0];
        ORB orb = ORB.init(args, null);

        // get reference to rootpoa & activate the POAManager
        POA rootpoa = null;
        try {
            rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootpoa.the_POAManager().activate();
            // create servant and register it with the ORB
            ImplementationOperations impobj =  new ImplementationOperations(this.server_name, this.LogObj);
            impobj.setORB(orb);

            // get object reference from the servant
            org.omg.CORBA.Object ref = null;

            ref = rootpoa.servant_to_reference(impobj);
            InterfaceOperatios href = InterfaceOperatiosHelper.narrow(ref);
            // which is used to locate the remote object.
            org.omg.CORBA.Object objRef = null;

            objRef = orb.resolve_initial_references("NameService");
            // This line narrows the initial reference to a NamingContext,
            // which is used to look up the remote object.
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
            // bind the Object Reference in Naming
            NameComponent path[] = new NameComponent[0];

            path = ncRef.to_name(this.server_name);
            ncRef.rebind(path, href);
            System.out.println("Server is started at the PORT- "+ RMIPortNum);

            Runnable task = () -> {serve_listener(impobj, this.LogObj);};
            Thread t1 = new Thread(task);
            t1.start();

            orb.run();
        }catch (InvalidName e) {
            throw new RuntimeException(e);
        }catch (AdapterInactive e) {
            throw new RuntimeException(e);
        }catch (ServantNotActive e) {
            throw new RuntimeException(e);
        }catch (WrongPolicy e) {
            throw new RuntimeException(e);
        } catch (org.omg.CosNaming.NamingContextPackage.InvalidName e) {
            throw new RuntimeException(e);
        }catch (NotFound e) {
            throw new RuntimeException(e);
        } catch (CannotProceed e) {
            throw new RuntimeException(e);
        }
    }
}

