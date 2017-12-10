package edu.illinois.finalproject;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.StreamSupport;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.Track;

import static edu.illinois.finalproject.MainSignInActivity.getAccessToken;

class DJBoxUtils extends AppCompatActivity {

    static Map<Track, Integer> songs;

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

    static SpotifyService getSpotifyService() {
        SpotifyApi api = new SpotifyApi();

        final String accessToken = getAccessToken();
        api.setAccessToken(accessToken);
        SpotifyService spotify = api.getService();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        return spotify;
    }

    static DatabaseReference roomsRef;
    static DataSnapshot roomsSnapshot;

    /**
     * Sets DataSnapshot to a snapshot of the "Rooms" reference
     *
     * @throws Exception if writeSignal throws InterruptedException
     */
    static void setRoomsSnapshot() throws Exception {
        final CountDownLatch writeSignal = new CountDownLatch(1);
        roomsRef = FirebaseDatabase.getInstance().getReference("Rooms");

        roomsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                roomsSnapshot = dataSnapshot;
                writeSignal.countDown();
            }
            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

        writeSignal.await(2, TimeUnit.SECONDS);
    }

    /**
     * Return number of active rooms
     *
     * @return              number of active rooms
     * @throws Exception    if setRoomSnapshot() does not work properly
     */
    static int getNumActiveRooms() throws Exception {
        // makes sure the snapshot of the room is updated
        setRoomsSnapshot();
        return (int) roomsSnapshot.getChildrenCount();
    }

    /**
     * Adds a room of a specified roomID to the Firebase database
     *
     * @param roomID         String representing room ID
     * @param djID           String representing the room's DJ
     * @param name           String representing the name of room
     * @param radius         Integer representing the size of the room
     * @param latitude       Double representing the horizontal coordinates of room respect to Earth
     * @param longitude      Double representing the vertical coordinates of room respect to Earth
     * @param pass           String representing the password required to join room;
     *                       null if public room
     * @param songIDs        String representing songs to add to playlist
     * @param songsWithLikes variable String[] representing songs tha are liked and their users
     * @throws Exception     if addSongs() is not properly executed
     */
    static void createRoom(final String roomID, final String djID, final String name,
                           final Integer radius, final Double latitude, final Double longitude,
                           final String pass, final String songIDs,
                           final String...songsWithLikes) throws Exception {

        Map<String, Object> roomVals = new HashMap<>();

        roomVals.put("DJ", djID);
        roomVals.put("Name", name);
        roomVals.put("Radius", radius);
        roomVals.put("lat", latitude);
        roomVals.put("long", longitude);

        // if a password exists, then access is set to private, else we know it's public
        if (pass != null) {
            roomVals.put("Access", "Private");
            roomVals.put("Pass", pass);
        } else {
            roomVals.put("Access", "Public");
        }

        createPlaylist(roomID, songIDs);
        addSongs(roomID, songsWithLikes);

        // create a roomID with proper room values
        roomsRef.child(roomID).updateChildren(roomVals);
    }

    /**
     * Adds a playlist containing songs within a specified room
     *
     * @param roomID        String representing the room ID
     * @param songIDs       String representing the songIDs separated by commas
     *                      songIDs: "{songID1}, {songID2}, {songID3}, ..."
     */
    static void createPlaylist(final String roomID, final String songIDs) {
        // split all the songIDs String into an array of Strings containing each songID
        String[] songIdsArray = songIDs.split(",");
        Map<String, Object> playlist = new HashMap<>();

        // add each songID to a playlist map {String: index, String: songID}
        for (int index = 0; index < songIdsArray.length; index++) {
            playlist.put(String.valueOf(index), songIdsArray[index]);
        }

        // add a playlist containing the songIDs
        roomsRef.child(roomID).child("PlaylistItem").updateChildren(playlist);
    }

    /**
     * Adds songs to a specified room
     *
     * @param roomID        String representing the room ID
     * @param songsInfo     variable String[] representing songs tha are liked and their users
     *                      songsInfo: {"{songID1}, {userID1}, {userID2}", "songID2"}
     * @throws Exception    if setRoomSnapshot() does not work properly
     */
    static void addSongs(final String roomID, final String...songsInfo) throws Exception {
        setRoomsSnapshot();
        for (String song : songsInfo) {
            // split String song into an array of Strings containing songID and users
            // that liked the song
            String[] attributes = song.split(",");
            String spotifyID = attributes[0];

            DatabaseReference roomRef = roomsRef.child(roomID);

            int numSongsInPlaylist = getNumSongsInPlaylist(roomID);

            // gets values of "PlaylistItem" snapshot as an Arraylist of Strings to represent the songs
            // in the playlist
            List<String> playlist =
                    getSnapshotValsList(roomsSnapshot.child(roomID).child("Playlist"));

            // adds song to "PlaylistItem" if it does not exist there already
            if (!playlist.contains(spotifyID)) {
                roomRef.child("Playlist").child(String.valueOf(numSongsInPlaylist))
                        .setValue(spotifyID);
            }

            Map<String, Object> users = new HashMap<>();

            // adds users that liked the song to a Map: {String: index, String: user ID}
            if (attributes.length > 1) {
                for (int index = 1; index < attributes.length; index++) {
                    users.put(String.valueOf(index - 1), attributes[index]);
                }
            }

            // add users that liked song to song within "Songs"
            roomRef.child("Songs").child(spotifyID).updateChildren(users);
        }
    }

    /**
     * Removes the last song in a playlist of the specified room ID
     *
     * @param roomID        String representing the room ID
     * @throws Exception    if setRoomsSnapshot does not work properly
     */
    static void removeLastSongInPlaylist(final String roomID) throws Exception {
        setRoomsSnapshot();

        // contains the key of the last song in playlist in database
        int lastSongKey = getNumSongsInPlaylist(roomID) - 1;

        // only done if playlist exists
        if (lastSongKey >= 0) {
            // gets spotifyID of last song in playlist
            String spotifyID = roomsSnapshot.child(roomID).child("PlaylistItem")
                    .child(String.valueOf(lastSongKey)).getValue(String.class);

            // removes the last song in playlist
            roomsRef.child(roomID).child("PlaylistItem")
                    .child(String.valueOf(lastSongKey)).removeValue();

            // song should not exist in "Songs" if it does not exist in the playlist
            roomsRef.child(roomID).child("Songs").child(spotifyID).removeValue();
        }
    }

    /**
     * Returns number of songs in playlist of specified room
     *
     * @param roomID        String representing the room ID
     * @return              number of songs in playlist of specified room
     * @throws Exception    if setRoomsSnapshot() does not work correctly
     */
    static int getNumSongsInPlaylist(final String roomID) throws Exception {
        setRoomsSnapshot();
        return (int) roomsSnapshot.child(roomID).child("PlaylistItem").getChildrenCount();
    }

    @TargetApi(Build.VERSION_CODES.N)
    static String[] getRoomsAttributeVals(String roomAttribute) {
        String[] roomIDs = getSnapshotKeysArray(roomsSnapshot);

        return Arrays.stream(roomIDs).map(roomID -> roomsSnapshot.child(roomID)
                .child(roomAttribute).getValue(String.class)).toArray(String[]::new);
    }

    /**
     * Returns an List of values of Datasnapshot of an array of Strings
     *
     * @param arraySnapshot     DataSnapshot representing the snapshot of array of Strings
     * @return                  an List of values of DataSnapshot of an array of Strings
     */
    static List<String> getSnapshotValsList(final DataSnapshot arraySnapshot) {
        List<String> stringList = new ArrayList<>();

        // adds each value of arraySnapshot to stringList
        for (DataSnapshot val : arraySnapshot.getChildren()) {
            stringList.add(val.getValue(String.class));
        }

        return stringList;
    }

    /**
     * Returns a String[] array containing the keys of an array
     *
     * @param arraySnapshot     DataSnapshot representing the snapshot of an array of sub-snapshots
     * @return                  a String[] array containing the keys of an array
     */
    @TargetApi(Build.VERSION_CODES.N)
    static String[] getSnapshotKeysArray(final DataSnapshot arraySnapshot) {
        // converts snapshot's children of type Iterable<String> to a String[]
        return StreamSupport.stream(arraySnapshot.getChildren().spliterator(), false)
                .map(DataSnapshot::getKey).toArray(String[]::new);
    }

