package com.aleksey.statisticservice.kafka.consumer;

import com.aleksey.statisticservice.kafka.model.StatisticModel;
import com.aleksey.statisticservice.repository.StatisticDao;
import com.aleksey.statisticservice.service.StatisticService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
@Slf4j
public class StatisticConsumer {

    private final StatisticService statisticService;

    @Bean
    public Consumer<StatisticModel> consumer() {
        return statisticModel -> {
            log.info("📥 Received statistic from Kafka: {}", statisticModel);
            statisticService.saveStatistic(statisticModel);
        };
    }
}