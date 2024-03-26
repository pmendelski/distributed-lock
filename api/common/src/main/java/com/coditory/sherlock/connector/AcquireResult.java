package com.coditory.sherlock.connector;

import java.util.Objects;
import java.util.function.Supplier;

public final class AcquireResult {
    public static AcquireResult of(boolean acquired) {
        return acquired ? ACQUIRED : NOT_ACQUIRED;
    }

    public static AcquireResult acquiredResult() {
        return ACQUIRED;
    }

    public static AcquireResult notAcquiredResult() {
        return NOT_ACQUIRED;
    }

    private static final AcquireResult ACQUIRED = new AcquireResult(true);
    private static final AcquireResult NOT_ACQUIRED = new AcquireResult(false);

    private final boolean acquired;

    private AcquireResult(boolean acquired) {
        this.acquired = acquired;
    }

    public boolean acquired() {
        return acquired;
    }

    public AcquireResult onNotAcquired(Runnable action) {
        if (!acquired) {
            action.run();
        }
        return this;
    }

    public AcquireResult onAcquired(Runnable action) {
        if (acquired) {
            action.run();
        }
        return this;
    }

    public <T> LockedActionResult<T> onAcquired(Supplier<T> action) {
        if (acquired) {
            T value = action.get();
            return LockedActionResult.acquiredResult(value);
        }
        return LockedActionResult.notAcquiredResult();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AcquireResult that = (AcquireResult) o;
        return acquired == that.acquired;
    }

    @Override
    public int hashCode() {
        return Objects.hash(acquired);
    }

    @Override
    public String toString() {
        return "AcquireResult{" +
            "acquired=" + acquired +
            '}';
    }
}
