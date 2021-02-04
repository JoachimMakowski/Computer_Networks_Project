package client;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class MenuController {

    @FXML
    private TextFlow chat;
    @FXML
    private TextArea typeMessage;
    @FXML
    private ListView<String> roomList = new ListView<String>();
    @FXML
    private ListView<String> userList = new ListView<String>();
    @FXML
    private Button logoutButton;

    private String nickname;
    private Socket clientSocket;
    private PrintWriter writer;
    private BufferedReader reader;

    @FXML
    void initialize() {
    }

    void initData(String nick, Socket socket, PrintWriter printWriter, BufferedReader bufferedReader) throws IOException {
        this.nickname = nick;
        this.clientSocket = socket;
        this.writer = printWriter;
        this.reader = bufferedReader;
        userList.getItems().add(nick);

        //request to server to get list of clients saved on the server

        String request = "7\n";
        writer.println(request);
        System.out.println("REQUEST FOR CLIENTS AND ROOMS LIST");

        /*
        String listOfUsers = reader.readLine().trim();
        for (String retUsers:listOfUsers.split("|")){
            System.out.println(retUsers);
            userList.getItems().add(retUsers);
        }

        String listOfRooms = reader.readLine().trim();
        System.out.println(listOfRooms);

        for (String retRooms:listOfRooms.split("|")){
            System.out.println(retRooms);
            roomList.getItems().add(retRooms);
        }
         */

    }

    public void logout() throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("login.fxml"));

        Stage loginStage = new Stage();
        loginStage.setScene(new Scene(loader.load()));
        loginStage.setTitle("Logout from IRC app");
        loginStage.setResizable(false);

        loginStage.show();

        loginStage.setOnCloseRequest(e ->
        {
            e.consume();
            Main.closeProgram(loginStage);
        });

        Stage thisStage = (Stage) logoutButton.getScene().getWindow();
        thisStage.close();
    }

    @FXML
    public void logoutControl() throws IOException {
        Boolean answer = ConfirmBox.display("LOGOUT", "Are you sure to logout?", "OK", "Cancel");
        if (answer) {
            String msg = "1\n" + this.nickname;
            System.out.println(msg);
            writer.println(msg);
            clientSocket.close();
            logout();
        }
    }

    @FXML
    public void send(KeyEvent e) throws IOException {

        //checks if user tries to send an empty message or message is too long
        if(e.getCode().equals(KeyCode.ENTER)){

            String selectedUser = userList.getSelectionModel().getSelectedItem();
            System.out.println("SELECTED USER: " + selectedUser);

            String selectedRoom = roomList.getSelectionModel().getSelectedItem();
            System.out.println("SELECTED ROOM: " + selectedRoom);

            if (typeMessage.getText().length() > 1 && typeMessage.getText().length() < 200 && selectedUser!=null) {
                String message2face = "5\n" + typeMessage.getText() + "\n" + selectedUser + "\n" + this.nickname + "\n";
                //System.out.println(message2face);
                writer.println(message2face);
                System.out.println("SENDING MESSAGE2FACE");

                Text text = new Text(this.nickname + ":  " + typeMessage.getText());
                text.setStyle("-fx-font-size: 14px;");
                //text.setFill(Color.RED);
                text.setFill(Color.color(Math.random(), Math.random(), Math.random()));
                chat.getChildren().add(text);
            }
            else if(typeMessage.getText().length() > 1 && typeMessage.getText().length() < 200 && selectedRoom!=null){
                String messages2room = "4\n" + typeMessage.getText() + "\n" + selectedRoom + "\n" + this.nickname + "\n";
                //System.out.println(messages2room);
                writer.println(messages2room);
                System.out.println("SENDING MESSAGE2ROOM");

                Text text = new Text(this.nickname + ":  " + typeMessage.getText());
                text.setStyle("-fx-font-size: 14px;");
                text.setFill(Color.color(Math.random(), Math.random(), Math.random()));
                chat.getChildren().add(text);
            }
            else if (selectedUser == null || selectedRoom == null) {
                System.out.println("SELECT USER OR ROOM TO SEND A MESSAGE!");
            }
            else{
                System.out.println("WRONG SIZE OF A MESSAGE!");
            }
            typeMessage.clear();
        }
    }

    @FXML
    public void createRoom() throws IOException{

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("newRoom.fxml"));

        Stage createRoomStage = new Stage();
        createRoomStage.initModality(Modality.APPLICATION_MODAL);
        createRoomStage.setTitle("Create new room");
        createRoomStage.setResizable(false);
        createRoomStage.setScene(new Scene(fxmlLoader.load()));

        RoomController roomController = fxmlLoader.getController();
        roomController.initialData(nickname, clientSocket, writer, reader);
        roomController.primaryRoomList = roomList; //to control listview from RoomController
        createRoomStage.show();
    }

    @FXML
    public void joinRoom() throws IOException{

        FXMLLoader joinRoomLoader = new FXMLLoader();
        joinRoomLoader.setLocation(getClass().getResource("joinRoom.fxml"));
        AnchorPane frameJoin = joinRoomLoader.load();
        RoomController roomController = joinRoomLoader.getController();
        roomController.initialData(nickname, clientSocket, writer, reader);

        Stage joinRoomStage = new Stage();
        joinRoomStage.initModality(Modality.APPLICATION_MODAL);
        joinRoomStage.setTitle("Join room");
        joinRoomStage.setResizable(false);
        joinRoomStage.setScene(new Scene(frameJoin));
        joinRoomStage.show();
    }

    @FXML
    public void handleClickedUser() throws IOException {
        String userName = userList.getSelectionModel().getSelectedItem();
        System.out.println("user clicked on " + userName);

        String messageUserName = "81\n" + this.nickname +  "\n" + userName;
        System.out.println(messageUserName);
        writer.println(messageUserName);

        String listOfMessagesUser = reader.readLine().trim();
        for (String retMessages:listOfMessagesUser.split("|")){
            System.out.println(retMessages);

            Text text = new Text(retMessages);
            text.setStyle("-fx-font-size: 14px;");
            text.setFill(Color.color(Math.random(), Math.random(), Math.random()));
            chat.getChildren().add(text);
        }

    }

    @FXML
    public void handleClickedRoom() throws IOException {
        String roomName = roomList.getSelectionModel().getSelectedItem();
        System.out.println("room clicked on " + roomName);

        String messageRoomName = "80\n" + this.nickname + "\n" + roomName;
        System.out.println(messageRoomName);
        writer.println(messageRoomName);

        String listOfMessagesRoom = reader.readLine().trim();
        for (String retMessages2:listOfMessagesRoom.split("|")){
            System.out.println(retMessages2);

            Text text = new Text(retMessages2);
            text.setStyle("-fx-font-size: 14px;");
            text.setFill(Color.color(Math.random(), Math.random(), Math.random()));
            chat.getChildren().add(text);
        }
    }

}
