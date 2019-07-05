package com.coditory.xlock.api;

import com.coditory.xlock.common.InstanceId;
import com.coditory.xlock.common.LockId;
import com.coditory.xlock.common.driver.DistributedLockDriver;
import com.coditory.xlock.common.driver.LockRequest;

import java.time.Duration;

import static com.coditory.xlock.common.util.Preconditions.expectNonNull;

class DistributedSingleEntrantLock implements DistributedLock {
  private final LockId lockId;
  private final InstanceId instanceId;
  private final Duration duration;
  private final DistributedLockDriver driver;

  DistributedSingleEntrantLock(
      LockId lockId,
      InstanceId instanceId,
      Duration duration,
      DistributedLockDriver driver) {
    this.lockId = expectNonNull(lockId);
    this.instanceId = expectNonNull(instanceId);
    this.duration = expectNonNull(duration);
    this.driver = expectNonNull(driver);
  }

  @Override
  public boolean lock() {
    return tryLock(duration);
  }

  @Override
  public boolean lock(Duration duration) {
    expectNonNull(duration, "Expected non null duration");
    return tryLock(duration);
  }

  @Override
  public boolean lockInfinitely() {
    return tryLock(null);
  }

  private boolean tryLock(Duration duration) {
    LockRequest lockRequest = new LockRequest(lockId, instanceId, duration);
    return driver.lock(lockRequest);
  }

  @Override
  public boolean unlock() {
    return driver.unlock(lockId, instanceId);
  }
}
