package me.jetp250.asteroids.track;

import me.jetp250.asteroids.game.rendering.ItemFrameDisplay;

public final class Track {
    private final ItemFrameDisplay display;
    private final int id;

    public Track(ItemFrameDisplay display, TrackIdCounter idProvider) {
        this.display = display;
        this.id = idProvider.getNewTrackID();
    }

    public int getId() {
        return id;
    }

    public ItemFrameDisplay getDisplay() {
        return display;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Track)) {
            return false;
        }
        return id == ((Track)obj).id;
    }
}
