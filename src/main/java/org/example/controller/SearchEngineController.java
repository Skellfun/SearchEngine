package org.example.controller;

import org.example.config.Config;
import org.example.dto.Result;
import org.example.service.IndexService;
import org.example.service.SearchService;
import org.example.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@EnableConfigurationProperties(value = Config.class)
@RestController
public class SearchEngineController {
    @Autowired
    private IndexService indexService;
    @Autowired
    private SearchService searchService;
    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/admin/startIndexing")
    public ResponseEntity startIndexing() {
        Result result = indexService.scanAllSites();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/admin/stopIndexing")
    private ResponseEntity stopIndexing() {
        Result result = indexService.stopAllIndexing();
        return ResponseEntity.ok(result);
    }

    @PostMapping("/admin/indexPage")
    public ResponseEntity indexPage(@RequestParam String url) {
        Result result = indexService.scanPage(url);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/admin/statistics")
    public ResponseEntity getStatistics() {
        Result result = statisticsService.getStatistic();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/admin/search")
    public ResponseEntity search(@RequestParam(required = false) String site,
                                 @RequestParam String query,
                                 @RequestParam(defaultValue = "0") int offset,
                                 @RequestParam(defaultValue = "20") int limit) {

        Result result = searchService.search(query, offset, limit);
        return ResponseEntity.ok(result);
    }
}
