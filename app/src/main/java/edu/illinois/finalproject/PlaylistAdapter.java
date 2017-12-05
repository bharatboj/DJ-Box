package edu.illinois.finalproject;

import android.content.Context;

import java.util.ArrayList;

public class PlaylistAdapter {
    PlaylistAdapter(Context context, ArrayList<String> names
            , ArrayList<String> owners, ArrayList<String> infos) {

        super(context, 0, names);
    }
}
