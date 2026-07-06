package com.iplion.films.scheduler;

import com.iplion.films.config.KinopoiskGrabSchedulerProperties;
import com.iplion.films.dto.FilmImportItemDto;
import com.iplion.films.messaging.GrabbedItemsProducer;
import com.iplion.films.service.KinopoiskGrabService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
    prefix = "app.scheduler.grab-kinopoisk",
    name = "enabled",
    havingValue = "true"
)
public class KinopoiskGrabScheduler {
    private final KinopoiskGrabSchedulerProperties props;
    private final KinopoiskGrabService kinopoiskGrabService;
    private final GrabbedItemsProducer producer;

    @Scheduled(cron = "${app.scheduler.grab-kinopoisk.cron}")
    public void grab() {
        int dayOfWeekNum = LocalDate.now().getDayOfWeek().getValue();

        List<FilmImportItemDto> newItems = kinopoiskGrabService.grabFilmsByGenreWithRatingOrder(
            dayOfWeekNum,
            props.itemsNum()
        );

        producer.sendItems(newItems);
    }

}
