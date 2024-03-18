package audiovisitors;

import audioentities.audiocollections.Album;
import audioentities.audiocollections.Playlist;
import audioentities.audiocollections.Podcast;
import audioentities.audiofiles.Episode;
import audioentities.audiofiles.Song;
import userentities.user.User;
import userentities.user.player.Player;

import java.util.ArrayList;

public final class UpdateAudioVisitor implements AudioVisitor {
    // constants
    private static final Integer AD_DURATION = 10;
    private final User user;
    private final Player player;
    private final Integer timestamp;

    public UpdateAudioVisitor(final Player player, final Integer timestamp) {
        this.player = player;
        this.user = player.getUser();
        this.timestamp = timestamp;
    }

    private void updateSong(final Song song) {
        Integer timestampDiff = timestamp - player.getPrevCommandTimestamp();

        // if the difference between the timestamps is smaller
        // than the remained size of the song,
        // update the remained size and return
        if (timestampDiff < player.getRemainedTime()) {
            player.setRemainedTime(player.getRemainedTime() - timestampDiff);
            player.setPrevCommandTimestamp(timestamp);
        } else {
            // if the song has finished
            // and no repeat, reset the player
            if (player.getRepeat() == 0) {
                // add an ad at end of song
                if (!player.getAds().isEmpty() && !user.isPremium()) {
                    user.removeAd();
                }
                player.resetPlayer();
                player.setPrevCommandTimestamp(timestamp);
                song.setPlayed(false);
            } else if (player.getRepeat() == 1) { // if repeat once
                user.getWrapped().updateSongListens(song);

                Integer currSongDuration = song.getDuration();
                // if the duration of the song + the remaining size is bigger
                // than the difference between timestamps
                // just update the remained size and return
                if (player.getRemainedTime() + currSongDuration > timestampDiff) {
                    player.setRemainedTime(currSongDuration
                            + player.getRemainedTime() - timestampDiff);
                } else {
                    player.resetPlayer();
                    song.setPlayed(false);
                }
                player.setPrevCommandTimestamp(timestamp);
                player.setRepeat(0);
            } else { // if repeat infinite times
                Integer currSongDuration = song.getDuration();
                timestampDiff -= player.getRemainedTime();
                Integer songRepeatTimes = Math.floorDiv(timestampDiff, currSongDuration);
                timestampDiff -= (songRepeatTimes * currSongDuration);

                for (int i = 0; i < songRepeatTimes; ++i) {
                    user.getWrapped().updateSongListens(song);
                }
                if (timestampDiff != 0) {
                    user.getWrapped().updateSongListens(song);
                }

                player.setRemainedTime(currSongDuration - timestampDiff);
                player.setPrevCommandTimestamp(timestamp);
            }
        }
    }

    private void iteratePodcastEpisodes(final Podcast podcast, final int timestampDiff) {
        if (player.getPlayingPodcastState(podcast) == null) {
            return;
        }
        Episode currEpisode = player.getPlayingPodcastState(podcast).getCurrentEpisode();
        int currEpisodeIndex = podcast.getEpisodes().indexOf(currEpisode);

        boolean isEnded = true;

        for (int i = currEpisodeIndex + 1; i < podcast.getEpisodes().size(); ++i) {
            currEpisode = podcast.getEpisodes().get(i);

            user.getWrapped().updateEpisodeListens(currEpisode);

            player.setRemainedTime(player.getRemainedTime() + currEpisode.getDuration());
            if (timestampDiff < player.getRemainedTime()) {
                player.setRemainedTime(player.getRemainedTime() - timestampDiff);
                isEnded = false;
                break;
            }
        }

        // if the episodes are ended
        if (isEnded) {
            Episode firstEpisode = podcast.getEpisodes().get(0);
            player.getPlayingPodcastState(podcast).setCurrentEpisode(firstEpisode);
            player.getPlayingPodcastState(podcast)
                    .setEpisodeRemainingTime(firstEpisode.getDuration());
            player.resetPlayer();
            podcast.setPlayed(false);
        } else {
            player.getPlayingPodcastState(podcast).setCurrentEpisode(currEpisode);
            player.getPlayingPodcastState(podcast)
                    .setEpisodeRemainingTime(player.getRemainedTime());
        }
    }

