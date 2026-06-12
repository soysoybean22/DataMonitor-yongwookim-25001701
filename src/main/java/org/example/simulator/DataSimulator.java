package org.example.simulator;

import org.example.model.DataEntry.Status;
import org.example.store.DataStore;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * PoC용 - 실제 시스템에서는 외부 데이터 소스로 교체
 */
public class DataSimulator {

    private static final List<String> KEYS = List.of(
            "cpu.usage", "memory.used", "disk.io", "network.in",
            "network.out", "db.connections", "cache.hit_rate", "queue.depth"
    );
    private static final List<String> STRING_VALUES = List.of(
            "OK", "WARN", "CRITICAL", "DEGRADED", "UNKNOWN"
    );

    private final DataStore store;
    private final Random random = new Random();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final List<String> registeredIds;

    public DataSimulator(DataStore store, int initialEntries) {
        this.store = store;
        this.registeredIds = new java.util.ArrayList<>();
        seedInitialData(initialEntries);
    }

    private void seedInitialData(int count) {
        for (int i = 0; i < count; i++) {
            String id = UUID.randomUUID().toString().substring(0, 8);
            String key = KEYS.get(i % KEYS.size());
            store.put(id, key + "[" + i + "]", randomValue(key));
            registeredIds.add(id);
        }
    }

    public void start() {
        // 기존 항목 업데이트
        scheduler.scheduleAtFixedRate(this::updateRandom, 300, 300, TimeUnit.MILLISECONDS);
        // 신규 항목 추가
        scheduler.scheduleAtFixedRate(this::addNew, 2, 4, TimeUnit.SECONDS);
        // 항목 제거
        scheduler.scheduleAtFixedRate(this::removeRandom, 5, 7, TimeUnit.SECONDS);
    }

    public void stop() {
        scheduler.shutdownNow();
    }

    private void updateRandom() {
        if (registeredIds.isEmpty()) return;
        String id = registeredIds.get(random.nextInt(registeredIds.size()));
        Status status = randomStatus();
        String key = KEYS.get(random.nextInt(KEYS.size()));
        store.update(id, randomValue(key), status);
    }

    private void addNew() {
        String id = UUID.randomUUID().toString().substring(0, 8);
        String key = KEYS.get(random.nextInt(KEYS.size()));
        store.put(id, key + "[new]", randomValue(key));
        registeredIds.add(id);
    }

    private void removeRandom() {
        if (registeredIds.size() <= 3) return;
        int idx = random.nextInt(registeredIds.size());
        String id = registeredIds.remove(idx);
        store.remove(id);
    }

    private Object randomValue(String key) {
        if (key.contains("rate") || key.contains("usage")) {
            return String.format("%.1f%%", random.nextDouble() * 100);
        }
        if (key.contains("connections") || key.contains("depth")) {
            return random.nextInt(500);
        }
        return STRING_VALUES.get(random.nextInt(STRING_VALUES.size()));
    }

    private Status randomStatus() {
        int r = random.nextInt(10);
        if (r < 6) return Status.ACTIVE;
        if (r < 8) return Status.STALE;
        return Status.ERROR;
    }
}
