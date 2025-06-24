package epita.projectse.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true) // Ignore unknown properties
public class CollaboratorRequest {
    @JsonProperty
    @Id
    private long id;
    @JsonProperty
    private long collaborator_id;
    @JsonProperty
    private String song_title;
    @JsonProperty
    private int song_creation_date;
    @JsonProperty
    private String song_file;
    @JsonProperty
    private String song_cover;
    @JsonProperty
    private long artist_id;
    @JsonProperty
    private String artist_name;
    @JsonProperty
    private String artist_biography;
    @JsonProperty
    private String artist_profile_picture;
    @JsonProperty
    private long album_id;
    @JsonProperty
    private String album_name;
    @JsonProperty
    private int album_creation_date;
    @JsonProperty
    private String album_cover;

    public CollaboratorRequest(long collaborator_id, String song_title, int song_creation_date, String song_file, String song_cover,long artist_id, String artist_name, String artist_biography, String artist_profile_picture, long album_id, String album_name, int album_creation_date, String album_cover) {
        this.id = -1;
        this.collaborator_id = collaborator_id;
        this.song_title = song_title;
        this.song_creation_date = song_creation_date;
        this.song_file = song_file;
        this.song_cover = song_cover;
        this.artist_id = artist_id;
        this.artist_name = artist_name;
        this.artist_biography = artist_biography;
        this.artist_profile_picture = artist_profile_picture;
        this.album_id = album_id;
        this.album_name = album_name;
        this.album_creation_date = album_creation_date;
        this.album_cover = album_cover;
    }

    public CollaboratorRequest(){}
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public long getCollaboratorId() {
        return collaborator_id;
    }
    public void setCollaboratorId(long collaborator_id) {
        this.collaborator_id = collaborator_id;
    }
    public String getSong_title() {
        return song_title;
    }
    public void setSong_title(String song_title) {
        this.song_title = song_title;
    }
    public int getSong_creation_date() {
        return song_creation_date;
    }
    public void setSong_creation_date(int song_creation_date) {
        this.song_creation_date = song_creation_date;
    }
    public String getSong_file() {
        return song_file;
    }
    public void setSong_file(String song_file) {
        this.song_file = song_file;
    }
    public String getSong_cover() {
        return song_cover;
    }
    public void setSong_cover(String song_cover) {
        this.song_cover = song_cover;
    }
    public long getArtist_id() {
        return artist_id;
    }
    public void setArtist_id(long artist_id) {
        this.artist_id = artist_id;
    }
    public String getArtist_name() {
        return artist_name;
    }
    public void setArtist_name(String artist_name) {
        this.artist_name = artist_name;
    }
    public String getArtist_biography() {
        return artist_biography;
    }
    public void setArtist_biography(String artist_biography) {
        this.artist_biography = artist_biography;
    }
    public String getArtist_profile_picture() {
        return artist_profile_picture;
    }
    public void setArtist_profile_picture(String artist_profile_picture) {
        this.artist_profile_picture = artist_profile_picture;
    }
    public long getAlbum_id() {
        return album_id;
    }
    public void setAlbum_id(long album_id) {
        this.album_id = album_id;
    }
    public String getAlbum_name() {
        return album_name;
    }
    public void setAlbum_name(String album_name) {
        this.album_name = album_name;
    }
    public int getAlbum_creation_date() {
        return album_creation_date;
    }
    public void setAlbum_creation_date(int album_creation_date) {
        this.album_creation_date = album_creation_date;
    }
    public String getAlbum_cover() {
        return album_cover;
    }
    public void setAlbum_cover(String album_cover) {
        this.album_cover = album_cover;
    }

    @JsonIgnore
    private String getProfilePictureJson(){
        if (artist_profile_picture != null && !artist_profile_picture.isEmpty()) return "\"" + artist_profile_picture + "\"";
        return null;
    }
    @JsonIgnore
    private String getSongCoverJson(){
        if (song_cover != null && !song_cover.isEmpty()) return "\"" + song_cover + "\"";
        return null;
    }
    @JsonIgnore
    private String getAlbumCoverJson(){
        if (album_cover != null && !album_cover.isEmpty()) return "\"" + album_cover + "\"";
        return null;
    }

    public String toJson(){
        return "{\"id\":" + id +
                ",\"collaborator_id\":" + collaborator_id +
                ",\"song_title\":\"" + song_title + "\"" +
                ",\"song_creation_date\":" + song_creation_date +
                ",\"song_file\":\"" + song_file + "\"" +
                ",\"song_cover\":" + getSongCoverJson() +
                ",\"artist_id\":" + artist_id +
                ",\"artist_name\":\"" + artist_name + "\"" +
                ",\"artist_biography\":\"" + artist_biography + "\"" +
                ",\"artist_profile_picture\":" + getProfilePictureJson() +
                ",\"album_id\":" + album_id +
                ",\"album_name\":\"" + album_name + "\"" +
                ",\"album_creation_date\":" + album_creation_date +
                ",\"album_cover\":" + getAlbumCoverJson() +
                "}";
    }
}
