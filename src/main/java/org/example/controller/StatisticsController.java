package org.example.controller;

import org.example.dto.StatisticsResult;
import org.example.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatisticsController {

    @Autowired
    StatisticsService statisticsService;

    @GetMapping("/statistics")
    public ResponseEntity statistics() {
        StatisticsResult result = new StatisticsResult();
        result.setTotal(statisticsService.getTotalStatistic());
        return ResponseEntity.ok(result);
    }
}
