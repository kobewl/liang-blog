package com.liang.blog.util;

import java.time.Clock;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class TimeProvider {

    private final Clock clock;

    public TimeProvider() {
        this.clock = Clock.systemDefaultZone();
    }

    public TimeProvider(Clock clock) {
        this.clock = clock;
    }

    public LocalDateTime now() {
        return LocalDateTime.now(clock);
    }
}
