package me.jetp250.asteroids.track;

public final class TrackIdCounter {
    private int lastId;

    public TrackIdCounter(int initialId) {
        this.lastId = initialId;
    }

    public int getNewTrackID() {
        return lastId++;
    }

}
