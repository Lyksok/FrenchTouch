package epita.projectse;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        LogManager logManager = LogManager.getLogManager();
        Logger logger = logManager.getLogger("");
        logger.setLevel(Level.SEVERE); //could be Level.OFF

        // Check if the configuration is valid
        if (!epita.projectse.data_access.ConfigurationAccess.isConfigurationValid()) {
            System.err.println("Configuration is not valid. Please check your app.config file.");
            System.exit(1);
        }

        FXMLLoader fxmlLoader = new FXMLLoader();
        URL fxmlLocation = getClass().getResource("application/credentialsView.fxml");
        if (fxmlLocation == null) {
            throw new IllegalArgumentException("Cannot find FXML file.");
        }
        fxmlLoader.setLocation(fxmlLocation);
        Scene scene = new Scene(fxmlLoader.load(), 1000, 600);
        stage.setResizable(false);
        stage.setTitle("French Touch - Login/Register");
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) {
        launch();
    }
}