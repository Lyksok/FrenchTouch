package epita.projectse.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlaylistSearch {
    @Id
    @JsonProperty
    private long playlist_id;
    @JsonProperty
    private String playlist_name;
    @JsonProperty
    private String playlist_cover;
    @JsonProperty
    private String artist_name;

    public PlaylistSearch() {}
    public long getPlaylist_id() {
        return playlist_id;
    }
    public void setPlaylist_id(long playlist_id) { this.playlist_id = playlist_id; }
    public String getArtist_name() {
        return artist_name;
    }
    public void setArtist_name(String artist_name) {
        this.artist_name = artist_name;
    }
    public String getPlaylist_name() { return playlist_name; }
    public void setPlaylist_name(String playlist_name) { this.playlist_name = playlist_name; }
    public String getPlaylist_cover() {
        return playlist_cover;
    }
    public void setPlaylist_cover(String playlist_cover) {
        this.playlist_cover = playlist_cover;
    }
    public PlaylistSearch(long album_id, String playlist_name,String artist_name,  String playlist_cover) {
        this.playlist_id = album_id;
        this.playlist_name = playlist_name;
        this.artist_name = artist_name;
        this.playlist_cover = playlist_cover;
    }
}
