package epita.projectse.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserLikesAlbum {
    @JsonProperty
    @Id
    private long user_id;
    @JsonProperty
    private long album_id;
    public UserLikesAlbum() {    }
    public UserLikesAlbum(long user_id, long album_id)
    {
        this.user_id = user_id;
        this.album_id = album_id;
    }
    public void setUserId(long user_id) {
        this.user_id = user_id;
    }
    public long getUserId() {
        return user_id;
    }
    public void setAlbumId(long album_id) {
        this.album_id = album_id;
    }
    public long getAlbumId() {
        return album_id;
    }
    public String toJson() {
        return "{" +
                "\"user_id\": " + user_id +
                ",\"album_id\": " + album_id +
                "}";
    }
}
