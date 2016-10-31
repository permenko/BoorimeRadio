package com.boorime.radio.EventBus;

public class MediaPlayerStateEvent {
    private int state;

    public MediaPlayerStateEvent(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }
}
