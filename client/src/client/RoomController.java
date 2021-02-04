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
    private Button createRoomCancelButton;

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

    void initialData(String nick, Socket socket,PrintWriter printWriter, BufferedReader bufferedReader) {
        this.nickname = nick;
        this.clientSocket = socket;
        this.writer = printWriter;
        this.reader = bufferedReader;
    }

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

            if ("30".equals(serverRoomMessage)) {
                System.out.println("Created new room #" + newRoomName.getText());
                initialize("#" + newRoomName.getText());
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
            System.out.println(serverRoomMessage.equals("21"));

            System.out.println("JOINING ROOM " + roomNameText.getText());
            Stage thisStage = (Stage) joinRoomCancelButton.getScene().getWindow();
            thisStage.close();
        }
    }

    @FXML
    private void closeButtonAction(ActionEvent event){
        Button button = (Button) event.getSource();
        Stage stage = (Stage) button.getScene().getWindow();
        stage.close();
    }

    public void initialize(String roomName){
        ObservableList<String> rooms= FXCollections.observableArrayList(roomName);
        primaryRoomList.setItems(rooms);
    }

}
