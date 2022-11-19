package org.example.service;

import org.example.config.Config;
import org.example.model.Page;
import org.example.model.Site;
import org.example.repository.PageRepository;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveAction;

public class GetLinksAction extends RecursiveAction {

    private final List<GetLinksAction> actions = new ArrayList<>();
    PageRepository pageRepository;
    private final String path;
    private final Site site;
    private final IndexService indexService;
    private final Config config;

    public GetLinksAction(String path, Site site, PageRepository pageRepository, IndexService indexService) {
        this.pageRepository = pageRepository;
        this.path = path;
        this.site = site;
        this.indexService = indexService;
        config = indexService.getConfig();
    }

    @Override
    protected void compute() {
        if (getPool().isShutdown() || !isNewLink(path, site)) {
            return;
        }
        try {
            int timeout = 500 + (int) (1000 * Math.random());
            Thread.sleep(timeout);
            Page page = createPage(path, site, config.getUserAgent());
            if (page == null) {
                return;
            }
            indexService.indexPage(page);
            Document document = Jsoup.parse(page.getContent());
            if (page.getCode() % 100 >= 4) {
                return;
            }
            List<String> childes = getChildes(document);
            if (childes.isEmpty()) {
                return;
            }
            childes.forEach(child -> {
                GetLinksAction newAction = new GetLinksAction(child, site, pageRepository, indexService);
                actions.add(newAction);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        invokeAll(actions);
    }

    public Page createPage(String path, Site site, String userAgent) throws Exception {
        String url = site.getUrl() + path;
        Connection connection = Jsoup.connect(url).userAgent(userAgent)
                .referrer("http://www.google.com")
                .ignoreHttpErrors(true);
        Document document = connection.maxBodySize(0).get();
        int code = connection.execute().statusCode();
        String content = document.html();
        Page page = new Page(path, code, content, site);
        synchronized (site) {
            if (isNewLink(path, site)) {
                pageRepository.saveAndFlush(page);
            } else {
                return null;
            }
        }
        return page;
    }

    public List<String> getChildes(Document document) {
        List<String> hrefs = new ArrayList<>();
        Elements elements = document.select("a[href]");
        for (Element element : elements) {
            String href = element.attr("href");
            if (hrefs.contains(href) || href.isEmpty()) {
                continue;
            }
            if (href.matches("[A-z0-9-_/]+(.html)?")) {
                hrefs.add(href);
            }
        }
        return hrefs;
    }

    public boolean isNewLink(String path, Site site) {
        long siteId = site.getId();
        return pageRepository.findByPathAndSiteId(path, siteId).isEmpty();
    }
}
