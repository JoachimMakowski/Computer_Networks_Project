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
    public TextField login;
    public TextField address;
    public TextField port;
    public Label loginErrorEmpty;
    public Label loginErrorLong;
    public Label serverErrorConnection;
    public Label connectingLabel;

    @FXML
    public void check() throws IOException {
        loginErrorEmpty.setVisible(false);
        loginErrorLong.setVisible(false);
        connectingLabel.setVisible(false);
        if (login.getText() == null || login.getText().trim().isEmpty()) {
            loginErrorEmpty.setVisible(true);
        }
        if (login.getText().trim().length() <= 20 && login.getText().trim().length() > 0) {
            connectingLabel.setVisible(true);
            client.Main.login();
        }
        if (login.getText().trim().length() > 20){
            loginErrorLong.setVisible(true);
        }
    }

    /*
    @FXML
    public void logIn() {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("main.fxml"));
            SplitPane yourNewView = fxmlLoader.load();
            sceneContainer.getChildren().setAll(yourNewView);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

     */

    @FXML
    public void enter(KeyEvent e) throws IOException {
        if(e.getCode().equals(KeyCode.ENTER)) {
            check();
        }
    }
}