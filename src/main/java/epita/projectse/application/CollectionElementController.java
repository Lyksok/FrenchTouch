package epita.projectse.application;

import epita.projectse.domain.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.File;

public class CollectionElementController implements IController {

    @FXML
    private ImageView collectionCover;
    @FXML
    private Label collectionName, collectionArtist;
    @FXML
    private VBox collectionVbox;

    private Object searchObj;
    private CollectionType collectionType;
    private MainLayoutController mainLayoutController;

    public void initElt(Object searchObj, CollectionType collectionType) {
        if (searchObj instanceof AlbumSearch albumSearch) {
            // Initialize the collection element with album details
            this.searchObj = mainLayoutController.getDbAccess().select_album_by_id(albumSearch.getAlbum_id());
            this.collectionType = CollectionType.ALBUM;

                collectionName.setText(albumSearch.getAlbum_name());
                collectionArtist.setText(albumSearch.getArtist_name());
            Platform.runLater(() -> {
                String imagePath = mainLayoutController.getDbAccess().get_picture_by_name(albumSearch.getAlbum_cover());
                if (imagePath != null) {
                    collectionCover.setImage(new Image(new File(imagePath).toURI().toString()));
                }
            });
        } else if (searchObj instanceof PlaylistSearch playlistSearch) {
            // Initialize the collection element with playlist details
            this.searchObj = mainLayoutController.getDbAccess().select_playlist_by_id(playlistSearch.getPlaylist_id());
            this.collectionType = CollectionType.PLAYLIST;

                collectionName.setText(playlistSearch.getPlaylist_name());
                collectionArtist.setText(playlistSearch.getArtist_name());
            Platform.runLater(() -> {
                String imagePath = mainLayoutController.getDbAccess().get_picture_by_name(playlistSearch.getPlaylist_cover());
                if (imagePath != null) {
                    collectionCover.setImage(new Image(new File(imagePath).toURI().toString()));
                }
            });
        } else {
            System.out.println("Unknown type of search object: " + searchObj.getClass().getName());
        }
        setupButtons();
    }

    public void initElt(UserLikesAlbum userLikesAlbum, CollectionType collectionType) {
        this.searchObj = mainLayoutController.getDbAccess().select_album_by_id(userLikesAlbum.getAlbumId());
        this.collectionType = CollectionType.ALBUM;
        User artist_user = mainLayoutController.getDbAccess().select_user_by_id(mainLayoutController.getDbAccess().select_artist_by_id(((Album) this.searchObj).getArtistId()).getUserId());
        // Initialize the collection element with album details

            collectionArtist.setText(artist_user.getUsername());
            collectionName.setText(((Album) this.searchObj).getTitle());
        Platform.runLater(() -> {
            String imagePath = mainLayoutController.getDbAccess().get_picture_by_name(((Album) searchObj).getCoverImage());
            if (imagePath != null) {
                collectionCover.setImage(new Image(new File(imagePath).toURI().toString()));
            }
        });
        setupButtons();
    }

    public void initElt(UserLikesPlaylist userLikesPlaylist, CollectionType collectionType) {
        this.searchObj = mainLayoutController.getDbAccess().select_playlist_by_id(userLikesPlaylist.getPlaylistId());
        this.collectionType = CollectionType.PLAYLIST;
        User user = mainLayoutController.getDbAccess().select_user_by_id(((Playlist) this.searchObj).getUserId());
        // Initialize the collection element with playlist details

            collectionArtist.setText(user.getUsername());
            collectionName.setText(((Playlist) this.searchObj).getTitle());
        Platform.runLater(() -> {
            String imagePath = mainLayoutController.getDbAccess().get_picture_by_name(((Playlist) searchObj).getCoverImage());
            if (imagePath != null) {
                collectionCover.setImage(new Image(new File(imagePath).toURI().toString()));
            }
        });
        setupButtons();
    }

    public void initElt(UserLikesArtist userLikesArtist, CollectionType collectionType) {
        this.searchObj = mainLayoutController.getDbAccess().select_artist_by_id(userLikesArtist.getArtistId());
        this.collectionType = CollectionType.ARTIST;
        User user = mainLayoutController.getDbAccess().select_user_by_id(((Artist) this.searchObj).getUserId());
        // Initialize the collection element with artist details

            collectionArtist.setText("");
            collectionName.setText(user.getUsername());
        Platform.runLater(() -> {
            String imagePath = mainLayoutController.getDbAccess().get_user_profile_picture(user.getEmail());
            if (imagePath != null) {
                collectionCover.setImage(new Image(new File(imagePath).toURI().toString()));
            }
        });
        collectionCover.setOnMouseClicked(mouseEvent -> {
            // Logic to play the song
            System.out.println("Playing artist: " + collectionName.getText());
            mainLayoutController.loadContent("artistView.fxml");
            ArtistController artistController = (ArtistController) mainLayoutController.controllerCache.get("artistView.fxml");
            artistController.initializeContentWithId(((Artist) this.searchObj).getId());
        });
    }

    public void setupButtons() {
        collectionCover.setOnMouseClicked(event -> {
            // Logic to play the song
            System.out.println("Playing collection: " + collectionName.getText());
            mainLayoutController.loadContent("collectionView.fxml");
            CollectionController collectionController = (CollectionController) mainLayoutController.controllerCache.get("collectionView.fxml");
            collectionController.initializeContentWithId(this.getCollectionId(), collectionType);
        });
    }

    public long getCollectionId() {
        if (searchObj instanceof Album) {
            return ((Album) searchObj).getId();
        } else if (searchObj instanceof Playlist) {
            return ((Playlist) searchObj).getId();
        }
        return -1;
    }

    @Override
    public void initializeContent(MainLayoutController mainLayoutController) {
        this.mainLayoutController = mainLayoutController;
    }
}
