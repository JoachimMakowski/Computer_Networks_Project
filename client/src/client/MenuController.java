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
    private ListView<String> roomList;
    @FXML
    private ListView<String> userList;
    @FXML
    private Button logoutButton;

    private String nickname;
    private Socket clientSocket;
    private PrintWriter writer;
    private BufferedReader reader;

    @FXML
    void initialize() {
        userList.getItems().add("Item 1");
        userList.getItems().add("Item 2");
    }

    void initData(String nick, Socket socket, PrintWriter printWriter, BufferedReader bufferedReader) throws IOException {
        this.nickname = nick;

        //only added for tests
        this.clientSocket = socket;
        this.writer = printWriter;
        this.reader = bufferedReader;
        userList.getItems().add(nick);

        //request to server to get list of clients saved on the server
        String request = "7\n";
        writer.println(request);
        System.out.println("REQUEST FOR CLIENTS LIST");

        //String listOfUsers = reader.readLine();
        //System.out.println(listOfUsers);
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
            logout();
            clientSocket.close();
        }
    }

    @FXML
    public void send(KeyEvent e) throws IOException {

        //checks if user tries to send an empty message or message is too long
        if(e.getCode().equals(KeyCode.ENTER)){

            String selectedUser = userList.getSelectionModel().getSelectedItem();
            System.out.println("SELECTED USER: " + selectedUser);

            if (typeMessage.getText().length() > 1 && typeMessage.getText().length() < 200 && selectedUser!=null) {
                String msg = "4\n" + typeMessage.getText() + "\n" + selectedUser + "\n" + this.nickname + "\n";
                System.out.println(msg);
                writer.println(msg);
                System.out.println("SENDING MESSAGE");

                //String timeStamp = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());

                Text text = new Text(this.nickname + "  " + typeMessage.getText());
                text.setStyle("-fx-font-size: 14px;");
                //text.setFill(Color.RED);
                text.setFill(Color.color(Math.random(), Math.random(), Math.random()));
                chat.getChildren().add(text);
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
        System.out.println(this.nickname);
        roomController.initialData(this.nickname, this.clientSocket, this.writer, this.reader);
        roomController.primaryRoomList = roomList; //to control listview from RoomController

        createRoomStage.show();
    }

    @FXML
    public void joinRoom() throws IOException{

        FXMLLoader joinRoomLoader = new FXMLLoader();
        joinRoomLoader.setLocation(getClass().getResource("joinRoom.fxml"));
        AnchorPane frameJoin = joinRoomLoader.load();
        RoomController roomController = joinRoomLoader.getController();
        roomController.initialData(this.nickname, this.clientSocket, this.writer, this.reader);

        Stage joinRoomStage = new Stage();
        joinRoomStage.initModality(Modality.APPLICATION_MODAL);
        joinRoomStage.setTitle("Join room");
        joinRoomStage.setResizable(false);
        joinRoomStage.setScene(new Scene(frameJoin));
        joinRoomStage.show();
    }

}
