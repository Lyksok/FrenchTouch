package epita.projectse.utils;

import epita.projectse.application.MainLayoutController;
import epita.projectse.domain.*;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.File;


/*
Abstract helping implementation to display both songs and artists from a searchbar
*/
public class ASearchBarHelper {
    public String mainTitle;
    public String SubTitle;
    public String cover;
    public Type type;
    public MainLayoutController mainLayoutController;

    public enum Type{
        SONG,
        ARTIST,
        PLAYLIST,
        ALBUM,
    }

    public long id;

    public ASearchBarHelper(SongSearch song, MainLayoutController mainLayoutController) {
        this.mainTitle = song.getSong_title();
        this.SubTitle = song.getArtist_name();
        this.cover = song.getSong_cover();
        this.id = song.getSong_id();
        this.type = Type.SONG;
        this.mainLayoutController = mainLayoutController;
    }

    public ASearchBarHelper(ArtistSearch artist, MainLayoutController mainLayoutController) {
        this.mainTitle = artist.getArtist_name();
        this.cover = artist.getArtist_cover();
        this.id = artist.getArtist_id();
        this.type = Type.ARTIST;
        this.mainLayoutController = mainLayoutController;
    }

    public ASearchBarHelper(PlaylistSearch playlist, MainLayoutController mainLayoutController) {
        this.mainTitle = playlist.getPlaylist_name();
        this.SubTitle = playlist.getArtist_name();
        this.cover = playlist.getPlaylist_cover();
        this.id = playlist.getPlaylist_id();
        this.type = Type.PLAYLIST;
        this.mainLayoutController = mainLayoutController;
    }

    public ASearchBarHelper(AlbumSearch album, MainLayoutController mainLayoutController) {
        this.mainTitle = album.getAlbum_name();
        this.SubTitle = album.getArtist_name();
        this.cover = album.getAlbum_cover();
        this.id = album.getAlbum_id();
        this.type = Type.ALBUM;
        this.mainLayoutController = mainLayoutController;
    }

    public ASearchBarHelper() {}

    public long getId() {
        return id;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getCover() {
        return cover;
    }

    public void setMainLayoutController(MainLayoutController mainLayoutController) {
        this.mainLayoutController = mainLayoutController;
    }

    private void setImage(ImageView coverView) {
        mainLayoutController.executorService.submit(() -> {
            String URL = new File(mainLayoutController.getDbAccess().get_picture_by_name(cover)).toURI().toString();
            Image image = new Image(URL);
            Platform.runLater(() -> {
                coverView.setFitHeight(50);
                coverView.setFitWidth(50);
                coverView.setImage(image);
            });
        });
    }

    public HBox getCellFormat() {
        HBox cellFormat = new HBox();
        cellFormat.setStyle(
                "-fx-background-color: #101010;\n" +
                "-fx-background-radius: 5px;\n" +
                "-fx-pref-width: 90;\n" +
                        "-fx-pref-height: 40;\n" +
                        "-fx-padding: 10px;");
        ImageView coverView = new ImageView();

        VBox titles = new VBox();
        Text title = new Text(mainTitle);
        title.setStyle(
                "-fx-font-family: \"Arial Rounded MT Bold\";\n" +
                        "-fx-font-size: 13;\n" +
                        "-fx-fill: #FFFFFF;\n"
        );
        Text subTitle = new Text(SubTitle);
        subTitle.setStyle(
                "-fx-font-family: \"Arial Rounded MT Bold\";\n" +
                        "-fx-font-size: 10;\n" +
                        "-fx-fill: #FFFFFF;\n"
        );
        titles.setSpacing(10);
        titles.getChildren().addAll(title, subTitle);
        cellFormat.getChildren().addAll(coverView, titles);
        setImage(coverView);
        return cellFormat;
    }
}
