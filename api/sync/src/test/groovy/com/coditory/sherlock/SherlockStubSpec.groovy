package com.coditory.sherlock


import spock.lang.Specification

import static com.coditory.sherlock.DistributedLockMock.lockStub
import static com.coditory.sherlock.base.DistributedLockAssertions.assertAlwaysClosedLock
import static com.coditory.sherlock.base.DistributedLockAssertions.assertAlwaysOpenedLock

class SherlockStubSpec extends Specification {
  def "should create sherlock returning always opened locks"() {
    given:
      String lockId = "some-lock"
      Sherlock sherlock = SherlockStub.withReleasedLocks()

    expect:
      assertAlwaysOpenedLock(sherlock.createLock(lockId), lockId)
      assertAlwaysOpenedLock(sherlock.createReentrantLock(lockId), lockId)
      assertAlwaysOpenedLock(sherlock.createOverridingLock(lockId), lockId)
  }

  def "should create sherlock returning always closed locks"() {
    given:
      String lockId = "some-lock"
      Sherlock sherlock = SherlockStub.withAcquiredLocks()

    expect:
      assertAlwaysClosedLock(sherlock.createLock(lockId), lockId)
      assertAlwaysClosedLock(sherlock.createReentrantLock(lockId), lockId)
      assertAlwaysClosedLock(sherlock.createOverridingLock(lockId), lockId)
  }

  def "should create sherlock returning closed locks by default and opened lock for specific id"() {
    given:
      String lockId = "some-lock"
      Sherlock sherlock = SherlockStub.withAcquiredLocks()
        .withLock(lockStub(lockId, true))
    expect:
      sherlock.createLock("other-lock").acquire() == false
    and:
      sherlock.createLock(lockId).acquire() == true
  }
}
