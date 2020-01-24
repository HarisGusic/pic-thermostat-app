package pic.thermostat;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pic.thermostat.comms.Communication;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main_window.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("PIC Thermostat");
        primaryStage.setScene(new Scene(root));
        primaryStage.setMaxWidth(800);
        primaryStage.setMaxHeight(500);
        primaryStage.setOnCloseRequest(e -> {
            Communication.release();
        });
        primaryStage.show();

        Communication.initialize();
    }
}