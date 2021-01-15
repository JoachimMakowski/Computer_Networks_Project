package client;

import javafx.scene.control.Button;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.geometry.*;

import java.awt.*;

public class ConfirmBox {

    static boolean answer;

    public static boolean display(String title, String message, String button1, String button2){
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(250);
        window.setMinHeight(150);
        window.setResizable(false);
        Label label = new Label();
        label.setText(message);

        //create two buttons
        Button yesButton = new Button(button1);
        Button noButton = new Button(button2);

        yesButton.setOnAction(e -> {
            answer = true;
            window.close();
        });

        noButton.setOnAction(e -> {
            answer = false;
            window.close();
        });

        VBox layout = new VBox(10);
        if(title.equals("THE GAME IS OVER")){
            layout.getChildren().addAll(label,noButton);
        }
        else layout.getChildren().addAll(label,yesButton,noButton);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();

        return answer;
    }
}
