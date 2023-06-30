package org.avniproject.etl.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EtlServiceConfig {
    @Value("${avni.current.time.offset.seconds}")
    private int currentTimeOffsetSeconds;

    public int getCurrentTimeOffsetSeconds() {
        return currentTimeOffsetSeconds;
    }
}
