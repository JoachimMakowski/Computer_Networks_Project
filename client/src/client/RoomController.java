package client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RoomController {

    public ListView<String> primaryRoomList;

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
    public void checkNewRoom(){
        newRoomNameError.setVisible(false);
        newRoomTakenError.setVisible(false);
        newRoomLongerError.setVisible(false);
        if (newRoomName.getText() == null || newRoomName.getText().trim().isEmpty()) {
            newRoomNameError.setVisible(true);
        }
        if (newRoomName.getText().trim().length() <= 20 && newRoomName.getText().trim().length() > 0) {
            System.out.println("Created new room #" + newRoomName.getText());
            initialize("#"+newRoomName.getText());
            Stage thisStage = (Stage) createRoomCreateButton.getScene().getWindow();
            thisStage.close();
        }
        if (newRoomName.getText().trim().length() > 20){
            newRoomLongerError.setVisible(true);
        }
    }

    @FXML
    public void checkRoomToJoin(){
        noRoomNameError.setVisible(false);
        emptyRoomNameError.setVisible(false);
        if (roomNameText.getText() == null || roomNameText.getText().trim().isEmpty()) {
            emptyRoomNameError.setVisible(true);
        }
        else {
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
