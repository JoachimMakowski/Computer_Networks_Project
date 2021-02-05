package client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RoomController {

    @FXML
    private TextField newRoomName;
    @FXML
    private Label newRoomNameError;
    @FXML
    private Label newRoomTakenError;
    @FXML
    private Label newRoomLongerError;
    @FXML
    private Button createRoomCreateButton;

    @FXML
    private TextField roomNameText;
    @FXML
    private Button joinRoomCancelButton;
    @FXML
    private Label noRoomNameError;
    @FXML
    private Label emptyRoomNameError;
    @FXML
    private Label noSpaceError;

    public ListView<String> primaryRoomList;

    private String nickname;
    private Socket clientSocket;
    private PrintWriter writer;
    private BufferedReader reader;

    @FXML
    void initialize() {

    }

    /**
     * Method to initialize data in the create/join room screen and pass the connection data
    */

    void initialData(String nick, Socket socket,PrintWriter printWriter, BufferedReader bufferedReader) {
        this.nickname = nick;
        this.clientSocket = socket;
        this.writer = printWriter;
        this.reader = bufferedReader;
    }

    /**
     * Method for data validation when creating new room
     * @throws IOException
     */

    @FXML
    public void checkNewRoom() throws IOException {
        newRoomNameError.setVisible(false);
        newRoomTakenError.setVisible(false);
        newRoomLongerError.setVisible(false);
        noSpaceError.setVisible(false);

        if (newRoomName.getText() == null || newRoomName.getText().trim().isEmpty()) {
            newRoomNameError.setVisible(true);
        }
        if (newRoomName.getText().trim().length() <= 20 && newRoomName.getText().trim().length() > 0) {

            String roomName = "3\n" + newRoomName.getText();
            System.out.println(roomName);
            writer.println(roomName);

            String serverRoomMessage = reader.readLine().trim();
            System.out.println(serverRoomMessage);

            if ("30".equals(serverRoomMessage)) {
                System.out.println("Created new room #" + newRoomName.getText());
                primaryRoomList.getItems().add("#" + newRoomName.getText());
                Stage thisStage = (Stage) createRoomCreateButton.getScene().getWindow();
                thisStage.close();
            } else if ("31".equals(serverRoomMessage)) {
                newRoomTakenError.setVisible(true);
            } else if ("32".equals(serverRoomMessage)) {
                noSpaceError.setVisible(true);
            }

        }
        if (newRoomName.getText().trim().length() > 20){
            newRoomLongerError.setVisible(true);
        }
    }

    /**
     * Method for data validation when joining new room, checks if the room with the given name is already on the server
     * @throws IOException
     */

    @FXML
    public void checkRoomToJoin() throws IOException {
        noRoomNameError.setVisible(false);
        emptyRoomNameError.setVisible(false);
        if (roomNameText.getText() == null || roomNameText.getText().trim().isEmpty()) {
            emptyRoomNameError.setVisible(true);
        }
        else {
            String joinRoom = "2\n" + roomNameText.getText() + "\n" + nickname;
            System.out.println(joinRoom);
            writer.println(joinRoom);

            String serverRoomMessage = reader.readLine().trim();
            System.out.println(serverRoomMessage);

            if ("20".equals(serverRoomMessage)){
                System.out.println("JOINING ROOM " + roomNameText.getText());
                Stage thisStage = (Stage) joinRoomCancelButton.getScene().getWindow();
                thisStage.close();
            }
            else if ("21".equals(serverRoomMessage)){
                noRoomNameError.setVisible(true);
            }
        }
    }

    /**
     * Method to handle disabling the application 
     * @param event
     */

    @FXML
    private void closeButtonAction(ActionEvent event){
        Button button = (Button) event.getSource();
        Stage stage = (Stage) button.getScene().getWindow();
        stage.close();
    }

}
