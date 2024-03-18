Afanasiuc Daniel - 322CA

# Global waves - third part

## AudioEntities
* **AudioEntities**:  
  Refers to all audio files/collections
  * **AudioFile**:  
  An audio file may be either a song or an episode
  * **AudioCollection**:  
  An audio collection may be either a podcast or a playlist

## Database
* The database is made singleton, so each user has access to the same database instance.
* the database keeps references to all existing audio and user entities.
* The database can add or delete any type of user entity.

## Pages
* Printing pages is implemented using the Strategy Design Pattern.
* **Page**:  
  Refers to all types of pages
  * **HomePage**
  * **LikedContentPage**
  * **ArtistPage**
  * **HostPage**

## Users
### Normal Users
* A user can either be premium or non-premium.
* A non-premium user has ads that on each play add revenue to the artists current song.
* A premium user has a "wallet", from which adds revenue to an artist.
* Each normal user can:
  * **create a playlist**
  * **add or remove songs from a playlist**
  * **switch the visibility of a playlist**
  * **follow a playlist**
  * **like a song**
  * **switch its connection status**
  * **print his current page**
  * **change his current page**
  * **calculate its wrapped data**
  * **calculate revenue for what he is listening to**
  * **add or remove an ad**
  * **buy or remove a premium subscription**
  * **subscribe to an artist or a host**
  * **receive notifications whenever an artist/host that he is subscribed to add something new**
  * **buy merch from an artist**
  * **iterate through its own page history**
  * **recommend either songs or playlists**

* Each normal user has its own:
  * **ConnectionStatus**
  * **Player**: composition relation exists between User and Player
  * **SearchBar**: composition relation between User and SearchBar
  * **Playlists**
  * **LikedSongs**
  * **FollowedPlaylists**
  * **CurrentPage**
  * **Home Page**
  * **Liked Content Page**
  * **Page History**
  * **Wrapped**
  * **Recommended Songs**
  * **Recommended Playlists**
  * **BoughtMerch**
  * **Notifications**

#### Player
* A player first loads an audio entity whenever the load command is set.  
* At every other player related command (such as play/pause, status, repeat etc.),
the player gets updated by checking what happened to the current playing entity
between the previous timestamp (the timestamp of the previous command) and the current timestamp.
* By checking the type of the current playing entity, the player gets updated accordingly,
checking the repeat, play and shuffle state of the player.
* Each user's player keeps the state of every podcast in the database
(a podcast state being where the player remained in that certain podcast).

#### SearchBar
* At every search, the player gets updated to the current timestamp, and then it resets.
* Based on the search criteria, the foundAudio or foundUsers
array of audio or user entities gets updated.
* Upon select, one entity from the foundAudio or foundUsers array gets selected.

### Artists
* Each artist can:
  * **add an album**
  * **remove an album**
  * **add an event**
  * **remove an event**
  * **add merchandise**
  * **add notifications to its subscribers**
  * **calculate its wrapped**
  * **calculate its revenue**
  * **calculate its most profitable song**

* Each artist has its own:
  * **Albums**
  * **Events**
  * **Merch**
  * **TotalLikeCount**
  * **Page**
  * **Wrapped**
  * **Songs Revenue**
  * **Merch Revenue**
  * **Ranking**
  * **MostProfitabelSongName**
  * **MostProfitabelSongRevenue**

### Hosts
* Each host can:
  * **add a podcast**
  * **remove a podcast**
  * **add an announcement**
  * **remove an announcement**
  * **add notifications to its subscribers**
  * **calculate its wrapped**

* Each host has its own:
  * **Podcasts**
  * **Announcements**
  * **Wrapped**
  * **Page**

## Wrapped
* Each user entity has its own Wrapped.
* A user's Wrapped gives details and statistics of what that certain user listened.
* An artist/host Wrapped gives statistics on how many audio files and collections of
the artist/host were listened by all the other users.

## Design Patterns

### Singleton:
* The database of the application is made singleton so only an instance of it exists.
* Basically implemented it as a shared resource.

### AudioVisitors:
* Based on the user's player command, one of the visitors gets created and executes the command
based on the type of audio entity that is currently loaded in the player.
* Type of AudioVisitors:
  * **LoadAudioVisitor**:  
  loads the selected type of audio entity
  *  **UpdateAudioVisitor**:  
  updates the currently playing type of audio entity
  *  **StatusAudioVisitor**:  
  gets the status of the currently playing type of audio entity
  *  **RepeatAudioVisitor**:  
  gets the repeat message of the currently playing type of audio entity
  *  **ShuffleAudioVisitor**:  
  sets the shuffle array of the currently playing type of audio entity
  *  **ForwardAudioVisitor**:  
  forwards the currently playing type of audio entity
  *  **BackwardAudioVisitor**:  
  rewinds the currently playing type of audio entity
  *  **NextAudioVisitor**:  
  skips the currently playing type of audio entity
  *  **PrevAudioVisitor**:  
  goes back to the previous track based on the type of audio entity
  * **LikeAudioVisitor**
  * **AddRemoveInPlaylistAudioVisitor**
  * **FollowPlaylistAudioVisitor**
  * **GetSongAudioVisitor**:  
  gets the song that its currently playing based on the audio type
  * **IsAlbumDeletableAudioVisitor**:  
  checks if an album is deletable based on the audio type that's currently playing
  * **ResetCurrentPlayingAudioVisitor**

### Observer
* Implemented a Notification Manager that handles subscription/un-subscription to other users.
* On each type of add, subscribed users are notified and their notifications array gets updated
* based on what the artist/host added.

### Strategy:
* Used strategy for:
  * Printing the page a user is currently on.
  * Calculating the wrapped of a user entity.

### Factory:
* Implemented Factory for adding users to the database,
* as there are multiple types of users the database can handle.

## Functionalities yet to be added, removed or changed
* The type of user entity should be removed and a visitor pattern for user entities should be added