//    static void updateSongs(ListView listView, User userType, Context context) {
//
////                for (DataSnapshot trackSnap : roomSnapshot.child("Playlist").getChildren()) {
////                    String trackID = trackSnap.getValue(String.class);
////                    Track track = spotify.getTrack(trackID);
////                    int numLikesForTrack = getNumLikesForSong(roomSnapshot, trackID);
////                    songs.put(track, numLikesForTrack);
//
//        songs = new HashMap<>();
//
//        roomRef.addValueEventListener(new ValueEventListener() {
//            @TargetApi(Build.VERSION_CODES.N)
//            @Override
//            public void onDataChange(DataSnapshot roomSnapshot) {
//                SpotifyService spotify = getSpotifyService();
//
//                // Searches each trackID in Iterable snapshot, gets associated Spotify Track for it,
//                // and creates a list of type Track containing each of these tracks
//
//                StreamSupport.stream(roomSnapshot.getChildren().spliterator()
//                        , false).forEach(trackID -> songs.put(spotify.getTrack(trackID.getValue(String.class))
//                        , getNumLikesForSong(roomSnapshot, trackID.getValue(String.class))));
//
//                songs.put(spotify.getTrack("4bMIRaesSZwIldFWCGs96S"), 0);
//                songs.put(spotify.getTrack("6vtLtrPHZAo5OUy1Sk9BTH"), 0);
//                songs.put(spotify.getTrack("3CVPyuuD6HxWXgPbbGqbg6"), 0);
//                songs.put(spotify.getTrack("4PZzEAYfBb26M5Fhm8xbZd"), 0);
//                songs.put(spotify.getTrack("1C50sKrPuWp8mkSC0GA6DW"), 0);
//                songs.put(spotify.getTrack("7mlJpcVvVRzgGwkL3GUgY3"), 0);
//                songs.put(spotify.getTrack("1yzbqrSF0vuaO7nuKYSd6f"), 0);
//                songs.put(spotify.getTrack("5JqXtvd8bAyyPTjaK6cSDH"), 0);
//                songs.put(spotify.getTrack("1vcAHEXL5Cl9TUk0ESvWnN"), 0);
//                songs.put(spotify.getTrack("7q8jJ1XZEliBakZSUMFde5"), 0);
//
//                displaySongs(listView, userType, context);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//            }
//        });
//    }