    private void updatePodcast(final Podcast podcast) {
        Integer timestampDiff = timestamp - player.getPrevCommandTimestamp();

        if (podcast.getEpisodes().isEmpty()) {
            player.setPrevCommandTimestamp(timestamp);
            return;
        }

        // if the difference between the timestamps is smaller
        // than the remained size of the episode,
        // update the remained size and return
        if (timestampDiff < player.getRemainedTime()) {
            player.setRemainedTime(player.getRemainedTime() - timestampDiff);
            player.setPrevCommandTimestamp(timestamp);
            player.getPlayingPodcastState(podcast)
                    .setEpisodeRemainingTime(player.getRemainedTime());
        } else {
            // if the episode has finished
            // and no repeat, reach the next episodes
            if (player.getRepeat() == 0) {
                iteratePodcastEpisodes(podcast, timestampDiff);
                player.setPrevCommandTimestamp(timestamp);

            } else if (player.getRepeat() == 1) { // if repeat once
                Episode currEpisode = player.getPlayingPodcastState(podcast).getCurrentEpisode();
                Integer currEpisodeDuration =
                        player.getPlayingPodcastState(podcast).getCurrentEpisode().getDuration();

                user.getWrapped().updateEpisodeListens(currEpisode);

                // if the duration of the episode + the remaining size is bigger
                // than the difference between timestamps
                // just update the remained size and return
                if (player.getRemainedTime() + currEpisodeDuration > timestampDiff) {
                    player.setRemainedTime(currEpisodeDuration
                            + player.getRemainedTime() - timestampDiff);
                    player.setPrevCommandTimestamp(timestamp);
                    player.getPlayingPodcastState(podcast)
                            .setEpisodeRemainingTime(player.getRemainedTime());
                } else { // reach the next episodes
                    timestampDiff -= currEpisodeDuration;

                    iteratePodcastEpisodes(podcast, timestampDiff);

                    player.setPrevCommandTimestamp(timestamp);
                }
                player.setRepeat(0);
            } else { // if repeat infinite times
                Episode currEpisode = player.getPlayingPodcastState(podcast).getCurrentEpisode();
                Integer currEpisodeDuration =
                        player.getPlayingPodcastState(podcast).getCurrentEpisode().getDuration();
                timestampDiff -= player.getRemainedTime();
                Integer episodeRepeatTimes = Math.floorDiv(timestampDiff, currEpisodeDuration);
                timestampDiff -= (episodeRepeatTimes * currEpisodeDuration);

                for (int i = 0; i < episodeRepeatTimes; ++i) {
                    user.getWrapped().updateEpisodeListens(currEpisode);
                }
                if (timestampDiff != 0) {
                    user.getWrapped().updateEpisodeListens(currEpisode);
                }

                player.setRemainedTime(currEpisodeDuration - timestampDiff);
                player.setPrevCommandTimestamp(timestamp);
                player.getPlayingPodcastState(podcast)
                        .setEpisodeRemainingTime(player.getRemainedTime());
            }
        }
    }

