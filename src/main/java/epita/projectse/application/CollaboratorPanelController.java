package epita.projectse.application;

import epita.projectse.domain.AlbumSearch;
import epita.projectse.domain.ArtistSearch;
import epita.projectse.domain.CollaboratorRequest;
import epita.projectse.utils.ASearchBarHelper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.File;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class CollaboratorPanelController implements IController{
    @FXML
    private TextField artistNameField, songTitleField, albumNameField;
    @FXML
    private ComboBox<ASearchBarHelper> artistSearchComboBox, albumSearchComboBox;
    @FXML
    private TextArea artistBiographyField;
    @FXML
    private Button selectArtistProfilePictureButton, selectSongTrackButton, selectSongCoverButton, selectAlbumCoverButton;
    @FXML
    private Label artistProfilePictureLabel, songTrackLabel, songCoverLabel, albumCoverLabel;
    @FXML
    private DatePicker songCreationDateField, albumCreationDateField;
    @FXML
    private Label dialogLabel;
    @FXML
    private Button submitButton;
    @FXML
    private Button cancelButton;

    private Stage stage;
    private String songTrackPath;
    private String songCoverPath;
    private String artistProfilePicturePath;
    private String albumCoverPath;
    private CollaboratorRequest collaboratorRequest = new CollaboratorRequest();
    private ExecutorService executorService;

    private MainLayoutController mainLayoutController;

    @Override
    public void initializeContent(MainLayoutController mainLayoutController) {
        this.mainLayoutController = mainLayoutController;
        this.executorService = mainLayoutController.executorService;
        collaboratorRequest.setCollaboratorId(mainLayoutController.getDbAccess().select_collaborator_by_user_id(mainLayoutController.getUser().getId()).getId());
        collaboratorRequest.setArtist_id(-1);
        collaboratorRequest.setAlbum_id(-1);
    }

    private void clearAllFields() {
        songTitleField.clear();
        songCreationDateField.setValue(null);
        songTrackLabel.setText("No track selected");
        songCoverLabel.setText("No cover selected");
        artistNameField.clear();
        artistBiographyField.clear();
        artistProfilePictureLabel.setText("No picture selected");
        artistProfilePicturePath = null;
        songTrackPath = null;
        songCoverPath = null;
        albumSearchComboBox.getItems().clear();
        albumNameField.clear();
        albumCreationDateField.setValue(null);
        albumCoverLabel.setText("No cover selected");
        artistSearchComboBox.getItems().clear();
        dialogLabel.setText("");
    }

    @FXML
    protected void onSubmitButtonClicked() {
        // Song title, song track and artist name are mandatory
        System.out.println("onSubmitButtonClicked");
        System.out.println("Collaborator ID: " + collaboratorRequest.getCollaboratorId());
        System.out.println("Song title: " + collaboratorRequest.getSong_title());
        System.out.println("Song creation date: " + songCreationDateField.getValue().toEpochSecond(LocalTime.now(), java.time.ZoneOffset.UTC));
        System.out.println("Song file: " + songTrackPath);
        System.out.println("Song cover: " + songCoverPath);
        System.out.println("Artist ID: " + collaboratorRequest.getArtist_id());
        System.out.println("Artist name: " + collaboratorRequest.getArtist_name());
        System.out.println("Artist biography: " + artistBiographyField.getText());
        System.out.println("Artist profile picture: " + artistProfilePicturePath);
        System.out.println("Album ID: " + collaboratorRequest.getAlbum_id());
        System.out.println("Album name: " + collaboratorRequest.getAlbum_name());
        System.out.println("Album creation date: " + albumCreationDateField.getValue().toEpochSecond(LocalTime.now(), java.time.ZoneOffset.UTC));
        System.out.println("Album cover: " + albumCoverPath);

        if (((collaboratorRequest.getArtist_id() == -1 && collaboratorRequest.getArtist_name().isEmpty())
                || (collaboratorRequest.getAlbum_id() == -1 && (collaboratorRequest.getAlbum_name().isEmpty() || albumCreationDateField.getValue() == null))
                || songTitleField.getText().trim().isEmpty()
                || songTrackLabel.getText().equals("No track selected")
                || songCreationDateField.getValue() == null)) {
            dialogLabel.setText("Please fill in all mandatory fields.");
            return;
        }
        // logic to save the data to a database

        //     public CollaboratorRequest(long collaborator_id, String song_title, int song_creation_date, String song_file, String song_cover,long artist_id, String artist_name, String artist_biography, String artist_profile_picture, long album_id, String album_name, int album_creation_date, String album_cover)
        collaboratorRequest.setSong_title(songTitleField.getText().trim());
        collaboratorRequest.setSong_creation_date((int) songCreationDateField.getValue().toEpochSecond(LocalTime.now(), java.time.ZoneOffset.UTC));
        collaboratorRequest.setSong_file(songTrackPath);
        collaboratorRequest.setSong_cover(songCoverPath);

        if(collaboratorRequest.getArtist_id() == -1) {
            collaboratorRequest.setArtist_biography(artistBiographyField.getText().trim());
            collaboratorRequest.setArtist_profile_picture(artistProfilePicturePath);
        }
        if(collaboratorRequest.getAlbum_id() == -1) {
            collaboratorRequest.setAlbum_creation_date((int) albumCreationDateField.getValue().toEpochSecond(LocalTime.now(), java.time.ZoneOffset.UTC));
            collaboratorRequest.setAlbum_cover(albumCoverPath);
        }

        System.out.println("Collaborator Request: " + collaboratorRequest.toJson());

        collaboratorRequest = mainLayoutController.getDbAccess().insert_collaborator_request(collaboratorRequest);

        // Clear all fields after submission
        dialogLabel.setText("Collaborator request submitted successfully.");
        clearAllFields();
        collaboratorRequest = new CollaboratorRequest();
    }

    @FXML
    protected void onCancelButtonClicked() {
        // Handle cancel button click
        System.out.println("Cancel button clicked");
        clearAllFields();
    }

    private void performArtistSearch(String searchText) {
        if (searchText != null || !searchText.isEmpty()) {
            Task<List<ASearchBarHelper>> searchTask = new Task<List<ASearchBarHelper>>() {
                @Override
                protected List<ASearchBarHelper> call() throws Exception {
                    List<ArtistSearch> artists = mainLayoutController.getDbAccess().select_search_artists(searchText);

                    List<ASearchBarHelper> searchBarHelperList = new ArrayList<>();

                    if (!searchText.isEmpty()) {
                        // Add option to create a new artist
                        ASearchBarHelper newArtistOption = new ASearchBarHelper();
                        newArtistOption.mainTitle = searchText.trim();
                        newArtistOption.SubTitle = "Create a new artist";
                        searchBarHelperList.add(newArtistOption);
                    }

                    if(artists != null && !artists.isEmpty()) {
                        for (ArtistSearch artist : artists) {
                            if (artist!= null) {
                                ASearchBarHelper searchBarHelper = new ASearchBarHelper(artist, mainLayoutController);
                                searchBarHelperList.add(searchBarHelper);
                            }
                        }
                    }

                    return searchBarHelperList;
                }
            };

            searchTask.setOnSucceeded(event -> {
                artistSearchComboBox.getItems().clear();
                artistSearchComboBox.getItems().addAll(searchTask.getValue());
                artistSearchComboBox.show();
            });

            executorService.submit(searchTask);
        }
    }

    private void performAlbumSearch(String searchText) {
        if (searchText != null || !searchText.isEmpty()) {
            Task<List<ASearchBarHelper>> searchTask = new Task<List<ASearchBarHelper>>() {
                @Override
                protected List<ASearchBarHelper> call() throws Exception {
                    List<AlbumSearch> albums;
                    if(collaboratorRequest.getArtist_id()==-1) {
                        albums = mainLayoutController.getDbAccess().select_search_albums(searchText);
                    }
                    else {
                        albums = mainLayoutController.getDbAccess().select_search_album_by_artist_id(collaboratorRequest.getArtist_id());
                        // filter algorithm
                        albums.removeIf(album -> !album.getAlbum_name().toLowerCase().contains(searchText.toLowerCase()));
                    }

                    List<ASearchBarHelper> searchBarHelperList = new ArrayList<>();

                    if (!searchText.isEmpty()) {
                        // Add option to create a new artist
                        ASearchBarHelper newArtistOption = new ASearchBarHelper();
                        newArtistOption.mainLayoutController = mainLayoutController;
                        newArtistOption.mainTitle = searchText.trim();
                        newArtistOption.SubTitle = "Create a new album";
                        searchBarHelperList.add(newArtistOption);
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
                albumSearchComboBox.getItems().clear();
                albumSearchComboBox.getItems().addAll(searchTask.getValue());
                albumSearchComboBox.show();
            });

            executorService.submit(searchTask);
        }
    }

    public void initializeContent() {
        // Set default values for labels
        clearAllFields();

        artistNameField.textProperty().addListener((observable, oldValue, newValue) -> {
            performArtistSearch(newValue);
        });

        /*
            Inner Class to Help Precise the typer of Item inside SearchBarComboBox
            */
        artistSearchComboBox.setCellFactory(new Callback<ListView<ASearchBarHelper>, ListCell<ASearchBarHelper>>() {
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

        // Set up event handlers for buttons
        selectArtistProfilePictureButton.setOnAction(event -> {
            // Handle artist profile picture selection
            System.out.println("Select artist profile picture button clicked");

            // Implement file chooser logic here
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Image files", "*.png");
            fileChooser.getExtensionFilters().add(extFilter);
            File selectedFile = fileChooser.showOpenDialog(stage);
            if (selectedFile != null) {
                artistProfilePictureLabel.setText(selectedFile.getName());
                artistProfilePicturePath = selectedFile.getAbsolutePath();
                System.out.println("Artist profile picture: " + artistProfilePicturePath);
            } else {
                artistProfilePictureLabel.setText("No picture selected");
            }
        });

        selectSongTrackButton.setOnAction(event -> {
            // Handle song track selection
            System.out.println("Select song track button clicked");
            // Implement file chooser logic here

            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Song track files", "*.mp3");
            fileChooser.getExtensionFilters().add(extFilter);
            File selectedFile = fileChooser.showOpenDialog(stage);
            if (selectedFile != null) {
                songTrackLabel.setText(selectedFile.getName());
                songTrackPath = selectedFile.getAbsolutePath();
            } else {
                songTrackLabel.setText("No track selected");
            }
        });

        selectSongCoverButton.setOnAction(event -> {
            // Handle song cover selection
            System.out.println("Select song cover button clicked");
            // Implement file chooser logic here

            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Image files", "*.png");
            fileChooser.getExtensionFilters().add(extFilter);
            File selectedFile = fileChooser.showOpenDialog(stage);
            if (selectedFile != null) {
                songCoverLabel.setText(selectedFile.getName());
                songCoverPath = selectedFile.getAbsolutePath();
            } else {
                songCoverLabel.setText("No cover selected");
            }
        });

        selectAlbumCoverButton.setOnAction(event -> {
            // Handle album cover selection
            System.out.println("Select album cover button clicked");
            // Implement file chooser logic here

            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Image files", "*.png");
            fileChooser.getExtensionFilters().add(extFilter);
            File selectedFile = fileChooser.showOpenDialog(stage);
            if (selectedFile != null) {
                albumCoverLabel.setText(selectedFile.getName());
                albumCoverPath = selectedFile.getAbsolutePath();
            } else {
                albumCoverLabel.setText("No cover selected");
            }
        });

        // Set up event handlers for combo boxes
        artistSearchComboBox.valueProperty().addListener(new ChangeListener<ASearchBarHelper>() {
            @Override
            public void changed(ObservableValue<? extends ASearchBarHelper> observableValue, ASearchBarHelper aSearchBarHelper, ASearchBarHelper t1) {
                if (t1 == null) {
                    return;
                }
                if (t1.SubTitle != null && t1.SubTitle.equals("Create a new artist")) {
                    collaboratorRequest.setArtist_id(-1);
                    collaboratorRequest.setArtist_name(t1.mainTitle);
                } else {
                artistNameField.setText(t1.mainTitle);
                collaboratorRequest.setArtist_id(t1.id);
                artistSearchComboBox.getItems().clear();
                }
            }
        });

        albumSearchComboBox.valueProperty().addListener(new ChangeListener<ASearchBarHelper>() {
            @Override
            public void changed(ObservableValue<? extends ASearchBarHelper> observableValue, ASearchBarHelper aSearchBarHelper, ASearchBarHelper t1) {
                if (t1 == null) {
                    return;
                }
                if (t1.SubTitle != null && t1.SubTitle.equals("Create a new album")) {
                    collaboratorRequest.setAlbum_id(-1);
                    collaboratorRequest.setAlbum_name(t1.mainTitle);
                } else {
                    albumNameField.setText(t1.mainTitle);
                    collaboratorRequest.setAlbum_id(t1.id);
                    albumSearchComboBox.getItems().clear();
                }
            }
        });
    }
}








