package client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;

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
            User user = new User(nick.getText());
            client.Main.login();
        }
        if (nick.getText().trim().length() > 25){
            loginErrorLong.setVisible(true);
        }
    }

    @FXML
    public void enter(KeyEvent e) throws IOException {
        if(e.getCode().equals(KeyCode.ENTER)) {
            check();
        }
    }
}