package org.acme;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;

@Entity
public class FakeEnqueuedJob extends PanacheEntity {
    @Column(unique = true)
    public Long job_id;

    // This is supposed to simulate posting to an external
    // queue like SNS, where we are outside of the SQL
    // transaction guarantees. Models an at least once
    // delivery. If the transaction calling this one fails committing,
    // we might still send an event, and yet another event might be sent
    // in another batch.
    // BUT we separate the actual job that produced this with the enqueueing,
    // so the job that this service was expected to perform
    // did only occur once, but multiple events MIGHT be produced per work.
    // And we do guarantee that we won't rest until AT LEAST ONE event is sent.
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public static void enqueueJob(Job job) {
        FakeEnqueuedJob enqueuedJob = new FakeEnqueuedJob();
        enqueuedJob.job_id = job.id;
        enqueuedJob.persist();

        try {
            // Simulate latency to an external queue system like SNS.
            // It is super long, but the scheduler won't run any faster than once
            // per second.
            Thread.sleep(500);
        } catch (InterruptedException e) {
            System.out.println("FakeEnqueuedJob interrupted while sleeping");
        }
    }
}
