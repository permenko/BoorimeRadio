package com.boorime.radio.screen.player;

import android.support.annotation.NonNull;

public interface PlayerView {

    boolean isServiceRunning();

    void startService();

    void stopService();

    void share();

    void showPlay();

    void showPause();

    void changeTitle(@NonNull String artistName, @NonNull String trackName);

    void openAboutScreen();
}
