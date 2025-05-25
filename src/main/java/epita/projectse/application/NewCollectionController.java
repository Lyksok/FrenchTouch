package epita.projectse.application;

import epita.projectse.domain.Album;
import epita.projectse.domain.Playlist;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

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

public class NewCollectionController implements IController{
    @FXML
    private ImageView coverImageView;
    @FXML
    private Label coverLabel;
    @FXML
    private TextField titleTextArea;
    @FXML
    private Button newAlbumButton, newPlaylistButton;

    private ExecutorService executorService;
    private MainLayoutController mainLayoutController;
    private Image image;
    private String imagePath;

    @Override
    public void initializeContent(MainLayoutController mainLayoutController) {
        this.mainLayoutController = mainLayoutController;
        this.executorService = mainLayoutController.executorService;
        clearFields();
        newAlbumButton.setVisible(mainLayoutController.getDbAccess().is_artist(mainLayoutController.getUser().getId()));
    }

    @FXML
    void onNewAlbumButtonClicked(ActionEvent event) {
        String title = titleTextArea.getText();
        Album album = new Album(title, imagePath, 0, (int) (System.currentTimeMillis() / 1000L), mainLayoutController.getDbAccess().select_artist_by_user_id(mainLayoutController.getUser().getId()).getId());
        mainLayoutController.getDbAccess().insert_album(album);
        clearFields();
    }

    @FXML
    void onNewPlaylistButtonClicked(ActionEvent event) {
        String title = titleTextArea.getText();
        Playlist playlist = new Playlist(title, imagePath, 0, (int) (System.currentTimeMillis() / 1000L), mainLayoutController.getUser().getId());
        mainLayoutController.getDbAccess().insert_playlist(playlist);
        clearFields();
    }

    @FXML
    void onSelectCoverButtonClicked(ActionEvent event) {
        onChangePictureButtonClicked();
    }

    public void displayProfilePicture() {
        try {
            Platform.runLater(() -> {
                try {
                    System.out.println(imagePath);
                    image = new Image(new File(imagePath).toURI().toString());
                    coverImageView.setImage(image);
                } catch (Exception e) {
                    System.out.println("Error loading profile picture: " + e.getMessage());
                }
            });
        } catch (Exception e) {
            System.out.println("Error loading profile picture: " + e.getMessage());
        }
    }

    private void onChangePictureButtonClicked(){
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Image Format (PNG)", "*.png");
        fileChooser.getExtensionFilters().add(extFilter);
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try {
                // Process image in background
                executorService.submit(() -> {
                    try {
                        Image image = new Image(selectedFile.toURI().toString(), 512, 512, false, false);

                        // Create cache directory if it doesn't exist
                        Path cacheDir = Path.of(".cache");
                        if (!Files.exists(cacheDir)) {
                            Files.createDirectory(cacheDir);
                        }

                        // Create a temporary file
                        File outputFile = File.createTempFile("playlist_picture", ".png", cacheDir.toFile());

                        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
                        bufferedImage = bufferedImage.getSubimage(0, 0, 512, 512);
                        ImageIO.write(bufferedImage, "png", outputFile);
                        imagePath = outputFile.toPath().toString();
                        System.out.println("image path :" + imagePath);

                        // Update UI on JavaFX thread
                        Platform.runLater(() -> {
                            //userProfilePictureLabel.setText(fileName);
                            coverLabel.setText("Updating profile picture...");
                        });

                        // Update UI after upload
                        Platform.runLater(() -> {
                            coverLabel.setText("Successfully updated the profile picture");
                            displayProfilePicture();
                            mainLayoutController.initializeProfilePicture();
                        });

                    } catch (IOException e) {
                        Platform.runLater(() -> {
                            coverLabel.setText("Error updating profile picture: " + e.getMessage());
                        });
                    }
                });
            } catch (Exception e) {
                coverLabel.setText("Error reading image file: " + e.getMessage());
            }
        } else {
            coverLabel.setText("Picture selection cancelled");
        }
    }

    public void clearFields() {
        titleTextArea.setText("");
        coverLabel.setText("Select a cover");
        imagePath = null;
        image = null;
        coverImageView.setImage(null);
    }
}
