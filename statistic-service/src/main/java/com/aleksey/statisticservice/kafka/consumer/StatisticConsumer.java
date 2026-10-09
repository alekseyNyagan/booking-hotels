package com.aleksey.statisticservice.kafka.consumer;

import com.aleksey.statisticservice.kafka.model.StatisticModel;
import com.aleksey.statisticservice.service.StatisticService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
@Slf4j
public class StatisticConsumer {

    private final StatisticService statisticService;

    @Bean
    public Consumer<Message<List<StatisticModel>>> consumer() {
        return message -> {
            List<StatisticModel> statisticList = message.getPayload();

            log.info("📥 Received batch of statistics from Kafka. Size: {}", statisticList.size());
            statisticService.saveStatisticBatch(statisticList);
        };
    }
}