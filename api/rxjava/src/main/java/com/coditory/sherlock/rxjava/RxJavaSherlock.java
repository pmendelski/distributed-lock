package com.coditory.sherlock.rxjava;

import com.coditory.sherlock.reactive.ReactiveSherlock;

import java.time.Duration;

/**
 * Creates and manages reactive distributed locks with reactor api.
 */
public interface RxJavaSherlock {
  /**
   * Maps reactive sherlock to a one using RxJava API
   *
   * @param locks reactive locks to be wrapped in Reactor api
   * @return reactor version of sherlock locks
   */
  static RxJavaSherlock wrapReactiveSherlock(ReactiveSherlock locks) {
    return new RxJavaSherlockWrapper(locks);
  }

  /**
   * @return owner id is most often the application instance id
   */
  String getOwnerId();

  /**
   * @return the default lock duration
   */
  Duration getLockDuration();

  /**
   * Create a distributed lock. Lock expires after {@link RxJavaSherlock#getLockDuration()}.
   *
   * @param lockId the lock id
   * @return the lock
   * @see RxJavaSherlock#createLock(String, Duration)
   */
  RxJavaDistributedLock createLock(String lockId);

  /**
   * Create a lock. Created lock may be acquired only once by the same application instance:
   *
   * <pre>{@code
   * assert reentrantLock.acquire() == true
   * assert reentrantLock.acquire() == false
   * }</pre>
   *
   * @param lockId the lock id
   * @param duration after that time lock expires and is released
   * @return the lock
   */
  RxJavaDistributedLock createLock(String lockId, Duration duration);

  /**
   * Create a distributed reentrant lock. Lock expires after {@link RxJavaSherlock#getLockDuration()}.
   *
   * @param lockId the lock id
   * @return the reentrant lock
   * @see RxJavaSherlock#createReentrantLock(String, Duration)
   */
  RxJavaDistributedLock createReentrantLock(String lockId);

  /**
   * Create a distributed reentrant lock. Reentrant lock maybe acquired multiple times by the same
   * application instance:
   *
   * <pre>{@code
   * assert reentrantLock.acquire() == true
   * assert reentrantLock.acquire() == true
   * }</pre>
   *
   * @param lockId the lock id
   * @param duration after that time lock expires and is released
   * @return the reentrant lock
   */
  RxJavaDistributedLock createReentrantLock(String lockId, Duration duration);

  /**
   * Create a distributed overriding lock. Lock expires after {@link RxJavaSherlock#getLockDuration()}.
   *
   * @param lockId the lock id
   * @return the reentrant lock
   * @see RxJavaSherlock#createOverridingLock(String, Duration)
   */
  RxJavaDistributedLock createOverridingLock(String lockId);

  /**
   * Create a distributed overriding lock. Returned lock overrides lock state without checking if it
   * was released.
   *
   * @param lockId the lock id
   * @param duration after that time lock expires and is released
   * @return the reentrant lock
   */
  RxJavaDistributedLock createOverridingLock(String lockId, Duration duration);
}