package de.bank.oos;

import de.bank.oos.bank.PrivateBank;
import de.bank.oos.controller.AccountsViewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage stage) throws IOException {
        PrivateBank projectB = new PrivateBank("ProjectB", 0.1, 0.1, "./bank/");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/accountsView.fxml"));
        stage.setScene(new Scene(loader.load()));

        ((AccountsViewController) loader.getController()).init(stage, projectB);

        stage.setTitle(projectB.getName());
        stage.show();
    }
}
