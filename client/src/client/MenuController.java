package client;

import com.sun.javafx.charts.Legend;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MenuController {

    @FXML
    private TextFlow chat;
    @FXML
    private TextArea typeMessage;
    @FXML
    private ListView<String> roomList;
    @FXML
    private ListView<String> userList;


    @FXML
    public void logoutControl() throws IOException {
        Boolean answer = ConfirmBox.display("LOGOUT", "Are you sure to logout?", "OK", "Cancel");
        if (answer) {
            Main.logout();
        }
    }

    @FXML
    public void send(KeyEvent e) throws IOException {

        //checks if user tries to send an empty message
        if(e.getCode().equals(KeyCode.ENTER)){
            if (typeMessage.getText().length() > 1){
                System.out.println("SENDING MESSAGE");

                String timeStamp = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());

                Text text = new Text(timeStamp + "  " + typeMessage.getText());
                text.setStyle("-fx-font-size: 14px;");
                //text.setFill(Color.RED);
                text.setFill(Color.color(Math.random(), Math.random(), Math.random()));
                chat.getChildren().add(text);
                typeMessage.clear();
            }
            else{
                System.out.println("MESSAGE TOO SHORT!");
                typeMessage.clear();
            }
        }
    }

    @FXML
    public void createRoom() throws IOException{

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("newRoom.fxml"));
        AnchorPane frame = fxmlLoader.load();
        RoomController c = (RoomController) fxmlLoader.getController();

        //to control listview from RoomController
        c.primaryRoomList = roomList;
        Stage createRoomStage = new Stage();
        createRoomStage.initModality(Modality.APPLICATION_MODAL);
        createRoomStage.setTitle("Create new room");
        createRoomStage.setResizable(false);
        createRoomStage.setScene(new Scene(frame));
        createRoomStage.show();
    }

    @FXML
    public void joinRoom() throws IOException{

        Parent joinRoomScene = FXMLLoader.load(getClass().getResource("joinRoom.fxml"));
        Stage joinRoomStage = new Stage();
        joinRoomStage.initModality(Modality.APPLICATION_MODAL);
        joinRoomStage.setTitle("Join room");
        joinRoomStage.setResizable(false);
        joinRoomStage.setScene(new Scene(joinRoomScene));
        joinRoomStage.show();
    }

}
