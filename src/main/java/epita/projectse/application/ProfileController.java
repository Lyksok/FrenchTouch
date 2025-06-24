package epita.projectse.application;

import epita.projectse.business_logic.AuthenticationModule;
import epita.projectse.business_logic.IPassChecker;
import epita.projectse.domain.*;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProfileController implements IController {

    @FXML
    private Label welcomeText, outputText, userProfilePictureLabel;

    @FXML
    private ImageView profilePictureView;

    @FXML
    private TextField oldPasswordField, newPasswordField, confirmPasswordField;
    @FXML
    private CheckBox oldPasswordCheck, newPasswordCheck, confirmPasswordCheck;
    @FXML
    private StackPane oldPasswordPane, newPasswordPane, confirmPasswordPane;
    @FXML
    private Button toCollaboratorButton, toArtistButton, toAdminButton;

    private Stage stage;
    private MainLayoutController mainLayoutController;
    private ExecutorService executorService;

    @Override
    public void initializeContent(MainLayoutController mainLayoutController) {
        this.mainLayoutController = mainLayoutController;
        this.executorService = mainLayoutController.executorService;
        displayProfilePicture();
        hideButtons();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void hideButtons() {
        // Check if user is already a specific role
        long id = mainLayoutController.getUser().getId();
        setWelcomeText(mainLayoutController.getUser().getUsername());

        // Use Platform.runLater to update UI from background thread
        Platform.runLater(() -> {
            // Disable buttons initially
            toCollaboratorButton.setDisable(true);
            toArtistButton.setDisable(true);
            toAdminButton.setDisable(true);
        });

        // Check collaborator status in background
        executorService.submit(() -> {
            boolean isCollaborator = mainLayoutController.getDbAccess().is_collaborator(id);
            boolean hasCollaboratorRequest = mainLayoutController.getDbAccess().request_to_collaborator_exists(id);

            Platform.runLater(() -> {
                if (isCollaborator) {
                    toCollaboratorButton.setVisible(false);
                    toCollaboratorButton.setManaged(false);
                } else if (hasCollaboratorRequest) {
                    toCollaboratorButton.setDisable(true);
                } else {
                    toCollaboratorButton.setDisable(false);
                }
            });
        });

        // Check artist status in background
        executorService.submit(() -> {
            boolean isArtist = mainLayoutController.getDbAccess().is_artist(id);
            boolean hasArtistRequest = mainLayoutController.getDbAccess().request_to_artist_exists(id);

            Platform.runLater(() -> {
                if (isArtist) {
                    toArtistButton.setVisible(false);
                    toArtistButton.setManaged(false);
                } else if (hasArtistRequest) {
                    toArtistButton.setDisable(true);
                } else {
                    toArtistButton.setDisable(false);
                }
            });
        });

        // Check admin status in background
        executorService.submit(() -> {
            boolean isAdmin = mainLayoutController.getDbAccess().is_admin(id);
            boolean hasAdminRequest = mainLayoutController.getDbAccess().request_to_admin_exists(id);

            Platform.runLater(() -> {
                if (isAdmin) {
                    toAdminButton.setVisible(false);
                    toAdminButton.setManaged(false);
                } else if (hasAdminRequest) {
                    toAdminButton.setDisable(true);
                } else {
                    toAdminButton.setDisable(false);
                }
            });
        });
    }

    private Image image;

    public void displayProfilePicture() {
        executorService.submit(() -> {
            String path = mainLayoutController.getDbAccess().get_user_profile_picture(mainLayoutController.getUser().getEmail());

            if (path == null) {
                try {
                    URL res = getClass().getClassLoader().getResource("base_profile_pic.png");
                    if (res != null) {
                        File file = Paths.get(res.toURI()).toFile();
                        path = file.getAbsolutePath();
                    }
                } catch (Exception e) {
                    System.out.println("base_profile_picture not found");
                }
            }

            if (path != null) {
                try {
                    final String finalPath = path;
                    Platform.runLater(() -> {
                        try {
                            image = new Image(new File(finalPath).toURI().toString());
                            profilePictureView.setImage(image);
                        } catch (Exception e) {
                            System.out.println("Error loading profile picture: " + e.getMessage());
                        }
                    });
                } catch (Exception e) {
                    System.out.println("Error loading profile picture: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    public void onBecomeArtistButtonClicked(ActionEvent event) {
        executorService.submit(() -> {
            RequestToArtist requestToArtist = new RequestToArtist(mainLayoutController.getUser().getId());
            mainLayoutController.getDbAccess().insert_request_to_artist(requestToArtist);
            Platform.runLater(() -> {
                hideButtons();
                outputText.setText("Artist request submitted successfully");
            });
        });
    }

    @FXML
    public void onBecomeCollaboratorButtonClicked(ActionEvent event) {
        executorService.submit(() -> {
            RequestToCollaborator requestToCollaborator = new RequestToCollaborator(mainLayoutController.getUser().getId());
            mainLayoutController.getDbAccess().insert_request_to_collaborator(requestToCollaborator);
            Platform.runLater(() -> {
                hideButtons();
                outputText.setText("Collaborator request submitted successfully");
            });
        });
    }

    @FXML
    public void onBecomeAdminButtonClicked(ActionEvent event) {
        executorService.submit(() -> {
            RequestToAdmin requestToAdmin = new RequestToAdmin(mainLayoutController.getUser().getId());
            mainLayoutController.getDbAccess().insert_request_to_admin(requestToAdmin);
            Platform.runLater(() -> {
                hideButtons();
                outputText.setText("Admin request submitted successfully");
            });
        });
    }

    public void setWelcomeText(String user) {
        welcomeText.setText("Welcome back " + user);
    }

    @FXML
    private void onChangePictureButtonClicked(ActionEvent event) throws IOException {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("png files", "*.png");
        fileChooser.getExtensionFilters().add(extFilter);
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            try {
                // Process image in background
                executorService.submit(() -> {
                    try {
                        Image image = new Image(selectedFile.toURI().toString(), 512, 512, false, false);

                        // Create cache directory if it doesn't exist
                        Path cacheDir = Path.of("./.cache");
                        if (!Files.exists(cacheDir)) {
                            Files.createDirectory(cacheDir);
                        }

                        // Create a temporary file
                        File outputFile = File.createTempFile("profile_picture", ".png", cacheDir.toFile());

                        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
                        bufferedImage = bufferedImage.getSubimage(0, 0, 512, 512);
                        ImageIO.write(bufferedImage, "png", outputFile);

                        final String userProfilePicturePath = outputFile.getAbsolutePath();
                        final String fileName = selectedFile.getName();

                        // Update UI on JavaFX thread
                        Platform.runLater(() -> {
                            userProfilePictureLabel.setText(fileName);
                            outputText.setText("Updating profile picture...");
                        });

                        // Upload profile picture
                        mainLayoutController.getDbAccess().upload_user_profile_picture(
                                mainLayoutController.getUser().getEmail(),
                                userProfilePicturePath
                        );

                        // Update UI after upload
                        Platform.runLater(() -> {
                            outputText.setText("Successfully updated the profile picture");
                            displayProfilePicture();
                            mainLayoutController.initializeProfilePicture();
                        });

                        // Delete the temporary file
                        outputFile.delete();

                    } catch (IOException e) {
                        Platform.runLater(() -> {
                            outputText.setText("Error updating profile picture: " + e.getMessage());
                        });
                    }
                });
            } catch (Exception e) {
                outputText.setText("Error reading image file: " + e.getMessage());
            }
        } else {
            userProfilePictureLabel.setText("No picture selected");
            outputText.setText("Picture selection cancelled");
        }
    }

    @FXML
    void onChangePasswordButtonClicked(ActionEvent event) {
        if (!newPasswordField.getText().equals(confirmPasswordField.getText())) {
            outputText.setText("The new passwords do not match");
            return;
        }

        executorService.submit(() -> {
            AuthInfo authInfo = new AuthInfo(mainLayoutController.getUser(), oldPasswordField.getText());
            IPassChecker passChecker = new AuthenticationModule();
            passChecker.setDbAccess(mainLayoutController.getDbAccess());
            AuthHash authHash = passChecker.authenticateUser(mainLayoutController.getUser().getEmail(), oldPasswordField.getText());

            if (authHash != null && authHash.isValid()) {
                AuthInfoNewPassword authInfoNewPassword = new AuthInfoNewPassword(authHash, authInfo, newPasswordField.getText());
                mainLayoutController.getDbAccess().update_user_password(authInfoNewPassword);

                Platform.runLater(() -> {
                    outputText.setText("Successfully updated the password");
                });
            } else {
                Platform.runLater(() -> {
                    outputText.setText("The password is incorrect");
                });
            }
        });
    }

    public void initialize() {
        // Set the password show/hide functionality
        oldPasswordCheck.setOnAction(_ -> togglePasswordVisibility(oldPasswordField, oldPasswordPane, oldPasswordCheck));
        newPasswordCheck.setOnAction(_ -> togglePasswordVisibility(newPasswordField, newPasswordPane, newPasswordCheck));
        confirmPasswordCheck.setOnAction(_ -> togglePasswordVisibility(confirmPasswordField, confirmPasswordPane, confirmPasswordCheck));

        // Set up key event handlers
        oldPasswordField.setOnKeyPressed(ke -> handleKeyPress(ke, newPasswordField));
        newPasswordField.setOnKeyPressed(ke -> handleKeyPress(ke, confirmPasswordField));
        confirmPasswordField.setOnKeyPressed(ke -> handleEnterKeyPress(ke));

        outputText.setVisible(true);

        // Load profile picture asynchronously
        //displayProfilePicture();
    }

    private void togglePasswordVisibility(TextField passwordField, StackPane passwordPane, CheckBox checkBox) {
        if (checkBox.isSelected()) {
            passwordField.setManaged(false);
            passwordField.setVisible(false);
            String password = passwordField.getText();
            passwordField.setText("");
            passwordPane.getChildren().remove(passwordField);
            passwordField = new TextField(password);
            passwordField.setId(passwordField.getId());
            passwordField.setPromptText(passwordField.getPromptText());
            passwordPane.getChildren().addFirst(passwordField);
        } else {
            passwordField.setManaged(false);
            passwordField.setVisible(false);
            String password = passwordField.getText();
            passwordPane.getChildren().remove(passwordField);
            passwordField = new PasswordField();
            passwordField.setText(password);
            passwordField.setId(passwordField.getId());
            passwordField.setPromptText(passwordField.getPromptText());
            passwordPane.getChildren().addFirst(passwordField);
        }
    }

    private void handleKeyPress(javafx.scene.input.KeyEvent ke, TextField nextField) {
        if (ke.getCode().equals(KeyCode.ENTER)) {
            nextField.requestFocus();
        }
    }

    private void handleEnterKeyPress(javafx.scene.input.KeyEvent ke) {
        if (ke.getCode().equals(KeyCode.ENTER)) {
            onChangePasswordButtonClicked(new ActionEvent());
        }
    }

    // Clean up resources when controller is no longer needed
    public void cleanup() {
        executorService.shutdown();
    }
}
