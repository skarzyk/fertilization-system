package com.app.scheduler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Component
public class MonitoringScheduler {

    private final TaskScheduler taskScheduler;
    private final long fixedDelayMs;
    private final Map<Long, ScheduledFuture<?>> tasks = new ConcurrentHashMap<>();

    public MonitoringScheduler(TaskScheduler taskScheduler,
                               @Value("${monitor.fixed-delay-ms}") long fixedDelayMs) {
        this.taskScheduler = taskScheduler;
        this.fixedDelayMs = fixedDelayMs;
    }

    public void start(Long plotId, Runnable task) {
        ScheduledFuture<?> future = taskScheduler.scheduleWithFixedDelay(
                task,
                Duration.ofMillis(fixedDelayMs)
        );
        tasks.put(plotId, future);
    }

    public void stop(Long plotId) {
        ScheduledFuture<?> future = tasks.remove(plotId);
        if (future != null) {
            future.cancel(false);
        }
    }
}