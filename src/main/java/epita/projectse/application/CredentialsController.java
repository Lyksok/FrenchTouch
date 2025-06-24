package epita.projectse.application;

import epita.projectse.business_logic.AuthenticationModule;
import epita.projectse.business_logic.IPassChecker;
import epita.projectse.data_access.*;
import epita.projectse.domain.AuthHash;
import epita.projectse.domain.AuthInfo;
import epita.projectse.domain.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.net.URL;

public class CredentialsController{
    @FXML
    private TextField loginEmailField, loginPasswordField;
    @FXML
    private CheckBox loginPasswordCheck;
    @FXML
    private Label loginDialogLabel;
    @FXML
    private StackPane loginPasswordPane;

    @FXML
    private TextField registerUsernameField, registerEmailField;
    @FXML
    private TextField registerPasswordField;
    @FXML
    private CheckBox registerPasswordCheck;
    @FXML
    private Label registerDialogLabel;
    @FXML
    private StackPane registerPasswordPane;
    @FXML
    private TabPane tabPane;
    @FXML
    private Tab loginTab, registerTab;

    protected Scene appScene;
    protected MainLayoutController appController;

    private IPassChecker businessLogic;
    private DbAccess dbAccess;

    public void setBusinessLogic(IPassChecker checker) { this.businessLogic = checker; }

    public void setDbAccess(DbAccess dbAccess) { this.dbAccess = dbAccess; }

    public DbAccess getDbAccess() { return dbAccess; }

    @FXML
    protected void onLoginButtonClick()
    {
        // Get email and password
        String email = loginEmailField.getText();
        String password = loginPasswordField.getText();

        // Check credentials
        AuthHash authHash = businessLogic.authenticateUser(email, password);

        // If there is no error, login the user
        if(authHash != null && authHash.isValid()){
            System.out.println("Login successful");
            loginDialogLabel.setText("Login successful");
            loginDialogLabel.getStyleClass().set(0,"alert-success");

            businessLogic.getDbAccess().update_user_last_connection(email);
            businessLogic.getDbAccess().setAuthHash(authHash);

            // Empty the fields
            loginEmailField.setText("");
            loginPasswordField.setText("");

            // Log the user in
            int numStages = (int) Stage.getWindows().stream().filter(Window::isShowing).count();

            if(numStages > 1) return;

            Stage stage = new Stage();
            stage.setTitle("French Touch - Application");
            stage.setScene(appScene);
            User user = dbAccess.select_user_by_email(email);
            appController.setUser(user);
            appController.initializeProfilePicture();
            appController.updateDisplayView();
            stage.setResizable(false);
            Stage.getWindows().getFirst().hide();
            stage.show();
        }
        // If there is a problem, inform the user it is his fault
        else {
            loginDialogLabel.setText("Wrong email or password");
            loginDialogLabel.getStyleClass().set(0,"alert-danger");
        }
    }

    @FXML
    protected void onRegisterButtonClick()
    {
        String email = registerEmailField.getText();

        DbAccess dbAccess = businessLogic.getDbAccess();

        // Check if the email is already registered
        // if it is, inform the user
        if (dbAccess.user_exists(email)){
            registerDialogLabel.setText("Email already registered");
            registerDialogLabel.getStyleClass().set(0,"alert-danger");
        }
        // If the email is not registered, register the user
        else {
            String username = registerUsernameField.getText();
            String password = registerPasswordField.getText();

            // Get actual time from epoch in seconds
            int currentTime = (int) (System.currentTimeMillis() / 1000L);

            // Register the user
            User user = new User(username, email, currentTime, currentTime, null);
            AuthInfo authInfo = new AuthInfo(user, password);
            if (businessLogic.registerUser(authInfo)) {
                // Inform the user that the registration was successful
                loginDialogLabel.setText("Registration successful");
                loginDialogLabel.getStyleClass().set(0,"alert-success");

                loginEmailField.setText(registerEmailField.getText());

                // Empty the fields
                registerUsernameField.setText("");
                registerEmailField.setText("");
                registerPasswordField.setText("");
                tabPane.getSelectionModel().select(loginTab);
            } else {
                registerDialogLabel.setText("Something went wrong");
                registerDialogLabel.getStyleClass().set(0,"alert-danger");
            }
        }
    }

