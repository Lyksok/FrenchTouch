package epita.projectse.domain;

public class SongAlbum {
    private long song_id;
    private long album_id;

    public SongAlbum(long song_id, long album_id) {
        this.song_id = song_id;
        this.album_id = album_id;
    }

    public SongAlbum() {}

    public long getAlbumId() {
        return album_id;
    }

    public void setAlbumId(long album_id) {
        this.album_id = album_id;
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
                ",\"album_id\": " + album_id +
                "}";
    }
}