package columns.model;

import columns.model.kernel.Platform;
import columns.model.kernel.RandomGenerator;
import columns.model.kernel.Screen;

/**
 * Fake Platform where the test controls time, events, and key state.
 * delay() does not actually sleep — it advances the internal clock
 * by the requested duration so that time-based loops progress.
 */
class FakePlatform implements Platform {

    private long currentTime = 0;
    private long tc = 0;
    private boolean keyPressed = false;
    private GameEvent pendingEvent = GameEvent.NONE;
    private final FakeScreen screen = new FakeScreen();
    private RandomGenerator random;

    // Records how many times delay was called and total delay requested
    int delayCallCount = 0;
    long totalDelayRequested = 0;

    FakePlatform(RandomGenerator random) {
        this.random = random;
    }

    /** Advance the fake clock by the given amount. */
    void advanceTime(long millis) {
        currentTime += millis;
    }

    /** Set a pending event that getEvent() will return once. */
    void queueEvent(GameEvent event) {
        this.pendingEvent = event;
        this.keyPressed = true;
    }

    @Override
    public void delay(long t) {
        delayCallCount++;
        totalDelayRequested += t;
        // Advance time so processUserEventsIfAny loop can exit
        currentTime += t;
    }

    @Override
    public long currentTime() {
        return currentTime;
    }

    @Override
    public boolean isKeyPressed() {
        return keyPressed;
    }

    @Override
    public void setKeyPressed(boolean isKeyPressed) {
        this.keyPressed = isKeyPressed;
    }

    @Override
    public Screen getScreen() {
        return screen;
    }

    @Override
    public long getTc() {
        return tc;
    }

    @Override
    public void setTc(long time) {
        this.tc = time;
    }

    @Override
    public int getKeyPressed() {
        return 0;
    }

    @Override
    public GameEvent getEvent() {
        GameEvent e = pendingEvent;
        pendingEvent = GameEvent.NONE;
        keyPressed = false;
        return e;
    }

    @Override
    public RandomGenerator getRandomGenerator() {
        return random;
    }
}
