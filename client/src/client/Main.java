package client;

import javafx.application.Application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/*      0 - login
        00 - user already logged
        01 - limit of active user on the server
        02 - limit of users saved to the server
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
*/

public class Main extends Application {

    private static Stage parentWindow;

    @Override
    public void start(Stage primaryStage) throws Exception{
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

    public static void closeProgram(Stage primaryStage) {
        Boolean answer = ConfirmBox.display("EXIT", "Sure you want to exit?", "YES", "NO");
        if (answer)
            primaryStage.close();
    }

}
