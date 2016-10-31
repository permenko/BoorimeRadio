package com.boorime.radio.EventBus;

public class UpdateFavoriteEvent {
    private String metadata;
    private boolean isFavorite;

    public UpdateFavoriteEvent(String metadata, boolean isFavorite) {
        this.metadata = metadata;
        this.isFavorite = isFavorite;
    }

    public String getSong() {
        return metadata;
    }

    public boolean getIsFavorite() {
        return isFavorite;
    }
}
