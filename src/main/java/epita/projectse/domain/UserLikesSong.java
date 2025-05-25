package epita.projectse.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import javafx.fxml.FXML;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserLikesSong {
    @JsonProperty
    @Id
    private long user_id;
    @JsonProperty
    private long song_id;

    public UserLikesSong() {    }
    public UserLikesSong(long user_id, long song_id)
    {
        this.user_id = user_id;
        this.song_id = song_id;
    }

    public void setUserId(long user_id) {
        this.user_id = user_id;
    }
    public long getUserId() {
        return user_id;
    }
    public void setSongId(long song_id) {
        this.song_id = song_id;
    }
    public long getSongId() {
        return song_id;
    }
    public String toJson() {
        return "{" +
                "\"user_id\": " + user_id +
                ",\"song_id\": " + song_id +
                "}";
    }
}
