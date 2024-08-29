package com.aleksey.statisticservice.model;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.UUID;

@Document(collection = "statistic")
@Data
public class Statistic {
    @Id
    @CsvBindByName(column = "ID")
    private String id;

    @CsvBindByName(column = "User ID")
    private UUID userId;

    @CsvBindByName(column = "Check-In Date")
    private LocalDate checkInDate;

    @CsvBindByName(column = "Check-Out Date")
    private LocalDate checkOutDate;
}