package epita.projectse.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestToArtist {
    @JsonProperty
    @Id
    private long user_id;

    public RequestToArtist(long user_id) {
        this.user_id = user_id;
    }

    public RequestToArtist() {}

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }
    
    public String toJson() {
        return "{\"user_id\":" + user_id + "}";
    }
}
