package com.boorime.radio.EventBus;

import com.boorime.radio.Metadata;

public class UpdateMetadataEvent {
    private Metadata metadata;

    public UpdateMetadataEvent(Metadata metadata) {
        this.metadata = metadata;
    }

    public Metadata getMetadata() {
        return metadata;
    }
}
