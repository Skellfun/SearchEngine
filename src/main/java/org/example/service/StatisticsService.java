package org.example.service;

import org.example.dto.NegativeResult;
import org.example.dto.Result;
import org.example.dto.StatisticsResult;
import org.example.model.Page;
import org.example.model.Site;
import org.example.repository.LemmaRepository;
import org.example.repository.PageRepository;
import org.example.repository.SiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StatisticsService {
    @Autowired
    PageRepository pageRepository;
    @Autowired
    SiteRepository siteRepository;
    @Autowired
    LemmaRepository lemmaRepository;

    public StatisticsResult.Total getTotalStatistic() {
        StatisticsResult.Total total = new StatisticsResult.Total();
        total.setPages(pageRepository.count());
        total.setSites(siteRepository.count());
        total.setLemmas(lemmaRepository.count());
        List<Site.Status> sitesStatus = siteRepository.findAll().stream().map(Site::getStatus).distinct().toList();
        if (sitesStatus.isEmpty()) {
            total.setIndexing(false);
        } else {
            total.setIndexing(sitesStatus.size() == 1 && sitesStatus.get(0) == Site.Status.INDEXED);
        }
        return total;
    }

    private List<StatisticsResult.Detailed> getDetailedStatistic() {
        List<StatisticsResult.Detailed> detailedList = new ArrayList<>();
        List<Site> sites = siteRepository.findAll();
        for (Site site : sites) {
            StatisticsResult.Detailed detailed = new StatisticsResult.Detailed();
            List<Page> pages = site.getPages();
            detailed.setPages(pages.size());
            long lemmaCount = pages.stream().mapToLong(p -> p.getLemmas().size()).sum();
            detailed.setLemmas(lemmaCount);
            detailed.setUrl(site.getUrl());
            detailed.setName(site.getName());
            detailed.setStatus(site.getStatus().toString());
            detailed.setStatusTime(site.getStatusTime().getTime());
            detailed.setError(site.getLast_error());
            detailedList.add(detailed);
        }
        return detailedList;
    }

    public Result getStatistic() {
        StatisticsResult result = new StatisticsResult();
        try {
            result.setTotal(getTotalStatistic());
            result.setDetailed(getDetailedStatistic());
        } catch (Exception e) {
            return new NegativeResult("Ошибка сбора статистики");
        }
        return result;
    }
}
