package client;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Controller {

    @FXML
    public TextField nick;
    @FXML
    private TextField address;
    @FXML
    private TextField port;
    @FXML
    private Label loginErrorEmpty;
    @FXML
    private Label loginErrorLong;
    @FXML
    private Label serverErrorConnection;
    @FXML
    private Label connectingLabel;
    @FXML
    private Label alreadyLoggedError;
    @FXML
    private Button button;

    private Socket clientSocket;
    private PrintWriter writer;
    private BufferedReader reader;

    @FXML
    void initialize() {
    }

    @FXML
    public void check() throws IOException {
        loginErrorEmpty.setVisible(false);
        loginErrorLong.setVisible(false);
        connectingLabel.setVisible(false);
        serverErrorConnection.setVisible(false);
        alreadyLoggedError.setVisible(false);

        if (nick.getText() == null || nick.getText().trim().isEmpty()) {
            loginErrorEmpty.setVisible(true);
        }
        if (nick.getText().trim().length() <= 25 && nick.getText().trim().length() > 0) {

            try{
                this.clientSocket = new Socket(address.getText(), Integer.parseInt(port.getText()));
                this.writer = new PrintWriter(clientSocket.getOutputStream(), true);
                this.reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                String msg = "0\n" + nick.getText();
                writer.println(msg);

            } catch (UnknownHostException e) {
                System.out.println("Unknown host name: " + e);
                return;
            } catch (IOException e) {
                System.out.println("IO exception");
                serverErrorConnection.setVisible(true);
                return;
            }

            String serverMessage = reader.readLine();
            System.out.println(serverMessage);

            switch (serverMessage) {
                case "02", ("03") -> serverErrorConnection.setVisible(true);
                case "01" -> alreadyLoggedError.setVisible(true);
                case "00" -> {
                    connectingLabel.setVisible(true);
                    System.out.println("Welcome " + nick.getText());
                    login();
                }
            }

        }
        if (nick.getText().trim().length() > 25){
            loginErrorLong.setVisible(true);
        }
    }

    public void login() throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("menu.fxml"));

        Stage menuStage = new Stage();
        menuStage.setScene(new Scene(loader.load()));
        menuStage.setTitle("Internet Relay Chat");
        menuStage.setResizable(false);

        MenuController controller = loader.getController();
        //only added for tests
        controller.initData(nick.getText(), this.clientSocket, this.writer, this.reader);

        menuStage.show();

        menuStage.setOnCloseRequest(e ->
        {
            e.consume();
            try {
                controller.logoutControl();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });

        Stage thisStage = (Stage) button.getScene().getWindow();
        thisStage.close();
    }

    @FXML
    public void enter(KeyEvent e) throws IOException {
        if(e.getCode().equals(KeyCode.ENTER)) {
            check();
        }
    }

}