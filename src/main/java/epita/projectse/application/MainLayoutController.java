package epita.projectse.application;

import epita.projectse.data_access.DbAccess;
import epita.projectse.domain.*;
import epita.projectse.utils.ASearchBarHelper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainLayoutController {

    @FXML
    private TextField searchBar;
    @FXML
    private BorderPane contentPane;
    @FXML
    private VBox ViewVBoxContainer;
    @FXML
    private ComboBox<ASearchBarHelper> searchBarComboBox;
    @FXML
    private ImageView profilePictureView, toArtistView, toCollaboratorView, toAdminView;
    @FXML
    private VBox viewVbox;
    @FXML
    private Button searchButton;

    public final Map<String, Pane> contentCache = new HashMap<>();
    public final Map<String, IController> controllerCache = new HashMap<>();
    public final ExecutorService executorService = Executors.newCachedThreadPool();
    private User user;
    private DbAccess dbAccess;
    private MediaPlayer mediaPlayer = new MediaPlayer(new Media(new File("src/main/resources/empty.mp3").toURI().toString()));
    private final List<Song> songQueue = new ArrayList<>();
    private int songIndex = 0;
    private boolean isPlaying = false;

    public void setUser(User user) {
        this.user = user;
    }
    public User getUser() {
        return user;
    }
    public void setDbAccess(DbAccess dbAccess) {
        this.dbAccess = dbAccess;
    }
    public DbAccess getDbAccess() {
        return dbAccess;
    }
    public boolean isPlaying() {
        return isPlaying;
    }
    public void setPlaying(boolean playing) {
        this.isPlaying = playing;
    }
    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }
    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }
    public List<Song> getSongQueue() {
        return songQueue;
    }
    public int getSongIndex() {
        return songIndex;
    }

    public void previousSong(){
        if (mediaPlayer.getCurrentTime().toSeconds() >= 5){
            mediaPlayer.seek(Duration.ZERO);
        }
        else {
            if (songIndex > 0) {
                songIndex--;
                this.playSong();
            } else {
                System.out.println("No previous song in the queue.");
                mediaPlayer.seek(Duration.ZERO);
                mediaPlayer.stop();
            }
        }
    }
    public void nextSong(){
        if (songIndex < songQueue.size() - 1) {
            songIndex++;
            this.playSong();
        } else {
            System.out.println("No next song in the queue.");
            mediaPlayer.seek(Duration.ZERO);
            mediaPlayer.stop();
        }
    }
    public void insertPlaySong(Song song){
        if (songQueue.isEmpty()) {
            songQueue.add(song);
            songIndex = -1;
        } else {
            songQueue.add(songIndex+1, song);
        }
        this.nextSong();
    }
    public void enqueueSong(Song song){
        songQueue.add(song);
    }
    public void playSong(){
        Song song = songQueue.get(songIndex);
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer(song.getMedia(dbAccess));
            mediaPlayer.setOnReady(() -> {
                mediaPlayer.play();
                isPlaying = true;
            });
        } else {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = new MediaPlayer(song.getMedia(dbAccess));
            mediaPlayer.setOnReady(() -> {
                mediaPlayer.play();
                isPlaying = true;
            });
        }
        ((ApplicationController)controllerCache.get("applicationView.fxml")).initialize();
        controllerCache.get("applicationView.fxml").initializeContent(this);
        controllerCache.get("applicationMiniView.fxml").initializeContent(this);

    }
    public void switchPauseSong(){
        if (mediaPlayer == null) {
            return;
        }
        if (isPlaying()) {
            mediaPlayer.pause();
            isPlaying = false;
        } else {
            mediaPlayer.play();
            isPlaying = true;
        }
            mediaPlayer.pause();
            MediaPlayer.Status status = mediaPlayer.getStatus();
            if (status == MediaPlayer.Status.PLAYING) {
                mediaPlayer.pause();
            } else if (status == MediaPlayer.Status.READY || status == MediaPlayer.Status.PAUSED || status == MediaPlayer.Status.HALTED || status == MediaPlayer.Status.STOPPED) {
                mediaPlayer.play();
            }
    }


    @FXML
    void onQuitButtonPressed(ActionEvent event) {
        Stage stage = (Stage) contentPane.getScene().getWindow();
        System.out.println("logout button pressed");
        executorService.shutdown();
        stage.close();
    }

    @FXML
    void onLogoutButtonPressed(ActionEvent event) {
        executorService.shutdown();
        mediaPlayer.stop();
        mediaPlayer.dispose();
        songQueue.clear();
        controllerCache.clear();
        contentCache.clear();
        Stage stage = (Stage) contentPane.getScene().getWindow();
        Scene scene = null;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            URL fxmlLocation = getClass().getResource("credentialsView.fxml");
            if (fxmlLocation == null) {
                throw new IllegalArgumentException("Cannot find FXML file.");
            }
            fxmlLoader.setLocation(fxmlLocation);
            scene = new Scene(fxmlLoader.load(), 1000, 600);
            stage.setResizable(false);
            stage.setTitle("French Touch - Login/Register");
            stage.setScene(scene);
            stage.show();
        }
        catch (IOException e) {
            System.out.println("Application View not found");
            throw new RuntimeException(e);
        }
        stage.setScene(scene);
    }

    @FXML
    void onAddPlaylistButtonPressed(MouseEvent event) {
        loadContent("newCollectionView.fxml");
    }

    @FXML
    void onApplicationButtonClicked(MouseEvent event) {
        loadContent("applicationView.fxml");
    }

    @FXML
    void onAdminButtonClicked(MouseEvent event) {
        loadContent("adminPanelView.fxml");
    }

    @FXML
    void onArtistButtonClicked(MouseEvent event) {
        loadContent("artistPanelView.fxml");
    }

    @FXML
    void onCollaboratorButtonClicked(MouseEvent event) {
        loadContent("collaboratorPanelView.fxml");
        ((CollaboratorPanelController)controllerCache.get("collaboratorPanelView.fxml")).initializeContent();
    }

    @FXML
    void onFavouritesButtonClicked(
            Event event) {
        loadContent("favouritesView.fxml");
        ((FavouritesController)controllerCache.get("favouritesView.fxml")).initializeContent();
    }

    @FXML
    void onQueueButtonClicked(Event event) {
        loadContent("queueView.fxml");
        ((QueueController)controllerCache.get("queueView.fxml")).initializeContent();
    }

    @FXML
    void onProfileButtonClicked(MouseEvent event) {
        loadContent("profileView.fxml");
    }

    @FXML
    void onShowSettingsButtonClicked (ActionEvent event) {
        if (!ViewVBoxContainer.isVisible()){
            updateDisplayView();
        }
        ViewVBoxContainer.setVisible(ViewVBoxContainer.isDisable());
        ViewVBoxContainer.setManaged(ViewVBoxContainer.isDisable());
        ViewVBoxContainer.setDisable(!ViewVBoxContainer.isDisable());

    }

    void updateDisplayView(){
        long id = user.getId();
        if (!dbAccess.is_artist(id)){
            toArtistView.setVisible(false);
            toArtistView.setDisable(true);
            toArtistView.setManaged(false);
        }
        if (!dbAccess.is_collaborator(id)){
            toCollaboratorView.setVisible(false);
            toCollaboratorView.setDisable(true);
            toCollaboratorView.setManaged(false);
        }
        if (!dbAccess.is_admin(id)){
            toAdminView.setVisible(false);
            toAdminView.setDisable(true);
            toAdminView.setManaged(false);
        }
    }

    void onSearchButtonClicked(ActionEvent event) {
        String searchText = searchBar.getText();
        if (searchText != null && !searchText.isEmpty()) {
            performSearch(searchText);
        }
    }

    private void performSearch(String searchText) {
        if (searchText != null && !searchText.isEmpty()) {
            Task<List<ASearchBarHelper>> searchTask = new Task<List<ASearchBarHelper>>() {
                @Override
                protected List<ASearchBarHelper> call() throws Exception {
                    List<ArtistSearch> artists = dbAccess.select_search_artists(searchText);
                    List<SongSearch> songs = dbAccess.select_search_songs(searchText);
                    List<AlbumSearch> albums = dbAccess.select_search_albums(searchText);
                    List<PlaylistSearch> playlists = dbAccess.select_search_playlists(searchText);

                    List<ASearchBarHelper> searchBarHelperList = new ArrayList<>();

                    if (artists != null && !artists.isEmpty()) {
                        for (ArtistSearch artist : artists) {
                            if (artist != null) {
                                ASearchBarHelper artistHelper = new ASearchBarHelper(artist, MainLayoutController.this);
                                searchBarHelperList.add(artistHelper);
                            }
                        }
                    }

                    if (songs != null && !songs.isEmpty()) {
                        for (SongSearch song : songs) {
                            if (song != null) {
                                ASearchBarHelper songHelper  = new ASearchBarHelper(song, MainLayoutController.this);
                                searchBarHelperList.add(songHelper);
                            }
                        }
                    }

                    if (albums != null && !albums.isEmpty()) {
                        for (AlbumSearch album : albums) {
                            if (album != null) {
                                ASearchBarHelper albumHelper = new ASearchBarHelper(album, MainLayoutController.this);
                                searchBarHelperList.add(albumHelper);
                            }
                        }
                    }

                    if (playlists != null && !playlists.isEmpty()) {
                        for (PlaylistSearch playlist : playlists) {
                            if (playlist != null) {
                                ASearchBarHelper playlistHelper = new ASearchBarHelper(playlist, MainLayoutController.this);
                                searchBarHelperList.add(playlistHelper);
                            }
                        }
                    }

                    return searchBarHelperList;
                }
            };

            searchTask.setOnSucceeded(event -> {
                searchBarComboBox.getItems().clear();
                List<ASearchBarHelper> list = searchTask.getValue();
                if (list != null && !list.isEmpty())
                    searchBarComboBox.setPrefHeight(list.size() * 100);{
                    searchBarComboBox.getItems().addAll(list);
                    searchBarComboBox.show();
                }
            });

            executorService.submit(searchTask);
        }
    }

    public void loadContent(String fxmlFile) {
        try {
            // Check if content is already cached
            Pane content = contentCache.get(fxmlFile);
            if (content == null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource((fxmlFile)));
                content = loader.load();
                IController controller = loader.getController();

                contentCache.put(fxmlFile, content);
                controllerCache.put(fxmlFile, controller);
            }
            content = contentCache.get(fxmlFile);
            IController controller = controllerCache.get(fxmlFile);
            controller.initializeContent(this);

            if (!fxmlFile.equals("applicationView.fxml")){
                contentPane.setBottom(contentCache.get("applicationMiniView.fxml"));
                controllerCache.get("applicationMiniView.fxml").initializeContent(this);
            }
            else {
                contentPane.setBottom(null);
            }
            contentPane.setCenter(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initializeProfilePicture() {
        user = dbAccess.select_user_by_id(user.getId());
        String path = dbAccess.get_picture_by_name(user.getProfilePictureWithoutQuotes());
        if (path == null) {
            path = getClass().getClassLoader().getResource("base_profile_pic.png").getFile();
        }
        Image image = new Image(new File(path).toURI().toString());
        profilePictureView.setImage(image);
    }

    public void initialize() {
        searchBarComboBox.setMaxHeight(200);
        loadContent("applicationView.fxml");
        try {
            FXMLLoader miniloader = new FXMLLoader(getClass().getResource("applicationMiniView.fxml"));
            Pane miniPane = miniloader.load();
            IController controller = miniloader.getController();
            contentCache.put("applicationMiniView.fxml", miniPane);
            controllerCache.put("applicationMiniView.fxml", controller);
        } catch (IOException e) {
            System.out.println("applicationMiniView.fxml not found");
        }

        searchBar.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("ENTER")) {
                searchButton.fire();
            }
        });

        // Add a listener to the textProperty of the searchBar
        searchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            performSearch(newValue);
        });

        searchBarComboBox.setCellFactory(new Callback<ListView<ASearchBarHelper>, ListCell<ASearchBarHelper>>() {
            @Override
            public ListCell<ASearchBarHelper> call(ListView<ASearchBarHelper> aSearchBarHelperListView) {
                class CellHelper extends ListCell<ASearchBarHelper> {
                    @Override
                    public void updateItem(ASearchBarHelper item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            setGraphic(item.getCellFormat());
                        }
                    }
                }
                return new CellHelper();
            }
        });

        // Set the maximum height of the ComboBox's popup list
        searchBarComboBox.showingProperty().addListener((obs, wasShowing, isShowing) -> {
            if (isShowing) {
                ListView<ASearchBarHelper> listView = (ListView<ASearchBarHelper>) searchBarComboBox.lookup(".list-view");
                if (listView != null) {
                    listView.setMaxHeight(300); // Adjust the value as needed
                }
            }
        });

        searchBarComboBox.valueProperty().addListener(new ChangeListener<ASearchBarHelper>() {
            @Override
            public void changed(ObservableValue<? extends ASearchBarHelper> observableValue, ASearchBarHelper aSearchBarHelper, ASearchBarHelper t1) {
                if (t1 == null) {
                } else if (t1.type == ASearchBarHelper.Type.SONG) {
                    Song currentSong = dbAccess.select_song_by_id(t1.id);
                    insertPlaySong(currentSong);
                    currentSong.setSongFile(dbAccess.get_song_track(currentSong.getSongFile()));
                    ApplicationController applicationController = (ApplicationController) controllerCache.get("applicationView.fxml");
                    ApplicationController miniController = (ApplicationController) controllerCache.get("applicationMiniView.fxml");

                    applicationController.setCurrentArtist(dbAccess.select_user_by_id(dbAccess.select_artist_by_id(currentSong.getArtistId()).getUserId()));
                    miniController.setCurrentArtist(applicationController.getCurrentArtist());

                    applicationController.updateMusic(currentSong);
                    miniController.updateMusic(currentSong);

                    applicationController.initializeContent(MainLayoutController.this);
                    miniController.initializeContent(MainLayoutController.this);
                    searchBarComboBox.getItems().clear();
                } else if (t1.type == ASearchBarHelper.Type.ARTIST) {
                    loadContent("artistView.fxml");
                    ((ArtistController) controllerCache.get("artistView.fxml")).initializeContentWithId(t1.id);
                } else if (t1.type == ASearchBarHelper.Type.ALBUM) {
                    loadContent("collectionView.fxml");
                    ((CollectionController) controllerCache.get("collectionView.fxml")).initializeContentWithId(t1.id, CollectionType.ALBUM);
                } else if (t1.type == ASearchBarHelper.Type.PLAYLIST) {
                    loadContent("collectionView.fxml");
                    ((CollectionController) controllerCache.get("collectionView.fxml")).initializeContentWithId(t1.id, CollectionType.PLAYLIST);
                } else {
                    System.out.println("Unknown type");
                }
            }
        });
    }


}
