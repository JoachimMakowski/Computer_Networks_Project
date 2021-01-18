package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

/*      0 - login
        1 - logout
        2 - join room
        3 - create new room
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


    public static void login() throws IOException {
        Parent menu = FXMLLoader.load(Main.class.getResource("menu.fxml"));
        Stage menuStage = new Stage();
        //menuStage = Main.parentWindow;
        parentWindow.close();
        parentWindow = menuStage;
        menuStage.setTitle("Internet Relay Chat");
        //menuStage.setMaximized(true);
        menuStage.setResizable(false);
        menuStage.setScene(new Scene(menu, 900, 700));
        menuStage.show();

        menuStage.setOnCloseRequest(e ->
        {
            e.consume();
            closeProgram(menuStage);
        });
    }

    public static void logout() throws IOException {
        Parent menu = FXMLLoader.load(Main.class.getResource("login.fxml"));
        Stage logoutStage = new Stage();
        parentWindow.close();
        parentWindow = logoutStage;
        logoutStage.setTitle("Logout from IRC app");
        logoutStage.setResizable(false);
        logoutStage.setScene(new Scene(menu, 400,400));
        logoutStage.show();

        logoutStage.setOnCloseRequest(e ->
        {
            e.consume();
            closeProgram(logoutStage);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }

    private static void closeProgram(Stage primaryStage) {
        Boolean answer = ConfirmBox.display("EXIT", "Sure you want to exit?", "YES", "NO");
        if (answer)
            primaryStage.close();
    }
}
