package ru.rbkn99.sd.clock;

import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class EventStatisticImplTest {
    private SetableClock clock;
    private EventStatistic stats;

    @Before
    public void setUp() {
        clock = new SetableClock(Instant.now());
        stats = new EventStatisticImpl(clock);
    }

    @Test
    public void testNotExistingName() {
        assertThat(stats.getEventStatisticByName("event0")).isZero();
    }

    @Test
    public void testEventDelete() {
        stats.incEvent("event1");
        clock.plus(1, ChronoUnit.HOURS);
        assertThat(stats.getEventStatisticByName("event1")).isZero();
    }

    @Test
    public void testStatisticByName() {
        stats.incEvent("event1");
        stats.incEvent("event1");
        stats.incEvent("event2");

        assertThat(stats.getEventStatisticByName("event1"))
                .isEqualTo(1.0 / 30);
        assertThat(stats.getEventStatisticByName("event2"))
                .isEqualTo(1.0 / 60);
    }

    @Test
    public void testMultipleEvents() {
        stats.incEvent("event1");
        clock.plus(30, ChronoUnit.MINUTES);
        stats.incEvent("event2");
        stats.incEvent("event2");
        stats.incEvent("event2");
        clock.plus(30, ChronoUnit.MINUTES);
        stats.incEvent("event3");
        stats.incEvent("event3");
        assertThat(stats.getAllEventStatistic())
                .containsOnlyKeys("event2", "event3");
        assertThat(stats.getAllEventStatistic())
                .containsEntry("event2", 1.0 / 20);
        assertThat(stats.getAllEventStatistic())
                .containsEntry("event3", 1.0 / 30);
    }

}
