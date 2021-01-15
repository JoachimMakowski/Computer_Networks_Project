package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;


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
        menuStage.setTitle("Internet Relay Chat");
        //menuStage.setMaximized(true);
        menuStage.setResizable(false);
        menuStage.setScene(new Scene(menu, 800, 600));
        menuStage.show();
        parentWindow.close();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void closeProgram(Stage primaryStage) {
        Boolean answer = ConfirmBox.display("EXIT", "Sure you want to exit?", "YES", "NEVER");
        if (answer)
            primaryStage.close();
    }
}
