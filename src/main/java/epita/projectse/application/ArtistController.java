package epita.projectse.application;

import epita.projectse.domain.*;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.File;
import java.util.List;

public class ArtistController implements IController {

    @FXML
    private ImageView artistProfileImage, likedImage;
    @FXML
    private Label nbOfStreamsLabel, artistNameLabel;
    @FXML
    private HBox albumHbox;
    @FXML
    private VBox songVbox;

    private MainLayoutController mainLayoutController;
    private Artist artist;
    private boolean isLiked;

    @Override
    public void initializeContent(MainLayoutController mainLayoutController) {
        this.mainLayoutController = mainLayoutController;
    }

    public void initializeContentWithId(long artist_id) {
        System.out.println("Initializing content...");
        this.artist = mainLayoutController.getDbAccess().select_artist_by_id(artist_id);
        User user = mainLayoutController.getDbAccess().select_user_by_id(artist.getUserId());
        this.isLiked = mainLayoutController.getDbAccess().user_likes_artist_exists(mainLayoutController.getUser().getId(), artist_id);

        likedImage.setOnMouseClicked(event -> {
            if (isLiked) {
                mainLayoutController.getDbAccess().delete_user_likes_artist(new UserLikesArtist(mainLayoutController.getUser().getId(), artist_id));
                likedImage.setImage(new Image(getClass().getResourceAsStream("/unliked.png")));
            } else {
                mainLayoutController.getDbAccess().insert_user_likes_artist(new UserLikesArtist(mainLayoutController.getUser().getId(), artist_id));
                likedImage.setImage(new Image(getClass().getResourceAsStream("/liked.png")));
            }
            isLiked = !isLiked;
        });

        if (artist != null) {
            artistNameLabel.setText(user.getUsername());
            nbOfStreamsLabel.setText("Nb. of Streams: "+ artist.getNbOfStreams());
            String path = mainLayoutController.getDbAccess().get_picture_by_name(user.getProfilePictureWithoutQuotes());
            artistProfileImage.setImage(new Image(new File(path).toURI().toString()));
        } else {
            System.out.println("Artist not found");
        }

        likedImage.setImage(new Image(getClass().getResourceAsStream(isLiked ? "/liked.png" : "/unliked.png")));

        loadRequestsInBackground(songVbox, CollectionType.SONG);
        loadRequestsInBackground(albumHbox, CollectionType.ALBUM);
    }

    private void loadRequestsInBackground(Pane box, CollectionType type) {
        Task<List<?>> task = new Task<>() {
            @Override
            protected List<?> call() throws Exception {
                return switch (type) {
                    case SONG -> mainLayoutController.getDbAccess().select_search_song_by_artist_id(artist.getId());
                    case ALBUM -> mainLayoutController.getDbAccess().select_search_album_by_artist_id(artist.getId());
                    default -> throw new IllegalArgumentException("Unknown type");
                };
            }
        };

        task.setOnSucceeded(event -> {
            List<?> requests = task.getValue();
            loadRequests(box, requests, type);
        });

        task.setOnFailed(event -> {
            task.getException().printStackTrace();
        });

        new Thread(task).start();
    }

    private void loadRequests(Pane box, List<?> requests, CollectionType type) {
        box.getChildren().clear(); // Clear existing children
        if (requests != null && !requests.isEmpty()) {
            System.out.println(type + " requests: " + requests.size());
            for (Object request : requests) {
                switch (type) {
                    case ALBUM:
                        if (request instanceof AlbumSearch) {
                            addAlbumToBox((HBox) box, request, type);
                        }
                        break;
                    case SONG:
                        if (request instanceof SongSearch) {
                            addSongToBox((VBox) box, (SongSearch) request);
                        }
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown request type");
                }
            }
        }
    }

    private void addAlbumToBox(HBox hBox, Object searchObj, CollectionType type) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("collectionElement.fxml"));
        try {
            Pane viewElt = loader.load();
            CollectionElementController controller = loader.getController();
            controller.initializeContent(mainLayoutController);
            controller.initElt(searchObj, type);
            hBox.getChildren().add(viewElt);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
  
    private void addSongToBox(VBox vBox, SongSearch songSearch) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("songElement.fxml"));
        try {
            Pane viewElt = loader.load();
            SongElementController controller = loader.getController();
            controller.initializeContent(mainLayoutController);
            controller.initElt(songSearch);

            vBox.getChildren().add(viewElt);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

