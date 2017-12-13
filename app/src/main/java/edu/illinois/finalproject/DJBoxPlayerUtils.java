package edu.illinois.finalproject;

import android.content.Context;
import android.widget.ToggleButton;

import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import static edu.illinois.finalproject.MainSignInActivity.getAccessToken;
import static edu.illinois.finalproject.SpotifyClient.CLIENT_ID;

public class DJBoxPlayerUtils {
    static Player mPlayer;
    static int currentTrackPosMs;

    static void usePlayButtonToPlaySong(Context context, ToggleButton playButton, String trackID) {
        setPlayer(context);
        playButton.setOnCheckedChangeListener((compoundButton, isPaused) -> {
            if (!isPaused) {
                mPlayer.playUri(null, "spotify:track:" + trackID, 0, currentTrackPosMs);
                currentTrackPosMs = (int) mPlayer.getPlaybackState().positionMs;
            } else {
                mPlayer.pause(null);
            }
        });
    }

    private static void setPlayer(final Context context) {
        Config playerConfig = new Config(context, getAccessToken(), CLIENT_ID);

        Spotify.getPlayer(playerConfig, context, new SpotifyPlayer.InitializationObserver() {
            @Override
            public void onInitialized(SpotifyPlayer spotifyPlayer) {
                mPlayer = spotifyPlayer;
                mPlayer.addConnectionStateCallback((ConnectionStateCallback) context);
                mPlayer.addNotificationCallback((Player.NotificationCallback) context);
            }

            @Override
            public void onError(Throwable throwable) {
            }
        });
    }
}
