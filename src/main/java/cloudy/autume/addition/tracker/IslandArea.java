package cloudy.autume.addition.tracker;

public enum IslandArea {
    NONE,
    DWARVEN_MINES,
    CRYSTAL_HOLLOWS,
    GLACITE_TUNNELS,
    MINESHAFT,
    TORRHUS_CANYON,
    CRITTER_SAFARI,
    CRIMSON_ISLE,
    THE_END;

    public boolean isMiningIsland() {
        return this == DWARVEN_MINES || this == CRYSTAL_HOLLOWS || this == GLACITE_TUNNELS || this == MINESHAFT;
    }
}
