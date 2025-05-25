package epita.projectse.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserLikesArtist {
    @JsonProperty
    @Id
    private long user_id;
    @JsonProperty
    private long artist_id;
    public UserLikesArtist() {    }
    public UserLikesArtist(long user_id, long artist_id)
    {
        this.user_id = user_id;
        this.artist_id = artist_id;
    }
    public void setUserId(long user_id) {
        this.user_id = user_id;
    }
    public long getUserId() {
        return user_id;
    }
    public void setArtistId(long artist_id) {
        this.artist_id = artist_id;
    }
    public long getArtistId() {
        return artist_id;
    }
    public String toJson() {
        return "{" +
                "\"user_id\": " + user_id +
                ",\"artist_id\": " + artist_id +
                "}";
    }
}
