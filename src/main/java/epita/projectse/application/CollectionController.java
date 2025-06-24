package epita.projectse.application;

import epita.projectse.domain.*;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.File;
import java.util.List;

public class CollectionController implements IController {

    @FXML
    private ImageView coverImage, likedImage;
    @FXML
    private Label nbOfStreamsLabel, nameLabel, usernameLabel;
    @FXML
    private VBox songVbox;

    private User user;
    private MainLayoutController mainLayoutController;
    private boolean isLiked;

    @Override
    public void initializeContent(MainLayoutController mainLayoutController) {
        this.mainLayoutController = mainLayoutController;
        this.user = mainLayoutController.getUser();
    }

    public void initializeContentWithId(long collection_id, CollectionType collectionType) {
        // Check if user has liked collection

        String path;
        if (collectionType == CollectionType.ALBUM) {
            AlbumSearch albumSearch = mainLayoutController.getDbAccess().select_search_album_by_album_id(collection_id);
            usernameLabel.setText(albumSearch.getArtist_name());
            nameLabel.setText(albumSearch.getAlbum_name());
            path = mainLayoutController.getDbAccess().get_picture_by_name(albumSearch.getAlbum_cover());
            isLiked = mainLayoutController.getDbAccess().user_likes_album_exists(user.getId(), collection_id);

            likedImage.setOnMouseClicked(event -> {
                if (isLiked) {
                    mainLayoutController.getDbAccess().delete_user_likes_album(new UserLikesAlbum(user.getId(), collection_id));
                    likedImage.setImage(new Image(getClass().getResourceAsStream("/unliked.png")));
                } else {
                    mainLayoutController.getDbAccess().insert_user_likes_album(new UserLikesAlbum(user.getId(), collection_id));
                    likedImage.setImage(new Image(getClass().getResourceAsStream("/liked.png")));
                }
                isLiked = !isLiked;
            });

        } else if (collectionType == CollectionType.PLAYLIST) {
            PlaylistSearch playlistSearch = mainLayoutController.getDbAccess().select_search_playlist_by_playlist_id(collection_id);
            usernameLabel.setText(playlistSearch.getArtist_name());
            nameLabel.setText(playlistSearch.getPlaylist_name());
            path = mainLayoutController.getDbAccess().get_picture_by_name(playlistSearch.getPlaylist_cover());
            isLiked = mainLayoutController.getDbAccess().user_likes_playlist_exists(user.getId(), collection_id);

            likedImage.setOnMouseClicked(event -> {
                if (isLiked) {
                    mainLayoutController.getDbAccess().delete_user_likes_playlist(new UserLikesPlaylist(user.getId(), collection_id));
                    likedImage.setImage(new Image(getClass().getResourceAsStream("/unliked.png")));
                } else {
                    mainLayoutController.getDbAccess().insert_user_likes_playlist(new UserLikesPlaylist(user.getId(), collection_id));
                    likedImage.setImage(new Image(getClass().getResourceAsStream("/liked.png")));
                }
                isLiked = !isLiked;
            });
        }
        else {
            throw new IllegalArgumentException("Unknown collection type");
        }
        if (path != null) {
            coverImage.setImage(new Image(new File(path).toURI().toString()));
        }

        // Set up the like button
        likedImage.setImage(new Image(getClass().getResourceAsStream(isLiked ? "/liked.png" : "/unliked.png")));

        loadRequestsInBackground(songVbox, collection_id, collectionType);
    }

    private void loadRequestsInBackground(Pane box, long collection_id, CollectionType type) {
        Task<List<?>> task = new Task<>() {
            @Override
            protected List<?> call() throws Exception {
                return switch (type) {
                    case PLAYLIST -> mainLayoutController.getDbAccess().select_search_songs_by_playlist_id(collection_id);
                    case ALBUM -> mainLayoutController.getDbAccess().select_search_songs_by_album_id(collection_id);
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
                if (request instanceof SongSearch) {
                    addSongToBox((VBox) box, (SongSearch) request);
                }

            }
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

    public void refreshVBox(long collection_id, CollectionType type) {
        switch (type) {
            case SONG:
                loadRequestsInBackground(songVbox, collection_id, CollectionType.SONG);
                break;
        }
    }
}