    private void iteratePlaylistSongs(final Playlist playlist, final ArrayList<Song> playlistSongs,
                                      final int timestampDiff) {
        boolean isFinished = false;
        int isFirstLoop = 1;
        while (!isFinished) {
            Song currSong = player.getPlaylistState();
            currSong.setPlayed(false);
            int currSongIndex = playlistSongs.indexOf(currSong);

            boolean isEnded = true;

            for (int i = currSongIndex + isFirstLoop; i < playlistSongs.size(); ++i) {
                // check ads
                if (!player.getAds().isEmpty() && !user.isPremium()) {
                    int realDiff = timestampDiff - player.getRemainedTime();
                    // a full ad cannot be played
                    if (realDiff < AD_DURATION) {
                        player.setPrevCommandTimestamp(timestamp - realDiff);
                        player.setAdUpdate(true);

                        user.removeAd();

                        player.setRemainedTime(playlistSongs.get(i).getDuration());
                        player.setPlaylistState(playlistSongs.get(i));
                        return;
                    }

                    user.removeAd();

                    player.setRemainedTime(player.getRemainedTime() + AD_DURATION);
                }

                currSong = playlistSongs.get(i);

                user.getWrapped().updateSongListens(currSong);

                player.setRemainedTime(player.getRemainedTime() + currSong.getDuration());
                if (timestampDiff < player.getRemainedTime()) {
                    player.setRemainedTime(player.getRemainedTime() - timestampDiff);
                    isEnded = false;
                    break;
                }
            }

            if (player.getRepeat() == 1) {
                isFirstLoop = 0;
            }

            if (player.getRepeat() == 0) {
                isFinished = true;
            }

            // if the playlist is ended
            if (isEnded) {
                if (player.getRepeat() == 1) {
                    player.setPlaylistState(playlistSongs.get(0));
                    playlistSongs.get(0).setPlayed(true);
                } else {
                    player.setPlaylistState(null);
                    playlist.setPlayed(false);

                    // add an ad at end of song
                    if (!player.getAds().isEmpty() && !user.isPremium()) {
                        user.removeAd();
                    }

                    player.resetPlayer();
                }
            } else {
                player.setPlaylistState(currSong);
                currSong.setPlayed(true);
                if (player.getRepeat() == 1) {
                    isFinished = true;
                }
            }

        }
        player.setPrevCommandTimestamp(timestamp);
    }

    private void updatePlaylist(final Playlist playlist) {
        ArrayList<Song> playlistSongs;
        if (player.getShuffle()) {
            playlistSongs = playlist.getShuffledSongs();
        } else {
            playlistSongs = playlist.getSongs();
        }

        if (playlistSongs.isEmpty()) {
            player.setPrevCommandTimestamp(timestamp);
            return;
        }

        Integer timestampDiff = timestamp - player.getPrevCommandTimestamp();

        if (player.isAdUpdate()) {
            if (timestampDiff < AD_DURATION) {
                return;
            }
            player.setPrevCommandTimestamp(player.getPrevCommandTimestamp() + AD_DURATION);
            timestampDiff = timestamp - player.getPrevCommandTimestamp();

            user.getWrapped().updateSongListens(player.getPlaylistState());
            player.getPlaylistState().setPlayed(true);
            player.setAdUpdate(false);
        }

        // if the difference between the timestamps is smaller
        // than the remained size of the song,
        // update the remained size and return
        if (timestampDiff < player.getRemainedTime()) {
            player.setRemainedTime(player.getRemainedTime() - timestampDiff);
            player.setPrevCommandTimestamp(timestamp);
        } else {
            // if the song has finished
            // and no repeat, reach the next song
            if (player.getRepeat() == 0) {
                iteratePlaylistSongs(playlist, playlistSongs, timestampDiff);

            } else if (player.getRepeat() == 1) { // repeat all
                iteratePlaylistSongs(playlist, playlistSongs, timestampDiff);
            } else { // repeat current song
                Song currSong = player.getPlaylistState();
                Integer currSongDuration = player.getPlaylistState().getDuration();
                timestampDiff -= player.getRemainedTime();
                Integer songRepeatTimes = Math.floorDiv(timestampDiff, currSongDuration);
                timestampDiff -= (songRepeatTimes * currSongDuration);

                for (int i = 0; i < songRepeatTimes; ++i) {
                    user.getWrapped().updateSongListens(currSong);
                }
                if (timestampDiff != 0) {
                    user.getWrapped().updateSongListens(currSong);
                }

                player.setRemainedTime(currSongDuration - timestampDiff);
                player.setPrevCommandTimestamp(timestamp);
            }
        }
    }

