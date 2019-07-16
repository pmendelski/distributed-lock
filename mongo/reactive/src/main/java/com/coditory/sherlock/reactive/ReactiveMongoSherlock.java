package com.coditory.sherlock.reactive;

import com.coditory.sherlock.common.LockDuration;
import com.coditory.sherlock.common.OwnerId;
import com.mongodb.reactivestreams.client.MongoCollection;
import org.bson.Document;

import java.time.Clock;
import java.time.Duration;

import static com.coditory.sherlock.common.SherlockDefaults.DEFAULT_CLOCK;
import static com.coditory.sherlock.common.SherlockDefaults.DEFAULT_INSTANCE_ID;
import static com.coditory.sherlock.common.SherlockDefaults.DEFAULT_LOCK_DURATION;
import static com.coditory.sherlock.common.util.Preconditions.expectNonNull;

/**
 * Builds {@link ReactiveSherlock} that uses MongoDB for locking mechanism.
 */
public class ReactiveMongoSherlock {
  private MongoCollection<Document> collection;
  private LockDuration duration = DEFAULT_LOCK_DURATION;
  private OwnerId ownerId = DEFAULT_INSTANCE_ID;
  private Clock clock = DEFAULT_CLOCK;

  /**
   * @return new instance of the builder
   */
  public static ReactiveMongoSherlock builder() {
    return new ReactiveMongoSherlock();
  }

  private ReactiveMongoSherlock() {
    // deliberately empty
  }

  /**
   * @param collection mongo collection to be used for locking
   * @return the instance
   */
  public ReactiveMongoSherlock withMongoCollection(MongoCollection<Document> collection) {
    this.collection = expectNonNull(collection, "Expected non null mongoClient");
    return this;
  }

  /**
   * @param duration how much time a lock should be active. When time passes lock is expired and
   *     becomes released. Default: {@link com.coditory.sherlock.common.SherlockDefaults#DEFAULT_LOCK_DURATION}
   * @return the instance
   */
  public ReactiveMongoSherlock withLockDuration(Duration duration) {
    this.duration = LockDuration.of(duration);
    return this;
  }

  /**
   * @param ownerId owner id most often should be a unique application instance identifier.
   *     Default: {@link com.coditory.sherlock.common.SherlockDefaults#DEFAULT_INSTANCE_ID}
   * @return the instance
   */
  public ReactiveMongoSherlock withOwnerId(String ownerId) {
    this.ownerId = OwnerId.of(ownerId);
    return this;
  }

  /**
   * @param clock time provider used in locking mechanism. Default: {@link
   *     com.coditory.sherlock.common.SherlockDefaults#DEFAULT_CLOCK}
   * @return the instance
   */
  public ReactiveMongoSherlock withClock(Clock clock) {
    this.clock = expectNonNull(clock, "Expected non null clock");
    return this;
  }

  /**
   * @return sherlock instance
   * @throws IllegalArgumentException when some required values are missing
   */
  public <T> T build(ReactiveSherlockApiWrapper<T> apiWrapper) {
    return apiWrapper.wrapApi(build());
  }

  public ReactiveSherlockWithConnector build() {
    expectNonNull(collection, "Expected non null collection");
    ReactiveMongoDistributedLockConnector connector = new ReactiveMongoDistributedLockConnector(
        collection, clock);
    return new ReactiveSherlockWithConnector(connector, ownerId, duration);
  }
}
