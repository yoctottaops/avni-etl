package org.avniproject.etl.config;

public class StubEtlServiceConfig extends EtlServiceConfig {
    @Override
    public int getCurrentTimeOffsetSeconds() {
        return 0;
    }
}
