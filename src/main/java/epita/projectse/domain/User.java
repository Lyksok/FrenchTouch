package epita.projectse.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

@Entity
@Table(name = "Users")
@JsonIgnoreProperties(ignoreUnknown = true) // Ignore unknown properties
public class User {
    @JsonProperty
    @Id
    private long id;
    @JsonProperty
    private String username;
    @JsonProperty
    @Column(unique = true, nullable = false)
    private String email;
    @JsonProperty
    private int last_connection;
    @JsonProperty
    private int creation_date;
    @JsonProperty
    private String profile_picture;

    // Constructors, getters, and setters remain the same
    public User() {
    }

    public User(String username, String email, int last_connection, int creation_date, String profile_picture) {
        this.id = -1;
        this.username = username;
        this.email = email;
        this.last_connection = last_connection;
        this.creation_date = creation_date;
        this.profile_picture = profile_picture;
    }

    public User(User user) {
        this.id = user.id;
        this.username = user.username;
        this.email = user.email;
        this.last_connection = user.last_connection;
        this.creation_date = user.creation_date;
        this.profile_picture = user.profile_picture;
    }

    @JsonIgnore
    public long getId() {
        return id;
    }

    @JsonIgnore
    public void setId(long id) {
        this.id = id;
    }

    @JsonIgnore
    public void setUsername(String username) {
        this.username = username;
    }

    @JsonIgnore
    public void setEmail(String email) {
        this.email = email;
    }

    @JsonIgnore
    public void setLastConnection(int last_connection) {
        this.last_connection = last_connection;
    }

    @JsonIgnore
    public void setCreationDate(int creation_date) {
        this.creation_date = creation_date;
    }

    @JsonIgnore
    public void setProfilePicture(String profile_picture) {
        this.profile_picture = profile_picture;
    }

    @JsonIgnore
    public String getUsername() {
        return username;
    }

    @JsonIgnore
    public String getEmail() {
        return email;
    }

    @JsonIgnore
    public int getLastConnection() {
        return last_connection;
    }

    @JsonIgnore
    public int getCreationDate() {
        return creation_date;
    }

    @JsonIgnore
    public String getProfilePicture() {
        if(profile_picture==null){
            return null;
        } else {
            return "\""+ profile_picture + "\"";
        }
    }

    @JsonIgnore
    public String getProfilePictureWithoutQuotes() {
        if(profile_picture==null){
            return null;
        } else {
            return profile_picture;
        }
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", last_connection='" + last_connection + '\'' +
                ", creation_date='" + creation_date + '\'' +
                ", profile_picture='" + profile_picture + '\'' +
                '}';
    }

    public String toJson() {
        return "{" +
                "\"id\":" + id +
                ",\"username\":\"" + username + "\"" +
                ",\"email\":\"" + email + "\"" +
                ",\"last_connection\":" + last_connection +
                ",\"creation_date\":" + creation_date +
                ",\"profile_picture\":" + getProfilePicture() +
                '}';
    }
}
