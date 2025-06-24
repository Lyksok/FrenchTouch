package epita.projectse.application;

import epita.projectse.domain.Song;
import epita.projectse.domain.SongSearch;
import epita.projectse.domain.User;
import epita.projectse.domain.UserLikesSong;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.io.File;

public class SongElementController implements IController {

    @FXML
    private Label songName, artistName;
    @FXML
    private ImageView songCover, likeImage;
    @FXML
    private Pane addCollectionPane, addQueuePane, addFavouritesPane, mainPane;

    private long songId;
    private Song song;
    private boolean isLiked;
    private MainLayoutController mainLayoutController;

    @Override
    public void initializeContent(MainLayoutController mainLayoutController) {
        this.mainLayoutController = mainLayoutController;
    }

    public void initElt(SongSearch songSearch) {
        this.songId = songSearch.getSong_id();
        this.song = mainLayoutController.getDbAccess().select_song_by_id(songId);
        this.isLiked = mainLayoutController.getDbAccess().user_likes_song_exists(mainLayoutController.getUser().getId(), songId);

            songName.setText(songSearch.getSong_title());
            artistName.setText(songSearch.getArtist_name());
        Platform.runLater(() -> {
            String imagePath = mainLayoutController.getDbAccess().get_picture_by_name(songSearch.getSong_cover());
            if (imagePath != null) {
                songCover.setImage(new Image(new File(imagePath).toURI().toString()));
            }
        });

        setupButtons();
    }

    public void initElt(UserLikesSong userLikesSong) {
        this.songId = userLikesSong.getSongId();
        this.song = mainLayoutController.getDbAccess().select_song_by_id(songId);
        this.isLiked = mainLayoutController.getDbAccess().user_likes_song_exists(mainLayoutController.getUser().getId(), songId);

            songName.setText(song.getTitle());
            User artist_user = mainLayoutController.getDbAccess().select_user_by_id(mainLayoutController.getDbAccess().select_artist_by_id(song.getArtistId()).getUserId());
            artistName.setText(artist_user.getUsername());
        Platform.runLater(() -> {
            String imagePath = mainLayoutController.getDbAccess().get_picture_by_name(song.getCoverImage());
            if (imagePath != null) {
                songCover.setImage(new Image(new File(imagePath).toURI().toString()));
            }
        });

        setupButtons();
    }

    public void initElt(Song song) {
        this.songId = song.getId();
        this.song = song;
        this.isLiked = mainLayoutController.getDbAccess().user_likes_song_exists(mainLayoutController.getUser().getId(), songId);

            songName.setText(song.getTitle());
            User artist_user = mainLayoutController.getDbAccess().select_user_by_id(mainLayoutController.getDbAccess().select_artist_by_id(song.getArtistId()).getUserId());
            artistName.setText(artist_user.getUsername());
        Platform.runLater(() -> {
            String imagePath = mainLayoutController.getDbAccess().get_picture_by_name(song.getCoverImage());
            if (imagePath != null) {
                songCover.setImage(new Image(new File(imagePath).toURI().toString()));
            }
        });

        setupButtons();
    }

    private void setupButtons() {
        songCover.setOnMouseClicked(mouseEvent -> {
            // Logic to play the song
            System.out.println("Playing song: " + songName.getText());
            mainLayoutController.insertPlaySong(song);
        });

        addCollectionPane.setOnMouseClicked(event -> {
            // Logic to add song to collection
            System.out.println("Song added to collection: " + songName.getText());
        });

        addQueuePane.setOnMouseClicked(event -> {
            // Logic to add song to queue
            System.out.println("Song added to queue: " + songName.getText());
            mainLayoutController.enqueueSong(song);
        });

        Platform.runLater(() -> {
            if (isLiked) {
                likeImage.setImage(new Image(getClass().getResourceAsStream("/liked.png")));
            } else {
                likeImage.setImage(new Image(getClass().getResourceAsStream("/unliked.png")));
            }
        });

        addFavouritesPane.setOnMouseClicked(event -> {
            if (isLiked) {
                // Logic to remove song from favourites
                System.out.println("Song removed from favourites: " + songName.getText());
                mainLayoutController.getDbAccess().delete_user_likes_song(new UserLikesSong(mainLayoutController.getUser().getId(), songId));
                Platform.runLater(() -> {
                    likeImage.setImage(new Image(getClass().getResourceAsStream("/unliked.png")));
                });
                isLiked = false;
            } else {
                // Logic to add song to favourites
                System.out.println("Song added to favourites: " + songName.getText());
                mainLayoutController.getDbAccess().insert_user_likes_song(new UserLikesSong(mainLayoutController.getUser().getId(), songId));
                Platform.runLater(() -> {
                    likeImage.setImage(new Image(getClass().getResourceAsStream("/liked.png")));
                });
                isLiked = true;
            }
        });
    }

    public void setFirstSongInQueue() {
        // Set background to light green
        Platform.runLater(() -> {
            mainPane.setStyle("-fx-background-color: #90EE90;"); // Light green color
        });
    }

    private void handleAddToCollection() {
        // Logic to add song to collection
        System.out.println("Song added to collection: " + songName.getText());
    }

    private void handleAddToQueue() {
        // Logic to add song to queue
        System.out.println("Song added to queue: " + songName.getText());
    }

    private void handleAddToFavourites() {
        // Logic to add song to favourites
        System.out.println("Song added to favourites: " + songName.getText());
    }
}
