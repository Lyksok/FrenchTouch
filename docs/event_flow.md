## Event Flow
Event Flow for music streaming app French Touch

### 1. User Interactions

#### Login/Signup:

- User opens the app and is greeted with a login window.
- User can log in with existing credentials or sign up for a new account.
- User is log automaticaly after first succesful log in.
- Upon successful login, the user's last connection date is updated.

#### Main Page:

- User lands on the main page displaying favorite artists and recommended songs.
- User can see their liked songs, playlists, and listening history.

#### Listen to Songs:

- User selects a song to listen to from the main page, search bar, or an album/playlist.
- The song starts playing, and the user can pause, skip, or go to the previous song.
- The number of streams for the song is incremented.
- The song is added to the user's listening history.

#### Follow Artists:

- User can follow artists to receive updates on new releases
- Following an artist adds them to the user's favorite artists list.

#### Create/Modify Playlist:

- User can create a new playlist and add songs to it.
- User can modify existing playlists by adding or removing songs.
- Playlists have a cover image and a calculated length based on the songs included.

#### Change Account Type:

- User can request to change their account type to an artist account.
- The request is sent to the admin for approval.

### 2. Artist Interactions

#### Add/Modify Song:

- Artist can add a new song or modify an existing one.
- If the artist is not verified, the request is sent to the admin for approval.
- Artist can request verification from the admin.
- The song is uploaded with an MP3 file, cover image, and metadata (length, collaborators, etc.).

#### View Discography:

- Artist can see the number of streams for each song and album.

### 3. Admin Interactions

#### Accept Add/Modification Requests:

- Admin receives requests from artists to add or modify songs if not verified.
- Admin reviews the requests and approves or rejects them.
- Upon approval, the song is added to the artist's discography and becomes available for streaming.

#### Verify Artists:

- Admin receives verification requests from artists.
- Admin reviews the requests and verifies the artist if the criteria are met.
- Verified artists can add or modify songs without admin approval.

#### Delete Users or Artists:
- Admin judge if someone does not respects policy
- Admin can delete account and ban user with specific mail adress

### 4. System Interactions

#### Search Functionality:

- Users and admins can search for songs, artists, albums, and playlists using the search bar.
- Search results are displayed based on relevance and popularity.

#### Notifications:

- Users receive notifications for new releases from followed artists.
- Artists receive notifications for the status of their requests.
- Admins receive notifications for song/album/account type change requests.