//    static void displaySongs(ListView listView, User userType, Context context) {
//        List<SimpleTrack> songItems = new ArrayList<>();
//
//        for (Track track : songs.keySet()) {
//            songItems.add(new SimpleTrack(track.id, track.name, getArtistsAsString(track.artists)
//                    , getDurationAsString(track.duration_ms), songs.get(track)
//                    , track.album.images.get(0).url));
//        }
//
//        ArrayAdapter<SimpleTrack> songAdapter;
//        if (userType == User.DISC_JOCKEY)
//            songAdapter = new DJSongAdapter(context, songItems);
//        else {
//            songAdapter = new AudienceSongAdapter(context, songItems);
//        }
//
//        listView.setAdapter(songAdapter);
//    }

    static String getDurationAsString(long durationMs) {
        int durationInMins = (int) durationMs / 60000;
        int durationInSecs = (int) durationMs % 60000 / 1000;

        return durationInMins + ":" + ((durationInSecs < 10) ? ("0" + durationInSecs) : durationInSecs);
    }

    static String getArtistsAsString(List<ArtistSimple> artistList) {
        StringBuilder str = new StringBuilder();

        for (ArtistSimple artist : artistList) {
            str.append(artist.name).append(", ");
        }

        str.setLength(str.length() - 2);
        return str.toString();
    }

    /**
     * Returns number of likes for a song in a specified room
     *
     * @param roomID        String representing the room ID
     * @param trackID       String representing the Spotify track ID
     * @return              number of likes for a song in the room
     * @throws Exception    if setRoomsSnapshot() does not work properly
     */
    static int getNumLikesForSong(String roomID, String trackID) {
        DatabaseReference trackIDRef = FirebaseDatabase.getInstance().getReference("Rooms")
                .child(roomID).child("likes").child(trackID);

        int[] num = new int[1];

        trackIDRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                num[0] = (int) dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return num[0];
    }

    static String getTrackDuration(long durationMs) {
        int durationInMins = (int) durationMs / 60000;
        int durationInSecs = (int) durationMs % 60000 / 1000;

        return durationInMins + ":" + ((durationInSecs < 10) ? ("0" + durationInSecs) : durationInSecs);
    }
}
