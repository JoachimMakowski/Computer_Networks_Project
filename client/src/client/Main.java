package client;

import javafx.application.Application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 *     Main running class
 */

/*
        0 - login
            00 - user logged successfully
            01 - user already logged
            02 - limit of active user on the server
            03 - limit of users saved to the server
        1 - logout
        2 - join room
            20 - successfully joined the room
            21 - there is no room with that name
        3 - create new room
            30 - room created successfully
            31 - room with that name already exists
            32 - out of space on the server (10 rooms)
        4 - message room
        5 - message face2face
        6 - change room (username, type of conversation from ('0' room, '1' chat), room name/username2 from,
            type of conversation to ('0' room, '1' chat), room name/username2 to)
        7 - actual logged user
            70\n%s\n - actually logged users %s = list of users
        8 - messages from room/ private conversation
            ('0' room, '1' chat) + active user + from which room/user name
*/

public class Main extends Application {

    private static Stage parentWindow;
    private static ServerConnection serverConnection;

    @Override
    public void start(Stage primaryStage) throws Exception{

        serverConnection = new ServerConnection();
        Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
        parentWindow = primaryStage;
        primaryStage.getIcons().add(new Image("file:icon.png"));
        primaryStage.setTitle("Login to IRC app");
        primaryStage.setScene(new Scene(root, 400, 400));
        primaryStage.setResizable(false);
        primaryStage.show();

        primaryStage.setOnCloseRequest(e ->
        {
            e.consume();
            closeProgram(primaryStage);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }

    /**
     *  Method to display confirm box before closing the app
     */
    public static void closeProgram(Stage primaryStage) {
        Boolean answer = ConfirmBox.display("EXIT", "Sure you want to exit?", "YES", "NO");
        if (answer)
            primaryStage.close();
    }
}
