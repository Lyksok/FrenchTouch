package epita.projectse.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Album")
@JsonIgnoreProperties(ignoreUnknown = true) // Ignore unknown properties
public class Album {
    @JsonProperty
    @Id
    private long id;
    @JsonProperty
    private String title;
    @JsonProperty
    private String cover_image;
    @JsonProperty
    private int nb_of_streams;
    @JsonProperty
    private int creation_date;
    @JsonProperty
    private long artist_id;

    public Album() {}
    public Album(String title, String cover_image, int nb_of_streams, int creation_date, long artist_id) {
        this.id = -1;
        this.title = title;
        this.cover_image = cover_image;
        this.nb_of_streams = nb_of_streams;
        this.creation_date = creation_date;
        this.artist_id = artist_id;
    }
    public Album(Album album) {
        this.id = album.id;
        this.title = album.title;
        this.cover_image = album.cover_image;
        this.nb_of_streams = album.nb_of_streams;
        this.creation_date = album.creation_date;
        this.artist_id = album.artist_id;
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
    public String getCoverImage() {
        return cover_image;
    }
    public void setCoverImage(String cover_image) {
        this.cover_image = cover_image;
    }
    public int getNbOfStreams() {
        return nb_of_streams;
    }
    public void setNbOfStreams(int nb_of_streams) {
        this.nb_of_streams = nb_of_streams;
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
    @Override
    public String toString() {
        return "Album{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", cover_image='" + cover_image + '\'' +
                ", nb_of_streams=" + nb_of_streams +
                ", creation_date=" + creation_date +
                ", artist_id=" + artist_id +
                '}';
    }
    public String toJson() {
        return "{" +
                "\"id\":" + id +
                ",\"title\":\"" + title + "\"" +
                ",\"cover_image\":\"" + cover_image + "\"" +
                ",\"nb_of_streams\":" + nb_of_streams +
                ",\"creation_date\":" + creation_date +
                ",\"artist_id\":" + artist_id +
                '}';
    }
}
