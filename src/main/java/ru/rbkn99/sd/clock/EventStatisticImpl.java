package ru.rbkn99.sd.clock;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EventStatisticImpl implements EventStatistic {
    private final Clock clock;
    private final Map<String, List<Instant>> events = new HashMap<>();

    public EventStatisticImpl(Clock clock) {
        this.clock = clock;
    }

    @Override
    public void incEvent(String name) {
        if (!events.containsKey(name)) {
            events.put(name, new ArrayList<>());
        }
        events.get(name).add(clock.instant());
    }

    @Override
    public double getEventStatisticByName(String name) {
        return getEventStatisticByNameImpl(name, true);
    }

    @Override
    public Map<String, Double> getAllEventStatistic() {
        deleteOldEvents();
        return events.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> getEventStatisticByNameImpl(entry.getKey(), false)));
    }

    @Override
    public void printStatistic() {
        Map<String, Double> statistic = getAllEventStatistic();
        for (String name : statistic.keySet()) {
            System.out.printf("RPM for %s is %f%n", name, statistic.get(name));
        }
    }

    private double getEventStatisticByNameImpl(String name, boolean deleteOld) {
        if (deleteOld) {
            deleteOldEvents();
        }
        if (!events.containsKey(name)) {
            return 0.0;
        }
        return events.get(name).size() / 60.0;
    }

    private void deleteOldEvents() {
        Instant hourAgo = clock.instant().minus(1, ChronoUnit.HOURS);
        for (String name : events.keySet()) {
            List<Instant> actualEvents = events.get(name)
                    .stream()
                    .filter(instant -> instant.isAfter(hourAgo))
                    .collect(Collectors.toList());
            events.put(name, actualEvents);
        }
        events.entrySet().removeIf(entry -> entry.getValue().isEmpty());
    }

}