    private void iterateAlbumSongs(final Album album, final ArrayList<Song> albumSongs,
                                      final int timestampDiff) {
        boolean isFinished = false;
        int isFirstLoop = 1;
        while (!isFinished) {
            Song currSong = player.getAlbumState();
            currSong.setPlayed(false);
            int currSongIndex = albumSongs.indexOf(currSong);

            boolean isEnded = true;

            for (int i = currSongIndex + isFirstLoop; i < albumSongs.size(); ++i) {
                // check ads
                if (!player.getAds().isEmpty() && !user.isPremium()) {
                    int realDiff = timestampDiff - player.getRemainedTime();
                    // a full ad cannot be played
                    if (realDiff < AD_DURATION) {
                        player.setPrevCommandTimestamp(timestamp - realDiff);
                        player.setAdUpdate(true);

                        user.removeAd();

                        player.setRemainedTime(albumSongs.get(i).getDuration());
                        player.setAlbumState(albumSongs.get(i));
                        return;
                    }

                    user.removeAd();

                    player.setRemainedTime(player.getRemainedTime() + AD_DURATION);
                }

                currSong = albumSongs.get(i);

                user.getWrapped().updateSongListens(currSong);

                player.setRemainedTime(player.getRemainedTime() + currSong.getDuration());
                if (timestampDiff < player.getRemainedTime()) {
                    player.setRemainedTime(player.getRemainedTime() - timestampDiff);
                    isEnded = false;
                    break;
                }
            }

            if (player.getRepeat() == 1) {
                isFirstLoop = 0;
            }

            if (player.getRepeat() == 0) {
                isFinished = true;
            }

            // if the album is ended
            if (isEnded) {
                if (player.getRepeat() == 1) {
                    player.setAlbumState(albumSongs.get(0));
                    albumSongs.get(0).setPlayed(true);
                } else {
                    player.setAlbumState(null);
                    album.setPlayed(false);

                    // add an ad at end of song
                    if (!player.getAds().isEmpty() && !user.isPremium()) {
                        user.removeAd();
                    }

                    player.resetPlayer();
                }
            } else {
                player.setAlbumState(currSong);
                currSong.setPlayed(true);
                if (player.getRepeat() == 1) {
                    isFinished = true;
                }
            }
        }
        player.setPrevCommandTimestamp(timestamp);
    }

    private void updateAlbum(final Album album) {
        ArrayList<Song> albumSongs;
        if (player.getShuffle()) {
            albumSongs = album.getShuffledSongs();
        } else {
            albumSongs = album.getSongs();
        }

        if (albumSongs.isEmpty()) {
            player.setPrevCommandTimestamp(timestamp);
            return;
        }

        Integer timestampDiff = timestamp - player.getPrevCommandTimestamp();

        if (player.isAdUpdate()) {
            if (timestampDiff < AD_DURATION) {
                return;
            }
            player.setPrevCommandTimestamp(player.getPrevCommandTimestamp() + AD_DURATION);
            timestampDiff = timestamp - player.getPrevCommandTimestamp();

            user.getWrapped().updateSongListens(player.getAlbumState());
            player.getAlbumState().setPlayed(true);
            player.setAdUpdate(false);
        }

        // if the difference between the timestamps is smaller
        // than the remained size of the song,
        // update the remained size and return
        if (timestampDiff < player.getRemainedTime()) {
            player.setRemainedTime(player.getRemainedTime() - timestampDiff);
            player.setPrevCommandTimestamp(timestamp);
        } else {
            // if the song has finished
            // and no repeat, reach the next song
            if (player.getRepeat() == 0) {
                iterateAlbumSongs(album, albumSongs, timestampDiff);
            } else if (player.getRepeat() == 1) { // repeat all
                iterateAlbumSongs(album, albumSongs, timestampDiff);
            } else { // repeat current song
                Song currSong = player.getAlbumState();
                Integer currSongDuration = player.getAlbumState().getDuration();
                timestampDiff -= player.getRemainedTime();
                Integer songRepeatTimes = Math.floorDiv(timestampDiff, currSongDuration);
                timestampDiff -= (songRepeatTimes * currSongDuration);

                for (int i = 0; i < songRepeatTimes; ++i) {
                    user.getWrapped().updateSongListens(currSong);
                }
                if (timestampDiff != 0) {
                    user.getWrapped().updateSongListens(currSong);
                }

                player.setRemainedTime(currSongDuration - timestampDiff);
                player.setPrevCommandTimestamp(timestamp);
            }
        }
    }

    @Override
    public void visit(final Song song) {
        updateSong(song);
    }

    @Override
    public void visit(final Podcast podcast) {
        updatePodcast(podcast);
    }

    @Override
    public void visit(final Playlist playlist) {
        updatePlaylist(playlist);
    }

    @Override
    public void visit(final Album album) {
        updateAlbum(album);
    }
}
