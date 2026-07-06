package com.iplion.films.messaging;

import com.iplion.films.config.ActiveMqProperties;
import com.iplion.films.integration.kinopoisk.dto.KinopoiskItemDto;
import com.iplion.films.mapper.FilmImportItemMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class GrabbedItemsProducer {
    private final JmsTemplate jmsTemplate;
    private final FilmImportItemMapper mapper;
    private final ActiveMqProperties props;

    public void sendItems(List<KinopoiskItemDto> items) {
        var message = new GrabbedItemsMessageDto(
            items.stream()
                .map(mapper::toFilmImportItemDto)
                .toList()
        );

        jmsTemplate.convertAndSend(
            props.getQueueName(ActiveMqProperties.GRABBED_FILMS_QUEUE_KEY),
            message
        );

        log.info("{} items from kinopoisk sent to queue", items.size());
    }
}
