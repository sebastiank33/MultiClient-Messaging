import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import java.util.Objects;
import javafx.scene.control.Alert;

public class Controller implements Initializable {

    @FXML
    ListView<String> listItems, listItems2, clientsConnected;
    @FXML
    BorderPane startPane, serverPane, clientPane, hostPortPane;
    @FXML
    Button serverChoice, clientChoice, sendB, connect;
    @FXML
    TextField textMsg, whoToMsg, host, port;
    @FXML
    HBox buttonBox, whoBox, whatBox;
    @FXML
    VBox clientBox, hostPort;
    @FXML
    Label sendLabel, instructions, hostLabel, portLabel;

    Server serverConnection; // allows us to access the serverConnection from the server class
    Client clientConnection; // allows us to access the clientConnection from the client class

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    // event handler for "server" button on start scene
    public void server() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("server.fxml"));
        Parent serverPane = loader.load();             // load view into parent
        Controller myctr = loader.getController();     // get controller created by FXMLLoader
        serverPane.getStylesheets().add("style1.css"); // style the server scene with css file
        startPane.getScene().setRoot(serverPane);

        // create new serverConnection and receive data from client
        myctr.serverConnection = new Server(data -> {
            Platform.runLater(() -> {
                myctr.listItems.getItems().add(data.toString());
            });
        });
    }

    // event handler for "client" button on start scene
    public void client() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("hostAndPort.fxml"));
        Parent hostPort = loader.load();               // load view into parent
        hostPort.getStylesheets().add("style1.css");
        startPane.getScene().setRoot(hostPort);
    }

    // event handler for "connect" button on the hostPortPane screen
    public void connect() throws IOException {
        // if the Host IP address or Port text fields are empty
        if (Objects.equals(host.getText(), "") || Objects.equals(port.getText(), "")) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Host or port is empty.");
            alert.showAndWait();
        }
        else {
            String hostIP = host.getText();
            Integer portNum = Integer.parseInt(port.getText());
            System.out.println("Host: " + hostIP + "  Port: " + portNum);

            // load new screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("client.fxml"));
            Parent clientBox = loader.load();              // load view into parent
            Controller myctr = loader.getController();     // get controller created by FXMLLoader
            clientBox.getStylesheets().add("style1.css");
            hostPort.getScene().setRoot(clientBox);

            // create new clientConnection and receive data from server
            myctr.clientConnection = new Client(hostIP, portNum, data -> {
                Platform.runLater(() -> {
                    myctr.listItems2.getItems().add(data.toString());
                });
            });
            myctr.clientConnection.start(); // starts the client thread
            // */
        }
    }

    // event handler for when user sends message by pressing enter or the send button
    public void send() {
        // if send to textfield is empty
        if (Objects.isNull(whoToMsg.getText()) || Objects.equals(whoToMsg.getText(), "")) {
            Alert alert = new Alert(Alert.AlertType.WARNING,
                    "You must choose a client to send your message to. " +
                            "Type everyone to send to all clients or choose specific clients");
            alert.showAndWait();
        } else { // sends message to client
            Message message = new Message();
            message.data = textMsg.getText();
            message.receivingClients = whoToMsg.getText();
            clientConnection.send(message);
            textMsg.clear();
        }
    }
}
