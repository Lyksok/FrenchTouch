package epita.projectse.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class AlbumSearch {
    @JsonProperty
    @Id
    private long album_id;
    @JsonProperty
    private String album_name;
    @JsonProperty
    private String album_cover;
    @JsonProperty
    private String artist_name;

    public AlbumSearch(long album_id, String album_name, String album_cover, String artist_name) {
        this.album_id = album_id;
        this.album_name = album_name;
        this.album_cover = album_cover;
        this.artist_name = artist_name;
    }

    public AlbumSearch() {}

    public long getAlbum_id() {
        return album_id;
    }
    public void setAlbum_id(long album_id) {
        this.album_id = album_id;
    }
    public String getArtist_name() {
        return artist_name;
    }
    public void setArtist_name(String artist_name) {
        this.artist_name = artist_name;
    }
    public String getAlbum_cover() {
        return album_cover;
    }
    public void setAlbum_cover(String album_cover) {
        this.album_cover = album_cover;
    }
    public String getAlbum_name() { return album_name; }
    public void setAlbum_name(String album_name) { this.album_name = album_name; }

}
