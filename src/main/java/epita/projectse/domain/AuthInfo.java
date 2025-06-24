package epita.projectse.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthInfo {
    @JsonProperty
    @Id
    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @JsonProperty
    private String password;

    public AuthInfo() {}

    public AuthInfo(User user, String password) {
        this.user = user;
        this.password = password;
    }
    public AuthInfo(AuthInfo authInfo) {
        this.user = authInfo.getUser();
        this.password = authInfo.getPassword();
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String toJson(){
        return "{\"user\":"+user.toJson()+",\"password\":\""+password+"\"}";
    }

}
