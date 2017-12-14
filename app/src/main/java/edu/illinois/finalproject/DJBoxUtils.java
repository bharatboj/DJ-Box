package edu.illinois.finalproject;

import android.content.Context;
import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;

import java.util.HashMap;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.Track;

import static edu.illinois.finalproject.MainSignInActivity.getAccessToken;

class DJBoxUtils extends AppCompatActivity {

    /**
     * This function allows user to open an Activity from a different Activity
     *
     * @param packageContext    Context object containing context/information of the first activity
     *                          to leave
     * @param classToOpen       Class object containing the Activity to change screens to
     */
    static void openActivity(final Context packageContext, final Class classToOpen) {
        Intent intentToOpenActivity = new Intent(packageContext, classToOpen);
        packageContext.startActivity(intentToOpenActivity);
    }

    /**
     * Returns a SpotifyService object that allows user to make calls to the Spotify API with
     *
     * @return      a SpotifyService object that allows user to make calls to the Spotify API with
     */
    static SpotifyService getSpotifyService() {
        SpotifyApi api = new SpotifyApi();

        final String accessToken = getAccessToken();
        api.setAccessToken(accessToken);
        SpotifyService spotify = api.getService();

        // run on asynchronous thread
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        return spotify;
    }

    /**
     * Returns a String representing the names of all the artists for a song
     *
     * @param artistList    List of ArtistSimple objects representing the info about the artists
     * @return              a String representing the names of all the artists for a song
     */
    static String getArtistsAsString(List<ArtistSimple> artistList) {
        StringBuilder str = new StringBuilder();

        for (ArtistSimple artist : artistList) {
            str.append(artist.name).append(", ");
        }

        str.setLength(str.length() - 2);
        return str.toString();
    }

    /**
     * Returns the duration in milliseconds as a String
     *
     * @param durationMs    total duration in Milliseconds (used to represent track duration)
     * @return              the duration in milliseconds as a String
     */
    static String getTrackDuration(long durationMs) {
        int durationInMins = (int) durationMs / 60000;
        int durationInSecs = (int) durationMs % 60000 / 1000;

        return durationInMins + ":" + ((durationInSecs < 10) ? ("0" + durationInSecs) : durationInSecs);
    }

    /**
     * Returns a SimpleTrack object based on the information the Track object has
     *
     * @param track     Track object that represents the track to convert
     * @return          SimpleTrack object based on the information the Track object has
     */
    static SimpleTrack getSimpleTrack(Track track) {
        return new SimpleTrack(track.name, getArtistsAsString(track.artists)
                , getTrackDuration(track.duration_ms), (int) track.duration_ms, 0,
                new HashMap<>(), track.album.images.get(0).url);
    }

}
