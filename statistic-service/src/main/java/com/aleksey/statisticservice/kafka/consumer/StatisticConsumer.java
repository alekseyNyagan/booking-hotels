package com.aleksey.statisticservice.kafka.consumer;

import com.aleksey.statisticservice.kafka.model.StatisticModel;
import com.aleksey.statisticservice.model.Statistic;
import com.aleksey.statisticservice.service.StatisticService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class StatisticConsumer {

    private final StatisticService statisticService;

    @Bean
    public Consumer<StatisticModel> consumer() {
        return statisticModel -> {
            Statistic statistic = new Statistic();
            statistic.setUserId(statisticModel.userId());
            statistic.setCheckInDate(statisticModel.arrivalDate());
            statistic.setCheckOutDate(statisticModel.departureDate());
            statisticService.saveStatistic(statistic);
        };
    }
}