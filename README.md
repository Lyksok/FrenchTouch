# SE1 Project

Team : French Touch <br>
Members : Jans GUILLOPE, Hugo COHEN

Welcome to French Touch, a dynamic music streaming application designed to connect users with their favorite artists and discover new music seamlessly. Our platform offers a rich set of features tailored for users, artists, and administrators, ensuring a comprehensive and engaging music experience.

# Server
The French Touch client can connect to a database written in Rust using HTTPS connection. Here is the link of the repo : [link](https://github.com/Lyksok/FrenchTouchServer).
This project is made by student, thus, it does not implement all safety features needed for public distribution. However, password are stored safely using hashes and are bruteforce and rainbow table proof. <br>

# Key Features

## User Interactions
- [X] Login/Signup: Users can easily log in or sign up for an account, with automatic login after the first successful attempt.
- [X] Choose the database connection type between offline (local) and online (HTTP Server).
- [ ] Main Page: Displays favorite artists and recommended songs, along with liked songs, playlists, and listening history.
- [ ] Listen to Songs: Users can play, pause, skip, or go to previous songs, with each play incrementing the song's stream count.
- [ ] Follow Artists: Receive updates on new releases from followed artists.
- [ ] Create/Modify Playlists: Users can create and customize playlists with cover images and calculated lengths.
- [ ] Change Account Type: Request to switch to an artist account, subject to admin approval.

## Artist Interactions
- [ ] Add/Modify Songs: Artists can upload new songs or modify existing ones, with admin approval required for unverified artists.
- [ ] View Discography: Track the number of streams for each song and album.
- [ ] Verification: Artists can request verification to gain more control over their content.

## Admin Interactions
- [ ] Request Management: Admins review and approve/reject song additions or modifications from unverified artists.
- [ ] Artist Verification: Verify artists based on specific criteria.
- [ ] User Management: Delete or ban users who violate platform policies.

## System Interactions
- [ ] Search Functionality: Users and admins can search for songs, artists, albums, and playlists.
- [ ] Notifications: Users receive updates on new releases, artists get request status updates, and admins are notified of pending requests.
