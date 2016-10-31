package com.boorime.radio;

import android.support.annotation.NonNull;

public class Metadata {

    @NonNull
    private String artistName;

    @NonNull
    private String trackName;

    public Metadata(@NonNull String artistName, @NonNull String trackName) {
        this.artistName = artistName;
        this.trackName = trackName;
    }

    public void setArtistName(@NonNull String artistName) {
        this.artistName = artistName;
    }

    @NonNull
    public String getArtistName() {
        return artistName;
    }

    public void setTrackName(@NonNull String trackName) {
        this.trackName = trackName;
    }

    @NonNull
    public String getTrackName() {
        return trackName;
    }
}
