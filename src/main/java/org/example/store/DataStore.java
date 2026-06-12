package org.example.store;

import org.example.model.DataEntry;
import org.example.model.DataEntry.Status;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class DataStore {

    private final ConcurrentHashMap<String, DataEntry> entries = new ConcurrentHashMap<>();
    private final AtomicLong totalUpdates = new AtomicLong(0);

    public DataEntry put(String id, String key, Object value) {
        DataEntry entry = new DataEntry(id, key, value);
        entries.put(id, entry);
        totalUpdates.incrementAndGet();
        return entry;
    }

    public boolean update(String id, Object newValue, Status status) {
        DataEntry entry = entries.get(id);
        if (entry == null) return false;
        entry.update(newValue, status);
        totalUpdates.incrementAndGet();
        return true;
    }

    public boolean remove(String id) {
        return entries.remove(id) != null;
    }

    public List<DataEntry> getAll() {
        return entries.values().stream()
                .sorted(Comparator.comparing(DataEntry::getUpdatedAt).reversed())
                .collect(Collectors.toList());
    }

    public Map<Status, Long> getStatusSummary() {
        Map<Status, Long> summary = new EnumMap<>(Status.class);
        for (Status s : Status.values()) summary.put(s, 0L);
        entries.values().forEach(e -> summary.merge(e.getStatus(), 1L, Long::sum));
        return summary;
    }

    public int size()               { return entries.size(); }
    public long getTotalUpdates()   { return totalUpdates.get(); }
}
