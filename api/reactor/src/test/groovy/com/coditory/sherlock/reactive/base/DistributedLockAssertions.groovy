package com.coditory.sherlock.reactive.base


import com.coditory.sherlock.reactive.ReactorDistributedLock

import java.time.Duration

class DistributedLockAssertions {
  static assertAlwaysOpenedLock(ReactorDistributedLock lock, String lockId = lock.id) {
    assertSingleStateLock(lock, lockId, true)
  }

  static assertAlwaysClosedLock(ReactorDistributedLock lock, String lockId = lock.id) {
    assertSingleStateLock(lock, lockId, false)
  }

  private static assertSingleStateLock(ReactorDistributedLock lock, String lockId, boolean expectedResult) {
    assert lock.id == lockId
    assert lock.acquire().block().locked == expectedResult
    assert lock.acquire(Duration.ofHours(1)).block().locked == expectedResult
    assert lock.acquireForever().block().locked == expectedResult
    assert lock.release().block().unlocked == expectedResult
    return true
  }
}
