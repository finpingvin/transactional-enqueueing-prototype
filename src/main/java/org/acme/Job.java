package org.acme;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.panache.common.Sort;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.jpa.AvailableHints;
import org.hibernate.type.SqlTypes;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

@Entity
public class Job extends PanacheEntity {
    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    public ZonedDateTime createdAt;

    @Column(nullable = false)
    public String jobType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false)
    public Map<String, String> jobData;

    @Transactional(Transactional.TxType.MANDATORY)
    public static List<Job> findNextBatch() {
        return find("from Job order by createdAt")
                .page(0, 5)
                // FOR UPDATE
                .withLock(LockModeType.PESSIMISTIC_WRITE)
                // SKIP LOCKED
                // With this we should be able to run parallel job enqueues.
                // Without this any parallel findNextBatch() would block until
                // this one is ready, but with SKIP LOCKED a parallel should just
                // skip the ones we pick and pick the next 5 ones (that are not locked).
                .withHint(AvailableHints.HINT_NATIVE_LOCK_MODE, LockMode.UPGRADE_SKIPLOCKED)
                .list();
    }
}
