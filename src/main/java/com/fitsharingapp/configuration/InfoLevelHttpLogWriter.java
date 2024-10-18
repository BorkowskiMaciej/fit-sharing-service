package com.fitsharingapp.configuration;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.zalando.logbook.Correlation;
import org.zalando.logbook.HttpLogWriter;
import org.zalando.logbook.Precorrelation;

@Slf4j
public class InfoLevelHttpLogWriter implements HttpLogWriter {

    @Override
    public void write(@NonNull Precorrelation precorrelation, @NonNull String request) {
        log.info(request);
    }

    @Override
    public void write(@NonNull Correlation correlation, @NonNull String response) {
        log.info(response);
    }

    @Override
    public boolean isActive() {
        return HttpLogWriter.super.isActive();
    }

}
