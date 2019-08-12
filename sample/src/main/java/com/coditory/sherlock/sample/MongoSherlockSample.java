package com.coditory.sherlock.sample;

import com.coditory.sherlock.DistributedLock;
import com.coditory.sherlock.Sherlock;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import static com.coditory.sherlock.MongoSherlockBuilder.mongoSherlock;

public class MongoSherlockSample {
  private static Sherlock createSherlock() {
    return createSherlock("localhost");
  }

  private static Sherlock createSherlock(String ownerId) {
    String database = "sherlock";
    MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017/" + database);
    MongoCollection<Document> collection = mongoClient
      .getDatabase("sherlock")
      .getCollection("locks");
    return mongoSherlock()
      .withLocksCollection(collection)
      .build();
  }

  public static void main(String... args) throws InterruptedException {
    Sherlock sherlock = createSherlock();
    DistributedLock simpleLock = sherlock.createLock().withLockId("sample-lock").build();
    DistributedLock reentrantLock = sherlock.createReentrantLock("sample-acquire");
    DistributedLock overridingLock = sherlock.createOverridingLock("sample-acquire");
    reentrantLock.acquire();
    Thread.sleep(10_000);
    reentrantLock.acquire();
  }

  public static void main2() {
    Sherlock sherlock = createSherlock();
    DistributedLock lock = sherlock.createLock("sample-acquire");
    lock.acquireAndExecute(() -> System.out.println("Lock granted!"));
  }
}
