package epita.projectse.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class SongSearch {
    @JsonProperty
    @Id
    private long song_id;
    @JsonProperty
    private String song_title;
    @JsonProperty
    private String song_cover;
    @JsonProperty
    private String artist_name;

    public SongSearch() {}
    public long getSong_id() {
        return song_id;
    }
    public void setSong_id(long song_id) {
        this.song_id = song_id;
    }
    public String getSong_title() {
        return song_title;
    }
    public void setSong_title(String song_title) {
        this.song_title = song_title;
    }
    public String getSong_cover() {
        return song_cover;
    }
    public void setSong_cover(String song_cover) {
        this.song_cover = song_cover;
    }
    public String getArtist_name() {
        return artist_name;
    }
    public void setArtist_name(String artist_name) {
        this.artist_name = artist_name;
    }
    public SongSearch(long song_id, String song_title, String song_cover, String artist_name) {
        this.song_id = song_id;
        this.song_title = song_title;
        this.song_cover = song_cover;
        this.artist_name = artist_name;
    }
}
