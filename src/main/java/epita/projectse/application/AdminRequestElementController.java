package epita.projectse.application;

import epita.projectse.domain.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.util.Locale;
import java.util.Random;

public class AdminRequestElementController implements IController {
    @FXML
    private Label artistNameLabel, songTitleLabel;
    @FXML
    private Button acceptButton, denyButton;

    private long requestId;
    private RequestReq requestType;

    private MainLayoutController mainLayoutController;

    public void initElt(long requestId, RequestReq requestType) {
        this.requestId = requestId;
        this.requestType = requestType;
        switch (requestType) {
            case COLLABORATOR_REQUEST:
                CollaboratorRequest collaboratorRequest = mainLayoutController.getDbAccess().select_collaborator_request_by_id(requestId);
                artistNameLabel.setText(collaboratorRequest.getArtist_name());
                songTitleLabel.setText(collaboratorRequest.getSong_title());
                break;
            case ARTIST_REQUEST:
                ArtistRequest artistRequest = mainLayoutController.getDbAccess().select_artist_request_by_id(requestId);
                Artist artist = mainLayoutController.getDbAccess().select_artist_by_id(artistRequest.getArtist_id());
                User user = mainLayoutController.getDbAccess().select_user_by_id(artist.getUserId());
                artistNameLabel.setText(user.getUsername());
                songTitleLabel.setText(artistRequest.getSong_title());
                break;
        }
        setupButtons();
    }

    private void setupButtons() {
        acceptButton.setOnAction(event -> {
            handleAccept();
            ((AdminPanelController)mainLayoutController.controllerCache.get("adminPanelView.fxml")).refreshVBox(requestType);
        });
        denyButton.setOnAction(event -> {
            handleDeny();
            ((AdminPanelController)mainLayoutController.controllerCache.get("adminPanelView.fxml")).refreshVBox(requestType);
        });
    }

    private void handleAccept() {
        switch (requestType) {
            case COLLABORATOR_REQUEST:
                CollaboratorRequest collaboratorRequest = mainLayoutController.getDbAccess().select_collaborator_request_by_id(requestId);
                if (collaboratorRequest != null) {
                    // Find email that does not exist in the database
                    long artist_id = collaboratorRequest.getArtist_id();
                    long album_id = collaboratorRequest.getAlbum_id();
                    if(collaboratorRequest.getArtist_id()==-1) {
                        String newEmail = collaboratorRequest.getArtist_name().toLowerCase(Locale.ROOT) + "_" + new Random().nextInt(1000000);
                        while (mainLayoutController.getDbAccess().user_exists(newEmail)) {
                            newEmail = collaboratorRequest.getArtist_name().toLowerCase(Locale.ROOT) + "_" + new Random().nextInt(1000000);
                        }
                        int currentTime = (int) (System.currentTimeMillis() / 1000L);
                        User user = new User(collaboratorRequest.getArtist_name(), newEmail, currentTime, currentTime, collaboratorRequest.getArtist_profile_picture());
                        user = mainLayoutController.getDbAccess().insert_user(user);
                        Artist artist = new Artist(user.getId(), 0, collaboratorRequest.getArtist_biography(), null, false);
                        artist_id = mainLayoutController.getDbAccess().insert_artist(artist).getId();
                    }
                    if(collaboratorRequest.getAlbum_id()==-1) {
                        Album album = new Album(collaboratorRequest.getAlbum_name(), collaboratorRequest.getAlbum_cover(), 0, collaboratorRequest.getAlbum_creation_date(), artist_id);
                        album_id = mainLayoutController.getDbAccess().insert_album(album).getId();
                    }
                    Song song = new Song(collaboratorRequest.getSong_title(),
                                collaboratorRequest.getSong_file(),
                                collaboratorRequest.getSong_cover(),
                                0,
                                0,
                                collaboratorRequest.getSong_creation_date(),
                                artist_id
                    );
                    song = mainLayoutController.getDbAccess().insert_song(song);
                    SongAlbum songAlbum = new SongAlbum(song.getId(), album_id);
                    songAlbum = mainLayoutController.getDbAccess().insert_song_album(songAlbum);
                    mainLayoutController.getDbAccess().delete_collaborator_request_by_id(requestId);
                }
                break;
            case ARTIST_REQUEST:
                ArtistRequest artistRequest = mainLayoutController.getDbAccess().select_artist_request_by_id(requestId);
                if (artistRequest != null) {
                    long album_id = artistRequest.getAlbum_id();
                    if(artistRequest.getAlbum_id()==-1) {
                        Album album = new Album(artistRequest.getAlbum_name(), artistRequest.getAlbum_cover(), 0, artistRequest.getAlbum_creation_date(), artistRequest.getArtist_id());
                        album_id = mainLayoutController.getDbAccess().insert_album(album).getId();
                    }
                    //String trackPath = instanceInfo.getDbAccess().get_song_track(artistRequest.getSong_file());8
                    Song song = new Song(artistRequest.getSong_title(),
                            artistRequest.getSong_file(),
                            artistRequest.getSong_cover(),
                            0,
                            0,
                            artistRequest.getSong_creation_date(),
                            artistRequest.getArtist_id());
                    song = mainLayoutController.getDbAccess().insert_song(song);
                    SongAlbum songAlbum = new SongAlbum(song.getId(), album_id);
                    songAlbum = mainLayoutController.getDbAccess().insert_song_album(songAlbum);
                    mainLayoutController.getDbAccess().delete_artist_request_by_id(requestId);
                }
                break;
        }
    }

    private void handleDeny() {
        switch (requestType) {
            case COLLABORATOR_REQUEST:
                mainLayoutController.getDbAccess().delete_collaborator_request_by_id(requestId);
                break;
            case ARTIST_REQUEST:
                mainLayoutController.getDbAccess().delete_artist_request_by_id(requestId);
                break;
        }
    }

    @Override
    public void initializeContent(MainLayoutController mainLayoutController) {
        this.mainLayoutController = mainLayoutController;
    }
}
