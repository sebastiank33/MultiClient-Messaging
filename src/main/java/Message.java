import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

class Message implements Serializable { //class to pass messages between clients and server
    String data; //message
    String receivingClients; //list of clients to send to
    ArrayList<Integer> connectedClients = new ArrayList<>(); //list of connected clients

    public String getData() {
        return data;
    } //getter for message

}
