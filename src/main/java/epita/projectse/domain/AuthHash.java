package epita.projectse.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthHash {
    @JsonProperty
    private String auth_hash;

    public AuthHash() {}

    public AuthHash(String hash) {
        this.auth_hash = hash;
    }
    public String getHash() {
        return auth_hash;
    }
    public void setHash(String auth_hash) {
        this.auth_hash = auth_hash;
    }
    @JsonIgnore
    public boolean isValid() {
        return auth_hash != null && !auth_hash.isEmpty();
    }

    public String toJson() {
        return "{\"auth_hash\": \"" + auth_hash + "\"}";
    }
}
