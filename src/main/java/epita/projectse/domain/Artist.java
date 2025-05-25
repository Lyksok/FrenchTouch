package epita.projectse.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Artist")
@JsonIgnoreProperties(ignoreUnknown = true) // Ignore unknown properties
public class Artist {
    @JsonProperty
    @Id
    private long id;
    @JsonProperty
    @Column(unique = true, nullable = false)
    private long user_id;
    @JsonProperty
    private int nb_of_streams;
    @JsonProperty
    private String biography;
    @JsonProperty
    private String url;
    @JsonProperty
    private boolean verified;

    public Artist() {}

    public Artist(long user_id, int nb_of_streams, String biography, String url, boolean verified) {
        this.id = -1;
        this.user_id = user_id;
        this.nb_of_streams = nb_of_streams;
        this.biography = biography;
        this.url = url;
        this.verified = verified;
    }

    public Artist(Artist artist) {
        this.id = artist.id;
        this.user_id = artist.user_id;
        this.nb_of_streams = artist.nb_of_streams;
        this.biography = artist.biography;
        this.url = artist.url;
        this.verified = artist.verified;
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
    public int getNbOfStreams() {
        return nb_of_streams;
    }
    public void setNbOfStreams(int nb_of_streams) {
        this.nb_of_streams = nb_of_streams;
    }
    public String getBiography() {
        return biography;
    }
    public void setBiography(String biography) {
        this.biography = biography;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public boolean isVerified() {
        return verified;
    }
    public void setVerified(boolean verified) {
        this.verified = verified;
    }
    @Override
    public String toString() {
        return "Artist{" +
                "id=" + id +
                ", user_id=" + user_id +
                ", nb_of_streams=" + nb_of_streams +
                ", biography='" + biography + '\'' +
                ", url='" + url + '\'' +
                ", verified=" + verified +
                '}';
    }

    public String toJson() {
        return "{" +
                "\"id\":" + id +
                ",\"user_id\":" + user_id +
                ",\"nb_of_streams\":" + nb_of_streams +
                ",\"biography\":\"" + biography + "\"" +
                ",\"url\":\"" + url + "\"" +
                ",\"verified\":" + verified +
                '}';
    }
}
