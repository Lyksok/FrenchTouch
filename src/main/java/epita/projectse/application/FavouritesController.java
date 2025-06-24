package epita.projectse.application;

import epita.projectse.domain.*;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.concurrent.ExecutorService;

public class FavouritesController implements IController {

    @FXML
    private HBox albumHbox, playlistHbox, artistHbox;
    @FXML
    private VBox songVbox;

    private User user;
    private MainLayoutController mainLayoutController;
    private ExecutorService executorService;

    @Override
    public void initializeContent(MainLayoutController mainLayoutController) {
        this.mainLayoutController = mainLayoutController;
        this.executorService = mainLayoutController.executorService;
    }

    public void initializeContent() {
        System.out.println("Initializing content...");
        this.user = mainLayoutController.getUser();
        loadRequestsInBackground(songVbox, CollectionType.SONG);
        loadRequestsInBackground(albumHbox, CollectionType.ALBUM);
        loadRequestsInBackground(playlistHbox, CollectionType.PLAYLIST);
        loadRequestsInBackground(artistHbox, CollectionType.ARTIST);
    }

    private void loadRequestsInBackground(Pane box, CollectionType type) {
        Task<List<?>> task = new Task<>() {
            @Override
            protected List<?> call() throws Exception {
                return switch (type) {
                    case SONG -> mainLayoutController.getDbAccess().select_user_likes_song_by_user_id(user.getId());
                    case ALBUM -> mainLayoutController.getDbAccess().select_user_likes_album_by_user_id(user.getId());
                    case PLAYLIST -> mainLayoutController.getDbAccess().select_user_likes_playlist_by_user_id(user.getId());
                    case ARTIST -> mainLayoutController.getDbAccess().select_user_likes_artist_by_user_id(user.getId());
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
                        if (request instanceof UserLikesAlbum) {
                            addCollectionToBox((HBox) box, request, type);
                        }
                        break;
                    case PLAYLIST:
                        if (request instanceof UserLikesPlaylist) {
                            addCollectionToBox((HBox) box, request, type);
                        }
                        break;
                    case SONG:
                        if (request instanceof UserLikesSong) {
                            addSongToBox((VBox) box, (UserLikesSong) request);
                        }
                        break;
                    case ARTIST:
                        if (request instanceof UserLikesArtist) {
                            addCollectionToBox((HBox) box, request, type);
                        }
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown request type");
                }
            }
        }
    }

    private void addCollectionToBox(HBox hBox, Object searchObj, CollectionType type) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("collectionElement.fxml"));
        try {
            Pane viewElt = loader.load();
            CollectionElementController controller = loader.getController();
            controller.initializeContent(mainLayoutController);
            if(searchObj instanceof UserLikesPlaylist) {
                controller.initElt(((UserLikesPlaylist)searchObj), type);
            } else if (searchObj instanceof UserLikesAlbum) {
                controller.initElt(((UserLikesAlbum)searchObj), type);
            } else if (searchObj instanceof UserLikesArtist) {
                controller.initElt(((UserLikesArtist)searchObj), type);
            } else {
                System.out.println("Unknown type of search object: "+ searchObj.getClass().getName());
            }
            hBox.getChildren().add(viewElt);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addSongToBox(VBox vBox, UserLikesSong userLikesSong) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("songElement.fxml"));
        try {
            Pane viewElt = loader.load();
            SongElementController controller = loader.getController();
            controller.initializeContent(mainLayoutController);
            controller.initElt(userLikesSong);
            vBox.getChildren().add(viewElt);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refreshVBox(CollectionType type) {
        switch (type) {
            case SONG:
                loadRequestsInBackground(songVbox, CollectionType.SONG);
                break;
            case ALBUM:
                loadRequestsInBackground(albumHbox, CollectionType.ALBUM);
                break;
            case PLAYLIST:
                loadRequestsInBackground(playlistHbox, CollectionType.PLAYLIST);
                break;
            case ARTIST:
                loadRequestsInBackground(artistHbox, CollectionType.ARTIST);
                break;
        }
    }

}
