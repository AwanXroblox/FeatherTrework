package uz.newgen.core;

import java.util.ArrayDeque;
import java.util.Deque;

/** Oxirgi 1 soniyadagi bosishlar soni (CPS). */
public final class ClickCounter {
    private final Deque<Long> clicks = new ArrayDeque<>();
    private boolean wasDown;

    /** Har tickda tugma holati bilan chaqiriladi; faqat bosilgan lahza hisoblanadi. */
    public void update(boolean down, long nowMillis) {
        if (down && !wasDown) clicks.addLast(nowMillis);
        wasDown = down;
        prune(nowMillis);
    }

    public int cps(long nowMillis) {
        prune(nowMillis);
        return clicks.size();
    }

    private void prune(long now) {
        while (!clicks.isEmpty() && now - clicks.peekFirst() > 1000) clicks.removeFirst();
    }
}
