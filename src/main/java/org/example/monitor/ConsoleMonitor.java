package org.example.monitor;

import org.example.model.DataEntry;
import org.example.model.DataEntry.Status;
import org.example.store.DataStore;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ConsoleMonitor {

    private static final String RESET  = "[0m";
    private static final String BOLD   = "[1m";
    private static final String GREEN  = "[32m";
    private static final String YELLOW = "[33m";
    private static final String RED    = "[31m";
    private static final String CYAN   = "[36m";
    private static final String GRAY   = "[90m";
    private static final String CLEAR  = "[2J[H";

    private static final DateTimeFormatter TIME_FMT =
            DateTimeFormatter.ofPattern("HH:mm:ss.SSS").withZone(ZoneId.systemDefault());
    private static final int MAX_ROWS = 20;

    private final DataStore store;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final Instant startedAt = Instant.now();

    public ConsoleMonitor(DataStore store) {
        this.store = store;
    }

    public void start(long intervalMs) {
        scheduler.scheduleAtFixedRate(this::render, 0, intervalMs, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        scheduler.shutdownNow();
    }

    private void render() {
        StringBuilder sb = new StringBuilder();
        sb.append(CLEAR);

        List<DataEntry> entries = store.getAll();
        Map<Status, Long> summary = store.getStatusSummary();
        String now = TIME_FMT.format(Instant.now());

        // 헤더
        sb.append(BOLD).append(CYAN)
          .append("╔══════════════════════════════════════════════════════════════╗\n")
          .append("║            DATA MONITOR  —  Admin Console (PoC)             ║\n")
          .append("╚══════════════════════════════════════════════════════════════╝\n")
          .append(RESET);

        // 요약 통계
        long upSec = (Instant.now().toEpochMilli() - startedAt.toEpochMilli()) / 1000;
        sb.append(GRAY).append(String.format("  갱신: %s   가동: %ds   누적 업데이트: %d\n",
                now, upSec, store.getTotalUpdates())).append(RESET);
        sb.append("\n");

        sb.append(BOLD).append("  [ 상태 요약 ]\n").append(RESET);
        sb.append(String.format("  전체: %s%d%s   ",
                BOLD, store.size(), RESET));
        sb.append(statusTag(Status.ACTIVE,  summary.getOrDefault(Status.ACTIVE,  0L)));
        sb.append("  ");
        sb.append(statusTag(Status.STALE,   summary.getOrDefault(Status.STALE,   0L)));
        sb.append("  ");
        sb.append(statusTag(Status.ERROR,   summary.getOrDefault(Status.ERROR,   0L)));
        sb.append("\n\n");

        // 데이터 테이블
        sb.append(BOLD)
          .append(String.format("  %-10s %-25s %-15s %-8s %s\n",
                  "ID", "KEY", "VALUE", "STATUS", "UPDATED"))
          .append(RESET);
        sb.append(GRAY)
          .append("  ──────────────────────────────────────────────────────────────\n")
          .append(RESET);

        int shown = 0;
        for (DataEntry e : entries) {
            if (shown++ >= MAX_ROWS) break;
            String statusStr = colorStatus(e.getStatus());
            String timeStr   = TIME_FMT.format(e.getUpdatedAt());
            String valueStr  = String.valueOf(e.getValue());
            if (valueStr.length() > 13) valueStr = valueStr.substring(0, 12) + "…";

            sb.append(String.format("  %-10s %-25s %-15s %-17s %s%s%s\n",
                    e.getId(),
                    truncate(e.getKey(), 24),
                    valueStr,
                    statusStr,
                    GRAY, timeStr, RESET));
        }

        if (entries.size() > MAX_ROWS) {
            sb.append(GRAY)
              .append(String.format("  ... 외 %d건 (최근 %d건만 표시)\n",
                      entries.size() - MAX_ROWS, MAX_ROWS))
              .append(RESET);
        }

        sb.append("\n").append(GRAY).append("  [Ctrl+C] 종료\n").append(RESET);

        System.out.print(sb);
    }

    private String statusTag(Status status, long count) {
        return colorStatus(status) + "(" + count + ")" + RESET;
    }

    private String colorStatus(Status status) {
        return switch (status) {
            case ACTIVE -> GREEN  + BOLD + "ACTIVE" + RESET;
            case STALE  -> YELLOW + BOLD + "STALE " + RESET;
            case ERROR  -> RED    + BOLD + "ERROR " + RESET;
        };
    }

    private String truncate(String s, int max) {
        return s.length() <= max ? s : s.substring(0, max - 1) + "…";
    }
}
