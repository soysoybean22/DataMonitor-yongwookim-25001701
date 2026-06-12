package org.example.store;

import org.example.model.DataEntry;
import org.example.model.DataEntry.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DataStoreTest {

    private DataStore store;

    @BeforeEach
    void setUp() {
        store = new DataStore();
    }

    @Test
    void put_항목을_저장하고_조회한다() {
        store.put("id1", "cpu.usage", "55%");
        assertEquals(1, store.size());
        DataEntry entry = store.getAll().get(0);
        assertEquals("id1", entry.getId());
        assertEquals("cpu.usage", entry.getKey());
        assertEquals("55%", entry.getValue());
        assertEquals(Status.ACTIVE, entry.getStatus());
    }

    @Test
    void update_값과_상태를_갱신한다() {
        store.put("id1", "cpu.usage", "55%");
        boolean updated = store.update("id1", "90%", Status.ERROR);
        assertTrue(updated);
        DataEntry entry = store.getAll().get(0);
        assertEquals("90%", entry.getValue());
        assertEquals(Status.ERROR, entry.getStatus());
        assertEquals(2, entry.getUpdateCount());
    }

    @Test
    void update_존재하지_않는_id는_false를_반환한다() {
        assertFalse(store.update("ghost", "value", Status.ACTIVE));
    }

    @Test
    void remove_항목을_삭제한다() {
        store.put("id1", "k", "v");
        store.put("id2", "k", "v");
        assertTrue(store.remove("id1"));
        assertEquals(1, store.size());
        assertFalse(store.remove("id1")); // 이미 삭제됨
    }

    @Test
    void getStatusSummary_상태별_집계를_반환한다() {
        store.put("a", "k", "v");
        store.put("b", "k", "v");
        store.update("b", "v2", Status.ERROR);
        store.put("c", "k", "v");
        store.update("c", "v2", Status.STALE);

        Map<Status, Long> summary = store.getStatusSummary();
        assertEquals(1L, summary.get(Status.ACTIVE));
        assertEquals(1L, summary.get(Status.STALE));
        assertEquals(1L, summary.get(Status.ERROR));
    }

    @Test
    void getAll_최근_업데이트_순으로_정렬된다() throws InterruptedException {
        store.put("first", "k", "v");
        Thread.sleep(10);
        store.put("second", "k", "v");

        var all = store.getAll();
        assertEquals("second", all.get(0).getId());
        assertEquals("first",  all.get(1).getId());
    }

    @Test
    void getTotalUpdates_put과_update를_누적_카운트한다() {
        store.put("a", "k", "v");
        store.put("b", "k", "v");
        store.update("a", "v2", Status.STALE);
        assertEquals(3L, store.getTotalUpdates());
    }
}
