package com.iplion.films.messaging;

import com.iplion.films.config.ActiveMqProperties;
import com.iplion.films.dto.FilmImportItemDto;
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
    private final ActiveMqProperties props;

    public void sendItems(List<FilmImportItemDto> items) {
        var message = new GrabbedItemsMessageDto(items);

        jmsTemplate.convertAndSend(
            props.getQueueName(ActiveMqProperties.GRABBED_FILMS_QUEUE_KEY),
            message
        );

        log.info("{} items from kinopoisk sent to queue", items.size());
    }
}
