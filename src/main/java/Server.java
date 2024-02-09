import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;

public class Server {

    int count = 1; //number of clients
    ArrayList<ClientThread> clients = new ArrayList<ClientThread>(); //list of clients
    TheServer server;
    private Consumer<Serializable> callback;

    Server(Consumer<Serializable> call) { //constructor
        callback = call;
        server = new TheServer();
        server.start();
    }

    public class TheServer extends Thread {
        public void run() { //standard run method for server
            try (ServerSocket mysocket = new ServerSocket(5555);) {
                System.out.println("Server is waiting for a client!");

                while (true) {
                    ClientThread c = new ClientThread(mysocket.accept(), count);
                    callback.accept("client has connected to server: " + "client #" + count);
                    clients.add(c);
                    c.start();
                    count++;
                }
            } //end of try
            catch (Exception e) {
                callback.accept("Server socket did not launch");
            }
        }//end of run
    }

    class ClientThread extends Thread {
        Socket connection;
        int ID; //client number
        ObjectInputStream in;   //server reading from client
        ObjectOutputStream out; //server writing to client
        String receivingClients = "everyone"; //default value for receivingClients

        ClientThread(Socket s, int count) { //constructor
            this.connection = s;
            this.ID = count;
            receivingClients = "everyone";
        }

        // server sending to client
        public void updateClients(String message, boolean update) {
            Message sendMessage = new Message();
            sendMessage.data = message;

            // if a client joins or leaves
            if (update) {
                sendMessage.connectedClients = new ArrayList<>();
                // update connectedClients
                for (ClientThread client : clients) {
                    sendMessage.connectedClients.add(client.ID);
                }
                // send this message to all clients
                for (ClientThread client : clients) {
                    try {
                        client.out.writeObject(sendMessage);
                    } catch (Exception e) {
                    }
                }
                return;
            }
            // if a client wants to send a message to everyone
            if (Objects.equals(receivingClients, "everyone")
                    || Objects.equals(receivingClients, "Everyone")) {
                for (ClientThread client : clients) {
                    try {
                        client.out.writeObject(sendMessage);
                    } catch (Exception e) {
                    }
                }
            }
            // if a client wants to send a message to certain clients only
            else {
                // parse string with a comma as a delimiter to get receiving clients ID
                String[] array = receivingClients.split(",", 0);
                ArrayList<Integer> receivingClientIDs = new ArrayList<>();

                // Build receivingClientsID
                for (String client : array) {
                    client = client.trim(); // get rid of white space
                    int i = 0;
                    // if string has no numbers
                    if (!client.matches("[0-9]")) {
                        i = -1;
                    } else {
                        i = Integer.parseInt(client);
                    }
                    receivingClientIDs.add(i);
                }
                // send msg to clients with matching clientIDs
                for (ClientThread client : clients) {
                    if (receivingClientIDs.contains(client.ID)) {
                        try {
                            client.out.writeObject(sendMessage);
                        } catch (Exception e) {
                        }
                    }
                }
            }
        }

        public void run() {
            try {
                in = new ObjectInputStream(connection.getInputStream());
                out = new ObjectOutputStream(connection.getOutputStream());
                connection.setTcpNoDelay(true);
            } catch (Exception e) {
                System.out.println("Streams not open");
            }
            updateClients("New client on server: client #" + ID, true);

            while (true) {
                try {
                    // server reading from client
                    Message message = (Message) in.readObject();
                    receivingClients = message.receivingClients;
                    callback.accept("client #" + ID + ": " + message.data);
                    updateClients("client #" + ID + ": " + message.data, false); //server sending to client
                } catch (Exception e) {
                    callback.accept("Something wrong with the socket from client #"
                            + ID + "....closing down!");
                    clients.remove(this); //remove client from list
                    updateClients("Client #" + ID + " has left the server!", true); //server sending to client
                    break;
                }
            }
        } //end of run
    } //end of client thread
}





