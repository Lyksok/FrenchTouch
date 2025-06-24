package epita.projectse.domain;

public class SongPlaylist {
    private long song_id;
    private long playlist_id;

    public SongPlaylist(long song_id, long playlist_id) {
        this.song_id = song_id;
        this.playlist_id = playlist_id;
    }

    public SongPlaylist() {}

    public long getPlaylistId() {
        return playlist_id;
    }

    public void setPlaylistId(long playlist_id) {
        this.playlist_id = playlist_id;
    }
    public long getSongId() {
        return song_id;
    }
    public void setSongId(long song_id) {
        this.song_id = song_id;
    }
    public String toJson() {
        return "{" +
                "\"song_id\": " + song_id +
                ",\"playlist_id\": " + playlist_id +
                "}";
    }
}
