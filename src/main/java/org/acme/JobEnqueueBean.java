package org.acme;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class JobEnqueueBean {
    // Bounces up to 1 second when starting,
    // says no less than 1 second is supported
    @Scheduled(every="PT0.200S")
    @Transactional
    void enqueue() {
        Job.findNextBatch().forEach(job -> {
            // Enqueue job (could be SNS in real world scenario)
            FakeEnqueuedJob.enqueueJob(job);
            // Enqueue is complete, remove job from queue
            job.delete();
            // If transaction fails for any reason, it would just be
            // retried in another schedule.
            // We might have sent the message, but that is fine, our
            // policy is AT LEAST ONCE.
        });
    }
}
