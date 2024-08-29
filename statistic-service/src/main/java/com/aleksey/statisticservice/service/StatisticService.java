package com.aleksey.statisticservice.service;

import com.aleksey.statisticservice.model.Statistic;
import com.aleksey.statisticservice.repository.StatisticRepository;
import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticService {

    private final StatisticRepository statisticRepository;

    public String getStatistic() throws CsvRequiredFieldEmptyException, CsvDataTypeMismatchException {
        List<Statistic> statistic = statisticRepository.findAll();
        StringWriter stringWriter = new StringWriter();
        StatefulBeanToCsv<Statistic> writer = new StatefulBeanToCsvBuilder<Statistic>(stringWriter)
                .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                .withOrderedResults(false)
                .build();

        writer.write(statistic);

        return stringWriter.toString();
    }

    public void saveStatistic(Statistic statistic) {
        statisticRepository.save(statistic);
    }
}