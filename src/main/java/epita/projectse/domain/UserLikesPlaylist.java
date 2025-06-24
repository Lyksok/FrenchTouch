package epita.projectse.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserLikesPlaylist {
    @JsonProperty
    @Id
    private long user_id;
    @JsonProperty
    private long playlist_id;

    public UserLikesPlaylist() {    }
    public UserLikesPlaylist(long user_id, long playlist_id)
    {
        this.user_id = user_id;
        this.playlist_id = playlist_id;
    }
    public void setUserId(long user_id) {
        this.user_id = user_id;
    }
    public long getUserId() {
        return user_id;
    }
    public void setPlaylistId(long playlist_id) {
        this.playlist_id = playlist_id;
    }
    public long getPlaylistId() {
        return playlist_id;
    }

    @JsonIgnoreProperties
    public String toJson() {
        return "{" +
                "\"user_id\": " + user_id +
                ",\"playlist_id\": " + playlist_id +
                "}";
    }
}
