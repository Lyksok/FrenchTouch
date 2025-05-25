package epita.projectse.application;

import epita.projectse.domain.AlbumSearch;
import epita.projectse.domain.ArtistRequest;
import epita.projectse.utils.ASearchBarHelper;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class ArtistPanelController implements IController {
    @FXML
    private TextField songTitleField, albumNameField;
    @FXML
    private DatePicker songCreationDateField, albumCreationDateField;
    @FXML
    private Button selectSongTrackButton, selectSongCoverButton, selectAlbumCoverButton;
    @FXML
    private Label songTrackLabel, songCoverLabel, albumCoverLabel;
    @FXML
    private ComboBox<ASearchBarHelper> albumSearchComboBox;

    @FXML
    private Label dialogLabel;
    @FXML
    private Button submitButton, cancelButton;

    private Stage stage;
    private String songTrackPath;
    private String songCoverPath;
    private String albumCoverPath;
    private MainLayoutController mainLayoutController;

    private ArtistRequest artistRequest = new ArtistRequest();
    private ExecutorService executorService;

    @Override
    public void initializeContent(MainLayoutController mainLayoutController) {
        this.mainLayoutController = mainLayoutController;
        this.executorService = mainLayoutController.executorService;
        artistRequest.setArtist_id(mainLayoutController.getDbAccess().select_artist_by_user_id(mainLayoutController.getUser().getId()).getId());
        artistRequest.setAlbum_id(-1);
        initialize();
    }

    private void clearAllFields() {
        Platform.runLater(() -> {
            songTitleField.clear();
            songCreationDateField.setValue(null);
            albumNameField.clear();
            albumCreationDateField.setValue(null);
            songTrackPath = null;
            songCoverPath = null;
            albumCoverPath = null;
            albumSearchComboBox.getItems().clear();
            songTrackLabel.setText("No track selected");
            songCoverLabel.setText("No cover selected");
            albumCoverLabel.setText("No cover selected");
        });
    }

    @FXML
    protected void onSubmitButtonClicked() {
        System.out.println("onSubmitButtonClicked");
        // Print the values of the fields
        System.out.println("Artist Id: " + artistRequest.getArtist_id());
        System.out.println("Song title: " + songTitleField.getText());
        System.out.println("Song creation date: " + songCreationDateField.getValue());
        System.out.println("Song file: " + songTrackPath);
        System.out.println("Song cover: " + songCoverPath);
        System.out.println("Album Id: " + artistRequest.getAlbum_id());
        System.out.println("Album Name: " + artistRequest.getAlbum_name());
        System.out.println("Album Creation Date: " + albumCreationDateField.getValue());
        System.out.println("Album Cover: " + albumCoverPath);

        artistRequest.setAlbum_name(albumNameField.getText());

        // Song title, song track and artist name are mandatory
        if ((artistRequest.getAlbum_id() == -1 && (artistRequest.getAlbum_name().isEmpty() || albumCreationDateField.getValue() == null))
                || songTitleField.getText().trim().isEmpty()
                || songTrackLabel.getText().equals("No track selected")
                || songCreationDateField.getValue() == null) {
            Platform.runLater(() -> dialogLabel.setText("Please fill in all mandatory fields."));
            return;
        }

        // Handle submit button click
        System.out.println("Submit button clicked");
        System.out.println("Song Title: " + songTitleField.getText());
        System.out.println("Song Creation Date: " + songCreationDateField.getValue());
        System.out.println("Song Track: " + songTrackPath);
        System.out.println("Song Cover: " + songCoverPath);
        // logic to save the data to a database

        artistRequest.setSong_title(songTitleField.getText());
        artistRequest.setSong_creation_date((int) (songCreationDateField.getValue().toEpochDay()));
        artistRequest.setSong_file(songTrackPath);
        artistRequest.setSong_cover(songCoverPath);
        if(artistRequest.getAlbum_id() == -1) {
            artistRequest.setAlbum_creation_date((int) (albumCreationDateField.getValue().toEpochDay()));
            artistRequest.setAlbum_cover(albumCoverPath);
        }

        System.out.println("Artist Request: " + artistRequest.toJson());

        artistRequest = mainLayoutController.getDbAccess().insert_artist_request(artistRequest);

        // Clear all fields after submission
        clearAllFields();
    }

    @FXML
    protected void onCancelButtonClicked() {
        // Handle cancel button click
        System.out.println("Cancel button clicked");
        clearAllFields();

        // Close the current window
        Platform.runLater(() -> {
            if (stage != null) {
                stage.close();
            }
        });
    }

    @FXML
    protected void onAlbumSearchButtonClicked() {
        String search = albumNameField.getText();
        performAlbumSearch(search);
    }

    private void performAlbumSearch(String searchText) {
        if (searchText != null && !searchText.isEmpty()) {
            Task<List<ASearchBarHelper>> searchTask = new Task<List<ASearchBarHelper>>() {
                @Override
                protected List<ASearchBarHelper> call() throws Exception {
                    List<AlbumSearch> albums = mainLayoutController.getDbAccess().select_search_album_by_artist_id(artistRequest.getArtist_id());
                    // filter algorithm
                    albums.removeIf(album -> !album.getAlbum_name().toLowerCase().contains(searchText.toLowerCase()));

                    List<ASearchBarHelper> searchBarHelperList = new ArrayList<>();

                    if (!searchText.isEmpty()) {
                        // Add option to create a new album
                        ASearchBarHelper newAlbumOption = new ASearchBarHelper();
                        newAlbumOption.mainLayoutController = mainLayoutController;
                        newAlbumOption.mainTitle = searchText.trim();
                        newAlbumOption.SubTitle = "Create a new album";
                        searchBarHelperList.add(newAlbumOption);
                    }

                    if(albums != null && !albums.isEmpty()) {
                        for (AlbumSearch album : albums) {
                            ASearchBarHelper searchBarHelper = new ASearchBarHelper(album, mainLayoutController);
                            searchBarHelperList.add(searchBarHelper);
                        }
                    }

                    return searchBarHelperList;
                }
            };

            searchTask.setOnSucceeded(event -> {
                Platform.runLater(() -> {
                    albumSearchComboBox.getItems().clear();
                    albumSearchComboBox.getItems().addAll(searchTask.getValue());
                    albumSearchComboBox.show();
                });
            });

            searchTask.setOnFailed(event -> {
                Platform.runLater(() -> {
                    searchTask.getException().printStackTrace();
                    // Optionally, show an error message to the user
                });
            });

            executorService.submit(searchTask);
        }
    }

    public void initialize() {
        // Set default values for labels
        clearAllFields();

        // Set up event handlers for buttons
        selectSongTrackButton.setOnAction(event -> {
            // Handle song track selection
            System.out.println("Select song track button clicked");
            // Implement file chooser logic here

            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Song track files (MP3)", "*.mp3");
            fileChooser.getExtensionFilters().add(extFilter);
            File selectedFile = fileChooser.showOpenDialog(stage);
            if (selectedFile != null) {
                Platform.runLater(() -> {
                    songTrackLabel.setText(selectedFile.getName());
                    songTrackPath = selectedFile.getAbsolutePath();
                });
            } else {
                Platform.runLater(() -> songTrackLabel.setText("No track selected"));
            }
        });

        selectSongCoverButton.setOnAction(event -> {
            // Handle song cover selection
            System.out.println("Select song cover button clicked");
            // Implement file chooser logic here

            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Image files (PNG)", "*.png");
            fileChooser.getExtensionFilters().add(extFilter);
            File selectedFile = fileChooser.showOpenDialog(stage);
            if (selectedFile != null) {
                Platform.runLater(() -> {
                    songCoverLabel.setText(selectedFile.getName());
                    songCoverPath = selectedFile.getAbsolutePath();
                });
            } else {
                Platform.runLater(() -> songCoverLabel.setText("No cover selected"));
            }
        });

        selectAlbumCoverButton.setOnAction(event -> {
            // Handle album cover selection
            System.out.println("Select album cover button clicked");
            // Implement file chooser logic here

            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Image files (PNG)", "*.png");
            fileChooser.getExtensionFilters().add(extFilter);
            File selectedFile = fileChooser.showOpenDialog(stage);
            if (selectedFile != null) {
                Platform.runLater(() -> {
                    albumCoverLabel.setText(selectedFile.getName());
                    albumCoverPath = selectedFile.getAbsolutePath();
                });
            } else {
                Platform.runLater(() -> albumCoverLabel.setText("No cover selected"));
            }
        });

        albumNameField.textProperty().addListener((observable, oldValue, newValue) -> {
            performAlbumSearch(newValue);
        });

        albumSearchComboBox.setCellFactory(new Callback<ListView<ASearchBarHelper>, ListCell<ASearchBarHelper>>() {
            @Override
            public ListCell<ASearchBarHelper> call(ListView<ASearchBarHelper> aSearchBarHelperListView) {
                class CellHelper extends ListCell<ASearchBarHelper> {
                    @Override
                    public void updateItem (ASearchBarHelper item,boolean empty){
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            item.setMainLayoutController(mainLayoutController);
                            setGraphic(item.getCellFormat());
                        }
                    }
                }
                return new CellHelper();
            }
        });

        // Set up event handlers for combo boxes
        albumSearchComboBox.valueProperty().addListener(new ChangeListener<ASearchBarHelper>() {
            @Override
            public void changed(ObservableValue<? extends ASearchBarHelper> observableValue, ASearchBarHelper aSearchBarHelper, ASearchBarHelper t1) {
                if (t1 == null) {
                    return;
                }
                if (t1.SubTitle != null && t1.SubTitle.equals("Create a new album")) {
                    artistRequest.setAlbum_id(-1);
                    artistRequest.setAlbum_name(t1.mainTitle);
                } else {
                    Platform.runLater(() -> albumNameField.setText(t1.mainTitle));
                    artistRequest.setAlbum_id(t1.id);
                }
                Platform.runLater(() -> albumSearchComboBox.getItems().clear());
            }
        });
    }
}
