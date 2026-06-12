package org.example;

import org.example.monitor.ConsoleMonitor;
import org.example.simulator.DataSimulator;
import org.example.store.DataStore;

public class DataMonitor {

    public static void main(String[] args) throws InterruptedException {
        DataStore store       = new DataStore();
        DataSimulator sim     = new DataSimulator(store, 12);
        ConsoleMonitor monitor = new ConsoleMonitor(store);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            sim.stop();
            monitor.stop();
            System.out.println("\n[DataMonitor] 종료되었습니다.");
        }));

        sim.start();
        monitor.start(500);

        Thread.currentThread().join(); // Ctrl+C 대기
    }
}
