package epita.projectse.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Collaborator")
@JsonIgnoreProperties(ignoreUnknown = true) // Ignore unknown properties
public class Collaborator {
    @JsonProperty
    @Id
    private long id;
    @JsonProperty
    private long user_id;
    @JsonProperty
    private boolean verified;

    public Collaborator() {}

    public Collaborator(long user_id, boolean verified) {
        this.id = -1;
        this.user_id = user_id;
        this.verified = verified;
    }

    public Collaborator(Collaborator collaborator) {
        this.id = collaborator.id;
        this.user_id = collaborator.user_id;
        this.verified = collaborator.verified;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public long getUserId() {
        return user_id;
    }
    public void setUserId(long user_id) {
        this.user_id = user_id;
    }
    public boolean isVerified() {
        return verified;
    }
    public void setVerified(boolean verified) {
        this.verified = verified;
    }
    @Override
    public String toString() {
        return "Collaborator{" +
                "id=" + id +
                ", user_id=" + user_id +
                ", verified=" + verified +
                '}';
    }

    public String toJson() {
        return "{" +
                "\"id\":" + id +
                ",\"user_id\":" + user_id +
                ",\"verified\":" + verified +
                '}';
    }
}
