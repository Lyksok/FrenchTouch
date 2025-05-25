package epita.projectse.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArtistSearch {
    @JsonProperty
    @Id
    private long artist_id;
    @JsonProperty
    private String artist_name;
    @JsonProperty
    private String artist_cover;

    public ArtistSearch() {}
    public long getArtist_id() {
        return artist_id;
    }
    public void setArtist_id(long artist_id) {
        this.artist_id = artist_id;
    }
    public String getArtist_name() {
        return artist_name;
    }
    public void setArtist_name(String artist_name) {
        this.artist_name = artist_name;
    }
    public String getArtist_cover() {
        return artist_cover;
    }
    public void setArtist_cover(String artist_cover) {
        this.artist_cover = artist_cover;
    }
    public ArtistSearch(long artist_id, String artist_name, String artist_cover) {
        this.artist_id = artist_id;
        this.artist_name = artist_name;
        this.artist_cover = artist_cover;
    }
}
