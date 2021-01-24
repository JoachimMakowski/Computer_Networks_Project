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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

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
    private Button button;

    @FXML
    void initialize() {
    }

    @FXML
    public void check() throws IOException {
        loginErrorEmpty.setVisible(false);
        loginErrorLong.setVisible(false);
        connectingLabel.setVisible(false);
        if (nick.getText() == null || nick.getText().trim().isEmpty()) {
            loginErrorEmpty.setVisible(true);
        }
        if (nick.getText().trim().length() <= 25 && nick.getText().trim().length() > 0) {
            connectingLabel.setVisible(true);
            System.out.println("Welcome " + nick.getText());
            //User user = new User(nick.getText());

            //Socket clientSocket = new Socket(address.getText(), Integer.parseInt(port.getText()));
            //OutputStream os = clientSocket.getOutputStream();
            //String msg = nick.getText();
            //os.write(msg.getBytes());

            //client.Main.login();
            login();
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
        controller.initData(nick.getText());

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