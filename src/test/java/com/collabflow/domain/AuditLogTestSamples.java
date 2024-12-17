package com.collabflow.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class AuditLogTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static AuditLog getAuditLogSample1() {
        return new AuditLog().id(1L).entityId(1L);
    }

    public static AuditLog getAuditLogSample2() {
        return new AuditLog().id(2L).entityId(2L);
    }

    public static AuditLog getAuditLogRandomSampleGenerator() {
        return new AuditLog().id(longCount.incrementAndGet()).entityId(longCount.incrementAndGet());
    }
}
