package epita.projectse.data_access;

import epita.projectse.domain.*;

import java.util.List;

public interface DbAccess {
    AuthHash authenticate(AuthInfo authInfo);
    void setAuthHash(AuthHash authHash);
    boolean register(AuthInfo authInfo);

    // INSERT
    Admin insert_admin(Admin admin);
    User insert_user(User user);
    Artist insert_artist(Artist artist);
    Song insert_song(Song song);
    Album insert_album(Album album);
    Playlist insert_playlist(Playlist playlist);
    SongAlbum insert_song_album(SongAlbum songAlbum);
    SongPlaylist insert_song_playlist(SongPlaylist songPlaylist);
    Collaborator insert_collaborator(Collaborator collaborator);
    ArtistRequest insert_artist_request(ArtistRequest artistRequest);
    UserLikesSong insert_user_likes_song(UserLikesSong userLikesSong);
    UserLikesPlaylist insert_user_likes_playlist(UserLikesPlaylist userLikesPlaylist);
    UserLikesAlbum insert_user_likes_album(UserLikesAlbum userLikesAlbum);
    UserLikesArtist insert_user_likes_artist(UserLikesArtist userLikesArtist);
    CollaboratorRequest insert_collaborator_request(CollaboratorRequest collaboratorRequest);
    RequestToArtist insert_request_to_artist(RequestToArtist requestToArtist);
    RequestToCollaborator insert_request_to_collaborator(RequestToCollaborator requestToCollaborator);
    RequestToAdmin insert_request_to_admin(RequestToAdmin requestToAdmin);

    // SELECT
    Admin select_admin_by_user_id(long user_id);
    User select_user_by_email(String email);
    User select_user_by_id(long id);
    Song select_song_by_id(long id);
    Artist select_artist_by_id(long id);
    Artist select_artist_by_user_id(long user_id);
    Collaborator select_collaborator_by_id(long id);
    Collaborator select_collaborator_by_user_id(long user_id);
    AlbumSearch select_search_album_by_album_id(long album_id);
    Album select_album_by_id(long id);
    Playlist select_playlist_by_id(long id);
    PlaylistSearch select_search_playlist_by_playlist_id(long playlist_id);
    List<SongSearch> select_search_songs(String title);
    List<SongSearch> select_search_songs_by_artist_id(long artist_id);
    List<SongSearch> select_search_songs_by_album_id(long album_id);
    List<SongSearch> select_search_songs_by_playlist_id(long playlist_id);
    List<ArtistSearch> select_search_artists(String name);
    List<AlbumSearch> select_search_albums(String name);
    List<PlaylistSearch> select_search_playlists(String name);
    List<Playlist> select_search_playlists_by_artist_id(long artist_id);
    RequestToArtist select_request_to_artist_by_user_id(long user_id);
    RequestToAdmin select_request_to_admin_by_user_id(long user_id);
    RequestToCollaborator select_request_to_collaborator_by_user_id(long user_id);
    List<RequestToArtist> select_request_to_artist_all();
    List<RequestToAdmin> select_request_to_admin_all();
    List<RequestToCollaborator> select_request_to_collaborator_all();
    ArtistRequest select_artist_request_by_id(long id);
    List<ArtistRequest> select_artist_request_all();
    CollaboratorRequest select_collaborator_request_by_id(long id);
    List<CollaboratorRequest> select_collaborator_request_all();
    List<PlaylistSearch> select_search_playlist_by_user_id(long user_id);
    List<SongSearch> select_search_song_by_artist_id(long artist_id);
    List<AlbumSearch> select_search_album_by_artist_id(long artist_id);
    List<UserLikesSong> select_user_likes_song_by_user_id(long user_id);
    List<UserLikesPlaylist> select_user_likes_playlist_by_user_id(long user_id);
    List<UserLikesAlbum> select_user_likes_album_by_user_id(long user_id);
    List<UserLikesArtist> select_user_likes_artist_by_user_id(long user_id);
    List<UserLikesArtist> select_user_likes_artist_by_artist_id(long artist_id);

    // EXISTS
    boolean admin_exists(long user_id);
    boolean user_exists(String email);
    boolean user_exists(long id);
    boolean artist_exists(long id);
    boolean collaborator_exists(long id);
    boolean request_to_admin_exists(long user_id);
    boolean request_to_collaborator_exists(long user_id);
    boolean request_to_artist_exists(long user_id);
    boolean user_likes_song_exists(long user_id, long song_id);
    boolean user_likes_album_exists(long user_id, long album_id);
    boolean user_likes_playlist_exists(long user_id, long playlist_id);
    boolean user_likes_artist_exists(long user_id, long artist_id);

    // IS TYPE
    boolean is_admin(long user_id);
    boolean is_collaborator(long user_id);
    boolean is_artist(long user_id);

    // UPDATE
    void update_user_last_connection(String email);
    void update_user_password (AuthInfoNewPassword authInfoNewPassword);
    void delete_user(String email);

    // GET/UPLOAD
    String get_picture_by_name(String name);
    String get_user_profile_picture(String email);
    String get_song_track(String name);
    void upload_user_profile_picture(String email, String imagePath);
    void upload_artist_profile_picture_by_user_id(long user_id, String imagePath);

    // DELETE
    void delete_collaborator_request_by_id(long id);
    void delete_artist_request_by_id(long id);
    void delete_request_to_collaborator(long user_id);
    void delete_request_to_artist(long user_id);
    void delete_request_to_admin(long user_id);
    void delete_user_likes_song(UserLikesSong userLikesSong);
    void delete_user_likes_playlist(UserLikesPlaylist userLikesPlaylist);
    void delete_user_likes_album(UserLikesAlbum userLikesAlbum);
    void delete_user_likes_artist(UserLikesArtist userLikesArtist);

    void close();
}
