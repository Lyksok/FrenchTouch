package epita.projectse.data_access;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import epita.projectse.domain.*;

import javax.net.ssl.*;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.util.Arrays;
import java.util.List;

public class OnlineDbAccess implements DbAccess {
    AuthHash authHash;
    @Override
    public void setAuthHash(AuthHash authHash) {
        this.authHash = authHash;
    }

    public String sendRequest(String req, String json, RequestType type) {
        try {
            // Create a trust manager that does not validate certificate chains
            TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }
                        public void checkClientTrusted(
                                java.security.cert.X509Certificate[] certs, String authType) {
                        }
                        public void checkServerTrusted(
                                java.security.cert.X509Certificate[] certs, String authType) {
                        }
                    }
            };

            // Install the all-trusting trust manager
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Create all-trusting host name verifier
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);

            // Use the custom SSLContext
            HttpClient httpClient = HttpClient.newBuilder()
                    .sslContext(sc)
                    .build();

            // Create the request based on the request type
            HttpRequest request;
            String url = "https://"+ ConfigurationAccess.getServerAddress() + ":" +
                    ConfigurationAccess.getServerPort();

            System.out.println("---------[" + type + "]---------");

            switch (type) {
                case Get:
                    // Check if ./.cache/ directory exists
                    Path cacheDir = Path.of("./.cache");
                    if (!Files.exists(cacheDir)) {
                        Files.createDirectory(cacheDir);
                    }

                    // Check if the file already exists
                    String fileName = req.substring(req.lastIndexOf("/") + 1);
                    Path filePath = cacheDir.resolve(fileName);
                    if (Files.exists(filePath)) {
                        System.out.println("File already exists in cache: " + filePath);
                        System.out.println("-----------------------------");
                        return filePath.toString();
                    }

                    // Create the request
                    request = HttpRequest.newBuilder()
                            .uri(URI.create(url + req))
                            .build();
                    // Send the request and get the response
                    HttpResponse<Path> response = httpClient.send(request, HttpResponse.BodyHandlers.ofFile(filePath));
                    // Save the file to the ./.cache/ directory
                    if (response.statusCode() != 200) {
                        System.out.println("Failed to download file: " + response.statusCode());
                        System.out.println("-----------------------------");
                        return null;
                    }

                    // TODO Remove
                    System.out.println("Request: " + request);
                    System.out.println("Response: " + response);
                    System.out.println("Response body: " + response.body());
                    System.out.println("File saved to cache: " + filePath);
                    System.out.println("-----------------------------");

                    return filePath.toString();
                case PostJsonPayload:
                    request = HttpRequest.newBuilder()
                            .uri(URI.create(url + req))
                            .header("Content-Type", "application/json")
                            .POST(HttpRequest.BodyPublishers.ofString(json))
                            .build();
                    break;
                case PostFilePayload:
                    // Create the request
                    request = HttpRequest.newBuilder()
                            .uri(URI.create(url + req))
                            .header("Content-Type", "application/octet-stream")
                            .POST(HttpRequest.BodyPublishers.ofFile(Path.of(json)))
                            .build();
                    break;
                case Select:
                    request = HttpRequest.newBuilder()
                            .uri(URI.create(url + req))
                            .build();
                    break;
                default:
                    throw new IllegalArgumentException("Invalid request type: " + type);
            }

            HttpResponse<String> response;

            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // TODO Remove
            System.out.println("Request: " + request);
            System.out.println("Response: " + response);
            System.out.println("Response body: " + response.body());
            System.out.println("-----------------------------");
            return response.body();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * Deserialize a JSON string to an Admin object
     */
    public Admin deserializeAdmin(String json) {
        System.out.println("Deserializing admin: " + json);
        try {
            return new ObjectMapper().readValue(json, Admin.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    /*
     * Deserialize a JSON string to a User object
     */
    public User deserializeUser(String json) {
        System.out.println("Deserializing user: " + json);
        try {
            return new ObjectMapper().readValue(json, User.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private Playlist deserializePlaylist(String resp) {
        System.out.println("Deserializing playlist: " + resp);
        try {
            return new ObjectMapper().readValue(resp, Playlist.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private Album deserializeAlbum(String resp) {
        System.out.println("Deserializing album: " + resp);
        try {
            return new ObjectMapper().readValue(resp, Album.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    /*
     * Deserialize a JSON string to a Collaborator object
     */
    public Collaborator deserializeCollaborator(String json) {
        System.out.println("Deserializing collaborator: " + json);
        try {
            return new ObjectMapper().readValue(json, Collaborator.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    /*
     * Deserialize a JSON string to an Artist object
     */
    public Artist deserializeArtist(String json) {
        System.out.println("Deserializing artist: " + json);
        try {
            return new ObjectMapper().readValue(json, Artist.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private ArtistRequest deserializeArtistRequest(String resp) {
        System.out.println("Deserializing artist request: " + resp);
        try {
            return new ObjectMapper().readValue(resp, ArtistRequest.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private CollaboratorRequest deserializeCollaboratorRequest(String resp) {
        System.out.println("Deserializing collaborator request: " + resp);
        try {
            return new ObjectMapper().readValue(resp, CollaboratorRequest.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private RequestToArtist deserializeRequestToArtist(String resp) {
        System.out.println("Deserializing request to artist: " + resp);
        try {
            return new ObjectMapper().readValue(resp, RequestToArtist.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private RequestToCollaborator deserializeRequestToCollaborator(String resp) {
        System.out.println("Deserializing request to collaborator: " + resp);
        try {
            return new ObjectMapper().readValue(resp, RequestToCollaborator.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private RequestToAdmin deserializeRequestToAdmin(String resp) {
        System.out.println("Deserializing request to admin: " + resp);
        try {
            return new ObjectMapper().readValue(resp, RequestToAdmin.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private Song deserializeSong(String resp) {
        System.out.println("Deserializing song: " + resp);
        try {
            return new ObjectMapper().readValue(resp, Song.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private SongAlbum deserializeAlbumSong(String resp) {
        System.out.println("Deserializing album song: " + resp);
        try {
            return new ObjectMapper().readValue(resp, SongAlbum.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private SongPlaylist deserializeSongPlaylist(String resp) {
        System.out.println("Deserializing song playlist: " + resp);
        try {
            return new ObjectMapper().readValue(resp, SongPlaylist.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public UserLikesSong deserializeUserLikesSong(String resp) {
        System.out.println("Deserializing user likes song: " + resp);
        try {
            return new ObjectMapper().readValue(resp, UserLikesSong.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public UserLikesPlaylist DeserializeUserLikesPlaylist(String resp) {
        System.out.println("Deserializing user likes playlist: " + resp);
        try {
            return new ObjectMapper().readValue(resp, UserLikesPlaylist.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public UserLikesAlbum deserializeUserLikesAlbum(String resp) {
        System.out.println("Deserializing user likes album: " + resp);
        try {
            return new ObjectMapper().readValue(resp, UserLikesAlbum.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private UserLikesArtist deserializeUserLikesArtist(String resp) {
        System.out.println("Deserializing user likes artist: " + resp);
        try {
            return new ObjectMapper().readValue(resp, UserLikesArtist.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public String addRequestWrapper(AuthHash auth_hash, String obj) {
        return new RequestWrapper(auth_hash.getHash(), obj).toJson();
    }

    public String addSearchWrapper(String obj){
        return new SearchWrapper(obj).toJson();
    }

    /*
     * Get the authentication uuid to allow update/delete requests depending
     * on the given permissions
     */
    @Override
    public AuthHash authenticate(AuthInfo authInfo) {
        String resp = sendRequest("/fts_login", authInfo.toJson(), RequestType.PostJsonPayload);
        try {
            AuthHash authHash = new ObjectMapper().readValue(resp, AuthHash.class);
            this.authHash = authHash;
            return authHash;
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Override
    public boolean register(AuthInfo authInfo) {
        String resp = sendRequest("/fts_register", authInfo.toJson(), RequestType.PostJsonPayload);
        try {
            AuthHash authHash = new ObjectMapper().readValue(resp, AuthHash.class);
            return authHash.isValid();
        } catch (JsonProcessingException e) {
            return false;
        }
    }

    @Override
    public Admin insert_admin(Admin admin) {
        if (admin_exists(admin.getId())) {
            System.out.println("Admin already exists");
            return null;
        }
        if (!user_exists(admin.getUser_id())) {
            System.out.println("User does not exist");
            return null;
        }
        String resp = sendRequest("/insert/admin", addRequestWrapper(authHash, admin.toJson()), RequestType.PostJsonPayload);
        return deserializeAdmin(resp);
    }

    /*
     * Store a User in the database
     */
    @Override
    public User insert_user(User user) {
        if (user_exists(user.getEmail())) {
            System.out.println("User already exists");
            return null;
        }
        String resp = sendRequest("/insert/user", user.toJson(), RequestType.PostJsonPayload);
        return deserializeUser(resp);
    }

    @Override
    public Artist insert_artist(Artist artist) {
        if (artist_exists(artist.getId())) {
            System.out.println("Artist already exists");
            return null;
        }
        if (!user_exists(artist.getUserId())) {
            System.out.println("User does not exist");
            return null;
        }
        String resp = sendRequest("/insert/artist", addRequestWrapper(authHash, artist.toJson()), RequestType.PostJsonPayload);
        return deserializeArtist(resp);
    }

    @Override
    public Song insert_song(Song song) {
        String resp = sendRequest("/insert/song", addRequestWrapper(authHash, song.toJson()), RequestType.PostJsonPayload);
        return deserializeSong(resp);
    }

    @Override
    public Album insert_album(Album album) {
        if (!artist_exists(album.getArtistId())) {
            System.out.println("Artist does not exist");
            return null;
        }
        if(album.getCoverImage()!= null && get_picture_by_name(album.getCoverImage())==null) {
            String fileName = sendRequest("/post/image", album.getCoverImage(), RequestType.PostFilePayload);
            album.setCoverImage(fileName.replace("\"", ""));
        }
        String resp = sendRequest("/insert/album", addRequestWrapper(authHash, album.toJson()), RequestType.PostJsonPayload);
        return deserializeAlbum(resp);
    }

    @Override
    public Playlist insert_playlist(Playlist playlist) {
        if (!user_exists(playlist.getUserId())) {
            System.out.println("User does not exist");
            return null;
        }
        if(playlist.getCoverImage() != null) {
            String fileName = sendRequest("/post/image", playlist.getCoverImage(), RequestType.PostFilePayload);
            playlist.setCoverImage(fileName.replace("\"", ""));
        }
        String resp = sendRequest("/insert/playlist", addRequestWrapper(authHash, playlist.toJson()), RequestType.PostJsonPayload);
        return deserializePlaylist(resp);
    }

    @Override
    public SongAlbum insert_song_album(SongAlbum songAlbum) {
        String resp = sendRequest("/insert/song_album", addRequestWrapper(authHash, songAlbum.toJson()), RequestType.PostJsonPayload);
        return deserializeAlbumSong(resp);
    }

    @Override
    public SongPlaylist insert_song_playlist(SongPlaylist songPlaylist) {
        String resp = sendRequest("/insert/song_playlist", addRequestWrapper(authHash, songPlaylist.toJson()), RequestType.PostJsonPayload);
        return deserializeSongPlaylist(resp);
    }

    @Override
    public Collaborator insert_collaborator(Collaborator collaborator) {
        if (collaborator_exists(collaborator.getId())) {
            System.out.println("Collaborator already exists");
            return null;
        }
        if (!user_exists(collaborator.getUserId())) {
            System.out.println("User does not exist");
            return null;
        }
        String resp = sendRequest("/insert/collaborator", addRequestWrapper(authHash, collaborator.toJson()), RequestType.PostJsonPayload);
        return deserializeCollaborator(resp);
    }

    @Override
    public ArtistRequest insert_artist_request(ArtistRequest artistRequest) {
        if (!artist_exists(artistRequest.getArtist_id())) {
            System.out.println("Artist does not exist");
            return null;
        }
        if(artistRequest.getSong_cover() != null) {
            String fileName = sendRequest("/post/image", artistRequest.getSong_cover(), RequestType.PostFilePayload);
            artistRequest.setSong_cover(fileName.replace("\"", ""));
        }
        if(artistRequest.getSong_file() != null) {
            String fileName = sendRequest("/post/song", artistRequest.getSong_file(), RequestType.PostFilePayload);
            artistRequest.setSong_file(fileName.replace("\"", ""));
        }
        String resp = sendRequest("/insert/artist_request", addRequestWrapper(authHash, artistRequest.toJson()), RequestType.PostJsonPayload);
        return deserializeArtistRequest(resp);
    }

    @Override
    public UserLikesSong insert_user_likes_song(UserLikesSong userLikesSong) {
        String resp = sendRequest("/insert/user_likes_song", addRequestWrapper(authHash, userLikesSong.toJson()), RequestType.PostJsonPayload);
        return deserializeUserLikesSong(resp);
    }

    @Override
    public UserLikesPlaylist insert_user_likes_playlist(UserLikesPlaylist userLikesPlaylist) {
        String resp = sendRequest("/insert/user_likes_playlist", addRequestWrapper(authHash, userLikesPlaylist.toJson()), RequestType.PostJsonPayload);
        return DeserializeUserLikesPlaylist(resp);
    }

    @Override
    public UserLikesAlbum insert_user_likes_album(UserLikesAlbum userLikesAlbum) {
        String resp = sendRequest("/insert/user_likes_album", addRequestWrapper(authHash, userLikesAlbum.toJson()), RequestType.PostJsonPayload);
        return deserializeUserLikesAlbum(resp);
    }

    @Override
    public UserLikesArtist insert_user_likes_artist(UserLikesArtist userLikesArtist) {
        String resp = sendRequest("/insert/user_likes_artist", addRequestWrapper(authHash, userLikesArtist.toJson()), RequestType.PostJsonPayload);
        return deserializeUserLikesArtist(resp);
    }

    @Override
    public CollaboratorRequest insert_collaborator_request(CollaboratorRequest collaboratorRequest) {
        if (!collaborator_exists(collaboratorRequest.getCollaboratorId())) {
            System.out.println("Collaborator does not exist");
            return null;
        }
        System.out.println("Collaborator request: " + collaboratorRequest.toJson());
        if(collaboratorRequest.getArtist_profile_picture() != null) {
            String fileName = sendRequest("/post/image", collaboratorRequest.getArtist_profile_picture(), RequestType.PostFilePayload);
            collaboratorRequest.setArtist_profile_picture(fileName.replace("\"", ""));
        }
        if(collaboratorRequest.getSong_cover() != null) {
            String fileName = sendRequest("/post/image", collaboratorRequest.getSong_cover(), RequestType.PostFilePayload);
            collaboratorRequest.setSong_cover(fileName.replace("\"", ""));
        }
        if(collaboratorRequest.getSong_file() != null) {
            String fileName = sendRequest("/post/song", collaboratorRequest.getSong_file(), RequestType.PostFilePayload);
            collaboratorRequest.setSong_file(fileName.replace("\"", ""));
        }
        if(collaboratorRequest.getAlbum_cover() != null) {
            String fileName = sendRequest("/post/image", collaboratorRequest.getAlbum_cover(), RequestType.PostFilePayload);
            collaboratorRequest.setAlbum_cover(fileName.replace("\"", ""));
        }
        String resp = sendRequest("/insert/collaborator_request", addRequestWrapper(authHash, collaboratorRequest.toJson()), RequestType.PostJsonPayload);
        return deserializeCollaboratorRequest(resp);
    }

    @Override
    public RequestToArtist insert_request_to_artist(RequestToArtist requestToArtist) {
        if (!user_exists(requestToArtist.getUser_id())) {
            System.out.println("User does not exist");
            return null;
        }
        String resp = sendRequest("/insert/request_to_artist", addRequestWrapper(authHash, requestToArtist.toJson()), RequestType.PostJsonPayload);
        return deserializeRequestToArtist(resp);
    }

    @Override
    public RequestToCollaborator insert_request_to_collaborator(RequestToCollaborator requestToCollaborator) {
        if (!user_exists(requestToCollaborator.getUser_id())) {
            System.out.println("User does not exist");
            return null;
        }
        String resp = sendRequest("/insert/request_to_collaborator", addRequestWrapper(authHash, requestToCollaborator.toJson()), RequestType.PostJsonPayload);
        return deserializeRequestToCollaborator(resp);
    }

    @Override
    public RequestToAdmin insert_request_to_admin(RequestToAdmin requestToAdmin) {
        if (!user_exists(requestToAdmin.getUser_id())) {
            System.out.println("User does not exist");
            return null;
        }
        String resp = sendRequest("/insert/request_to_admin", addRequestWrapper(authHash, requestToAdmin.toJson()), RequestType.PostJsonPayload);
        return deserializeRequestToAdmin(resp);
    }

    @Override
    public Admin select_admin_by_user_id(long user_id) {
        String json = sendRequest("/select/admin/user_id/"+user_id, null, RequestType.Select);
        return deserializeAdmin(json);
    }

    @Override
    public User select_user_by_email(String email) {
        String json = sendRequest("/select/user/email/"+email, null, RequestType.Select);
        return deserializeUser(json);
    }

    @Override
    public User select_user_by_id(long id) {
        String json = sendRequest("/select/user/id/"+id, null, RequestType.Select);
        return deserializeUser(json);
    }

    @Override
    public Song select_song_by_id(long id) {
        String json = sendRequest("/select/song/id/"+id, null, RequestType.Select);
        return deserializeSong(json);
    }

    @Override
    public Artist select_artist_by_id(long id) {
        String json = sendRequest("/select/artist/id/"+id, null, RequestType.Select);
        return deserializeArtist(json);
    }

    @Override
    public Artist select_artist_by_user_id(long user_id) {
        String json = sendRequest("/select/artist/user_id/"+ user_id, null, RequestType.Select);
        return deserializeArtist(json);
    }

    @Override
    public Collaborator select_collaborator_by_id(long id) {
        String json = sendRequest("/select/collaborator/id/"+id, null, RequestType.Select);
        return deserializeCollaborator(json);
    }

    @Override
    public Collaborator select_collaborator_by_user_id(long user_id) {
        String json = sendRequest("/select/collaborator/user_id/"+user_id, null, RequestType.Select);
        return deserializeCollaborator(json);
    }

    @Override
    public AlbumSearch select_search_album_by_album_id(long album_id) {
        String json = sendRequest("/select/search/album/album_id/"+album_id, null, RequestType.Select);
        try {
            return new ObjectMapper().readValue(json, AlbumSearch.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Override
    public Album select_album_by_id(long id) {
        String json = sendRequest("/select/album/id/"+id, null, RequestType.Select);
        return deserializeAlbum(json);
    }

    @Override
    public Playlist select_playlist_by_id(long id) {
        String json = sendRequest("/select/playlist/id/"+id, null, RequestType.Select);
        return deserializePlaylist(json);
    }

    @Override
    public PlaylistSearch select_search_playlist_by_playlist_id(long playlist_id) {
        String json = sendRequest("/select/search/playlist/playlist_id/"+playlist_id, null, RequestType.Select);
        try {
            return new ObjectMapper().readValue(json, PlaylistSearch.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Override
    public List<SongSearch> select_search_songs(String title) {
        String json = sendRequest("/select/search/song", addSearchWrapper(title), RequestType.PostJsonPayload);
        try {
            return Arrays.asList(new ObjectMapper().readValue(json, SongSearch[].class));
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Override
    public List<SongSearch> select_search_songs_by_artist_id(long artist_id) {
        String json = sendRequest("/select/search/song/artist_id/"+artist_id, null, RequestType.Select);
        try {
            return Arrays.asList(new ObjectMapper().readValue(json, SongSearch[].class));
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Override
    public List<SongSearch> select_search_songs_by_album_id(long album_id) {
        String json = sendRequest("/select/search/song/album_id/"+album_id, null, RequestType.Select);
        try {
            return Arrays.asList(new ObjectMapper().readValue(json, SongSearch[].class));
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Override
    public List<SongSearch> select_search_songs_by_playlist_id(long playlist_id) {
        String json = sendRequest("/select/search/song/playlist_id/"+playlist_id, null, RequestType.Select);
        try {
            return Arrays.asList(new ObjectMapper().readValue(json, SongSearch[].class));
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Override
    public List<AlbumSearch> select_search_albums(String title) {
        String json = sendRequest("/select/search/album", addSearchWrapper(title), RequestType.PostJsonPayload);
        try {
            return Arrays.asList(new ObjectMapper().readValue(json, AlbumSearch[].class));
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Override
    public List<PlaylistSearch> select_search_playlists(String title) {
        String json = sendRequest("/select/search/playlist", addSearchWrapper(title), RequestType.PostJsonPayload);
        try {
            return Arrays.asList(new ObjectMapper().readValue(json, PlaylistSearch[].class));
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Override
    public List<Playlist> select_search_playlists_by_artist_id(long artist_id) {
        String json = sendRequest("/select/search/playlist/artist_id/"+artist_id, null, RequestType.Select);
        try {
            return Arrays.asList(new ObjectMapper().readValue(json, Playlist[].class));
        } catch (JsonProcessingException e) {
            return null;
        }
    }


    @Override
    public List<RequestToAdmin> select_request_to_admin_all() {
        String json = sendRequest("/select/request_to_admin/all", null, RequestType.Select);
        try {
            System.out.println("JSON admin request: " + json);
            return Arrays.asList(new ObjectMapper().readValue(json, RequestToAdmin[].class));

        } catch (JsonProcessingException e) {
            System.out.println("Error parsing JSON admin request\n");
            return null;
        }
    }

    @Override
    public List<RequestToCollaborator> select_request_to_collaborator_all() {
        String json = sendRequest("/select/request_to_collaborator/all", null, RequestType.Select);
        try {
            return Arrays.asList(new ObjectMapper().readValue(json, RequestToCollaborator[].class));
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Override
    public ArtistRequest select_artist_request_by_id(long id) {
        String json = sendRequest("/select/artist_request/id/"+id, null, RequestType.Select);
        return deserializeArtistRequest(json);
    }

    @Override
    public List<ArtistRequest> select_artist_request_all() {
        String json = sendRequest("/select/artist_request/all", null, RequestType.Select);
        try {
            return Arrays.asList(new ObjectMapper().readValue(json, ArtistRequest[].class));
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Override
    public CollaboratorRequest select_collaborator_request_by_id(long id) {
        String json = sendRequest("/select/collaborator_request/id/"+id, null, RequestType.Select);
        return deserializeCollaboratorRequest(json);
    }

    @Override
    public List<CollaboratorRequest> select_collaborator_request_all() {
        String json = sendRequest("/select/collaborator_request/all", null, RequestType.Select);
        try {
            return Arrays.asList(new ObjectMapper().readValue(json, CollaboratorRequest[].class));
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Override
    public List<RequestToArtist> select_request_to_artist_all() {
        String json = sendRequest("/select/request_to_artist/all", null, RequestType.Select);
        try {
            return Arrays.asList(new ObjectMapper().readValue(json, RequestToArtist[].class));
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Override
    public List<ArtistSearch> select_search_artists(String name) {
        String json = sendRequest("/select/search/artist", addSearchWrapper(name), RequestType.PostJsonPayload);
        try {
            return Arrays.asList(new ObjectMapper().readValue(json, ArtistSearch[].class));
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Override
    public RequestToArtist select_request_to_artist_by_user_id(long user_id) {
        String json = sendRequest("/select/request_to_artist/user_id/"+user_id, null, RequestType.Select);
        return deserializeRequestToArtist(json);
    }

    @Override
    public RequestToAdmin select_request_to_admin_by_user_id(long user_id) {
        String json = sendRequest("/select/request_to_admin/user_id/"+user_id, null, RequestType.Select);
        return deserializeRequestToAdmin(json);
    }

    @Override
    public RequestToCollaborator select_request_to_collaborator_by_user_id(long user_id) {
        String json = sendRequest("/select/request_to_collaborator/user_id/"+user_id, null, RequestType.Select);
        return deserializeRequestToCollaborator(json);
    }

    @Override
    public List<PlaylistSearch> select_search_playlist_by_user_id(long user_id) {
        String json = sendRequest("/select/search/playlist/user_id/" + user_id,null, RequestType.Select);
        try {
            return Arrays.asList(new ObjectMapper().readValue(json, PlaylistSearch[].class));
        }
        catch (JsonProcessingException e){
            return null;
        }
    }

    @Override
    public List<AlbumSearch> select_search_album_by_artist_id(long artist_id) {
        String json = sendRequest("/select/search/album/artist_id/" + artist_id,null, RequestType.Select);
        System.out.println("JSON album search: " + json);
        try {
            return Arrays.asList(new ObjectMapper().readValue(json, AlbumSearch[].class));
        }
        catch (JsonProcessingException e){
            return null;
        }
    }

    @Override
    public List<UserLikesSong> select_user_likes_song_by_user_id(long user_id) {
        String json = sendRequest("/select/user_likes_song/user_id/" + user_id,null, RequestType.Select);
        try {
            return Arrays.asList(new ObjectMapper().readValue(json, UserLikesSong[].class));
        }
        catch (JsonProcessingException e){
            return null;
        }
    }

    @Override
    public List<UserLikesPlaylist> select_user_likes_playlist_by_user_id(long user_id) {
        String json = sendRequest("/select/user_likes_playlist/user_id/" + user_id,null, RequestType.Select);
        try {
            return Arrays.asList(new ObjectMapper().readValue(json, UserLikesPlaylist[].class));
        }
        catch (JsonProcessingException e){
            return null;
        }
    }

    @Override
    public List<UserLikesAlbum> select_user_likes_album_by_user_id(long user_id) {
        String json = sendRequest("/select/user_likes_album/user_id/" + user_id,null, RequestType.Select);
        try {
            return Arrays.asList(new ObjectMapper().readValue(json, UserLikesAlbum[].class));
        }
        catch (JsonProcessingException e){
            return null;
        }
    }

    @Override
    public List<UserLikesArtist> select_user_likes_artist_by_user_id(long user_id) {
        String json = sendRequest("/select/user_likes_artist/user_id/" + user_id,null, RequestType.Select);
        try {
            return Arrays.asList(new ObjectMapper().readValue(json, UserLikesArtist[].class));
        }
        catch (JsonProcessingException e){
            return null;
        }
    }

    @Override
    public List<UserLikesArtist> select_user_likes_artist_by_artist_id(long artist_id) {
        String json = sendRequest("/select/user_likes_artist/artist_id/" + artist_id,null, RequestType.Select);
        try {
            return Arrays.asList(new ObjectMapper().readValue(json, UserLikesArtist[].class));
        }
        catch (JsonProcessingException e){
            return null;
        }
    }

    @Override
    public List<SongSearch> select_search_song_by_artist_id(long artist_id) {
        String json = sendRequest("/select/search/song/artist_id/" + artist_id,null, RequestType.Select);
        try {
            return Arrays.asList(new ObjectMapper().readValue(json, SongSearch[].class));
        }
        catch (JsonProcessingException e){
            return null;
        }
    }

    @Override
    public boolean admin_exists(long user_id) {
        Admin admin = select_admin_by_user_id(user_id);
        return admin != null;
    }

    @Override
    public boolean user_exists(String email) {
        return select_user_by_email(email) != null;
    }

    @Override
    public boolean user_exists(long id) {
        String json = sendRequest("/select/user/id/"+id, null, RequestType.Select);
        return deserializeUser(json) != null;
    }

    @Override
    public void update_user_last_connection(String email) {
        User user = select_user_by_email(email);
        if (user == null) {
            System.out.println("User not found");
            return;
        }
        user.setLastConnection((int) (System.currentTimeMillis()/1000L));
        sendRequest("/update/user/last_connection/" + email, addRequestWrapper(authHash, user.toJson()), RequestType.PostJsonPayload);
    }

    @Override
    public void update_user_password(AuthInfoNewPassword authInfoNewPassword){
        User user = authInfoNewPassword.getAuthInfo().getUser();
        sendRequest("/update/user/password/" + user.getId(), authInfoNewPassword.toJson(), RequestType.PostJsonPayload );
    }

    @Override
    public void delete_user(String email) {
        // TODO
    }

    @Override
    public String get_picture_by_name(String name) {
        if(name == null) {
            System.out.println("Picture not set");
            return null;
        }
        return sendRequest("/get/image/" + name, null, RequestType.Get);
    }

    @Override
    public void upload_user_profile_picture(String email, String imagePath) {
        User user = select_user_by_email(email);
        if (user == null) {
            System.out.println("User not found");
            return;
        }

        // Post image
        String fileName = sendRequest("/post/image", imagePath , RequestType.PostFilePayload);
        user.setProfilePicture(fileName.replace("\"", ""));
        sendRequest("/update/user/profile_picture/" + email, addRequestWrapper(authHash,user.toJson()), RequestType.PostJsonPayload);
    }

    @Override
    public String get_user_profile_picture(String email) {
        User user = select_user_by_email(email);
        if (user == null) {
            System.out.println("User not found");
            return null;
        }
        String fileName = user.getProfilePicture();
        System.out.println("File name: " + fileName);
        if (fileName == null) {
            System.out.println("User has no profile picture");
            return null;
        }
        return sendRequest("/get/image/" + fileName.replace("\"", ""), null, RequestType.Get);
    }

    @Override
    public boolean is_admin(long user_id) {
        String json = sendRequest("/select/admin/user_id/" + user_id, null, RequestType.Select);
        return deserializeAdmin(json) != null;
    }

    @Override
    public boolean is_collaborator(long user_id) {
        String json = sendRequest("/select/collaborator/user_id/" + user_id, null, RequestType.Select);
        Collaborator collaborator = deserializeCollaborator(json);
        return collaborator != null;
    }

    @Override
    public boolean is_artist(long user_id) {
        String json = sendRequest("/select/artist/user_id/" + user_id, null, RequestType.Select);
        Artist artist = deserializeArtist(json);
        return artist != null;
    }

    @Override
    public boolean artist_exists(long id) {
        String json = sendRequest("/select/artist/id/" + id, null, RequestType.Select);
        return deserializeArtist(json)!=null;
    }

    @Override
    public boolean collaborator_exists(long id) {
        String json = sendRequest("/select/collaborator/id/" + id, null, RequestType.Select);
        return deserializeCollaborator(json)!=null;
    }

    @Override
    public boolean request_to_admin_exists(long user_id) {
        String json = sendRequest("/select/request_to_admin/user_id/" + user_id, null, RequestType.Select);
        return deserializeRequestToAdmin(json)!=null;
    }

    @Override
    public boolean request_to_collaborator_exists(long user_id) {
        String json = sendRequest("/select/request_to_collaborator/user_id/" + user_id, null, RequestType.Select);
        return deserializeRequestToCollaborator(json)!=null;
    }

    @Override
    public boolean request_to_artist_exists(long user_id) {
        String json = sendRequest("/select/request_to_artist/user_id/" + user_id, null, RequestType.Select);
        return deserializeRequestToArtist(json)!=null;
    }

    @Override
    public boolean user_likes_song_exists(long user_id, long song_id) {
        String json = sendRequest("/exist/user_likes_song", new UserLikesSong(user_id, song_id).toJson(), RequestType.PostJsonPayload);
        return json.isEmpty();
    }

    @Override
    public boolean user_likes_album_exists(long user_id, long album_id) {
        String json = sendRequest("/exist/user_likes_album", new UserLikesAlbum(user_id, album_id).toJson(), RequestType.PostJsonPayload);
        return json.isEmpty();
    }

    @Override
    public boolean user_likes_playlist_exists(long user_id, long playlist_id) {
        String json = sendRequest("/exist/user_likes_playlist", new UserLikesPlaylist(user_id, playlist_id).toJson(), RequestType.PostJsonPayload);
        return json.isEmpty();
    }

    @Override
    public boolean user_likes_artist_exists(long user_id, long artist_id) {
        String json = sendRequest("/exist/user_likes_artist", new UserLikesArtist(user_id, artist_id).toJson(), RequestType.PostJsonPayload);
        return json.isEmpty();
    }

    @Override
    public void upload_artist_profile_picture_by_user_id(long user_id, String imagePath) {
        if (!is_artist(user_id)) {
            System.out.println("User not an artist");
            return;
        }
        User user = select_user_by_id(user_id);
        if (user == null) {
            System.out.println("User not found");
            return;
        }

        // Post image
        String fileName = sendRequest("/post/image", imagePath , RequestType.PostFilePayload);
        user.setProfilePicture(fileName.replace("\"", ""));
        sendRequest("/update/user/profile_picture/" + user.getEmail(), user.toJson(), RequestType.PostJsonPayload);
    }

    @Override
    public String get_song_track(String name) {
        String fileName = sendRequest("/get/song/" + name.replace("\"",""), null, RequestType.Get);
        if (fileName == null) {
            System.out.println("Song not found");
            return null;
        }
        return fileName.replace("\"", "");
    }

    @Override
    public void delete_collaborator_request_by_id(long id) {
        sendRequest("/delete/collaborator_request/id/" + id, authHash.toJson(), RequestType.PostJsonPayload);
    }

    @Override
    public void delete_artist_request_by_id(long id) {
        sendRequest("/delete/artist_request/id/" + id, authHash.toJson(), RequestType.PostJsonPayload);
    }

    @Override
    public void delete_request_to_collaborator(long user_id) {
        sendRequest("/delete/request_to_collaborator/user_id/" + user_id, authHash.toJson(), RequestType.PostJsonPayload);
    }

    @Override
    public void delete_request_to_artist(long user_id) {
        sendRequest("/delete/request_to_artist/user_id/" + user_id, authHash.toJson(), RequestType.PostJsonPayload);
    }

    @Override
    public void delete_request_to_admin(long user_id) {
        sendRequest("/delete/request_to_admin/user_id/" + user_id, authHash.toJson(), RequestType.PostJsonPayload);
    }

    @Override
    public void delete_user_likes_song(UserLikesSong userLikesSong) {
        sendRequest("/delete/user_likes_song", addRequestWrapper(authHash, userLikesSong.toJson()), RequestType.PostJsonPayload);
    }

    @Override
    public void delete_user_likes_playlist(UserLikesPlaylist userLikesPlaylist) {
        sendRequest("/delete/user_likes_playlist", addRequestWrapper(authHash, userLikesPlaylist.toJson()), RequestType.PostJsonPayload);
    }

    @Override
    public void delete_user_likes_album(UserLikesAlbum userLikesAlbum) {
        sendRequest("/delete/user_likes_album", addRequestWrapper(authHash, userLikesAlbum.toJson()), RequestType.PostJsonPayload);
    }

    @Override
    public void delete_user_likes_artist(UserLikesArtist userLikesArtist) {
        sendRequest("/delete/user_likes_artist", addRequestWrapper(authHash, userLikesArtist.toJson()), RequestType.PostJsonPayload);
    }

    @Override
    public void close() {
        // No need to close anything for online access
    }
}
