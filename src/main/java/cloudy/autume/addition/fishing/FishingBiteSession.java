package cloudy.autume.addition.fishing;

/** Prevents one received bite marker from replaying every client tick. */
final class FishingBiteSession {
    private int hookEntityId = Integer.MIN_VALUE;
    private boolean played;

    boolean shouldPlay(int currentHookEntityId, boolean biteMarkerVisible) {
        if (hookEntityId != currentHookEntityId) {
            hookEntityId = currentHookEntityId;
            played = false;
        }
        if (!biteMarkerVisible || played) return false;
        played = true;
        return true;
    }

    void reset() {
        hookEntityId = Integer.MIN_VALUE;
        played = false;
    }
}
