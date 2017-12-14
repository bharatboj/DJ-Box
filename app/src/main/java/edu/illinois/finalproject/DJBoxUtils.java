package edu.illinois.finalproject;

import android.content.Context;
import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * Initialize tracks to the tracks that are within the playlist of the room
     *
     * @param songsList       ListView object representing the View that displays all the songs
     *                        within the room
     * @param roomID          String representing the id of the room
     */
    static void displaySongs(String roomID, ListView songsList) {
        DatabaseReference roomRef = FirebaseDatabase.getInstance()
                .getReference("Rooms").child(roomID);

        // updates the current playlist every time there's a change in the playlist list of tracks
        roomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                updateQueue(songsList, dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /**
     * Updates all current tracks and populates ListView with new set of tracks
     *
     * @param songsList       ListView object representing the View that displays all the songs
     *                        within the room
     * @param roomSnapshot    DataSnapshot object representing the playlist of a particular room
     */
    private static void updateQueue(ListView songsList, DataSnapshot roomSnapshot) {
        // sorts all tracks except 1st two by number of likes
        // the first two don't get sorted because they contain currently
        // playing track and queued track
        Room updatedRoom = roomSnapshot.getValue(Room.class);
        // updatedRoom is never null from the audience side when using app
        List<Map.Entry<String, SimpleTrack>> sortedPlayOrder = updatedRoom.getUpdatedPlaylist(true);

        // Uses an Adapter to populate the ListView DJ Home with each PlaylistTrack
        DJSongAdapter playlistAdapter = new DJSongAdapter(this, sortedPlayOrder);
        songsList.setAdapter(playlistAdapter);
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