    @FXML
    protected void onExitButtonClick()
    {
        // Clear the fields
        loginEmailField.setText("");
        loginPasswordField.setText("");
        registerUsernameField.setText("");
        registerEmailField.setText("");
        registerPasswordField.setText("");

        // Close database connection
        if (dbAccess != null) {
            dbAccess.close();
        }

        // Quit the application
        System.exit(0);
    }

    @FXML
    void initialize() throws IOException {

        // Set the password show/hide functionality
        loginPasswordCheck.setOnAction(_ -> {
            if (loginPasswordCheck.isSelected()) {
                loginPasswordField.setManaged(false);
                loginPasswordField.setVisible(false);
                String password = loginPasswordField.getText();
                loginPasswordField.setText("");
                loginPasswordPane.getChildren().remove(loginPasswordField);
                loginPasswordField = new TextField(password);
                loginPasswordField.setId("loginPasswordField");
                loginPasswordField.setPromptText("Password");
                loginPasswordPane.getChildren().addFirst(loginPasswordField);
            }
            else{
                loginPasswordField.setManaged(false);
                loginPasswordField.setVisible(false);
                String password = loginPasswordField.getText();
                loginPasswordPane.getChildren().remove(loginPasswordField);
                loginPasswordField = new PasswordField();
                loginPasswordField.setText(password);
                loginPasswordField.setId("loginPasswordField");
                loginPasswordField.setPromptText("Password");
                loginPasswordPane.getChildren().addFirst(loginPasswordField);
            }
        });
        registerPasswordCheck.setOnAction(_ -> {
            if (registerPasswordCheck.isSelected()) {
                registerPasswordField.setManaged(false);
                registerPasswordField.setVisible(false);
                String password = registerPasswordField.getText();
                registerPasswordField.setText("");
                registerPasswordPane.getChildren().remove(registerPasswordField);
                registerPasswordField = new TextField(password);
                registerPasswordField.setId("registerPasswordField");
                registerPasswordField.setPromptText("Password");
                registerPasswordPane.getChildren().addFirst(registerPasswordField);
            }
            else{
                registerPasswordField.setManaged(false);
                registerPasswordField.setVisible(false);
                String password = registerPasswordField.getText();
                registerPasswordPane.getChildren().remove(registerPasswordField);
                registerPasswordField = new PasswordField();
                registerPasswordField.setText(password);
                registerPasswordField.setId("registerPasswordField");
                registerPasswordField.setPromptText("Password");
                registerPasswordPane.getChildren().addFirst(registerPasswordField);
            }
        });

        loginEmailField.setOnKeyPressed(ke -> {
            if (ke.getCode().equals(KeyCode.ENTER)) {
                loginPasswordField.requestFocus();
            }
        });
        loginPasswordField.setOnKeyPressed(ke -> {
            if (ke.getCode().equals(KeyCode.ENTER)) {
                onLoginButtonClick();
            }
        });

        registerUsernameField.setOnKeyPressed(ke -> {
            if (ke.getCode().equals(KeyCode.ENTER)) {
                registerEmailField.requestFocus();
            }
        });
        registerEmailField.setOnKeyPressed(ke -> {
            if (ke.getCode().equals(KeyCode.ENTER)) {
                registerPasswordField.requestFocus();
            }
        });
        registerPasswordField.setOnKeyPressed(ke -> {
            if (ke.getCode().equals(KeyCode.ENTER)) {
                onRegisterButtonClick();
            }
        });

        setBusinessLogic(new AuthenticationModule());
        setDbAccess(new OnlineDbAccess());
        businessLogic.setDbAccess(dbAccess);

        FXMLLoader fxmlLoader = new FXMLLoader();
        URL fxmlLocation = getClass().getResource("/epita/projectse/application/mainLayoutView.fxml");
        if (fxmlLocation == null) {
            throw new IllegalArgumentException("Cannot find FXML file.");
        }
        fxmlLoader.setLocation(fxmlLocation);
        appScene = new Scene(fxmlLoader.load());
        appController = fxmlLoader.getController();
        appController.setDbAccess(dbAccess);
    }
}