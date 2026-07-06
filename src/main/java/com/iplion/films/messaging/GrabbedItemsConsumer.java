package com.iplion.films.messaging;

import com.iplion.films.dto.FilmImportItemDto;
import com.iplion.films.service.FilmImportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class GrabbedItemsConsumer {
    private final FilmImportService filmImportService;

    @JmsListener(destination = "${app.activemq.queues.grabbed-films}")
    public void consume(GrabbedItemsMessageDto message) {
        if (message.version() != GrabbedItemsMessageDto.VERSION) {
            throw new IllegalArgumentException("Unsupported message version: " + message.version());
        }

        List<FilmImportItemDto> items = Objects.requireNonNullElse(message.items(), List.of());

        int savedItemsCount = filmImportService.processNewItems(items);

        log.info("Saved {} new films from grabbed-films message", savedItemsCount);
    }

}
