package com.boorime.radio.screen.player;

import com.boorime.radio.player.MediaPlayerEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class PlayerPresenter {

    private PlayerView mView;

    PlayerPresenter(PlayerView view) {
        mView = view;
    }

    void init() {
        EventBus.getDefault().register(this);
        if (mView.isServiceRunning()) {
            mView.showPause();
        } else {
            mView.showPlay();
        }
    }

    void onAboutClick() {
        mView.openAboutScreen();
    }

    void onPlayPauseClick() {
        if (mView.isServiceRunning()) {
            mView.stopService();
        } else {
            mView.startService();
        }
    }

    void onShareClick() {
        mView.share();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MediaPlayerEvent.Play event) {
        mView.showPause();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MediaPlayerEvent.Pause event) {
        mView.showPlay();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MediaPlayerEvent.UpdateMetadata event) {
        mView.changeTitle(event.getMetadata().getArtistName(), event.getMetadata().getTrackName());
    }

    void release() {
        EventBus.getDefault().unregister(this);
    }
}
