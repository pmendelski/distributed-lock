package com.coditory.sherlock.migrator;

import com.coditory.sherlock.DistributedLock;
import com.coditory.sherlock.Sherlock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static com.coditory.sherlock.util.Preconditions.expectNonEmpty;

/**
 * Migration mechanism based on {@link Sherlock} distributed locks.
 * <p>
 * It can be used to perform one way database migrations.
 * <p>
 * Migration rules:
 * <ul>
 * <li> migrations must not run in parallel
 * <li> migration change sets are applied in order
 * <li> migration change sets must be run only once per all migrations
 * <li> migration process stops after first change set failure
 * </ul>
 */
public final class SherlockMigrator {
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private final List<MigrationChangeSet> migrationChangeSets = new ArrayList<>();
  private final String migrationId;
  private final Sherlock sherlock;
  private final DistributedLock migrationLock;

  /**
   * @param migrationId id used as lock id for the whole migration process
   * @param sherlock sherlock used to manage migration locks
   */
  public SherlockMigrator(String migrationId, Sherlock sherlock) {
    this.migrationId = migrationId;
    this.sherlock = sherlock;
    this.migrationLock = sherlock.createLock()
      .withLockId(migrationId)
      .withPermanentLockDuration()
      .withStaticUniqueOwnerId()
      .build();
  }

  /**
   * Adds change set to migration process.
   *
   * @param changeSetId unique change set id used. This is is used as a lock id in migration
   *   process.
   * @param changeSet change set action that should be run if change set was not already applied
   * @return the migrator
   */
  public SherlockMigrator addChangeSet(String changeSetId, Runnable changeSet) {
    expectNonEmpty(changeSetId, "Expected non empty changeSetId");
    DistributedLock changeSetLock = createChangeSetLock(changeSetId);
    migrationChangeSets.add(new MigrationChangeSet(changeSetId, changeSetLock, changeSet));
    return this;
  }

  private DistributedLock createChangeSetLock(String migrationId) {
    return sherlock.createLock()
      .withLockId(migrationId)
      .withPermanentLockDuration()
      .withStaticUniqueOwnerId()
      .build();
  }

  /**
   * Runs the migration process.
   */
  public void migrate() {
    migrationLock.acquireAndExecute(this::runMigrations);
  }

  private void runMigrations() {
    logger.info("Starting migration: {}", migrationId);
    migrationChangeSets.forEach(MigrationChangeSet::execute);
    logger.info("Migration finished successfully: {}", migrationId);
  }

  private static class MigrationChangeSet {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String id;
    private final DistributedLock lock;
    private final Runnable action;

    MigrationChangeSet(String id, DistributedLock lock, Runnable action) {
      this.id = id;
      this.lock = lock;
      this.action = action;
    }

    void execute() {
      if (lock.acquire()) {
        logger.debug("Executing migration change set: {}", id);
        try {
          action.run();
          logger.info("Migration change set applied: {}", id);
        } catch (Throwable exception) {
          logger.warn(
            "Migration change set failure: {}. Stopping migration process. Fix problem and rerun the migration.",
            id, exception);
          lock.release();
          throw exception;
        }
      } else {
        logger.info("Migration change set skipped: {}. It was already applied", id);
      }
    }
  }
}