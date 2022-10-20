package org.example.service;

import lombok.Getter;
import org.example.config.Config;
import org.example.dto.IndexingThread;
import org.example.dto.NegativeResult;
import org.example.dto.PositiveResult;
import org.example.dto.Result;
import org.example.model.*;
import org.example.repository.*;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ForkJoinPool;

@Service
@EnableConfigurationProperties(value = Config.class)
public class IndexService {

    private static final String NO_SITES_ERROR = "В конфигурационном файле отсутствуют сайты для индексации. Формат для добавления: \n " +
            "sites:\n" +
            "- \n" +
            "\turl: https://www.lenta.ru\n" +
            "\tname: Лента.ру\n" +
            "- \n" +
            "\turl: https://www.skillbox.ru\n" +
            "\tname: Skillbox";
    @Autowired
    @Getter
    Config config;
    PageRepository pageRepository;
    LemmaRepository lemmaRepository;
    FieldRepository fieldRepository;
    IndxRepository indxRepository;
    SiteRepository siteRepository;
    Lemmatyzer lemmatyzer;
    private Map<Site, IndexingThread> indexingThreads = new HashMap<>();
    @Getter
    private boolean isIndexing = false;

    @Autowired
    public IndexService(PageRepository pageRepository, LemmaRepository lemmaRepository, FieldRepository fieldRepository, IndxRepository indxRepository, SiteRepository siteRepository) {
        this.pageRepository = pageRepository;
        this.lemmaRepository = lemmaRepository;
        this.fieldRepository = fieldRepository;
        this.indxRepository = indxRepository;
        this.siteRepository = siteRepository;
        this.lemmatyzer = new Lemmatyzer();
    }

    public Result stopAllIndexing() {
        if (!isIndexing) {
            return new NegativeResult("Индексация не запущена");
        }
        isIndexing = false;
        indexingThreads.values().forEach(IndexingThread::stop);
        indexingThreads = new HashMap<>();
        siteRepository.findAll().forEach(site -> setSiteStatus(site, Site.Status.FAILED, "Индексация приостановлена пользователем"));
        return new PositiveResult();
    }

    public Result stopIndexing(Site site) {
        if (!indexingThreads.containsKey(site)) {
            return new NegativeResult("Индексация сайта: " + site.getUrl() + " не запущена");
        }
        indexingThreads.get(site).stop();
        indexingThreads.remove(site);
        return new PositiveResult();
    }

    public Result scanAllSites() {
        if (isIndexing) {
            return new NegativeResult("Индексация уже запущена");
        }
        siteRepository.deleteAll();
        isIndexing = true;
        List<Config.Site> preSites = config.getSites();
        if (preSites.isEmpty()) {
            return new NegativeResult(NO_SITES_ERROR);
        }
        for (Config.Site preSite : preSites) {
            Site site = new Site(Site.Status.INDEXING, new Date(), preSite.getUrl(), preSite.getName());
            siteRepository.saveAndFlush(site);
            scanSite(site);
        }
        return new PositiveResult();
    }

    public Result scanSite(Site site) {
        if(indexingThreads.containsKey(site)) {
            return new NegativeResult("Индексация сайта: " + site.getUrl() + " уже запущена");
        }
        IndexService indexService = this;
        ForkJoinPool pool = new ForkJoinPool();
        Runnable runnable = () -> {
            setSiteStatus(site, Site.Status.INDEXING, null);
            pool.invoke(new GetLinksAction("/", site, pageRepository, indexService));
            setSiteStatus(site, Site.Status.INDEXED, null);
            indexingThreads.remove(site);
            if (indexingThreads.isEmpty()) {
                isIndexing = false;
            }
        };
        Thread thread = new Thread(runnable);
        IndexingThread indexingThread = new IndexingThread(site, thread, pool);
        indexingThreads.put(site, indexingThread);
        try {
            thread.start();
        } catch (Exception e) {
            String error = "Ошибка индексации";
            setSiteStatus(site, Site.Status.FAILED, error);
            return new NegativeResult(error);
        }
        return new PositiveResult();
    }

    public Result scanPage(String url) {
        List<String> urls = siteRepository.findAll().stream().map(Site::getUrl).toList();
        Site site = null;
        boolean contains = false;
        for (String u : urls) {
            if (url.contains(u)) {
                contains = true;
                site = siteRepository.findByUrl(u).get();
                break;
            }
        }
        if (!contains) {
            return new NegativeResult("Данная страница находится за пределами сайтов, указанных в конфигурационном файле");
        }
        String path = url.replaceAll(site.getUrl(), "");
        Optional<Page> optionalPage = pageRepository.findByPath(path);
        optionalPage.ifPresent(page -> pageRepository.delete(page));
        try {
            Connection connection = Jsoup.connect(url).userAgent(config.getUserAgent())
                    .referrer("http://www.google.com")
                    .ignoreHttpErrors(true);
            Document document = connection.maxBodySize(0).get();
            int code = connection.execute().statusCode();
            String content = document.html();
            Page page = new Page(path, code, content, site);
            pageRepository.saveAndFlush(page);
            indexPage(page);
        } catch (Exception e) {
            return new NegativeResult("Не удалось подключиться, проверьте правильность написания адреса страницы");
        }
        return new PositiveResult();
    }

    public void indexPage(Page page) {
        if (page.getCode() % 100 >= 4) {
            return;
        }
        Map<Lemma, Float> lemmaEntities = new HashMap<>();
        List<Indx> indxEntities = new ArrayList<>();
        String content = page.getContent();
        List<Field> fields = fieldRepository.findAll();
        for (Field f : fields) {
            Elements elements = Jsoup.parse(content).select(f.getSelector());
            if (elements.isEmpty()) {
                continue;
            }
            String field = elements.get(0).toString();
            String cleanField = Jsoup.clean(field, new Safelist());
            Map<String, Integer> lemmas = lemmatyzer.getLemmas(cleanField);
            if (lemmas.isEmpty()) {
                continue;
            }

            lemmas.forEach((lemma, count) -> {
                Lemma newLemma = getLemma(lemma);
                Float rank = count * f.getWeight();
                lemmaEntities.merge(newLemma, rank, Float::sum);
            });
        }
        lemmaEntities.forEach((lemma, rank) -> {
            indxEntities.add(new Indx(page.getId(), lemma.getId(), rank));
        });
        indxRepository.saveAll(indxEntities);
        indxRepository.flush();
    }

    public void setSiteStatus(Site site, Site.Status status, String error) {
        site.setStatusTime(new Date());
        site.setStatus(status);
        site.setLast_error(error);
        siteRepository.saveAndFlush(site);
    }

    private synchronized Lemma getLemma(String lemma) {
        Lemma newLemma;
        Optional<Lemma> optionalLemma = lemmaRepository.findByLemma(lemma);
        if (optionalLemma.isEmpty()) {
            newLemma = new Lemma(lemma, 1);
        } else {
            newLemma = optionalLemma.get();
            newLemma.setFrequency(newLemma.getFrequency() + 1);
        }
        lemmaRepository.saveAndFlush(newLemma);
        return newLemma;
    }
}
