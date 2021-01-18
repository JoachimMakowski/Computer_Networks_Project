package client;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.IOException;

public class MenuController {

    @FXML
    public TextFlow chat;
    public TextArea typeMessage;

    @FXML
    public void logoutControl() throws IOException {
        Boolean answer = ConfirmBox.display("LOGOUT", "Are you sure to logout?", "OK", "Cancel");
        if (answer) {
            Main.logout();
        }
    }

    @FXML
    public void send(KeyEvent e) throws IOException {
        if(e.getCode().equals(KeyCode.ENTER)) {
            System.out.println("SENDING MESSAGE");
            Text text = new Text(typeMessage.getText());
            text.setStyle("-fx-font-size: 14px;");
            //text.setFill(Color.RED);
            text.setFill(Color.color(Math.random(), Math.random(), Math.random()));
            chat.getChildren().add(text);
            typeMessage.clear();
        }
    }



}
