package com.boorime.radio.player;

import com.boorime.radio.model.Metadata;

public class MediaPlayerEvent {

    public static class Play {}

    public static class Pause {}

    public static class UpdateMetadata {

        private Metadata mMetadata;

        public UpdateMetadata(Metadata metadata) {
            this.mMetadata = metadata;
        }

        public Metadata getMetadata() {
            return mMetadata;
        }

    }

}
