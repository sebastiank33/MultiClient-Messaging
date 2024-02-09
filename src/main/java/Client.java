import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.function.Consumer;

public class Client extends Thread {

    Socket socketClient;
    ObjectOutputStream out;
    ObjectInputStream in;
    private Consumer<Serializable> callback;
    String hostIP;
    Integer port;

    // constructor to initialize callback
    Client(String host, Integer port, Consumer<Serializable> call) {
        callback = call;
        this.hostIP = host;
        this.port = port;
    }

    // called when start() is
    public void run() {
        try {
            System.out.println("host IP Address: " + hostIP + " Port: " + port);
            socketClient = new Socket(hostIP, port);
            out = new ObjectOutputStream(socketClient.getOutputStream());
            in = new ObjectInputStream(socketClient.getInputStream());
            socketClient.setTcpNoDelay(true);
        } catch (Exception e) {
            System.out.println("Cannot connect to server at given IP address and port");
        }

        while (true) {
            try {
                // client reading from server
                Message message = (Message) in.readObject();
                if (message.connectedClients.size() > 0) { // if new client connects
                    String newClientList = "Connected Clients: ";
                    int size = message.connectedClients.size(); int count = 0;
                    for (int client : message.connectedClients) { // iterate through new list of connected clients
                        if (count == size - 1) {
                            newClientList += "#" + client;
                        } else {
                            newClientList += "#" + client + ", "; // to build new clients string
                        }
                        count++;
                    }
                    // send message along with the new client list to the GUI
                    callback.accept(message.data + "\n" + newClientList);
                } else {  // just send message
                    callback.accept(message.data);
                }
            } catch (Exception e) {
                System.out.println("Error");
            }
        }
    }

    // client sending to server
    public void send(Message message) {
        try {
            out.writeObject(message);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}


