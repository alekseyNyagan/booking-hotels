package com.aleksey.statisticservice.repository;

import com.aleksey.statisticservice.model.Statistic;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StatisticRepository extends MongoRepository<Statistic, String> {
}