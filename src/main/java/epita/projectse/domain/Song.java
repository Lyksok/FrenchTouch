package epita.projectse.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import epita.projectse.data_access.DbAccess;
import jakarta.persistence.*;

import java.io.File;

import javafx.scene.image.Image;
import javafx.scene.media.Media;

@Entity
@Table(name = "Song")
@JsonIgnoreProperties(ignoreUnknown = true) // Ignore unknown properties
public class Song {
    @JsonProperty
    @Id
    private long id;
    @JsonProperty
    private String title;
    @JsonProperty
    private String song_file;
    @JsonProperty
    private String cover_image;
    @JsonProperty
    private int nb_of_streams;
    @JsonProperty
    private int duration;
    @JsonProperty
    private int creation_date;
    @JsonProperty
    private long artist_id;

    public Song() {}

    public Song(String title, String song_file, String cover_image, int nb_of_streams, int duration, int creation_date, long artist_id) {
        this.id = -1;
        this.title = title;
        this.song_file = song_file;
        this.cover_image = cover_image;
        this.nb_of_streams = nb_of_streams;
        this.duration = duration;
        this.creation_date = creation_date;
        this.artist_id = artist_id;
    }

    public Song(Song song) {
        this.id = song.id;
        this.title = song.title;
        this.song_file = song.song_file;
        this.cover_image = song.cover_image;
        this.nb_of_streams = song.nb_of_streams;
        this.duration = song.duration;
        this.creation_date = song.creation_date;
        this.artist_id = song.artist_id;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getSongFile() {
        return song_file;
    }
    public void setSongFile(String song_file) {
        this.song_file = song_file;
    }
    public int getNbOfStreams() {
        return nb_of_streams;
    }
    public void setNbOfStreams(int nb_of_streams) {
        this.nb_of_streams = nb_of_streams;
    }
    public String getCoverImage() {
        return "./.cache/" + cover_image;
    }
    public void setCoverImage(String cover_image) {
        this.cover_image = cover_image;
    }
    public int getDuration() {
        return duration;
    }
    public void setDuration(int duration) {
        this.duration = duration;
    }
    public int getCreationDate() {
        return creation_date;
    }
    public void setCreationDate(int creation_date) {
        this.creation_date = creation_date;
    }
    public long getArtistId() {
        return artist_id;
    }
    public void setArtistId(long artist_id) {
        this.artist_id = artist_id;
    }

    public Media getMedia(DbAccess dbAccess){
        String path = dbAccess.get_song_track(song_file);
        if(path == null){
            System.out.println("Could not find song file");
            return null;
        }
        return new Media(new File(path).toURI().toString());
    }

    public Media getMedia(){
        return new Media(new File(song_file).toURI().toString());
    }

    public Image getCoverView(){
        try {
            return new Image(new File(getCoverImage()).toURI().toString());
        }
        catch (Exception e) {
            System.out.println("Could not load cover image");
            return null;

        }
    }

    @JsonIgnore
    public String getCover_image_json(){
        if(cover_image == null){
            return null;
        } else {
            return "\"" + cover_image + "\"";
        }
    }

    public void increaseStream(){
        this.nb_of_streams++;
    }

    @Override
    public String toString() {
        return "Song{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", song_file='" + song_file + '\'' +
                ", cover_image='" + cover_image + '\'' +
                ", nb_of_streams=" + nb_of_streams +
                ", duration=" + duration +
                ", creation_date=" + creation_date +
                ", artist_id=" + artist_id +
                '}';
    }
    public String toJson() {
        return "{" +
                "\"id\":" + id +
                ",\"title\":\"" + title + "\"" +
                ",\"song_file\":\"" + song_file + "\"" +
                ",\"cover_image\":" + getCover_image_json() +
                ",\"nb_of_streams\":" + nb_of_streams +
                ",\"duration\":" + duration +
                ",\"creation_date\":" + creation_date +
                ",\"artist_id\":" + artist_id +
                '}';
    }
}
