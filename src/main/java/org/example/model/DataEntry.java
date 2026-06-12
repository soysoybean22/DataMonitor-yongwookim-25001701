package org.example.model;

import java.time.Instant;

public class DataEntry {

    public enum Status { ACTIVE, STALE, ERROR }

    private final String id;
    private final String key;
    private volatile Object value;
    private volatile Status status;
    private volatile Instant updatedAt;
    private volatile long updateCount;

    public DataEntry(String id, String key, Object value) {
        this.id = id;
        this.key = key;
        this.value = value;
        this.status = Status.ACTIVE;
        this.updatedAt = Instant.now();
        this.updateCount = 1;
    }

    public synchronized void update(Object newValue, Status newStatus) {
        this.value = newValue;
        this.status = newStatus;
        this.updatedAt = Instant.now();
        this.updateCount++;
    }

    public String getId()           { return id; }
    public String getKey()          { return key; }
    public Object getValue()        { return value; }
    public Status getStatus()       { return status; }
    public Instant getUpdatedAt()   { return updatedAt; }
    public long getUpdateCount()    { return updateCount; }
}
